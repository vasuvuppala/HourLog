/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2014, 2015.
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

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.ent.Sysuser;

/**
 * Log entry
 * 
 * @author vuppala
 */

public class LogEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer logEntryId;
    
    private String logText;
    
    private String author;
    
    private Date occurredAt;
    
    private Date enteredAt;
    
    private int version;
   
    private Sysuser sysuser;
    
    private List<LogEntry> logEntryList;
    
    private LogEntry obsoletedBy;
    
    private Logbook logbook;
    
    private List<Artifact> artifactList;

    public LogEntry() {
    }

    public LogEntry(Integer logEntryId) {
        this.logEntryId = logEntryId;
    }

    public LogEntry(Integer logEntryId, String logText, String author, Date occurredAt, Date enteredAt, int version) {
        this.logEntryId = logEntryId;
        this.logText = logText;
        this.author = author;
        this.occurredAt = occurredAt;
        this.enteredAt = enteredAt;
        this.version = version;
    }

    public Integer getLogEntryId() {
        return logEntryId;
    }

    public void setLogEntryId(Integer logEntryId) {
        this.logEntryId = logEntryId;
    }

    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Date occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Date getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(Date enteredAt) {
        this.enteredAt = enteredAt;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Sysuser getSysuser() {
        return sysuser;
    }

    public void setSysuser(Sysuser sysuser) {
        this.sysuser = sysuser;
    }

    @XmlTransient
    @JsonIgnore
    public List<LogEntry> getLogEntryList() {
        return logEntryList;
    }

    public void setLogEntryList(List<LogEntry> logEntryList) {
        this.logEntryList = logEntryList;
    }

    public LogEntry getObsoletedBy() {
        return obsoletedBy;
    }

    public void setObsoletedBy(LogEntry obsoletedBy) {
        this.obsoletedBy = obsoletedBy;
    }

    public Logbook getLogbook() {
        return logbook;
    }

    public void setLogbook(Logbook logbook) {
        this.logbook = logbook;
    }

    @XmlTransient
    @JsonIgnore
    public List<Artifact> getArtifactList() {
        return artifactList;
    }

    public void setArtifactList(List<Artifact> artifactList) {
        this.artifactList = artifactList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (logEntryId != null ? logEntryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LogEntry)) {
            return false;
        }
        LogEntry other = (LogEntry) object;
        if ((this.logEntryId == null && other.logEntryId != null) || (this.logEntryId != null && !this.logEntryId.equals(other.logEntryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.LogEntry[ logEntryId=" + logEntryId + " ]";
    }
    
}
