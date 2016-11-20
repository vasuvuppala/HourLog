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
@Table(name = "element")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Element.findAll", query = "SELECT e FROM Element e"),
    @NamedQuery(name = "Element.findByElementId", query = "SELECT e FROM Element e WHERE e.elementId = :elementId"),
    @NamedQuery(name = "Element.findBySymbol", query = "SELECT e FROM Element e WHERE e.symbol = :symbol"),
    @NamedQuery(name = "Element.findByName", query = "SELECT e FROM Element e WHERE e.name = :name"),
    @NamedQuery(name = "Element.findByVersion", query = "SELECT e FROM Element e WHERE e.version = :version")})
public class Element implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "element_id")
    private Short elementId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "symbol")
    private String symbol;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @OneToMany(mappedBy = "element")
    private List<BeamEvent> beamEventList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "element")
    private List<Beam> beamList;

    public Element() {
    }

    public Element(Short elementId) {
        this.elementId = elementId;
    }

    public Element(Short elementId, String symbol, String name, int version) {
        this.elementId = elementId;
        this.symbol = symbol;
        this.name = name;
        this.version = version;
    }

    public Short getElementId() {
        return elementId;
    }

    public void setElementId(Short elementId) {
        this.elementId = elementId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        hash += (elementId != null ? elementId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Element)) {
            return false;
        }
        Element other = (Element) object;
        if ((this.elementId == null && other.elementId != null) || (this.elementId != null && !this.elementId.equals(other.elementId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.Element[ elementId=" + elementId + " ]";
    }
    
}
