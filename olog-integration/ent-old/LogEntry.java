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
package org.openepics.discs.hourlog.ent;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "log_entry")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LogEntry.findAll", query = "SELECT l FROM LogEntry l"),
    @NamedQuery(name = "LogEntry.findByLogEntryId", query = "SELECT l FROM LogEntry l WHERE l.logEntryId = :logEntryId"),
    @NamedQuery(name = "LogEntry.findByAuthor", query = "SELECT l FROM LogEntry l WHERE l.author = :author"),
    @NamedQuery(name = "LogEntry.findByOccurredAt", query = "SELECT l FROM LogEntry l WHERE l.occurredAt = :occurredAt"),
    @NamedQuery(name = "LogEntry.findByEnteredAt", query = "SELECT l FROM LogEntry l WHERE l.enteredAt = :enteredAt"),
    @NamedQuery(name = "LogEntry.findByVersion", query = "SELECT l FROM LogEntry l WHERE l.version = :version")})
public class LogEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "log_entry_id")
    private Integer logEntryId;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "log_text")
    private String logText;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "author")
    private String author;
    @Basic(optional = false)
    @NotNull
    @Column(name = "occurred_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date occurredAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "entered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enteredAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    @Version
    private int version;
    @JoinColumn(name = "sysuser", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Sysuser sysuser;
    @OneToMany(mappedBy = "obsoletedBy")
    private List<LogEntry> logEntryList;
    @JoinColumn(name = "obsoleted_by", referencedColumnName = "log_entry_id")
    @ManyToOne
    private LogEntry obsoletedBy;
    @JoinColumn(name = "logbook", referencedColumnName = "logbook_id")
    @ManyToOne(optional = false)
    private Logbook logbook;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "logEntry")
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
