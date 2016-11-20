
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
package org.openepics.discs.hourlog.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotAuthorizedException;
import org.openepics.discs.hourlog.auth.AuthManager;
import org.openepics.discs.hourlog.log.Artifact;
import org.openepics.discs.hourlog.ent.BeamEvent;
import org.openepics.discs.hourlog.ent.BreakdownEvent;
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Event;
import org.openepics.discs.hourlog.ent.ExprEvent;
import org.openepics.discs.hourlog.ent.SummaryEvent;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.ent.ModeEvent;
import org.openepics.discs.hourlog.ent.SourceEvent;
import org.openepics.discs.hourlog.ent.VaultEvent;
import org.openepics.discs.hourlog.log.StatusSnapshot;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.ent.Experiment;
import org.openepics.discs.hourlog.log.LogEntry;
import org.openepics.discs.hourlog.ent.Summary;
import org.openepics.discs.hourlog.log.LogbookManager;
import org.openepics.discs.hourlog.log.StatusfulLogEntry;
import org.openepics.discs.hourlog.util.Notifier;
import org.openepics.discs.hourlog.util.Utility;

/**
 * Event manager
 *
 * @author vuppala
 */
@Stateless
public class EventEJB {

    @EJB
    private HourLogEJB hourLogEJB;

    @EJB
    private SnapshotEJB snapshotEJB;

    @EJB
    private ShiftEJB shiftEJB;

    @EJB(beanName = "LocalAuthManager")
    private AuthManager authManager;

    @EJB(beanName = "OlogLogbookManager")
    private LogbookManager logbookEJB;

    @Inject
    private UserSession userSession;
    @Inject
    private Notifier notifier;

    private static final Logger logger = Logger.getLogger(EventEJB.class.getName());
    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    /**
     * Find edit history of the given event.
     *
     * @param eventId
     * @return edits for the event
     */
    public List<Event> findEventHistory(int eventId) {
        List<Event> events;

        Event event = em.find(Event.class, eventId);

        TypedQuery<Event> query = em.createQuery("SELECT a FROM Event a WHERE a.obsoletedBy = :event ORDER BY a.occurredAt DESC", Event.class)
                .setParameter("event", event);

        events = query.getResultList();
        events.add(0, event); // add the given event at the beginning
        logger.log(Level.FINE, "Log history entries found: {0}", events.size());
        return events;
    }

    /**
     * Find events related to a log entry.
     *
     * @param logEntryId
     * @return events
     */
    public List<Event> findEventsOfLog(int logEntryId) {
        List<Event> events;

        TypedQuery<Event> query = em.createQuery("SELECT a FROM Event a WHERE a.note = :logid ORDER BY a.logEnteredAt DESC", Event.class)
                .setParameter("logid", logEntryId);
        events = query.getResultList();

        if (events.isEmpty()) {
            logger.log(Level.FINE, "EventEJB.findEventsOfLog: No events found for log id {0}", logEntryId);
            return events;
        }

        logger.log(Level.FINE, "EventEJB.findEventsOfLog: Events for a log found: {0}", events.size());

        return events;
    }

    /**
     * Finds the earliest occurrence date of an experiment
     *
     * @param facility
     * @param expr
     *
     * @return occurrence date
     */
    public Date findExperimentBeginDate(Facility facility, Experiment expr) {
        TypedQuery<Date> query = em.createQuery("SELECT a.event.occurredAt FROM ExprEvent a WHERE a.event.facility = :facility AND a.experiment = :experiment AND a.event.obsoletedBy IS NULL ORDER BY a.event.occurredAt ASC", Date.class)
                .setParameter("experiment", expr)
                .setParameter("facility", facility)
                .setFirstResult(0)
                .setMaxResults(1);
        List<Date> dates = query.getResultList();
        if (dates == null || dates.isEmpty()) {
            logger.log(Level.WARNING, "eventEJB.findExperimentBeginDate: Experiment {0} not in the database!", expr);
            return null;
        }
        return dates.get(0);
    }

