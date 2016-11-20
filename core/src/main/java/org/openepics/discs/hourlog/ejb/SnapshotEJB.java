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

import java.util.ArrayList;
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
import org.openepics.discs.hourlog.ent.BeamEvent;
import org.openepics.discs.hourlog.ent.BeamSystem;
import org.openepics.discs.hourlog.ent.BreakdownCategory;
import org.openepics.discs.hourlog.ent.BreakdownEvent;
import org.openepics.discs.hourlog.ent.Event;
import org.openepics.discs.hourlog.ent.Experiment;
import org.openepics.discs.hourlog.ent.Summary;
import org.openepics.discs.hourlog.log.LogEntry;
import org.openepics.discs.hourlog.ent.Mode;
import org.openepics.discs.hourlog.ent.Source;
import org.openepics.discs.hourlog.ent.Vault;
import org.openepics.discs.hourlog.log.StatusSnapshot;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.ent.Shift;
import org.openepics.discs.hourlog.log.LogbookManager;
import org.openepics.discs.hourlog.log.StatusfulLogEntry;
import org.openepics.discs.hourlog.util.Utility;

/**
 * Manager for snapshots - the status of a facility at given instance
 *
 * @author vuppala
 */
@Stateless
public class SnapshotEJB {

    @EJB
    private HourLogEJB hourLogEJB;
//    @EJB
//    private LogEntryEJB logEntryEJB;
    @EJB
    private EventEJB eventEJB;
    @EJB
    private ShiftEJB shiftEJB;
    @Inject
    private UserSession userSession;

    @EJB(beanName = "OlogLogbookManager")
    private LogbookManager logbookEJB;

    private static final Logger logger = Logger.getLogger(SnapshotEJB.class.getName());
    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    /**
     * Snapshot of current facility just before a log entry occurred
     *
     * @param entry
     * @return snapshot
     */
    public StatusSnapshot snapshotJustBefore(LogEntry entry) {
        Facility facility = userSession.getFacility();
        return snapshotAtOrBefore(facility, entry.getOccurredAt(), true);
    }

    /**
     * Finds Snapshot at a given instance
     *
     * @param instance
     * @return snapshot
     */
    public StatusSnapshot snapshotAt(Date instance) {
        Facility facility = userSession.getFacility();
        return snapshotAtOrBefore(facility, instance, false);
    }

    /**
     * Finds the snapshot at given instance for a given facility
     *
     * @param facility
     * @param instance
     * @return snapshot
     */
    public StatusSnapshot snapshotAt(Facility facility, Date instance) {
        return snapshotAtOrBefore(facility, instance, false);
    }

    /**
     * Find the current facility summary status
     *
     * @param facility
     * @return summary status
     */
    public Summary currentSummary(Facility facility) {
        String queryText = "SELECT e.summary FROM SummaryEvent e WHERE e.event.facility = :facility AND e.event.obsoletedBy is NULL ORDER BY e.event.occurredAt DESC";
        TypedQuery<Summary> query = em.createQuery(queryText, Summary.class)
                .setParameter("facility", facility)
                .setFirstResult(0)
                .setMaxResults(1);
        List<Summary> fstats = query.getResultList();
        if (fstats == null || fstats.isEmpty()) {
            logger.log(Level.WARNING, "snapshotEJB: cannot determine current summary");
            return null;
        }
        return fstats.get(0);
    }

    /**
     * Finds snapshot at or before a given instance
     *
     * @param facility
     * @param instance
     * @param before if true before the instance, otherwise at the instance
     * @return snapshot
     */
    private StatusSnapshot snapshotAtOrBefore(Facility facility, Date instance, boolean before) {
        StatusSnapshot snapshot = new StatusSnapshot();

        snapshot.setTaken_at(instance);
        snapshot.setExperiment(experimentAt(facility, instance, before));
        snapshot.setSource(sourceAt(facility, instance, before));
        snapshot.setVault(vaultAt(facility, instance, before));
        snapshot.setMode(modeAt(facility, instance, before));
        snapshot.setSummary(summaryAt(facility, instance, before));
        snapshot.setBeams(beamsAt(facility, instance, before));
        snapshot.setBreakdowns(breakdownsAt(facility, instance, before));
        return snapshot;
    }

