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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "log_cache")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LogCache.findAll", query = "SELECT l FROM LogCache l"),
    @NamedQuery(name = "LogCache.findById", query = "SELECT l FROM LogCache l WHERE l.id = :id"),
    @NamedQuery(name = "LogCache.findByLogbook", query = "SELECT l FROM LogCache l WHERE l.logbook = :logbook"),
    @NamedQuery(name = "LogCache.findByAuthor", query = "SELECT l FROM LogCache l WHERE l.author = :author"),
    @NamedQuery(name = "LogCache.findByEnteredAt", query = "SELECT l FROM LogCache l WHERE l.enteredAt = :enteredAt"),
    @NamedQuery(name = "LogCache.findByOccurredAt", query = "SELECT l FROM LogCache l WHERE l.occurredAt = :occurredAt"),
    @NamedQuery(name = "LogCache.findByUpdatedAt", query = "SELECT l FROM LogCache l WHERE l.updatedAt = :updatedAt")})
public class LogCache implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "logbook")
    private String logbook;
    @Size(max = 128)
    @Column(name = "author")
    private String author;
    @Column(name = "entered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enteredAt;
    @Column(name = "occurred_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date occurredAt;
    @Lob
    @Size(max = 65535)
    @Column(name = "log_text")
    private String logText;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public LogCache() {
    }

    public LogCache(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogbook() {
        return logbook;
    }

    public void setLogbook(String logbook) {
        this.logbook = logbook;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(Date enteredAt) {
        this.enteredAt = enteredAt;
    }

    public Date getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Date occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LogCache)) {
            return false;
        }
        LogCache other = (LogCache) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.LogCache[ id=" + id + " ]";
    }
    
}