    /**
     * Finds the latest occurrence date of an experiment
     *
     * @param facility
     * @param expr
     * @return
     */
    public Date findExperimentEndDate(Facility facility, Experiment expr) {
        TypedQuery<Date> query = em.createQuery("SELECT a.event.occurredAt FROM ExprEvent a WHERE a.event.facility = :facility AND a.experiment = :experiment AND a.event.obsoletedBy IS NULL ORDER BY a.event.occurredAt DESC", Date.class)
                .setParameter("experiment", expr)
                .setParameter("facility", facility)
                .setFirstResult(0)
                .setMaxResults(1);
        List<Date> dates = query.getResultList();
        if (dates == null || dates.isEmpty()) {
            logger.log(Level.WARNING, "eventEJB.findExperimentEndDate: Experiment {0} not in the database!", expr);
            return null;
        }
        Date expdate = dates.get(0);

        query = em.createQuery("SELECT a.event.occurredAt FROM ExprEvent a WHERE a.event.facility = :facility AND a.experiment != :experiment AND a.event.occurredAt > :expdate AND a.event.obsoletedBy IS NULL ORDER BY a.event.occurredAt ASC", Date.class)
                .setParameter("experiment", expr)
                .setParameter("facility", facility)
                .setParameter("expdate", expdate)
                .setFirstResult(0)
                .setMaxResults(1);
        dates = query.getResultList();
        if (dates == null || dates.isEmpty()) { // On going experiment
            logger.log(Level.WARNING, "eventEJB.findExperimentEndDate: Ongoing experiment!");
            return Utility.currentDate();
        } else {
            return dates.get(0);
        }
    }

    /**
     * Is the given experiment the current experiment in the given facility
     *
     * @param facility
     * @param expr
     * @return true if it is
     */
    public Boolean ongoingxperiment(Facility facility, Experiment expr) {
        TypedQuery<Experiment> query = em.createQuery("SELECT a.experiment FROM ExprEvent a WHERE a.event.facility = :facility AND a.event.obsoletedBy IS NULL ORDER BY a.event.occurredAt DESC", Experiment.class)
                .setParameter("facility", facility)
                .setFirstResult(0)
                .setMaxResults(1);
        List<Experiment> experiments = query.getResultList();
        if (experiments == null || experiments.isEmpty()) {
            logger.log(Level.WARNING, "eventEJB.ongoingxperiment: No experiments in the database!");
            return false;
        }

        Experiment currentExp = experiments.get(0);
        return expr.equals(currentExp);
    }

    /**
     * Fetch 'size' number of events between given dates starting from 'start'
     * number in the current facility.
     *
     * @param start
     * @param size
     * @param beginDate
     * @param endDate
     * @return events
     */
    public List<Event> findEvents(int start, int size, Date beginDate, Date endDate) {
        Facility facility = userSession.getFacility();
        return findEvents(facility, start, size, beginDate, endDate);
    }

    /**
     * Fetch 'size' number of events between given dates starting from 'start'
     * number in the given facility.
     *
     * @param facility
     * @param start
     * @param size
     * @param beginDate
     * @param endDate
     * @return events
     */
    public List<Event> findEvents(Facility facility, int start, int size, Date beginDate, Date endDate) {
        List<Event> events;
        // Facility facility = userSession.getFacility();

        String queryStr = "SELECT a FROM Event a WHERE a.obsoletedBy is NULL AND a.facility = :facility ";

        if (beginDate != null) {
            queryStr += " AND a.occurredAt >= :begin";
        }

        if (endDate != null) {
            queryStr = queryStr + " AND a.occurredAt <= :end";
        }

        queryStr += " ORDER BY a.occurredAt DESC";

        //TypedQuery<Event> query = em.createQuery("SELECT a FROM Event a WHERE a.obsoletedBy is NULL ORDER BY a.occurredAt DESC", Event.class)
        TypedQuery<Event> query = em.createQuery(queryStr, Event.class).setParameter("facility", facility).setFirstResult(start).setMaxResults(size);

        if (beginDate != null) {
            query = query.setParameter("begin", beginDate);
        }

        if (endDate != null) {
            query = query.setParameter("end", endDate);
        }

        events = query.getResultList();

        logger.log(Level.FINE, "Event Entries found: " + events.size());
        return events;
    }

