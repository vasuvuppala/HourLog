/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013, 2014.
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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import org.openepics.discs.hourlog.status.OlogStatusEJB;

/**
 * Timer service to synchronize all caches and check status of external services
 *
 * @author vuppala
 */
@Stateless
public class CacheSyncEJB {

    @EJB
    private ExperimentServiceEJB experimentServiceEJB;
    @EJB
    private TroubleReportServiceEJB troubleReportServiceEJB;
    @EJB
    private TrainingServiceEJB trainingServiceEJB;
    @EJB
    private OlogStatusEJB ologStatusEJB;

    private static final Logger logger = Logger.getLogger(CacheSyncEJB.class.getName());

    @Schedule(second = "0", minute = "0", hour = "*", dayOfMonth = "*", month = "*", year = "*", dayOfWeek = "*", persistent = false)
    public void syncExperiments() {
        logger.log(Level.INFO, "Syncing Experiments: {0}", new Date());
        experimentServiceEJB.syncCache();
    }

    @Schedule(second = "0", minute = "*/15", hour = "*", dayOfMonth = "*", month = "*", year = "*", dayOfWeek = "*", persistent = false)
//    @Schedule(second = "0", minute = "0",  hour = "*", dayOfMonth = "*", month = "*", year = "*",  dayOfWeek = "*", persistent = false)   
    public void syncTroubleReports() {
        logger.log(Level.INFO, "Syncing Trouble Reports: {0}", new Date());
        troubleReportServiceEJB.syncCache();
    }

//    @Schedule(second = "0", minute = "0",  hour = "*/5", dayOfMonth = "*", month = "*", year = "*",  dayOfWeek = "*", persistent = false)   
    @Schedule(second = "0", minute = "0", hour = "*", dayOfMonth = "*", month = "*", year = "*", dayOfWeek = "*", persistent = false)
    public void syncTrainingRecords() {
        logger.log(Level.INFO, "Syncing Training Records: {0}", new Date());
        trainingServiceEJB.syncCache();
    }

    @Schedule(second = "0", minute = "*/30", hour = "*", dayOfMonth = "*", month = "*", year = "*", dayOfWeek = "*", persistent = false)
    public void checkOlog() {
        logger.log(Level.INFO, "Checkigng Olog service: {0}", new Date());
        ologStatusEJB.checkStatus();
    }
}