    /**
     * Finds the experiment running in a facility at or before a given instance
     *
     * @param facility
     * @param instance
     * @param before if true before the instance, otherwise at the instance
     * @return experiment
     */
    public Experiment experimentAt(Facility facility, Date instance, boolean before) {
        String queryText = "SELECT e.experiment FROM ExprEvent e WHERE e.event.facility = :facility AND e.event.obsoletedBy is NULL AND e.event.occurredAt "
                + (before ? "<" : "<=") + " :instance ORDER BY e.event.occurredAt DESC";
        TypedQuery<Experiment> query = em.createQuery(queryText, Experiment.class)
                .setParameter("facility", facility)
                .setParameter("instance", instance)
                .setFirstResult(0)
                .setMaxResults(1);
        List<Experiment> experiments = query.getResultList();
        if (experiments == null || experiments.isEmpty()) {
            logger.log(Level.WARNING, "snapshotEJB: cannot determine experiment at given instance");
            return null;
        }
        return experiments.get(0);
    }

    /**
     * Finds the summary of a facility at or before a given instance
     *
     * @param facility
     * @param instance
     * @param before if true before the instance, otherwise at the instance
     * @return experiment
     */
    public Summary summaryAt(Facility facility, Date instance, boolean before) {
        String queryText = "SELECT e.summary FROM SummaryEvent e WHERE e.event.facility = :facility AND e.event.obsoletedBy is NULL AND e.event.occurredAt "
                + (before ? "<" : "<=") + " :instance ORDER BY e.event.occurredAt DESC";
        TypedQuery<Summary> query = em.createQuery(queryText, Summary.class)
                .setParameter("facility", facility)
                .setParameter("instance", instance)
                .setFirstResult(0)
                .setMaxResults(1);
        List<Summary> fstats = query.getResultList();
        if (fstats == null || fstats.isEmpty()) {
            logger.log(Level.WARNING, "snapshotEJB: cannot determine experiment at given instance");
            return null;
        }
        return fstats.get(0);
    }

    /**
     * Finds the vault being used in a facility at or before a given instance
     *
     * @param facility
     * @param instance
     * @param before if true before the instance, otherwise at the instance
     * @return experiment
     */
    private Vault vaultAt(Facility facility, Date instance, boolean before) {
        String queryText = "SELECT e.vault FROM VaultEvent e WHERE e.event.facility = :facility AND e.event.obsoletedBy is NULL AND e.event.occurredAt "
                + (before ? "<" : "<=") + " :instance ORDER BY e.event.occurredAt DESC";
        TypedQuery<Vault> query = em.createQuery(queryText, Vault.class)
                .setParameter("facility", facility)
                .setParameter("instance", instance)
                .setFirstResult(0)
                .setMaxResults(1);
        List<Vault> vaults = query.getResultList();
        if (vaults == null || vaults.isEmpty()) {
            logger.log(Level.WARNING, "snapshotEJB: cannot determine vault at given instance");
            return null;
        }
        return vaults.get(0);
    }

    /**
     * Finds the source being used in a facility at or before a given instance
     *
     * @param facility
     * @param instance
     * @param before if true before the instance, otherwise at the instance
     * @return experiment
     */
    private Source sourceAt(Facility facility, Date instance, boolean before) {
        String queryText = "SELECT e.source FROM SourceEvent e WHERE e.event.facility = :facility AND e.event.obsoletedBy is NULL AND e.event.occurredAt "
                + (before ? "<" : "<=") + " :instance ORDER BY e.event.occurredAt DESC";

        TypedQuery<Source> query = em.createQuery(queryText, Source.class)
                .setParameter("facility", facility)
                .setParameter("instance", instance)
                .setFirstResult(0)
                .setMaxResults(1);
        List<Source> sources = query.getResultList();
        if (sources == null || sources.isEmpty()) {
            logger.log(Level.WARNING, "snapshotEJB: cannot determine source at given instance");
            return null;
        }
        return sources.get(0);
    }

