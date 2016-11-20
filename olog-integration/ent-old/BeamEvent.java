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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "beam_event")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BeamEvent.findAll", query = "SELECT b FROM BeamEvent b"),
    @NamedQuery(name = "BeamEvent.findById", query = "SELECT b FROM BeamEvent b WHERE b.id = :id"),
    @NamedQuery(name = "BeamEvent.findByMassNumber", query = "SELECT b FROM BeamEvent b WHERE b.massNumber = :massNumber"),
    @NamedQuery(name = "BeamEvent.findByCharge", query = "SELECT b FROM BeamEvent b WHERE b.charge = :charge"),
    @NamedQuery(name = "BeamEvent.findByEnergy", query = "SELECT b FROM BeamEvent b WHERE b.energy = :energy"),
    @NamedQuery(name = "BeamEvent.findByVersion", query = "SELECT b FROM BeamEvent b WHERE b.version = :version")})
public class BeamEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "mass_number")
    private Integer massNumber;
    @Column(name = "charge")
    private Integer charge;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "energy")
    private Float energy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @JoinColumn(name = "element", referencedColumnName = "element_id")
    @ManyToOne
    private Element element;
    @JoinColumn(name = "beam_system", referencedColumnName = "beam_system_id")
    @ManyToOne(optional = false)
    private BeamSystem beamSystem;
    @JoinColumn(name = "beam", referencedColumnName = "beam_id")
    @ManyToOne
    private Beam beam;
    @JoinColumn(name = "event", referencedColumnName = "event_id")
    @ManyToOne(optional = false)
    private Event event;

    public BeamEvent() {
    }

    public BeamEvent(Integer id) {
        this.id = id;
    }

    public BeamEvent(Integer id, int version) {
        this.id = id;
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMassNumber() {
        return massNumber;
    }

    public void setMassNumber(Integer massNumber) {
        this.massNumber = massNumber;
    }

    public Integer getCharge() {
        return charge;
    }

    public void setCharge(Integer charge) {
        this.charge = charge;
    }

    public Float getEnergy() {
        return energy;
    }

    public void setEnergy(Float energy) {
        this.energy = energy;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public BeamSystem getBeamSystem() {
        return beamSystem;
    }

    public void setBeamSystem(BeamSystem beamSystem) {
        this.beamSystem = beamSystem;
    }

    public Beam getBeam() {
        return beam;
    }

    public void setBeam(Beam beam) {
        this.beam = beam;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
        if (!(object instanceof BeamEvent)) {
            return false;
        }
        BeamEvent other = (BeamEvent) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.BeamEvent[ id=" + id + " ]";
    }
    
}
