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
@Table(name = "shift_status")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ShiftStatus.findAll", query = "SELECT s FROM ShiftStatus s"),
    @NamedQuery(name = "ShiftStatus.findByStatusId", query = "SELECT s FROM ShiftStatus s WHERE s.statusId = :statusId"),
    @NamedQuery(name = "ShiftStatus.findByName", query = "SELECT s FROM ShiftStatus s WHERE s.name = :name"),
    @NamedQuery(name = "ShiftStatus.findByDescription", query = "SELECT s FROM ShiftStatus s WHERE s.description = :description"),
    @NamedQuery(name = "ShiftStatus.findByVersion", query = "SELECT s FROM ShiftStatus s WHERE s.version = :version")})
public class ShiftStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "status_id")
    private Short statusId;
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
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "status")
    private List<ShiftStaffMember> shiftStaffMemberList;

    public ShiftStatus() {
    }

    public ShiftStatus(Short statusId) {
        this.statusId = statusId;
    }

    public ShiftStatus(Short statusId, String name, String description, int version) {
        this.statusId = statusId;
        this.name = name;
        this.description = description;
        this.version = version;
    }

    public Short getStatusId() {
        return statusId;
    }

    public void setStatusId(Short statusId) {
        this.statusId = statusId;
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
    public List<ShiftStaffMember> getShiftStaffMemberList() {
        return shiftStaffMemberList;
    }

    public void setShiftStaffMemberList(List<ShiftStaffMember> shiftStaffMemberList) {
        this.shiftStaffMemberList = shiftStaffMemberList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (statusId != null ? statusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ShiftStatus)) {
            return false;
        }
        ShiftStatus other = (ShiftStatus) object;
        if ((this.statusId == null && other.statusId != null) || (this.statusId != null && !this.statusId.equals(other.statusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.ShiftStatus[ statusId=" + statusId + " ]";
    }
    
}