    /**
     * Finds the mode of a facility at or before a given instance
     *
     * @param facility
     * @param instance
     * @param before if true before the instance, otherwise at the instance
     * @return experiment
     */
    private Mode modeAt(Facility facility, Date instance, boolean before) {
        String queryText = "SELECT e.mode FROM ModeEvent e WHERE e.event.facility = :facility AND e.event.obsoletedBy is NULL AND e.event.occurredAt "
                + (before ? "<" : "<=") + " :instance ORDER BY e.event.occurredAt DESC";

        TypedQuery<Mode> query = em.createQuery(queryText, Mode.class)
                .setParameter("facility", facility)
                .setParameter("instance", instance)
                .setFirstResult(0)
                .setMaxResults(1);
        List<Mode> modes = query.getResultList();
        if (modes == null || modes.isEmpty()) {
            logger.log(Level.WARNING, "snapshotEJB: cannot determine mode at given instance");
            return null;
        }
        return modes.get(0);
    }

    /**
     * Finds the beams in a facility at or before a given instance
     *
     * @param facility
     * @param instance
     * @param before if true before the instance, otherwise at the instance
     * @return experiment
     */
    private List<BeamEvent> beamsAt(Facility facility, Date instance, boolean before) {
        String queryText = "SELECT e FROM BeamEvent e WHERE e.event.facility = :facility AND e.beamSystem = :beamsys AND e.event.obsoletedBy is NULL AND e.event.occurredAt "
                + (before ? "<" : "<=") + " :instance ORDER BY e.event.occurredAt DESC";

        //TypedQuery<BeamSystem> bquery = em.createNamedQuery("BeamSystem.findAll", BeamSystem.class);
        //List<BeamSystem> beamSystems = bquery.getResultList();
        List<BeamSystem> beamSystems = hourLogEJB.findBeamSystems(facility);
        List<BeamEvent> beamEvents = new ArrayList<>();

        if (beamSystems == null || beamSystems.isEmpty()) {
            return null;
        }
        for (BeamSystem bsystem : beamSystems) {
            TypedQuery<BeamEvent> query = em.createQuery(queryText, BeamEvent.class)
                    .setParameter("facility", facility)
                    .setParameter("instance", instance)
                    .setParameter("beamsys", bsystem)
                    .setFirstResult(0)
                    .setMaxResults(1);
            List<BeamEvent> bevents = query.getResultList();
            if (bevents == null || bevents.isEmpty()) {
                logger.log(Level.WARNING, "snapshotEJB: cannot determine beam for facility {0} for beam system {1} at/before instance {2} (empty database?). Adding an empty event",
                        new Object[]{facility.getFacilityName(), bsystem.getName(), instance});
                // TODO: Can this be avoided?
                BeamEvent newEvent = new BeamEvent();
                newEvent.setBeamSystem(bsystem);
                beamEvents.add(newEvent);
            } else {
                beamEvents.add(bevents.get(0));
            }
        }
        return beamEvents;
    }
    
