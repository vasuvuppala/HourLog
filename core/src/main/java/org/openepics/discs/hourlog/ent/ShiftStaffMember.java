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
@Table(name = "shift_staff_member")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ShiftStaffMember.findAll", query = "SELECT s FROM ShiftStaffMember s"),
    @NamedQuery(name = "ShiftStaffMember.findBySsmId", query = "SELECT s FROM ShiftStaffMember s WHERE s.ssmId = :ssmId"),
    @NamedQuery(name = "ShiftStaffMember.findBySendSms", query = "SELECT s FROM ShiftStaffMember s WHERE s.sendSms = :sendSms")})
public class ShiftStaffMember implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ssm_id")
    private Integer ssmId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "send_sms")
    private boolean sendSms;
    @JoinColumn(name = "shift", referencedColumnName = "shift_id")
    @ManyToOne(optional = false)
    private Shift shift;
    @JoinColumn(name = "role", referencedColumnName = "op_role_id")
    @ManyToOne(optional = false)
    private OperationsRole role;
    @JoinColumn(name = "staff_member", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Sysuser staffMember;
    @JoinColumn(name = "status", referencedColumnName = "status_id")
    @ManyToOne(optional = false)
    private ShiftStatus status;

    public ShiftStaffMember() {
    }

    public ShiftStaffMember(Integer ssmId) {
        this.ssmId = ssmId;
    }

    public ShiftStaffMember(Integer ssmId, boolean sendSms) {
        this.ssmId = ssmId;
        this.sendSms = sendSms;
    }

    public Integer getSsmId() {
        return ssmId;
    }

    public void setSsmId(Integer ssmId) {
        this.ssmId = ssmId;
    }

    public boolean getSendSms() {
        return sendSms;
    }

    public void setSendSms(boolean sendSms) {
        this.sendSms = sendSms;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public OperationsRole getRole() {
        return role;
    }

    public void setRole(OperationsRole role) {
        this.role = role;
    }

    public Sysuser getStaffMember() {
        return staffMember;
    }

    public void setStaffMember(Sysuser staffMember) {
        this.staffMember = staffMember;
    }

    public ShiftStatus getStatus() {
        return status;
    }

    public void setStatus(ShiftStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ssmId != null ? ssmId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ShiftStaffMember)) {
            return false;
        }
        ShiftStaffMember other = (ShiftStaffMember) object;
        if ((this.ssmId == null && other.ssmId != null) || (this.ssmId != null && !this.ssmId.equals(other.ssmId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.ShiftStaffMember[ ssmId=" + ssmId + " ]";
    }
    
}
