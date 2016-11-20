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
package org.openepics.discs.hourlog.ologmig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.openepics.discs.hourlog.ent.LogEntry;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.log.LogbookManager;

/**
 * Olog Migration
 *
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class OlogMigrateView implements Serializable {

    @EJB(beanName = "LocalLogbookManager")
    private LogbookManager logbookEJB;

    @EJB
    private OlogImportEJB ologImportEJB;

    private static final Logger logger = Logger.getLogger(OlogMigrateView.class.getName());

    private static final int PAGE_SIZE = 100;

    public OlogMigrateView() {
    }

//    @PostConstruct
//    public void init() {
//        // logger.log(Level.FINE, "ExperimentManager.init: Init ExperimentManager"); 
//
//        logEntries = logbookEJB.findEntries(ccfLogbooks, 0, 2000, null, null, null);
//        logger.log(Level.INFO, "Found {0} log entries", logEntries.size());
//    }

    public void migrate() {
        List<Logbook> logbooks = logbookEJB.listLogbooks(); // all of them
        List<Logbook> ccfLogbooks = new ArrayList<>(); // only the CCF log book

        for (Logbook lb : logbooks) {
            if ("CCF Operations".equals(lb.getLogbookName())) {
                ccfLogbooks.add(lb);
            }
        }

        if (ccfLogbooks.size() != 1) {
            logger.log(Level.INFO, "CCF Operations log book missing!");
            return;
        }

        boolean done = false;
        List<LogEntry> logEntries; // log entries in Hour Log
        int pageNumber = 0;
        int recordNumber = 0;
        while (! done) {
            logEntries = logbookEJB.findEntries(ccfLogbooks, pageNumber * PAGE_SIZE, PAGE_SIZE, null, null, null);
            logger.log(Level.INFO, "Processing page#: {0}", pageNumber);
            for (LogEntry log : logEntries) {
                logger.log(Level.INFO, "Processing log entry#: {0}", recordNumber++);
                ologImportEJB.importEntry(log);
            }
            pageNumber += 1;
            done = logEntries.isEmpty();
        }
    }
}
