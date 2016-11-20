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
@Table(name = "beam_system")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BeamSystem.findAll", query = "SELECT b FROM BeamSystem b"),
    @NamedQuery(name = "BeamSystem.findByBeamSystemId", query = "SELECT b FROM BeamSystem b WHERE b.beamSystemId = :beamSystemId"),
    @NamedQuery(name = "BeamSystem.findByName", query = "SELECT b FROM BeamSystem b WHERE b.name = :name"),
    @NamedQuery(name = "BeamSystem.findByDescription", query = "SELECT b FROM BeamSystem b WHERE b.description = :description"),
    @NamedQuery(name = "BeamSystem.findByVersion", query = "SELECT b FROM BeamSystem b WHERE b.version = :version")})
public class BeamSystem implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "beam_system_id")
    private Short beamSystemId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "beamSystem")
    private List<BeamEvent> beamEventList;
    @JoinColumn(name = "facility", referencedColumnName = "facility_id")
    @ManyToOne(optional = false)
    private Facility facility;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "beamSystem")
    private List<Beam> beamList;

    public BeamSystem() {
    }

    public BeamSystem(Short beamSystemId) {
        this.beamSystemId = beamSystemId;
    }

    public BeamSystem(Short beamSystemId, String name, int version) {
        this.beamSystemId = beamSystemId;
        this.name = name;
        this.version = version;
    }

    public Short getBeamSystemId() {
        return beamSystemId;
    }

    public void setBeamSystemId(Short beamSystemId) {
        this.beamSystemId = beamSystemId;
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

    @XmlTransient
    @JsonIgnore
    public List<BeamEvent> getBeamEventList() {
        return beamEventList;
    }

    public void setBeamEventList(List<BeamEvent> beamEventList) {
        this.beamEventList = beamEventList;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    @XmlTransient
    @JsonIgnore
    public List<Beam> getBeamList() {
        return beamList;
    }

    public void setBeamList(List<Beam> beamList) {
        this.beamList = beamList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (beamSystemId != null ? beamSystemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BeamSystem)) {
            return false;
        }
        BeamSystem other = (BeamSystem) object;
        if ((this.beamSystemId == null && other.beamSystemId != null) || (this.beamSystemId != null && !this.beamSystemId.equals(other.beamSystemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.BeamSystem[ beamSystemId=" + beamSystemId + " ]";
    }
    
}
