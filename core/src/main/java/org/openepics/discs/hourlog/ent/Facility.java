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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "facility")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Facility.findAll", query = "SELECT f FROM Facility f"),
    @NamedQuery(name = "Facility.findByFacilityId", query = "SELECT f FROM Facility f WHERE f.facilityId = :facilityId"),
    @NamedQuery(name = "Facility.findByFacilityName", query = "SELECT f FROM Facility f WHERE f.facilityName = :facilityName"),
    @NamedQuery(name = "Facility.findByDescription", query = "SELECT f FROM Facility f WHERE f.description = :description"),
    @NamedQuery(name = "Facility.findByInOperation", query = "SELECT f FROM Facility f WHERE f.inOperation = :inOperation"),
    @NamedQuery(name = "Facility.findByVersion", query = "SELECT f FROM Facility f WHERE f.version = :version")})
public class Facility implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "facility_id")
    private Short facilityId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "facility_name")
    private String facilityName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "in_operation")
    private boolean inOperation;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @JoinColumn(name = "ops_logbook", referencedColumnName = "logbook_id")
    @ManyToOne
    private Logbook opsLogbook;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "facility")
    private List<Event> eventList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "facility")
    private List<ControlSignal> controlSignalList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "facility")
    private List<BeamSystem> beamSystemList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "facility")
    private List<Vault> vaultList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "facility")
    private List<Source> sourceList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "facility")
    private List<Shift> shiftList;

    public Facility() {
    }

    public Facility(Short facilityId) {
        this.facilityId = facilityId;
    }

    public Facility(Short facilityId, String facilityName, String description, boolean inOperation, int version) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.description = description;
        this.inOperation = inOperation;
        this.version = version;
    }

    public Short getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Short facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getInOperation() {
        return inOperation;
    }

    public void setInOperation(boolean inOperation) {
        this.inOperation = inOperation;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Logbook getOpsLogbook() {
        return opsLogbook;
    }

    public void setOpsLogbook(Logbook opsLogbook) {
        this.opsLogbook = opsLogbook;
    }

    @XmlTransient
    @JsonIgnore
    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    @XmlTransient
    @JsonIgnore
    public List<ControlSignal> getControlSignalList() {
        return controlSignalList;
    }

    public void setControlSignalList(List<ControlSignal> controlSignalList) {
        this.controlSignalList = controlSignalList;
    }

    @XmlTransient
    @JsonIgnore
    public List<BeamSystem> getBeamSystemList() {
        return beamSystemList;
    }

    public void setBeamSystemList(List<BeamSystem> beamSystemList) {
        this.beamSystemList = beamSystemList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Vault> getVaultList() {
        return vaultList;
    }

    public void setVaultList(List<Vault> vaultList) {
        this.vaultList = vaultList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Source> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<Source> sourceList) {
        this.sourceList = sourceList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Shift> getShiftList() {
        return shiftList;
    }

    public void setShiftList(List<Shift> shiftList) {
        this.shiftList = shiftList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (facilityId != null ? facilityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Facility)) {
            return false;
        }
        Facility other = (Facility) object;
        if ((this.facilityId == null && other.facilityId != null) || (this.facilityId != null && !this.facilityId.equals(other.facilityId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.Facility[ facilityId=" + facilityId + " ]";
    }
    
}
