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
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "shift")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Shift.findAll", query = "SELECT s FROM Shift s"),
    @NamedQuery(name = "Shift.findByShiftId", query = "SELECT s FROM Shift s WHERE s.shiftId = :shiftId"),
    @NamedQuery(name = "Shift.findByUpdatedAt", query = "SELECT s FROM Shift s WHERE s.updatedAt = :updatedAt"),
    @NamedQuery(name = "Shift.findByExpInCharge", query = "SELECT s FROM Shift s WHERE s.expInCharge = :expInCharge"),
    @NamedQuery(name = "Shift.findByNote", query = "SELECT s FROM Shift s WHERE s.note = :note")})
public class Shift implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "shift_id")
    private Integer shiftId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Size(max = 128)
    @Column(name = "exp_in_charge")
    private String expInCharge;
    @Basic(optional = false)
    @NotNull
    @Column(name = "note")
    private int note;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "shift")
    private List<ShiftStaffMember> shiftStaffMemberList;
    @JoinColumn(name = "op_in_charge", referencedColumnName = "user_id")
    @ManyToOne
    private Sysuser opInCharge;
    @JoinColumn(name = "facility", referencedColumnName = "facility_id")
    @ManyToOne(optional = false)
    private Facility facility;
    @JoinColumn(name = "updated_by", referencedColumnName = "user_id")
    @ManyToOne
    private Sysuser updatedBy;

    public Shift() {
    }

    public Shift(Integer shiftId) {
        this.shiftId = shiftId;
    }

    public Shift(Integer shiftId, Date updatedAt, int note) {
        this.shiftId = shiftId;
        this.updatedAt = updatedAt;
        this.note = note;
    }

    public Integer getShiftId() {
        return shiftId;
    }

    public void setShiftId(Integer shiftId) {
        this.shiftId = shiftId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getExpInCharge() {
        return expInCharge;
    }

    public void setExpInCharge(String expInCharge) {
        this.expInCharge = expInCharge;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    @XmlTransient
    @JsonIgnore
    public List<ShiftStaffMember> getShiftStaffMemberList() {
        return shiftStaffMemberList;
    }

    public void setShiftStaffMemberList(List<ShiftStaffMember> shiftStaffMemberList) {
        this.shiftStaffMemberList = shiftStaffMemberList;
    }

    public Sysuser getOpInCharge() {
        return opInCharge;
    }

    public void setOpInCharge(Sysuser opInCharge) {
        this.opInCharge = opInCharge;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public Sysuser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Sysuser updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (shiftId != null ? shiftId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Shift)) {
            return false;
        }
        Shift other = (Shift) object;
        if ((this.shiftId == null && other.shiftId != null) || (this.shiftId != null && !this.shiftId.equals(other.shiftId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.Shift[ shiftId=" + shiftId + " ]";
    }
    
}
