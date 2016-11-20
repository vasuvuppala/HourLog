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
package org.openepics.discs.hourlog.notes;

import org.openepics.discs.hourlog.log.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.ejb.EventEJB;
import org.openepics.discs.hourlog.ejb.SnapshotEJB;
import org.openepics.discs.hourlog.ent.Event;
import org.openepics.discs.hourlog.log.LogEntry;
import org.openepics.discs.hourlog.ent.Logbook;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * A lazy manager for notes ('note' is the text part of a log entry)
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class LazyNotesModel extends LazyDataModel<StatusfulLogEntry> {

    @EJB
    private EventEJB eventEJB;
    @EJB
    private SnapshotEJB snapshotEJB;

    @EJB(beanName = "OlogLogbookManager")
    private LogbookManager logbookEJB;
    @Inject
    private UserSession userSession;

    private static final Logger logger = Logger.getLogger(LazyLogModel.class.getName());

    private Date beginDate = null;
    private Date endDate = null;
    private List<StatusfulLogEntry> statefulEntries;
    private List<Logbook> logbooks;
    private String searchPhrase;

    public LazyNotesModel() {
    }

    @PostConstruct
    public void init() {
        logger.log(Level.FINE, "Lazy log model init");
        if (eventEJB == null) {
            logger.log(Level.FINE, "Lazy log mode: eventEJB is null");
        }
        logbooks = new ArrayList<>();
        logbooks.add(userSession.getFacility().getOpsLogbook());

        statefulEntries = new ArrayList<>();

        int totalEvents = logbookEJB.countEntries(logbooks, beginDate, endDate, null);
        this.setRowCount(totalEvents);
    }

    /**
     * Fetches log entry corresponding to a row (from log table)
     *
     * ToDo: improve the following code. no need to go to db everytime?
     *
     * @param rowKey
     * @return
     */
    @Override
    public StatusfulLogEntry getRowData(String rowKey) {
        logger.log(Level.WARNING, "LazyLogModel.getRowData key {0}", rowKey);
        LogEntry logEntry = logbookEJB.getEntry(Integer.parseInt(rowKey));
        StatusSnapshot snapshot = snapshotEJB.snapshotAt(logEntry.getOccurredAt());
        Event event = eventEJB.findEvent(logEntry);
        return new StatusfulLogEntry(logEntry, event, snapshot);
    }

    /**
     * Fetches the key for a log entry
     *
     * @param log
     * @return
     */
    @Override
    public Object getRowKey(StatusfulLogEntry log) {
        logger.log(Level.WARNING, "LazyLogModel.getRowkey");
        // return log.getEvent().getEventId();
        return log.getLogEntry().getLogEntryId();
    }

    /**
     * Fetches a page of notes (without any facility status information)
     *
     * @param first
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param filters
     * @return
     */
    @Override
    public List<StatusfulLogEntry> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

        try {
            logger.log(Level.FINE, "LazyLogMode.load: first {0} pageSize {1}", new Object[]{first, pageSize});
            statefulEntries.clear();
            snapshotEJB.findLogEntries(logbooks, first, pageSize, beginDate, endDate, searchPhrase, statefulEntries);
        } catch (Exception e) { //ToDo: Catch only RESTful errors
            logger.log(Level.WARNING, "Error in getting new log entries", e);
        }

        return statefulEntries;
    }

    /**
     * Sets filter period
     *
     * @param lbooks
     * @param begin
     * @param end
     * @param phrase
     */
    public void filterEvents(List<Logbook> lbooks, Date begin, Date end, String phrase) {
        logger.log(Level.FINE, "LazyLogModel.filterEvents: begin and end dates {0} {1}", new Object[]{begin, end});
        this.beginDate = begin;
        this.endDate = end;
        this.searchPhrase = phrase;
        this.logbooks.clear();
        this.logbooks.addAll(lbooks);
        int totalEvents = logbookEJB.countEntries(logbooks, beginDate, endDate, phrase);
        this.setRowCount(totalEvents);
    }
}