    /**
     * find an event given its id
     *
     * @param id
     * @return event
     */
    public Event findEvent(int id) {
        return em.find(Event.class, id);
    }

    /**
     * The latest date of addition/modification of events.
     *
     * @param facility
     * @return latest date
     */
    public Date latestEventDate(Facility facility) {
        TypedQuery<Date> query = em.createQuery("SELECT e.eventEnteredAt FROM Event e WHERE e.facility = :facility ORDER BY e.eventEnteredAt DESC", Date.class)
                .setParameter("facility", facility)
                .setFirstResult(0)
                .setMaxResults(1);

        List<Date> events = query.getResultList();
        if (events.isEmpty()) {
            return null;
        } else {
            return events.get(0);
        }
    }

    /**
     * Find a valid (not obsoleted) event corresponding to a log entry
     *
     * @param entry
     * @return the event
     */
    public Event findEvent(LogEntry entry) {
        List<Event> events;

        TypedQuery<Event> query = em.createQuery("SELECT a FROM Event a WHERE a.note = :logid AND a.obsoletedBy is NULL", Event.class)
                .setParameter("logid", entry.getLogEntryId());
        events = query.getResultList();

        if (events.isEmpty()) {
            logger.log(Level.FINE, "EventEJB.findEventsOfLog: No events found for log id {0}", entry.getLogEntryId());
            return null;
        }

        logger.log(Level.FINE, "EventEJB.findEventsOfLog: Events for a log found: {0}", events.size());

        return events.get(0);
    }

    /**
     * Adds an event to database.
     *
     * ToDo: optimize this method. add all events to Event and then persist
     *
     * @param currentSnapshot
     * @param occurredAt
     * @param notes
     * @param artifacts
     * @param inputSnapshot
     * @return
     * @throws NotAuthorizedException
     */
    public Event addEvent(StatusSnapshot currentSnapshot, Date occurredAt, String notes, List<Artifact> artifacts, StatusSnapshot inputSnapshot) throws NotAuthorizedException {
        if (!authManager.canCreateLog()) {
            throw new SecurityException("Current user is not authorized to create log entries.");
        }

        Sysuser currentUser = userSession.getUser();
        Facility facility = userSession.getFacility();
        Logbook logbook = facility.getOpsLogbook();

        // LogEntry note = new LogEntry();
        if (occurredAt == null) {
            occurredAt = Utility.currentDate();
        }
        // note.setLogText(notes);
        // note.setLogbook(logbook);
        LogEntry logEntry = logbookEJB.addEntry(logbook, currentUser, notes, occurredAt, artifacts);

        //TODO: Add event only if status has been changed
        if (inputSnapshot.equals(currentSnapshot)) {
            logger.log(Level.FINE, "No change in status. No events will be created.");
            return null;
        }

        Summary beforeSummary = snapshotEJB.currentSummary(facility);
        Event event = attachEventToLog(logEntry, currentSnapshot, occurredAt, inputSnapshot);

        // Notify shift staff if status has changed to UOF
        notifyAboutUOF(beforeSummary);

        return event;
    }

    /**
     * Notify staff if facility status has changed to UOF (Unscheduled off)
     *
     * @param beforeSummary
     */
    private void notifyAboutUOF(Summary beforeSummary) {
        Facility facility = userSession.getFacility();
        Summary uofSummary = hourLogEJB.unscheduledOff();
        Summary afterSummary = snapshotEJB.currentSummary(facility); // TODO: check transaction isolation
        
        logger.log(Level.FINE, "Before summary {0}", beforeSummary == null? "null" : beforeSummary.getName());
        logger.log(Level.FINE, "After summary {0}", afterSummary == null? "null" : afterSummary.getName());
        
        if (!uofSummary.equals(beforeSummary) && uofSummary.equals(afterSummary)) { // if status has changed to UOF
//            shiftEJB.notifyShiftStaff(String.format("%s on %s", facility.getFacilityName(), uofSummary.getName()),
//                    String.format("Facilty status for %s has just changed to %s", facility.getFacilityName(), uofSummary.getName()));
            notifier.notifyFacilityMonitors(facility, String.format("%s on %s", facility.getFacilityName(), uofSummary.getName()),
                    String.format("Facilty status for %s was changed to %s at %tc", facility.getFacilityName(), uofSummary.getName(), Utility.currentDate()));

            logger.log(Level.FINE, "Facilty status for {0} has just changed to {1}", new Object[]{facility.getFacilityName(), afterSummary.getName()});
        }
    }