    /**
     * Finds the beam in a beam system at or before a given instance
     *
     * @param facility
     * @param bsystem
     * @param instance
     * @param before if true before the instance, otherwise at the instance
     * @return experiment
     */
    public BeamEvent beamAt(Facility facility, BeamSystem bsystem, Date instance, boolean before) {
        if (facility == null || bsystem == null || instance == null) {
            logger.log(Level.WARNING, "Invalid input: either facility, beamsystem is null");
            return null;
        }
        
        String queryText = "SELECT e FROM BeamEvent e WHERE e.event.facility = :facility AND e.beamSystem = :beamsys AND e.event.obsoletedBy is NULL AND e.event.occurredAt "
                + (before ? "<" : "<=") + " :instance ORDER BY e.event.occurredAt DESC";

        List<BeamEvent> beamEvents = new ArrayList<>();

        TypedQuery<BeamEvent> query = em.createQuery(queryText, BeamEvent.class)
                .setParameter("facility", facility)
                .setParameter("instance", instance)
                .setParameter("beamsys", bsystem)
                .setFirstResult(0)
                .setMaxResults(1);
        List<BeamEvent> bevents = query.getResultList();
        if (bevents == null || bevents.isEmpty()) {
            logger.log(Level.WARNING, "Cannot determine beam for facility {0} for beam system {1} at/before instance {2} (empty database?). Adding an empty event",
                    new Object[]{facility.getFacilityName(), bsystem.getName(), instance});          
            return null;
        }
        
        return bevents.get(0);
    }

    /**
     * Finds the breakdown categories in a facility at or before a given
     * instance
     *
     * @param facility
     * @param instance
     * @param before if true before the instance, otherwise at the instance
     * @return experiment
     */
    public List<BreakdownEvent> breakdownsAt(Facility facility, Date instance, boolean before) {
        String queryText = "SELECT e FROM BreakdownEvent e WHERE e.event.facility = :facility AND e.category = :category AND e.event.obsoletedBy is NULL AND e.event.occurredAt "
                + (before ? "<" : "<=") + " :instance ORDER BY e.event.occurredAt DESC";

        TypedQuery<BreakdownCategory> bquery = em.createNamedQuery("BreakdownCategory.findAll", BreakdownCategory.class);
        List<BreakdownCategory> brkCategories = bquery.getResultList();
        List<BreakdownEvent> breakdownEvents = new ArrayList<>();

        if (brkCategories == null || brkCategories.isEmpty()) {
            return null;
        }
        for (BreakdownCategory b : brkCategories) {
            TypedQuery<BreakdownEvent> query = em.createQuery(queryText, BreakdownEvent.class)
                    .setParameter("facility", facility)
                    .setParameter("instance", instance)
                    .setParameter("category", b)
                    .setFirstResult(0)
                    .setMaxResults(1);
            List<BreakdownEvent> bevents = query.getResultList();
            if (bevents == null || bevents.isEmpty()) {
                logger.log(Level.FINE, "No breakdown event for {0} at given instance. Assuming it to be not broken.", b.getName()); 
                // No event for this breakdown category in the database, so assume it to be not broken
                BreakdownEvent bevent = new BreakdownEvent();
                bevent.setCategory(b);
                bevent.setFailed(false);
                breakdownEvents.add(bevent);
            } else {
                breakdownEvents.add(bevents.get(0));
            }
        }
        return breakdownEvents;
    }

    /**
     * Get a page of log entries in a given period with facility status.
     *
     * @param logbooks
     * @param firstRow
     * @param pageSize
     * @param beginDate
     * @param endDate
     * @param phrase
     * @param statefulEntries
     */
    public void findLogEntries(List<Logbook> logbooks, int firstRow, int pageSize, Date beginDate, Date endDate, String phrase, List<StatusfulLogEntry> statefulEntries) {
        logger.log(Level.FINE, "SnapshotEJB.findLogEntries: First {0}. Size {1}", new Object[]{firstRow, pageSize});
        List<LogEntry> logEntries = logbookEJB.findEntries(logbooks, firstRow, pageSize, beginDate, endDate, phrase);

        if (logEntries == null || logEntries.isEmpty()) {
            logger.log(Level.FINE, "SnapshotEJB.findLogEntries: No log entries found.");
            return;
        }

        StatusfulLogEntry statEntry;
        for (LogEntry entry : logEntries) {
            statEntry = new StatusfulLogEntry(entry, null, null);
            statefulEntries.add(statEntry);
        }

        logger.log(Level.FINE, "SnapshotEJB.findLogEntries: number of log entries created: {0}", statefulEntries.size());
    }

