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
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "training_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TrainingRecord.findAll", query = "SELECT t FROM TrainingRecord t"),
    @NamedQuery(name = "TrainingRecord.findByEmployeeNumber", query = "SELECT t FROM TrainingRecord t WHERE t.employeeNumber = :employeeNumber"),
    @NamedQuery(name = "TrainingRecord.findByEmployeeId", query = "SELECT t FROM TrainingRecord t WHERE t.employeeId = :employeeId"),
    @NamedQuery(name = "TrainingRecord.findByLastName", query = "SELECT t FROM TrainingRecord t WHERE t.lastName = :lastName"),
    @NamedQuery(name = "TrainingRecord.findByFirstName", query = "SELECT t FROM TrainingRecord t WHERE t.firstName = :firstName"),
    @NamedQuery(name = "TrainingRecord.findByWorkEmail", query = "SELECT t FROM TrainingRecord t WHERE t.workEmail = :workEmail"),
    @NamedQuery(name = "TrainingRecord.findByOic", query = "SELECT t FROM TrainingRecord t WHERE t.oic = :oic"),
    @NamedQuery(name = "TrainingRecord.findByUpdatedAt", query = "SELECT t FROM TrainingRecord t WHERE t.updatedAt = :updatedAt")})
public class TrainingRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "employee_number")
    private Integer employeeNumber;
    @Size(max = 64)
    @Column(name = "employee_id")
    private String employeeId;
    @Size(max = 128)
    @Column(name = "last_name")
    private String lastName;
    @Size(max = 128)
    @Column(name = "first_name")
    private String firstName;
    @Size(max = 255)
    @Column(name = "work_email")
    private String workEmail;
    @Column(name = "oic")
    private Boolean oic;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public TrainingRecord() {
    }

    public TrainingRecord(Integer employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public Integer getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(Integer employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public Boolean getOic() {
        return oic;
    }

    public void setOic(Boolean oic) {
        this.oic = oic;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (employeeNumber != null ? employeeNumber.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TrainingRecord)) {
            return false;
        }
        TrainingRecord other = (TrainingRecord) object;
        if ((this.employeeNumber == null && other.employeeNumber != null) || (this.employeeNumber != null && !this.employeeNumber.equals(other.employeeNumber))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.TrainingRecord[ employeeNumber=" + employeeNumber + " ]";
    }
    
}