    /**
     * Adds a new status event.
     *
     * @param logEntry
     * @param currentSnapshot
     * @param occurredAt
     * @param inputSnapshot
     * @return the new event
     */
    private Event attachEventToLog(LogEntry logEntry, StatusSnapshot currentSnapshot, Date occurredAt, StatusSnapshot inputSnapshot) {
        if (!authManager.canUpdateStatus()) {
            throw new SecurityException("Current user is not authorized to update status for this facility.");
        }
        Sysuser currentUser = userSession.getUser();
        Facility facility = userSession.getFacility();

        Event event = new Event();
        event.setReportedBy(currentUser);
        event.setEventEnteredAt(Utility.currentDate()); 
        event.setLogEnteredAt(logEntry.getEnteredAt()); // Make this time same as in logbook system so that obsoleted events and logs can be matched
        event.setOccurredAt(occurredAt);
        event.setFacility(facility);
        event.setNote(logEntry.getLogEntryId());
        //event.setNote(note.getLogEntryId());

        printEvent(event);
        // em.persist(note);
        em.persist(event);
        
        //if (!currentSnapshot.getExperiment().equals(inputSnapshot.getExperiment())) {
        if (! Utility.areSame(currentSnapshot.getExperiment(), inputSnapshot.getExperiment())) {
            ExprEvent exprevent = new ExprEvent();
            exprevent.setExperiment(inputSnapshot.getExperiment());
            exprevent.setEvent(event);
            em.persist(exprevent);
            event.getExprEventList().add(exprevent); // ToDo: recheck cascaded persistence 
        }
        //if (!currentSnapshot.getSummary().equals(inputSnapshot.getSummary())) {
        if (! Utility.areSame(currentSnapshot.getSummary(), inputSnapshot.getSummary())) {
            SummaryEvent fsEvent = new SummaryEvent();
            fsEvent.setSummary(inputSnapshot.getSummary());
            fsEvent.setEvent(event);
            em.persist(fsEvent);
            event.getSummaryEventList().add(fsEvent); // ToDo: recheck cacaded persistence 
        }
        // if (!currentSnapshot.getVault().equals(inputSnapshot.getVault())) {
        if (! Utility.areSame(currentSnapshot.getVault(), inputSnapshot.getVault())) {
            VaultEvent vaultEvent = new VaultEvent();
            vaultEvent.setVault(inputSnapshot.getVault());
            vaultEvent.setEvent(event);
            event.getVaultEventList().add(vaultEvent);
            em.persist(vaultEvent);
        }
        //if (!currentSnapshot.getSource().equals(inputSnapshot.getSource())) {
        if (! Utility.areSame(currentSnapshot.getSource(), inputSnapshot.getSource())) {
            SourceEvent sourceEvent = new SourceEvent();
            sourceEvent.setSource(inputSnapshot.getSource());
            sourceEvent.setEvent(event);
            event.getSourceEventList().add(sourceEvent);
            em.persist(sourceEvent);
        }
        //if (!currentSnapshot.getMode().equals(inputSnapshot.getMode())) {
        if (! Utility.areSame(currentSnapshot.getMode(), inputSnapshot.getMode())) {
            ModeEvent modeEvent = new ModeEvent();
            modeEvent.setMode(inputSnapshot.getMode());
            modeEvent.setEvent(event);
            event.getModeEventList().add(modeEvent);
            em.persist(modeEvent);
        }
        logger.log(Level.FINE, "Checking breakdowns");
        for (int i = 0; i < currentSnapshot.getBreakdowns().size(); i++) {
            logger.log(Level.FINE, " breakdown: {0}", i);
            BreakdownEvent inpEvent = inputSnapshot.getBreakdowns().get(i);
            BreakdownEvent curEvent = currentSnapshot.getBreakdowns().get(i);
            if (curEvent.getFailed() != inpEvent.getFailed()) {
                logger.log(Level.FINE, " adding breakdown event {0}", inpEvent.getCategory().getName());
                inpEvent.setEvent(event); // Todo: needed?
                event.getBreakdownEventList().add(inpEvent);
                em.persist(inpEvent);
            }
        }
        for (int i = 0; i < currentSnapshot.getBeams().size(); i++) {
            Boolean same;
            BeamEvent inpEvent = inputSnapshot.getBeams().get(i);
            BeamEvent curEvent = currentSnapshot.getBeams().get(i);
            // ToDo; improve. It does not work in cases where beams 
            // are same and non-null but  elements or mass numbers are different. 
            // This case should not occurr but we should cover for it.
            same = Utility.areSame(inpEvent.getBeam(), curEvent.getBeam())
                    && Utility.areSame(inpEvent.getElement(), curEvent.getElement())
                    && Utility.areSame(inpEvent.getMassNumber(), curEvent.getMassNumber());
            if (!same) {
                // inputSnapshot.print("Input");
                // currentSnapshot.print("Current");
                inpEvent.setEvent(event); // Todo: needed?
                event.getBeamEventList().add(inpEvent);
                em.persist(inpEvent);
            }
        }

        return event;
    }

