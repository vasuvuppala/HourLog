/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013.
 *  State University (c) Copyright 2013.
 *  
 *  You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *    http://www.gnu.org/licenses/gpl.txt
 *  
 *  Contact Information:
 *       Facility for Rare Isotope Beam
 *       Michigan State University
 *       East Lansing, MI 48824-1321
 *        http://frib.msu.edu
 */
package org.openepics.discs.hourlog.cache;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.openepics.discs.hourlog.ent.ExternalService;
import org.openepics.discs.hourlog.ent.TroubleReport;
import org.openepics.discs.hourlog.status.ExtServiceName;
import org.openepics.discs.hourlog.status.ExternalServiceEJB;
import org.openepics.discs.hourlog.status.ServiceStatusCode;
import org.openepics.discs.hourlog.util.AppProperties;
import org.openepics.discs.hourlog.util.AppProperties.HourLogProperty;
import org.openepics.discs.hourlog.util.Utility;

/**
 * Sync trouble reports cache with external source
 *
 * @author vuppala
 */
@Stateless
public class TroubleReportServiceEJB implements Serializable {

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss"; // date format used in the results from external TR service 

    /**
     * Remote/external Trouble Report (as it comes from the remote service)
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    private static class SvcTroubleReport implements Serializable {

        @XmlElement(name = "id")
        private String trNumber;
        @XmlElement(name = "date")
        private String submissionDate;
        @XmlElement(name = "problem")
        private String problem;
        @XmlElement(name = "system")
        private String system;
        @XmlElement(name = "employee_person_id")
        private String employeeUniqueName;
        @XmlElement(name = "employee_emp_no")
        private String employeeNumber;
        @XmlElement(name = "employee_name")
        private String employeeName;

        public SvcTroubleReport() {
        }

        /**
         * Convert the record from external service to local trouble report
         * entity
         *
         * @param updated_at
         * @return trouble report record
         * @throws Exception
         */
        public TroubleReport toTroubleReport(Date updated_at) throws Exception {
            TroubleReport tr = new TroubleReport();

            tr.setId(Utility.parseNumber(trNumber));
            tr.setReportDate(Utility.parseDate(DATE_FORMAT, submissionDate));
            tr.setProblem(problem);
            tr.setSystem(system);
            tr.setEmployeeId(Integer.parseInt(employeeNumber)); //TODO: is it better to keep this is as String?
            tr.setEmployeeName(employeeName == null ? (employeeUniqueName == null ? "NONE" : employeeUniqueName) : employeeName);
            tr.setEmployeeUniqName(employeeUniqueName == null ? "NONE" : employeeUniqueName);
            tr.setUpdatedAt(updated_at);

            return tr;
        }
    }

    @Inject
    private AppProperties appProperties;

    @EJB
    private ExternalServiceEJB extServiceEJB;

    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    private static final Logger logger = Logger.getLogger(TroubleReportServiceEJB.class.getName());
    private static final int TR_PAST_WINDOW = 7; // TODO: Update TRs from these many day from past. Needed only beause TR API does not have reported/modified date

    private ServiceStatusCode remSvcStatus = ServiceStatusCode.SERVICE_UP;
    private String remSvcStatusMsg = "Service is working";
    private ExternalService TRservice;

    public TroubleReportServiceEJB() {
    }

    /**
     * perform the sync
     *
     */
    public void syncCache() {
        Date currentDate = Utility.currentDate();
        logger.log(Level.INFO, "Syncing Trouble Reports ...");
        //convToJson();
        TRservice = extServiceEJB.findService(ExtServiceName.TROUBLE_REPORT);
        Date lastUpdatedOn = TRservice.getLastServicedAt();
        Boolean success = fetchTroubleReports(lastUpdatedOn);

        TRservice.setStatus(remSvcStatus.toString());
        TRservice.setStatusMessage(remSvcStatusMsg);
        if (success) {
            TRservice.setLastServicedAt(currentDate);
        }
        TRservice.setStatusUpdatedAt(currentDate);
        extServiceEJB.saveService(TRservice);

        logger.log(Level.INFO, "synced Trouble Reports cache");
    }

    /**
     * Get trouble reports (TR) from remote service and store them locally. Only
     * TRs modified after the given date are fetched.
     *
     * @param lastUpdatedOn
     * @return true if no errors
     */
    private boolean fetchTroubleReports(Date lastUpdatedOn) {
        boolean success = false;
        Client client = null;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

            client = ClientBuilder.newClient();
            List<SvcTroubleReport> troubleReports;

            String BASE_URI = appProperties.getProperty(HourLogProperty.TRSVC_BASE_URI);
            String serviceEnabled = appProperties.getProperty(HourLogProperty.TRSVC_ENABLED);

            if (serviceEnabled == null || !serviceEnabled.equals("true")) {
                remSvcStatus = ServiceStatusCode.SERVICE_DISABLED;
                remSvcStatusMsg = "Service is not enabled";
                logger.log(Level.WARNING, "Trouble Report service not enabled");
                return false;
            }

            logger.log(Level.INFO, "getting trouble reports");
            // TODO: workaround for problem with TR service. Replace '1960-01-01' with empty stirng
            // TODO: TR API checks 'incident occurrence date' which can be in the past. There is no way to check for reported/modified date.
            //  Hence this silly workaround. It does not work if a new TR has an incident date more than TR_PAST_WINDOW days ago. No good solution so far.
            String path = lastUpdatedOn == null ? "1960-01-01" : fmt.format(Utility.addDaysToDate(lastUpdatedOn, -TR_PAST_WINDOW));
            // String path = lastUpdatedOn == null ? "" : fmt.format(lastUpdatedOn);
            logger.log(Level.FINE, "Path Param {0}", path);

            troubleReports = client.target(BASE_URI).path(path).request(MediaType.APPLICATION_JSON).get(new GenericType<List<SvcTroubleReport>>() {
            });
            if (troubleReports != null) {
                logger.log(Level.FINE, "Found number of Trouble ReporTts from service: {0}", troubleReports.size());
            }

            if (troubleReports != null && !troubleReports.isEmpty()) {
                remSvcStatus = ServiceStatusCode.SERVICE_UP;
                remSvcStatusMsg = "Service is working";
                TroubleReport tr;
                Date updated_at = Utility.currentDate();
                for (SvcTroubleReport entry : troubleReports) {
                    tr = entry.toTroubleReport(updated_at);
                    if (tr.getReportDate() == null) {
                        logger.log(Level.WARNING, "TroubleReportManager#findTroubleReports: {0}", "TR date is null!");
                    }
                    if (tr.getEmployeeUniqName() == null) {
                        logger.log(Level.WARNING, "TroubleReportManager#findTroubleReports: {0}", "TR employee uniq name is null!");
                    }
                    if (tr.getId() == null) {
                        logger.log(Level.WARNING, "TroubleReportManager#findTroubleReports: {0}", "TR ID is null!");
                    }
                    em.merge(tr);
                }
                logger.log(Level.INFO, "Updatred Trouble Report Cache");
            }
            success = true;
            //logger.log(Level.FINE, "Found number of trouble reports: {0}", troubleReports.size());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Remote service appears to be down", e);
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return success;
    }
}