    /**
     * Add shift information to a log entry
     *
     * @param entries
     */
    public void addShiftInfo(List<StatusfulLogEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            logger.log(Level.WARNING, "SnapshotEJB#addShiftInfo: Null or empty input");
            return;
        }
        StatusfulLogEntry prevEntry = null;
        Date maxDate = entries.get(0).getLogEntry().getOccurredAt();
        Date minDate = entries.get(entries.size() - 1).getLogEntry().getOccurredAt();

        List<Shift> shifts = shiftEJB.findShifts(minDate, maxDate);
        int shiftIndex = 0;
        for (StatusfulLogEntry entry : entries) {
            if (shiftIndex < shifts.size()) {
                if (entry.getLogEntry().getOccurredAt().equals(shifts.get(shiftIndex).getUpdatedAt())) {
                    entry.setNewShift(true);
                } else if (entry.getLogEntry().getOccurredAt().before(shifts.get(shiftIndex).getUpdatedAt())) {
                    if (prevEntry != null) {
                        prevEntry.setNewShift(true);
                    }
                }
            }

            while (shiftIndex < shifts.size() && entry.getLogEntry().getOccurredAt().before(shifts.get(shiftIndex).getUpdatedAt())) {
                shiftIndex++;
            }
            if (shiftIndex < shifts.size()) {
                entry.setShift(shifts.get(shiftIndex));
            } else {
                logger.log(Level.WARNING, "SnapshotEJB#addShiftInfo: Log entry does not have a matching shift {0}", entry.getLogEntry().getOccurredAt());
            }
            prevEntry = entry;
        }
        // the earliest entry on a page must show the shift information 
        entries.get(entries.size() - 1).setNewShift(true);
    }

    /**
     * Finds history entries of a log entry
     *
     * @param logEntryId
     * @param statefulEntries
     */
    public void getLogHistory(int logEntryId, List<StatusfulLogEntry> statefulEntries) {
        logger.log(Level.FINE, "SnapshotEJB.getHistoryWithStatus: Entry ID {0}", logEntryId);
        List<LogEntry> logEntries = logbookEJB.getEntryWithHistory(logEntryId);

        if (logEntries == null || logEntries.isEmpty()) {
            logger.log(Level.FINE, "SnapshotEJB.getHistoryWithStatus: No log entries found.");
            return;
        }
        logger.log(Level.FINE, "SnapshotEJB.getHistoryWithStatus: number of log history entries found: {0}", logEntries.size());

        for (LogEntry entry : logEntries) {
            statefulEntries.add(new StatusfulLogEntry(entry, null, null));
        }
        logger.log(Level.FINE, "SnapshotEJB.getLogHistory: number of log history entries : {0}", statefulEntries.size());
    }

    /**
     * Gets a page of log entries, with the corresponding facility status, for
     * the given period
     *
     * @param logbooks
     * @param firstRow
     * @param pageSize
     * @param beginDate
     * @param endDate
     * @param statefulEntries
     */
    public void findLogEntriesWithStatus(List<Logbook> logbooks, int firstRow, int pageSize, Date beginDate, Date endDate, List<StatusfulLogEntry> statefulEntries) {
        logger.log(Level.FINE, "SnapshotEJB.findLogEntriesWithStatus: First {0}. Size {1}", new Object[]{firstRow, pageSize});
        List<LogEntry> logEntries = logbookEJB.findEntries(logbooks, firstRow, pageSize, beginDate, endDate, null);
        // List<LogEntry> logEntries = logEntryEJB.findLogEntries(firstRow, pageSize, beginDate, endDate);

        if (logEntries == null || logEntries.isEmpty()) {
            logger.log(Level.FINE, "SnapshotEJB.findLogEntriesWithStatus: No log entries found.");
            return;
        }
        List<Event> events = eventEJB.findEvents(logEntries.get(logEntries.size() - 1).getOccurredAt(), logEntries.get(0).getOccurredAt());

        logger.log(Level.FINE, "SnapshotEJB.findLogEntriesWithStatus: number of log entries found: {0}", logEntries.size());
        addStatusToLogs(logEntries, events, statefulEntries);
        addShiftInfo(statefulEntries);
        logger.log(Level.FINE, "SnapshotEJB.findLogEntriesWithStatus: number of log entries found: {0}", statefulEntries.size());
    }

    /**
     * Fetches history entries of a log entry, with facility status
     *
     * @param logEntryId
     * @param statefulEntries
     */
    public void getHistoryWithStatus(int logEntryId, List<StatusfulLogEntry> statefulEntries) {
        logger.log(Level.FINE, "SnapshotEJB.getHistoryWithStatus: Entry ID {0}", logEntryId);
        List<LogEntry> logEntries = logbookEJB.getEntryWithHistory(logEntryId);

        if (logEntries == null || logEntries.isEmpty()) {
            logger.log(Level.WARNING, "SnapshotEJB.getHistoryWithStatus: No log entries found.");
            return;
        }
        // logger.log(Level.FINE, "SnapshotEJB.getHistoryWithStatus: number of log entries found: {0}", logEntries.size());
        List<Event> events = eventEJB.findEventsOfLog(logEntryId);

        // logger.log(Level.FINE, "SnapshotEJB.getHistoryWithStatus: number of events  found: {0}", events.size());
        addStatusToObsoletedLogs(logEntries, events, statefulEntries);
        // logger.log(Level.FINE, "SnapshotEJB.getHistoryWithStatus: number of log entries found: {0}", statefulEntries.size());
    }

    /**
     * Adds facility status information to log entries. 
     *
     * @param logEntries
     * @param events
     * @param statefulEntries. Note: The computed entries are added to this list instead of being returned as a new list. This is done for performance.
     */
    private void addStatusToLogs( List<LogEntry> logEntries,  List<Event> events,  List<StatusfulLogEntry> statefulEntries) {
        int currentLogIdx = logEntries.size() - 1; // start with the last entry
        LogEntry currentLogEntry = logEntries.get(currentLogIdx); // the last entry
        // get all events betweens the first log and the last log entries
        // List<Event> events = eventEJB.findEvents(currentLogEntry.getOccurredAt(), logEntries.get(0).getOccurredAt());
        List<Shift> shifts = shiftEJB.findShifts(currentLogEntry.getOccurredAt(), logEntries.get(0).getOccurredAt());
        StatusSnapshot baseSnapshot = snapshotAt(currentLogEntry.getOccurredAt());

        int currentEventIdx = events.size() - 1;
        int nextEventIdx = currentEventIdx;
        Event currentEvent;
        StatusSnapshot currentSnapshot = baseSnapshot;
        StatusfulLogEntry statefulEntry;
        Date occurrenceTime = null;  // occurrence time for log entry ot an event
        Event eventWithLog = null;   // 
        LogEntry prevLogEntry;        
        while (currentLogIdx >= 0) {
            if (nextEventIdx >= 0) { // if there are  some events left 
                currentLogEntry = logEntries.get(currentLogIdx);
                occurrenceTime = currentLogEntry.getOccurredAt();

                while (nextEventIdx >= 0 && events.get(nextEventIdx).getOccurredAt().compareTo(occurrenceTime) <= 0) { // find the closest event in past
                    currentEventIdx = nextEventIdx;
                    baseSnapshot.update(events.get(nextEventIdx));
                    nextEventIdx--;
                }
                // currentEventPtr = nextEventPtr + 1;
                currentEvent = events.get(currentEventIdx);
                currentSnapshot = new StatusSnapshot(baseSnapshot);
                logger.log(Level.FINE, "SnapshotEJB.findLogEntriesWithStatus: log id {0} and note id {1}", new Object[]{currentLogEntry.getLogEntryId(), currentEvent.getNote()});
                if (currentLogEntry.getLogEntryId().equals(currentEvent.getNote())) { // This log entry belongs to this event.
                    logger.log(Level.FINE, "SnapshotEJB.findLogEntriesWithStatus: Matching event and logentry!");
                    eventWithLog = currentEvent;
                }
                if (nextEventIdx >= 0) { // get occurrence time of next event
                    occurrenceTime = events.get(nextEventIdx).getOccurredAt();
                }
            }
            // if nextEventPtr < 0, no more events left, so add rest of the log entries
            prevLogEntry = currentLogEntry;
            while (currentLogIdx >= 0 && (nextEventIdx < 0 || occurrenceTime.compareTo(currentLogEntry.getOccurredAt()) > 0)) {
                statefulEntry = new StatusfulLogEntry(currentLogEntry, eventWithLog, currentSnapshot);
                if (!Utility.isSameDay(prevLogEntry.getOccurredAt(), currentLogEntry.getOccurredAt())) {  // is it a new day?              
                    statefulEntry.setNewDay(true);
                }

//                if (newShift(prevLogEntry.getOccurredAt(), currentLogEntry.getOccurredAt(), shifts)) {  // new shift?
//                    // logger.log(Level.FINE, "SnapshotEJB.findLogEntriesWithStatus: new shift!");
//                    statefulEntry.setNewShift(true);
//                }
                statefulEntries.add(0, statefulEntry);
                prevLogEntry = currentLogEntry;
                if (--currentLogIdx >= 0) {
                    currentLogEntry = logEntries.get(currentLogIdx);
                }
                eventWithLog = null; // none of the rest of the events corresponds to the log entry
            }
        }
    }

    /**
     * Adds facility status to edited log entries
     *
     * @param logEntries
     * @param events
     * @param statefulEntries
     */
    private void addStatusToObsoletedLogs(List<LogEntry> logEntries, List<Event> events, List<StatusfulLogEntry> statefulEntries) {
        int currentLogIdx = logEntries.size() - 1; // start with the last entry

        StatusSnapshot baseSnapshot = snapshotJustBefore(logEntries.get(0));
        // baseSnapshot.print("History of logs");
        int currentEventIdx = events.size() - 1;

        StatusSnapshot currentSnapshot = new StatusSnapshot(baseSnapshot);
        StatusfulLogEntry statefulEntry;
        StatusfulLogEntry prevStatefulEntry = null;

        int comp;
        while (currentLogIdx >= 0 && currentEventIdx >= 0) {
//            logger.log(Level.FINE, "SnapshotEJB.addStatusToObsoletedLogs: event date entry {0}. log entry entry date {1}", 
//                    new Object[] {String.format("%d",events.get(currentEventIdx).getLogEnteredAt().getTime()), String.format("%d",logEntries.get(currentLogIdx).getEnteredAt().getTime())});
//            logger.log(Level.FINE, "SnapshotEJB.addStatusToObsoletedLogs: log entry {0}", logEntries.get(currentLogIdx));
//            logger.log(Level.FINE, "SnapshotEJB.addStatusToObsoletedLogs: event {0}", events.get(currentEventIdx));
            comp = events.get(currentEventIdx).getLogEnteredAt().compareTo(logEntries.get(currentLogIdx).getEnteredAt());
            if (comp > 0) { // current event was entered after current log entry
                logger.log(Level.FINE, "SnapshotEJB.addStatusToObsoletedLogs: Current event {0} wsa entered after log entry {1}", new Object[] {currentEventIdx, currentLogIdx});
                statefulEntry = new StatusfulLogEntry(logEntries.get(currentLogIdx), null, currentSnapshot);
                statefulEntries.add(0, statefulEntry);
                --currentLogIdx;
            } else if (comp < 0) { // current event is before current log entry
                logger.log(Level.FINE, "SnapshotEJB.addStatusToObsoletedLogs: Current event {0} was entered before log entry {1}", new Object[] {currentEventIdx, currentLogIdx});               
                currentSnapshot = new StatusSnapshot(baseSnapshot);
                currentSnapshot.update(events.get(currentEventIdx));
                if (currentLogIdx + 1 >= logEntries.size()) { // the first event is before the first log entry. should not happen
                    logger.log(Level.WARNING, "SnapshotEJB.addStatusToObsoletedLogs: first event entered before the first log entry!");
                    statefulEntry = new StatusfulLogEntry(logEntries.get(currentLogIdx), null, currentSnapshot); // this is not right but ...
                } else {
                    statefulEntry = new StatusfulLogEntry(logEntries.get(currentLogIdx + 1), null, currentSnapshot);
                }
                statefulEntries.add(0, statefulEntry);
                currentEventIdx--;
            } else {
                logger.log(Level.FINE, "SnapshotEJB.addStatusToObsoletedLogs: Matching event and logentry!");              
                currentSnapshot = new StatusSnapshot(baseSnapshot);
                currentSnapshot.update(events.get(currentEventIdx));
                statefulEntry = new StatusfulLogEntry(logEntries.get(currentLogIdx), events.get(currentEventIdx), currentSnapshot);
                statefulEntries.add(0, statefulEntry);
                currentEventIdx--;
                currentLogIdx--;
            }
            checkLogTextChanges(prevStatefulEntry, statefulEntry);
            prevStatefulEntry = statefulEntry;
        }

        if (currentEventIdx < 0) {
            while (currentLogIdx >= 0) {
                statefulEntry = new StatusfulLogEntry(logEntries.get(currentLogIdx), null, currentSnapshot);
                statefulEntries.add(0, statefulEntry);
                --currentLogIdx;
                checkLogTextChanges(prevStatefulEntry, statefulEntry);
                prevStatefulEntry = statefulEntry;
            }
        }

        if (currentLogIdx < 0) {
            while (currentEventIdx >= 0) {
                baseSnapshot.update(events.get(currentEventIdx));
                currentSnapshot = new StatusSnapshot(baseSnapshot);
                statefulEntry = new StatusfulLogEntry(logEntries.get(0), null, currentSnapshot);
                statefulEntries.add(0, statefulEntry);
                currentEventIdx--;
            }
        }
    }

    /**
     * Marks current entry, if text in a log has changed from previous entry
     *
     * @param prev previous log entry
     * @param current current log entry
     */
    private void checkLogTextChanges(StatusfulLogEntry prev, StatusfulLogEntry current) {
        if (prev != null && current != null) {
            logger.log(Level.FINE, "checking for same log text");
            if (!Utility.equalStrings(prev.getLogEntry().getLogText(), current.getLogEntry().getLogText())) {  // was log text updated?              
                current.setLogTextChanged(true);
                logger.log(Level.FINE, "updated log text");
            }
        }
    }

    /**
     * Finds if any shifts were started between the two given dates.
     *
     * @param minDate
     * @param maxDate
     * @param shifts List of shifts in descending order
     * @return true if s shift was started between the two dates
     */
//    private boolean newShift(Date minDate, Date maxDate, List<Shift> shifts) {
//        // shifts are assumed to be descedning order
//        for (Shift shift : shifts) {
//            if (shift.getStartedAt().compareTo(minDate) < 0) {
//                return false;
//            }
//            if (shift.getStartedAt().compareTo(maxDate) <= 0) {
//                return true;
//            }
//        }
//        return false;
//    }
}