    /**
     * Edits an event: obsoletes the current event and makes a new one.
     *
     * TODO: Make a copy of current event, and modify the original.
     *
     * @param event
     * @param logEntry
     * @param currentSnapshot
     * @param occurredAt
     * @param inputSnapshot
     * @return 
     */
    public Event editEvent(Event event, LogEntry logEntry, StatusSnapshot currentSnapshot, Date occurredAt, StatusSnapshot inputSnapshot) {

        Event newEvent = attachEventToLog(logEntry, currentSnapshot, occurredAt, inputSnapshot);
        Facility facility = userSession.getFacility();
        Summary beforeSummary = snapshotEJB.currentSummary(facility);

        // TODO: Optimistic concurrency control
//        Event oldEvent = em.find(Event.class, event.getEventId());
        // Event oldEvent = em.find(Event.class, event.getEventId(),LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        // em.flush();
        // Date originalDate = oldEvent.getOccurredAt();
        // oldEvent.setOccurredAt(Utility.currentDate());
//        newEvent = em.merge(newEvent);  // Todo: find a better way 
        // newEvent.setOccurredAt(originalDate);
        // TODO: Remove EventList from event entity?
//        newEvent.setEventList(oldEvent.getEventList()); // move all children to the new parent
//        oldEvent.setEventList(null);  // Todo: neccessary? 
        /*
         if ( newEvent.getEventList() == null ) {
         newEvent.setEventList(new ArrayList<Event>());
         }*/
//        newEvent.getEventList().add(oldEvent);
//        for (Event e : newEvent.getEventList()) {
//            e.setObsoletedBy(newEvent);
//        }
        // Notify shift staff if status has changed to UOF
        notifyAboutUOF(beforeSummary);
        return newEvent;
    }

