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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "control_signal")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ControlSignal.findAll", query = "SELECT c FROM ControlSignal c"),
    @NamedQuery(name = "ControlSignal.findBySignalId", query = "SELECT c FROM ControlSignal c WHERE c.signalId = :signalId"),
    @NamedQuery(name = "ControlSignal.findByName", query = "SELECT c FROM ControlSignal c WHERE c.name = :name"),
    @NamedQuery(name = "ControlSignal.findByDescription", query = "SELECT c FROM ControlSignal c WHERE c.description = :description")})
public class ControlSignal implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "signal_id")
    private Integer signalId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "facility", referencedColumnName = "facility_id")
    @ManyToOne(optional = false)
    private Facility facility;

    public ControlSignal() {
    }

    public ControlSignal(Integer signalId) {
        this.signalId = signalId;
    }

    public ControlSignal(Integer signalId, String name, String description) {
        this.signalId = signalId;
        this.name = name;
        this.description = description;
    }

    public Integer getSignalId() {
        return signalId;
    }

    public void setSignalId(Integer signalId) {
        this.signalId = signalId;
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

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (signalId != null ? signalId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ControlSignal)) {
            return false;
        }
        ControlSignal other = (ControlSignal) object;
        if ((this.signalId == null && other.signalId != null) || (this.signalId != null && !this.signalId.equals(other.signalId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.ControlSignal[ signalId=" + signalId + " ]";
    }
    
}
