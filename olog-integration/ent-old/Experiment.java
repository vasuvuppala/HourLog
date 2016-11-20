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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Table(name = "experiment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Experiment.findAll", query = "SELECT e FROM Experiment e"),
    @NamedQuery(name = "Experiment.findByNumber", query = "SELECT e FROM Experiment e WHERE e.number = :number"),
    @NamedQuery(name = "Experiment.findBySpokesperson", query = "SELECT e FROM Experiment e WHERE e.spokesperson = :spokesperson"),
    @NamedQuery(name = "Experiment.findByTitle", query = "SELECT e FROM Experiment e WHERE e.title = :title"),
    @NamedQuery(name = "Experiment.findByHoursApproved", query = "SELECT e FROM Experiment e WHERE e.hoursApproved = :hoursApproved"),
    @NamedQuery(name = "Experiment.findByHoursRequested", query = "SELECT e FROM Experiment e WHERE e.hoursRequested = :hoursRequested"),
    @NamedQuery(name = "Experiment.findByExperimentCompleted", query = "SELECT e FROM Experiment e WHERE e.experimentCompleted = :experimentCompleted"),
    @NamedQuery(name = "Experiment.findByA1900Contact", query = "SELECT e FROM Experiment e WHERE e.a1900Contact = :a1900Contact"),
    @NamedQuery(name = "Experiment.findByApprovalDate", query = "SELECT e FROM Experiment e WHERE e.approvalDate = :approvalDate"),
    @NamedQuery(name = "Experiment.findByPac", query = "SELECT e FROM Experiment e WHERE e.pac = :pac"),
    @NamedQuery(name = "Experiment.findByCompletionDate", query = "SELECT e FROM Experiment e WHERE e.completionDate = :completionDate"),
    @NamedQuery(name = "Experiment.findByOnTargetHours", query = "SELECT e FROM Experiment e WHERE e.onTargetHours = :onTargetHours"),
    @NamedQuery(name = "Experiment.findByInCharge", query = "SELECT e FROM Experiment e WHERE e.inCharge = :inCharge"),
    @NamedQuery(name = "Experiment.findByUpdatedAt", query = "SELECT e FROM Experiment e WHERE e.updatedAt = :updatedAt")})
public class Experiment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "number")
    private Integer number;
    @Size(max = 128)
    @Column(name = "spokesperson")
    private String spokesperson;
    @Size(max = 128)
    @Column(name = "title")
    private String title;
    @Column(name = "hours_approved")
    private Integer hoursApproved;
    @Column(name = "hours_requested")
    private Integer hoursRequested;
    @Size(max = 3)
    @Column(name = "experiment_completed")
    private String experimentCompleted;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Size(max = 64)
    @Column(name = "a1900_contact")
    private String a1900Contact;
    @Column(name = "approval_date")
    @Temporal(TemporalType.DATE)
    private Date approvalDate;
    @Column(name = "PAC")
    private Short pac;
    @Column(name = "completion_date")
    @Temporal(TemporalType.DATE)
    private Date completionDate;
    @Column(name = "on_target_hours")
    private Integer onTargetHours;
    @Size(max = 128)
    @Column(name = "in_charge")
    private String inCharge;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "experiment")
    private List<ExprEvent> exprEventList;

    public Experiment() {
    }

    public Experiment(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getSpokesperson() {
        return spokesperson;
    }

    public void setSpokesperson(String spokesperson) {
        this.spokesperson = spokesperson;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getHoursApproved() {
        return hoursApproved;
    }

    public void setHoursApproved(Integer hoursApproved) {
        this.hoursApproved = hoursApproved;
    }

    public Integer getHoursRequested() {
        return hoursRequested;
    }

    public void setHoursRequested(Integer hoursRequested) {
        this.hoursRequested = hoursRequested;
    }

    public String getExperimentCompleted() {
        return experimentCompleted;
    }

    public void setExperimentCompleted(String experimentCompleted) {
        this.experimentCompleted = experimentCompleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getA1900Contact() {
        return a1900Contact;
    }

    public void setA1900Contact(String a1900Contact) {
        this.a1900Contact = a1900Contact;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Short getPac() {
        return pac;
    }

    public void setPac(Short pac) {
        this.pac = pac;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public Integer getOnTargetHours() {
        return onTargetHours;
    }

    public void setOnTargetHours(Integer onTargetHours) {
        this.onTargetHours = onTargetHours;
    }

    public String getInCharge() {
        return inCharge;
    }

    public void setInCharge(String inCharge) {
        this.inCharge = inCharge;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExprEvent> getExprEventList() {
        return exprEventList;
    }

    public void setExprEventList(List<ExprEvent> exprEventList) {
        this.exprEventList = exprEventList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (number != null ? number.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Experiment)) {
            return false;
        }
        Experiment other = (Experiment) object;
        if ((this.number == null && other.number != null) || (this.number != null && !this.number.equals(other.number))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.Experiment[ number=" + number + " ]";
    }
    
}
