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

package org.openepics.discs.hourlog.log;

import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.ent.Sysuser;

/**
 * Interface to logbook system. Describes the interface used by Hour Log to interact with a logbook system.
 * 
 * @author vuppala
 */
@Local
public interface LogbookManager {
    /**
     * Add credential for authorization to logbook system
     * 
     * @param username
     * @param password
     */
    public void addCredential(String username, char[] password);
    
    /**
     * Ping service 
     * 
     * @return true if successful 
     */
    boolean pingService();
    
    /**
     * Finds all the logbooks in the logbook system.
     * 
     * @return List of logbooks
     */
    List<Logbook> listLogbooks();
    
    /**
     * Convert from native logbook to Hour Log logbook
     *
     * @param name  logbook name
     * @return
     */
    public Logbook toLogbook(String name);
    
    /**
     * Add an entry into a logbook
     * 
     * @param logbook - the logbook
     * @param author - the user
     * @param logText - text of the entry
     * @param occurredAt - the time when the event, being described by the log entry, occurred
     * @param artifacts - a list of files and Trouble Reports
     * @return - returns the newly created log entry
     */
    LogEntry addEntry(Logbook logbook, Sysuser author, String logText, Date occurredAt, List<Artifact> artifacts); 
    
    /**
     * Get the log entry with the given id. The Id is assumed to be unique across all logbooks, hence the logbook is not specified.
     * 
     * @param logEntryId - The id of the log entry
     * @return - The log entry 
     */
    LogEntry getEntry(int logEntryId);
    
    /**
     * Find the number of log entries satisfying the given constraints.
     * 
     * @param logbooks - must be in one of these logbooks
     * @param begin - must have occurred on or after this date
     * @param end - must have occurred on or before this time
     * @param phrase - must contain the phrase in the log text
     * @return - a count of matching log entries
     */
    int countEntries(List<Logbook> logbooks, Date begin, Date end, String phrase);
    
    /**
     * Get the timestamp of the log entry that was created/modified most recently. 
     * This is the timestamp given by the system which cannot be modified.
     * 
     * @param logbook
     * @return Timestamp of the most recently modified entry
     */
    Date latestLogDate(Logbook logbook);
    
    
    /**
     * Get the timestamp of the log entry that was created/modified most recently. This is the timestamp given by the system which cannot be modified.
     * 
     * @param logbooks
     * @return Timestamp of the most recently modified entry
     */
    Date latestLogDate(List<Logbook> logbooks);
    
    /**
     * Get the timestamp of the oldest log entry in the given logbook. 
     * This is generally the time the log event occurred i.e. the timestamp entered by the user. 
     * 
     * This is the oldest timestamp, of the various timestamps that a log entry can have, in the logbook. 
     * 
     * @param logbook
     * @return Timestamp of the most recently modified entry
     */
    Date earliestLogDate(Logbook logbook);
    
    /**
     * Get the timestamp of the oldest log entry in the given logbooks. 
     * This is generally the time the log event occurred i.e. the timestamp entered by the user. 
     * 
     * This is the oldest timestamp, of the various timestamps that a log entry can have, in the logbooks. 
     * 
     * @param logbooks
     * @return Timestamp of the earliest entry
     */
    Date earliestLogDate(List<Logbook> logbooks);
    
    /**
     * Get the history (edits) of a log entry.
     * 
     * @param logEntryId - log entry
     * @return - a list of obsoleted log entries
     */
    List<LogEntry> getEntryWithHistory(int logEntryId);
    
    /**
     * Update a log entry.
     * 
     * @param logentry - log entry
     * @param author - user
     * @param logText - new text of the entry
     * @param occurredAt - the date of occurrence of the log event
     * @param artifacts - attachments or Trouble Report numbers
     * @return  - the edited log entry
     */
    LogEntry editEntry(LogEntry logentry, Sysuser author, String logText, Date occurredAt, List<Artifact> artifacts);
    
    /**
     * Find log entries  matching the given criteria
     * 
     * @param logbooks - must be in these logbooks
     * @param start - starting from this entry index
     * @param size - return this or fewer number of entries
     * @param beginDate - must have occurred after this date
     * @param endDate - and before this date
     * @param phrase - containing this phrase in the log text. If null, ignore this option.
     * 
     * @return - a list of log entries that match the given criteria.
     */
    List<LogEntry> findEntries(List<Logbook> logbooks, int start, int size, Date beginDate, Date endDate, String phrase);
}
