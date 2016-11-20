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
package org.openepics.discs.hourlog.service.v0_1_0;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.openepics.discs.hourlog.log.LogEntry;

/**
 * Note i.e. log entry text (as seen by API clients)
 * 
 * @author vuppala
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class LogEntryRep {   

    private int logId;
    private Date occurred_at;
    private Date entered_at;
    private String logText;
    private UserRep author;   

    private LogEntryRep() {
    }

    /**
     * Notes  representation from logentry entity
     * 
     *
     * 
     * @param logEntry
     * @return 
     */
    public static LogEntryRep newInstance(LogEntry logEntry) {
        LogEntryRep resource = new LogEntryRep();
        resource.logId = logEntry.getLogEntryId();
        resource.entered_at = logEntry.getEnteredAt();
        resource.occurred_at = logEntry.getOccurredAt();
        resource.logText = logEntry.getLogText();
        resource.author = UserRep.newInstance(logEntry.getSysuser());       
        
        return resource;
    }     
}
