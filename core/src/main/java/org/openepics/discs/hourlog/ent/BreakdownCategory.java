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
@Table(name = "breakdown_category")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BreakdownCategory.findAll", query = "SELECT b FROM BreakdownCategory b"),
    @NamedQuery(name = "BreakdownCategory.findByBrkcatId", query = "SELECT b FROM BreakdownCategory b WHERE b.brkcatId = :brkcatId"),
    @NamedQuery(name = "BreakdownCategory.findByName", query = "SELECT b FROM BreakdownCategory b WHERE b.name = :name"),
    @NamedQuery(name = "BreakdownCategory.findByDescription", query = "SELECT b FROM BreakdownCategory b WHERE b.description = :description"),
    @NamedQuery(name = "BreakdownCategory.findByParent", query = "SELECT b FROM BreakdownCategory b WHERE b.parent = :parent"),
    @NamedQuery(name = "BreakdownCategory.findByActive", query = "SELECT b FROM BreakdownCategory b WHERE b.active = :active"),
    @NamedQuery(name = "BreakdownCategory.findByVersion", query = "SELECT b FROM BreakdownCategory b WHERE b.version = :version")})
public class BreakdownCategory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "brkcat_id")
    private Short brkcatId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @Column(name = "parent")
    private Integer parent;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "category")
    private List<BreakdownEvent> breakdownEventList;

    public BreakdownCategory() {
    }

    public BreakdownCategory(Short brkcatId) {
        this.brkcatId = brkcatId;
    }

    public BreakdownCategory(Short brkcatId, String name, String description, boolean active, int version) {
        this.brkcatId = brkcatId;
        this.name = name;
        this.description = description;
        this.active = active;
        this.version = version;
    }

    public Short getBrkcatId() {
        return brkcatId;
    }

    public void setBrkcatId(Short brkcatId) {
        this.brkcatId = brkcatId;
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

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @XmlTransient
    @JsonIgnore
    public List<BreakdownEvent> getBreakdownEventList() {
        return breakdownEventList;
    }

    public void setBreakdownEventList(List<BreakdownEvent> breakdownEventList) {
        this.breakdownEventList = breakdownEventList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (brkcatId != null ? brkcatId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BreakdownCategory)) {
            return false;
        }
        BreakdownCategory other = (BreakdownCategory) object;
        if ((this.brkcatId == null && other.brkcatId != null) || (this.brkcatId != null && !this.brkcatId.equals(other.brkcatId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.BreakdownCategory[ brkcatId=" + brkcatId + " ]";
    }
    
}
