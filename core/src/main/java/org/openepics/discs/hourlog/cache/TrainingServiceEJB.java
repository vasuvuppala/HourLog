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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.openepics.discs.hourlog.ent.ExternalService;
import org.openepics.discs.hourlog.ent.TrainingRecord;
import org.openepics.discs.hourlog.status.ExtServiceName;
import org.openepics.discs.hourlog.status.ExternalServiceEJB;
import org.openepics.discs.hourlog.status.ServiceStatusCode;
import org.openepics.discs.hourlog.util.AppProperties;
import org.openepics.discs.hourlog.util.AppProperties.HourLogProperty;
import org.openepics.discs.hourlog.util.Utility;

/**
 * Sync Training records
 *
 * @author vuppala
 */
@Stateless
public class TrainingServiceEJB implements Serializable {

    // private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss"; // date format used in the results from external TR service 
    // private static final String SERVICE_NAME = "TrainingRecords";
    /**
     * Training Records from external service
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    private static class SvcEmployee implements Serializable {

        @XmlElement(name = "emp_no")
        private String employeeNumber;
        @XmlElement(name = "person_id")
        private String personId;
        @XmlElement(name = "last_name")
        private String lastName;
        @XmlElement(name = "first_name")
        private String firstName;
        @XmlElement(name = "work_email")
        private String workEmail;

        public SvcEmployee() {
        }

        /**
         * Convert external training record to local entity
         *
         * @param updated_at
         * @return training record
         */
        public TrainingRecord toTrainingReocrd(Date updated_at) {
            TrainingRecord tr = new TrainingRecord();

            tr.setEmployeeNumber(Utility.parseNumber(employeeNumber));
            tr.setEmployeeId(personId);
            tr.setLastName(lastName);
            tr.setFirstName(firstName);
            tr.setWorkEmail(workEmail);
            tr.setUpdatedAt(updated_at);
            tr.setOic(true);

            return tr;
        }
    }

    @Inject
    private AppProperties appProperties;
    @EJB
    private ExternalServiceEJB extServiceEJB;

    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    private static final Logger logger = Logger.getLogger(TrainingServiceEJB.class.getName());

    private ServiceStatusCode remSvcStatus = ServiceStatusCode.SERVICE_UP;
    private String remSvcStatusMsg = "Service is working";
    private ExternalService trainingService;

    public TrainingServiceEJB() {
    }

    /**
     * Perform the sync
     *
     */
    public void syncCache() {
        Date currentDate = Utility.currentDate();
        logger.log(Level.INFO, "Syncing of training records started");
        trainingService = extServiceEJB.findService(ExtServiceName.TRAINING);

        boolean success = fetchUsersWithOIC();

        trainingService.setStatus(remSvcStatus.toString());
        trainingService.setStatusMessage(remSvcStatusMsg);
        if (success) {
            trainingService.setLastServicedAt(currentDate);
        }
        trainingService.setStatusUpdatedAt(currentDate);
        extServiceEJB.saveService(trainingService);

        logger.log(Level.INFO, "Synced Training cache");
    }

    /**
     * Fetch training records from external service and store them locally.
     *
     */
    private boolean fetchUsersWithOIC() {
        List<SvcEmployee> usersWithOIC = Collections.emptyList();
        boolean success = false;
        Client client = null;

        try {
            client = ClientBuilder.newClient();

            String BASE_URI = appProperties.getProperty(HourLogProperty.TRNGSVC_BASE_URI);
            String serviceEnabled = appProperties.getProperty(HourLogProperty.TRNGSVC_ENABLED);

            if (serviceEnabled == null || !serviceEnabled.equals("true")) {
                remSvcStatus = ServiceStatusCode.SERVICE_DISABLED;
                remSvcStatusMsg = "Service is not enabled";
            } else {
                logger.log(Level.FINE, " getting OIC trained employees");

                usersWithOIC = client.target(BASE_URI).request(MediaType.APPLICATION_JSON).get(new GenericType<List<SvcEmployee>>() {
                });

                if (usersWithOIC.isEmpty()) {
                    remSvcStatus = ServiceStatusCode.SERIVE_FLAKY;
                    remSvcStatusMsg = "Service did not return any employees";
                } else {
                    remSvcStatus = ServiceStatusCode.SERVICE_UP;
                    remSvcStatusMsg = "Service is working";
                    Query query = em.createQuery("UPDATE TrainingRecord c SET  c.oic = false");
                    int count = query.executeUpdate();
                    logger.log(Level.FINE, "Reset OIC training for {0} records", count);
                    TrainingRecord tr;
                    Date updated_at = Utility.currentDate();

                    for (SvcEmployee emp : usersWithOIC) {
                        logger.log(Level.FINE, "Employee {0}", emp.lastName);
                        tr = emp.toTrainingReocrd(updated_at);
                        tr = em.merge(tr);
                        tr.setOic(true);
                    }
                    logger.log(Level.FINE, "Updated {0} records", usersWithOIC.size());
                    logger.log(Level.FINE, "updated users with training information");
                    success = true;
                }
            }
        } catch (Exception e) {
            remSvcStatus = ServiceStatusCode.SERVICE_DOWN;
            remSvcStatusMsg = "Remote service appears to be down or there is an error in the response data";
            logger.log(Level.SEVERE, remSvcStatusMsg, e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        logger.log(Level.FINE, "Found {0} employees from external Training Service", usersWithOIC.size());
        logger.log(Level.FINE, remSvcStatusMsg);

        return success;
    }
}