    /**
     * Edits a log entry that has facility status event.
     *
     * It is assumed that the input has changed (occurrence date, log text,
     * events, or artifacts)
     *
     * @param entry
     * @param occurredAt
     * @param inputNote
     * @param inputArtifacts
     * @param inputSnapshot
     */
    public void editStatusfulEntry(StatusfulLogEntry entry, Date occurredAt, String inputNote, List<Artifact> inputArtifacts, StatusSnapshot inputSnapshot) {
        if (!authManager.canEditLog(entry.getLogEntry())) {
            throw new SecurityException("Current user is not authorized to edit this log entry.");
        }
        StatusSnapshot currentSnapshot = entry.getSnapshot();
        Sysuser currentUser = userSession.getUser();
        LogEntry logEntry = entry.getLogEntry();

        if (occurredAt == null) {
            occurredAt = Utility.currentDate();
        }
//        if (!logEntry.getOccurredAt().equals(occurredAt) || !inputNote.equals(logEntry.getLogText())) {
//            logEntry = logbookEJB.editEntry(logEntry, currentUser, inputNote, occurredAt, inputArtifacts);
//        }
        // Date oldOccDate = new Date(logEntry.getOccurredAt().getTime()); // old occurrence date
        // Boolean sameOccurrence = logEntry.getOccurredAt().equals(occurredAt); // occurrence data was not modified
        LogEntry editedLogEntry = logbookEJB.editEntry(logEntry, currentUser, inputNote, occurredAt, inputArtifacts); // It is assumed that something has changed so must make a log entry
        entry.setLogEntry(editedLogEntry);

        if ( logEntry.getOccurredAt().equals(occurredAt) && currentSnapshot.equals(inputSnapshot)) { // if neither backdated nor change in status,m then we are done
            logger.log(Level.FINE, "EvenEJB.editEntry: No change in status or occurrence date so leaving events as-is.");
            return;
        }

        StatusSnapshot prevSnapshot = snapshotEJB.snapshotJustBefore(logEntry);
        Event newEvent;
        if (entry.getEvent() == null) {
            logger.log(Level.FINE, "EvenEJB.editEntry: Event is null so adding a new event and log entry.");
            newEvent = attachEventToLog(editedLogEntry, prevSnapshot, occurredAt, inputSnapshot);
        } else {
            logger.log(Level.FINE, "EvenEJB.editEntry: Editing event and log entry.");
            newEvent = editEvent(entry.getEvent(), editedLogEntry, prevSnapshot, occurredAt, inputSnapshot);
        }
        obsoleteEvents(editedLogEntry, newEvent);
    }

    /**
     * Finds occurrence date of the earliest event
     *
     * @return date of the earliest event
     */
    public Date firstEventDate() {
        Facility facility = userSession.getFacility();
        return firstEventDate(facility);
    }

    /**
     * Finds occurrence date of the earliest event
     *
     * @param facility
     * @return date of the earliest event
     */
    public Date firstEventDate(Facility facility) {       
        TypedQuery<Date> query = em.createQuery("SELECT MIN(f.occurredAt) FROM Event f WHERE f.facility = :facility AND f.obsoletedBy IS NULL", Date.class)
                .setParameter("facility", facility);
        Date minDate = query.getSingleResult();
        return minDate;
    }
    
    /**
     * Finds occurrence date of the latest event
     *
     * @return date of the latest event
     */
    public Date lastEventDate() {
        Facility facility = userSession.getFacility();
        TypedQuery<Date> query = em.createQuery("SELECT MAX(f.occurredAt) FROM Event f WHERE f.facility = :facility AND f.obsoletedBy IS NULL", Date.class)
                .setParameter("facility", facility);
        Date maxDate = query.getSingleResult();
        return maxDate;
    }

    /**
     * find events, in the current facility, between a given period
     *
     * @param minDate start date
     * @param maxDate end date
     * @return list of events that cover the period
     */
    public List<Event> findEvents(Date minDate, Date maxDate) {
        List<Event> events;
        Facility facility = userSession.getFacility();

        TypedQuery<Event> query = em.createQuery("SELECT a FROM Event a WHERE a.facility = :facility AND a.obsoletedBy is NULL AND a.occurredAt >= :mindate AND a.occurredAt <= :maxdate ORDER BY a.occurredAt DESC", Event.class)
                .setParameter("facility", facility)
                .setParameter("mindate", minDate)
                .setParameter("maxdate", maxDate);
        events = query.getResultList();

        logger.log(Level.FINE, "EvenEJB.findEvents: Number of events found: {0}", events.size());

        return events;
    }

