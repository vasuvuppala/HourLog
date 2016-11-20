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
import javax.persistence.Lob;
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
@Table(name = "trouble_report")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TroubleReport.findAll", query = "SELECT t FROM TroubleReport t"),
    @NamedQuery(name = "TroubleReport.findById", query = "SELECT t FROM TroubleReport t WHERE t.id = :id"),
    @NamedQuery(name = "TroubleReport.findByReportDate", query = "SELECT t FROM TroubleReport t WHERE t.reportDate = :reportDate"),
    @NamedQuery(name = "TroubleReport.findBySystem", query = "SELECT t FROM TroubleReport t WHERE t.system = :system"),
    @NamedQuery(name = "TroubleReport.findByEmployeeUniqName", query = "SELECT t FROM TroubleReport t WHERE t.employeeUniqName = :employeeUniqName"),
    @NamedQuery(name = "TroubleReport.findByEmployeeId", query = "SELECT t FROM TroubleReport t WHERE t.employeeId = :employeeId"),
    @NamedQuery(name = "TroubleReport.findByEmployeeName", query = "SELECT t FROM TroubleReport t WHERE t.employeeName = :employeeName"),
    @NamedQuery(name = "TroubleReport.findByUpdatedAt", query = "SELECT t FROM TroubleReport t WHERE t.updatedAt = :updatedAt")})
public class TroubleReport implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "report_date")
    @Temporal(TemporalType.DATE)
    private Date reportDate;
    @Lob
    @Size(max = 65535)
    @Column(name = "problem")
    private String problem;
    @Size(max = 255)
    @Column(name = "system")
    private String system;
    @Size(max = 64)
    @Column(name = "employee_uniq_name")
    private String employeeUniqName;
    @Column(name = "employee_id")
    private Integer employeeId;
    @Size(max = 255)
    @Column(name = "employee_name")
    private String employeeName;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public TroubleReport() {
    }

    public TroubleReport(Integer id) {
        this.id = id;
    }

    public TroubleReport(Integer id, Date reportDate) {
        this.id = id;
        this.reportDate = reportDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getEmployeeUniqName() {
        return employeeUniqName;
    }

    public void setEmployeeUniqName(String employeeUniqName) {
        this.employeeUniqName = employeeUniqName;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TroubleReport)) {
            return false;
        }
        TroubleReport other = (TroubleReport) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.TroubleReport[ id=" + id + " ]";
    }
    
}
