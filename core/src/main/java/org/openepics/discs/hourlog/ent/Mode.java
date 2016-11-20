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
@Table(name = "mode")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Mode.findAll", query = "SELECT m FROM Mode m"),
    @NamedQuery(name = "Mode.findByModeId", query = "SELECT m FROM Mode m WHERE m.modeId = :modeId"),
    @NamedQuery(name = "Mode.findByName", query = "SELECT m FROM Mode m WHERE m.name = :name"),
    @NamedQuery(name = "Mode.findByDescription", query = "SELECT m FROM Mode m WHERE m.description = :description"),
    @NamedQuery(name = "Mode.findByActive", query = "SELECT m FROM Mode m WHERE m.active = :active")})
public class Mode implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "mode_id")
    private Short modeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "mode")
    private List<ModeEvent> modeEventList;

    public Mode() {
    }

    public Mode(Short modeId) {
        this.modeId = modeId;
    }

    public Mode(Short modeId, String name, String description, boolean active) {
        this.modeId = modeId;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public Short getModeId() {
        return modeId;
    }

    public void setModeId(Short modeId) {
        this.modeId = modeId;
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

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @XmlTransient
    @JsonIgnore
    public List<ModeEvent> getModeEventList() {
        return modeEventList;
    }

    public void setModeEventList(List<ModeEvent> modeEventList) {
        this.modeEventList = modeEventList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (modeId != null ? modeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Mode)) {
            return false;
        }
        Mode other = (Mode) object;
        if ((this.modeId == null && other.modeId != null) || (this.modeId != null && !this.modeId.equals(other.modeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.Mode[ modeId=" + modeId + " ]";
    }
    
}
