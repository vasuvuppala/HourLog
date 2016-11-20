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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.openepics.discs.hourlog.ent.Experiment;
import org.openepics.discs.hourlog.ent.ExternalService;
import org.openepics.discs.hourlog.status.ExtServiceName;
import org.openepics.discs.hourlog.status.ExternalServiceEJB;
import org.openepics.discs.hourlog.status.ServiceStatusCode;
import org.openepics.discs.hourlog.util.AppProperties;
import org.openepics.discs.hourlog.util.AppProperties.HourLogProperty;
import org.openepics.discs.hourlog.util.Utility;

/**
 * Sync the approved experiments cache
 *
 * @author vuppala
 */
@Stateless
public class ExperimentServiceEJB implements Serializable {

    private static final String DATE_FORMAT = "yyyy-MM-dd"; // date format used in the results from external Experiment service

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    private static class SvcExperiment {

        @XmlElement(name = "number")
        private String number;
        @XmlElement(name = "title")
        private String title;
        @XmlElement(name = "description")
        private String description;
        @XmlElement(name = "approval_date")
        private String approvalDate;
        @XmlElement(name = "PAC")
        private String pacNumber;
        @XmlElement(name = "hours_requested")
        private String hoursRequested;
        @XmlElement(name = "hours_approved")
        private String hoursApproved;
        @XmlElement(name = "completion_date")
        private String completaionDate;
        @XmlElement(name = "spokesperson")
        private String spokesperson;
        @XmlElement(name = "a1900_contact")
        private String a1900Contact;
        @XmlElement(name = "experiment_completed")
        private String experimentCompleted;
        @XmlElement(name = "on_target_hours")
        private String onTargetHours;

        public SvcExperiment() {

        }

        public Experiment toExperiment() throws Exception {
            Experiment experiment = new Experiment();

            experiment.setA1900Contact(a1900Contact);
            experiment.setDescription(description);
            experiment.setExperimentCompleted(experimentCompleted);
            experiment.setSpokesperson(spokesperson);
            experiment.setTitle(title);

            experiment.setNumber(Utility.parseNumber(number));
            experiment.setHoursApproved(Utility.parseNumber(hoursApproved));
            experiment.setHoursRequested(Utility.parseNumber(hoursRequested));
            experiment.setOnTargetHours(Utility.parseNumber(onTargetHours));

            experiment.setApprovalDate(Utility.parseDate(DATE_FORMAT, approvalDate));
            experiment.setCompletionDate(Utility.parseDate(DATE_FORMAT, completaionDate));

            return experiment;
        }
    }

    @Inject
    private AppProperties appProperties;

    @EJB
    private ExternalServiceEJB extServiceEJB;

    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    private static final Logger logger = Logger.getLogger(ExperimentServiceEJB.class.getName());

    private ServiceStatusCode remSvcStatus = ServiceStatusCode.SERVICE_UP;
    private String remSvcStatusMsg = "Service is working";
    private ExternalService experimentService;

    public ExperimentServiceEJB() {
    }

    /**
     * perform the sync
     *
     */
    public void syncCache() {
        Date currentDate = Utility.currentDate();
        logger.fine("Sync of Experiments cache started");
        experimentService = extServiceEJB.findService(ExtServiceName.EXPERIMENTS);

        boolean success = findAllExperiments();

        experimentService.setStatus(remSvcStatus.toString());
        experimentService.setStatusMessage(remSvcStatusMsg);
        if (success) {
            experimentService.setLastServicedAt(currentDate);
        }
        experimentService.setStatusUpdatedAt(currentDate);
        extServiceEJB.saveService(experimentService);

        logger.log(Level.INFO, "synced Experiments cache");
    }

    /**
     * get the experiments from the remote service
     *
     */
    private boolean findAllExperiments() {
        List<SvcExperiment> extExperiments = Collections.emptyList();
        boolean success = false;
        Client client = null;
        try {
            client = ClientBuilder.newClient();

            String BASE_URI = appProperties.getProperty(HourLogProperty.EXPSVC_BASE_URI);
            String API_KEY = appProperties.getProperty(HourLogProperty.EXPSVC_API_KEY);
            String API_APP = appProperties.getProperty(HourLogProperty.EXPSVC_API_APP);
            String serviceEnabled = appProperties.getProperty(HourLogProperty.EXPSVC_ENABLED);

            if (serviceEnabled == null || !serviceEnabled.equals("true")) {
                remSvcStatus = ServiceStatusCode.SERVICE_DISABLED;
                remSvcStatusMsg = "Service is not enabled";
            } else {
                Form form = new Form();
                form.param("api_key", API_KEY);
                form.param("api_application", API_APP);
                logger.info("getting experiments");

                extExperiments = client.target(BASE_URI)
                        .request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE),
                                new GenericType<List<SvcExperiment>>() {
                                });
                if (extExperiments.isEmpty()) {
                    remSvcStatus = ServiceStatusCode.SERIVE_FLAKY;
                    remSvcStatusMsg = "Service did not return any experiments";
                } else {
                    remSvcStatus = ServiceStatusCode.SERVICE_UP;
                    remSvcStatusMsg = "Service is working";
                    for (SvcExperiment expr : extExperiments) {
                        Experiment experiment = expr.toExperiment();
                        em.merge(experiment);
                    }
                    success = true;
                    logger.log(Level.FINE, "updated cache");
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

        logger.log(Level.FINE, "Found {0} experiments from external Experiments Service", extExperiments.size());
        logger.log(Level.FINE, remSvcStatusMsg);

        return success;
    }
}
