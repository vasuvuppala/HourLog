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
@Table(name = "operations_role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OperationsRole.findAll", query = "SELECT o FROM OperationsRole o"),
    @NamedQuery(name = "OperationsRole.findByOpRoleId", query = "SELECT o FROM OperationsRole o WHERE o.opRoleId = :opRoleId"),
    @NamedQuery(name = "OperationsRole.findByName", query = "SELECT o FROM OperationsRole o WHERE o.name = :name"),
    @NamedQuery(name = "OperationsRole.findByDescription", query = "SELECT o FROM OperationsRole o WHERE o.description = :description"),
    @NamedQuery(name = "OperationsRole.findByActive", query = "SELECT o FROM OperationsRole o WHERE o.active = :active"),
    @NamedQuery(name = "OperationsRole.findByVersion", query = "SELECT o FROM OperationsRole o WHERE o.version = :version")})
public class OperationsRole implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "op_role_id")
    private Short opRoleId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "role")
    private List<ShiftStaffMember> shiftStaffMemberList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "role")
    private List<StaffRole> staffRoleList;

    public OperationsRole() {
    }

    public OperationsRole(Short opRoleId) {
        this.opRoleId = opRoleId;
    }

    public OperationsRole(Short opRoleId, String name, boolean active, int version) {
        this.opRoleId = opRoleId;
        this.name = name;
        this.active = active;
        this.version = version;
    }

    public Short getOpRoleId() {
        return opRoleId;
    }

    public void setOpRoleId(Short opRoleId) {
        this.opRoleId = opRoleId;
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

    @XmlTransient
    @JsonIgnore
    public List<StaffRole> getStaffRoleList() {
        return staffRoleList;
    }

    public void setStaffRoleList(List<StaffRole> staffRoleList) {
        this.staffRoleList = staffRoleList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (opRoleId != null ? opRoleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OperationsRole)) {
            return false;
        }
        OperationsRole other = (OperationsRole) object;
        if ((this.opRoleId == null && other.opRoleId != null) || (this.opRoleId != null && !this.opRoleId.equals(other.opRoleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.OperationsRole[ opRoleId=" + opRoleId + " ]";
    }
    
}