    /**
     * Print an event.
     *
     * TODO: Convert to toString()
     *
     * @param event
     */
    private void printEvent(Event event) {
        if (event == null) {
            logger.log(Level.FINE, "Event is null");
            return;
        }
        logger.log(Level.FINE, "Event: ");
        logger.log(Level.FINE, "  Facility: {0}", event.getFacility() == null ? "null" : event.getFacility().getFacilityName());
        logger.log(Level.FINE, "  Log Entered at: {0}", event.getLogEnteredAt() == null ? "null" : event.getLogEnteredAt());
        logger.log(Level.FINE, "  Event Entered at: {0}", event.getEventEnteredAt() == null ? "null" : event.getEventEnteredAt());
        logger.log(Level.FINE, "  Occurred at: {0}", event.getOccurredAt() == null ? "null" : event.getOccurredAt());
        logger.log(Level.FINE, "  Note : {0}", event.getNote() == null ? "null" : event.getNote());
        logger.log(Level.FINE, "  Author: {0}", event.getReportedBy() == null ? "null" : event.getReportedBy().getLastName());
        logger.log(Level.FINE, "  Obsoleted by: {0}", event.getObsoletedBy() == null ? "null" : event.getObsoletedBy().getEventId());
    }

    /**
     * Find breakdown events between given dates
     *
     * @author vuppala
     * @param facility
     * @param start start date
     * @param end end date
     * @return list of events
     */
    public List<BreakdownEvent> findBreakdownEvents(Facility facility, Date start, Date end) {
        List<BreakdownEvent> bevents;

        TypedQuery<BreakdownEvent> query = em.createQuery("SELECT b FROM BreakdownEvent b WHERE b.event.facility = :facility AND b.event.obsoletedBy IS NULL AND b.event.occurredAt BETWEEN :start AND :end ORDER BY b.event.occurredAt ASC", BreakdownEvent.class)
                .setParameter("facility", facility)
                .setParameter("start", start)
                .setParameter("end", end);

        bevents = query.getResultList();

        return bevents;
    }

    /**
     * Find summary events between given dates
     *
     * @author vuppala
     * @param facility
     * @param start start date
     * @param end end date
     * @return list of events
     */
    public List<SummaryEvent> findSummaryEvents(Facility facility, Date start, Date end) {
        List<SummaryEvent> bevents;

        TypedQuery<SummaryEvent> query = em.createQuery("SELECT e FROM SummaryEvent e WHERE e.event.facility = :facility AND e.event.obsoletedBy IS NULL AND e.event.occurredAt BETWEEN :start AND :end ORDER BY e.event.occurredAt ASC", SummaryEvent.class)
                .setParameter("facility", facility)
                .setParameter("start", start)
                .setParameter("end", end);

        bevents = query.getResultList();

        return bevents;
    }

    /**
     * Find events, in a facility, between given dates (exclusive)
     *
     * @author vuppala
     * @param facility
     * @param start start date
     * @param end end date
     * @return list of events
     */
    public List<Event> findEvents(Facility facility, Date start, Date end) {
        List<Event> events;

        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e WHERE e.facility = :facility AND e.obsoletedBy IS NULL AND e.occurredAt BETWEEN :start AND :end ORDER BY e.occurredAt ASC", Event.class)
                .setParameter("facility", facility)
                .setParameter("start", start)
                .setParameter("end", end);

        events = query.getResultList();

        return events;
    }

    /**
     * Obsolete events that correspond to a log entry.
     *
     * @author vuppala
     * @param logEntry
     * @param newEvent
     *
     */
    public void obsoleteEvents(LogEntry logEntry, Event newEvent) {
        List<Event> events;

        if (logEntry == null || newEvent == null) {
            logger.log(Level.WARNING, "EventEJB#obsoleteEnvents: log entry or the new event is null!");
            return;
        }

        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e WHERE e.note = :note AND e != :event", Event.class)
                .setParameter("note", logEntry.getLogEntryId())
                .setParameter("event", newEvent);

        events = query.getResultList();
        logger.log(Level.FINE, "EventEJB#obsoleteEnvents: events found {0}", events.size());

        for (Event event : events) {
            // logger.log(Level.FINE, "EventEJB#obsoleteEnvents: obsoleting {0}", event.getEventId());
            event.setObsoletedBy(newEvent);
        }
    }
}
