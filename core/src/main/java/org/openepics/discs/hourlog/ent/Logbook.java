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
package org.openepics.discs.hourlog.ent;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Table(name = "logbook")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Logbook.findAll", query = "SELECT l FROM Logbook l"),
    @NamedQuery(name = "Logbook.findByLogbookId", query = "SELECT l FROM Logbook l WHERE l.logbookId = :logbookId"),
    @NamedQuery(name = "Logbook.findByLogbookName", query = "SELECT l FROM Logbook l WHERE l.logbookName = :logbookName"),
    @NamedQuery(name = "Logbook.findByDescription", query = "SELECT l FROM Logbook l WHERE l.description = :description"),
    @NamedQuery(name = "Logbook.findByVersion", query = "SELECT l FROM Logbook l WHERE l.version = :version")})
public class Logbook implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "logbook_id")
    private Short logbookId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "logbook_name")
    private String logbookName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @OneToMany(mappedBy = "opsLogbook")
    private List<Facility> facilityList;

    public Logbook() {
    }

    public Logbook(Short logbookId) {
        this.logbookId = logbookId;
    }

    public Logbook(Short logbookId, String logbookName, String description, int version) {
        this.logbookId = logbookId;
        this.logbookName = logbookName;
        this.description = description;
        this.version = version;
    }

    public Short getLogbookId() {
        return logbookId;
    }

    public void setLogbookId(Short logbookId) {
        this.logbookId = logbookId;
    }

    public String getLogbookName() {
        return logbookName;
    }

    public void setLogbookName(String logbookName) {
        this.logbookName = logbookName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @XmlTransient
    @JsonIgnore
    public List<Facility> getFacilityList() {
        return facilityList;
    }

    public void setFacilityList(List<Facility> facilityList) {
        this.facilityList = facilityList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (logbookId != null ? logbookId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Logbook)) {
            return false;
        }
        Logbook other = (Logbook) object;
        if ((this.logbookId == null && other.logbookId != null) || (this.logbookId != null && !this.logbookId.equals(other.logbookId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.Logbook[ logbookId=" + logbookId + " ]";
    }
    
}
