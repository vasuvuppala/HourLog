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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "beam")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Beam.findAll", query = "SELECT b FROM Beam b"),
    @NamedQuery(name = "Beam.findByBeamId", query = "SELECT b FROM Beam b WHERE b.beamId = :beamId"),
    @NamedQuery(name = "Beam.findByCharge", query = "SELECT b FROM Beam b WHERE b.charge = :charge"),
    @NamedQuery(name = "Beam.findByMassNumber", query = "SELECT b FROM Beam b WHERE b.massNumber = :massNumber"),
    @NamedQuery(name = "Beam.findByEnergy", query = "SELECT b FROM Beam b WHERE b.energy = :energy"),
    @NamedQuery(name = "Beam.findByVersion", query = "SELECT b FROM Beam b WHERE b.version = :version")})
public class Beam implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "beam_id")
    private Short beamId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "charge")
    private int charge;
    @Basic(optional = false)
    @NotNull
    @Column(name = "mass_number")
    private int massNumber;
    @Basic(optional = false)
    @NotNull
    @Column(name = "energy")
    private float energy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @OneToMany(mappedBy = "beam")
    private List<BeamEvent> beamEventList;
    @JoinColumn(name = "beam_system", referencedColumnName = "beam_system_id")
    @ManyToOne(optional = false)
    private BeamSystem beamSystem;
    @JoinColumn(name = "element", referencedColumnName = "element_id")
    @ManyToOne(optional = false)
    private Element element;

    public Beam() {
    }

    public Beam(Short beamId) {
        this.beamId = beamId;
    }

    public Beam(Short beamId, int charge, int massNumber, float energy, int version) {
        this.beamId = beamId;
        this.charge = charge;
        this.massNumber = massNumber;
        this.energy = energy;
        this.version = version;
    }

    public Short getBeamId() {
        return beamId;
    }

    public void setBeamId(Short beamId) {
        this.beamId = beamId;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getMassNumber() {
        return massNumber;
    }

    public void setMassNumber(int massNumber) {
        this.massNumber = massNumber;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
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

    public BeamSystem getBeamSystem() {
        return beamSystem;
    }

    public void setBeamSystem(BeamSystem beamSystem) {
        this.beamSystem = beamSystem;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (beamId != null ? beamId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Beam)) {
            return false;
        }
        Beam other = (Beam) object;
        if ((this.beamId == null && other.beamId != null) || (this.beamId != null && !this.beamId.equals(other.beamId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.Beam[ beamId=" + beamId + " ]";
    }
    
}
