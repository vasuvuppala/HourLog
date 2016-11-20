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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "breakdown_status")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BreakdownStatus.findAll", query = "SELECT b FROM BreakdownStatus b"),
    @NamedQuery(name = "BreakdownStatus.findByBrkStatusId", query = "SELECT b FROM BreakdownStatus b WHERE b.brkStatusId = :brkStatusId"),
    @NamedQuery(name = "BreakdownStatus.findByName", query = "SELECT b FROM BreakdownStatus b WHERE b.name = :name"),
    @NamedQuery(name = "BreakdownStatus.findByDescription", query = "SELECT b FROM BreakdownStatus b WHERE b.description = :description"),
    @NamedQuery(name = "BreakdownStatus.findByVersion", query = "SELECT b FROM BreakdownStatus b WHERE b.version = :version")})
public class BreakdownStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "brk_status_id")
    private Short brkStatusId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;

    public BreakdownStatus() {
    }

    public BreakdownStatus(Short brkStatusId) {
        this.brkStatusId = brkStatusId;
    }

    public BreakdownStatus(Short brkStatusId, String name, String description, int version) {
        this.brkStatusId = brkStatusId;
        this.name = name;
        this.description = description;
        this.version = version;
    }

    public Short getBrkStatusId() {
        return brkStatusId;
    }

    public void setBrkStatusId(Short brkStatusId) {
        this.brkStatusId = brkStatusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (brkStatusId != null ? brkStatusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BreakdownStatus)) {
            return false;
        }
        BreakdownStatus other = (BreakdownStatus) object;
        if ((this.brkStatusId == null && other.brkStatusId != null) || (this.brkStatusId != null && !this.brkStatusId.equals(other.brkStatusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.BreakdownStatus[ brkStatusId=" + brkStatusId + " ]";
    }
    
}
