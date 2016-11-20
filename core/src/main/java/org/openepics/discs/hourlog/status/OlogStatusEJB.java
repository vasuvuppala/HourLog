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
package org.openepics.discs.hourlog.status;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.openepics.discs.hourlog.ent.ExternalService;
import org.openepics.discs.hourlog.log.LogbookManager;
import org.openepics.discs.hourlog.util.Utility;

/**
 * Sync trouble reports cache with external source
 *
 * @author vuppala
 */
@Stateless
public class OlogStatusEJB implements Serializable {

    @EJB
    private ExternalServiceEJB extServiceEJB;
    @EJB(beanName = "OlogLogbookManager")
    private LogbookManager logbookEJB;

    private static final Logger logger = Logger.getLogger(OlogStatusEJB.class.getName());

    public OlogStatusEJB() {
    }

    /**
     * Update service status
     *
     * @param status
     * @param statusMessage
     * @param success was the last action successful?
     */
    public void updateStatus(ServiceStatusCode status, String statusMessage, boolean success) {
        Date currentDate = Utility.currentDate();
        ExternalService ologService = extServiceEJB.findService(ExtServiceName.LOGBOOK);

        ologService.setStatus(status.toString());
        ologService.setStatusMessage(statusMessage);
        if (success) {
            ologService.setLastServicedAt(currentDate);
        }
        ologService.setStatusUpdatedAt(currentDate);
        extServiceEJB.saveService(ologService);
    }

    /**
     * Check Olog Status
     *
     * @return
     */
    public ServiceStatusCode checkStatus() {
        boolean success = false;
        ServiceStatusCode remSvcStatus = ServiceStatusCode.SERVICE_DOWN;
        String remSvcStatusMsg = "Service is down";

        try {
            logger.log(Level.INFO, "Checking Olog status ...");
            if (logbookEJB.pingService()) { // TODO: Use Olog heartbeat API, once it is available
                remSvcStatus = ServiceStatusCode.SERVICE_UP;
                remSvcStatusMsg = "Service is working";
                success = true;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Olog is having problems", e);
        }

        updateStatus(remSvcStatus, remSvcStatusMsg, success);
        return remSvcStatus;
    }
}
