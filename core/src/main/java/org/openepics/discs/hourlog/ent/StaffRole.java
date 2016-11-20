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
@Table(name = "staff_role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "StaffRole.findAll", query = "SELECT s FROM StaffRole s"),
    @NamedQuery(name = "StaffRole.findByStaffRoleId", query = "SELECT s FROM StaffRole s WHERE s.staffRoleId = :staffRoleId"),
    @NamedQuery(name = "StaffRole.findByVersion", query = "SELECT s FROM StaffRole s WHERE s.version = :version")})
public class StaffRole implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "staff_role_id")
    private Integer staffRoleId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @JoinColumn(name = "staff_member", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Sysuser staffMember;
    @JoinColumn(name = "role", referencedColumnName = "op_role_id")
    @ManyToOne(optional = false)
    private OperationsRole role;

    public StaffRole() {
    }

    public StaffRole(Integer staffRoleId) {
        this.staffRoleId = staffRoleId;
    }

    public StaffRole(Integer staffRoleId, int version) {
        this.staffRoleId = staffRoleId;
        this.version = version;
    }

    public Integer getStaffRoleId() {
        return staffRoleId;
    }

    public void setStaffRoleId(Integer staffRoleId) {
        this.staffRoleId = staffRoleId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Sysuser getStaffMember() {
        return staffMember;
    }

    public void setStaffMember(Sysuser staffMember) {
        this.staffMember = staffMember;
    }

    public OperationsRole getRole() {
        return role;
    }

    public void setRole(OperationsRole role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (staffRoleId != null ? staffRoleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StaffRole)) {
            return false;
        }
        StaffRole other = (StaffRole) object;
        if ((this.staffRoleId == null && other.staffRoleId != null) || (this.staffRoleId != null && !this.staffRoleId.equals(other.staffRoleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.StaffRole[ staffRoleId=" + staffRoleId + " ]";
    }
    
}
