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
@Table(name = "sysuser")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sysuser.findAll", query = "SELECT s FROM Sysuser s"),
    @NamedQuery(name = "Sysuser.findByUserId", query = "SELECT s FROM Sysuser s WHERE s.userId = :userId"),
    @NamedQuery(name = "Sysuser.findByFirstName", query = "SELECT s FROM Sysuser s WHERE s.firstName = :firstName"),
    @NamedQuery(name = "Sysuser.findByLastName", query = "SELECT s FROM Sysuser s WHERE s.lastName = :lastName"),
    @NamedQuery(name = "Sysuser.findByLoginId", query = "SELECT s FROM Sysuser s WHERE s.loginId = :loginId"),
    @NamedQuery(name = "Sysuser.findByNickName", query = "SELECT s FROM Sysuser s WHERE s.nickName = :nickName"),
    @NamedQuery(name = "Sysuser.findByDepartment", query = "SELECT s FROM Sysuser s WHERE s.department = :department"),
    @NamedQuery(name = "Sysuser.findByEmail", query = "SELECT s FROM Sysuser s WHERE s.email = :email"),
    @NamedQuery(name = "Sysuser.findByJob", query = "SELECT s FROM Sysuser s WHERE s.job = :job"),
    @NamedQuery(name = "Sysuser.findByCurrentEmployee", query = "SELECT s FROM Sysuser s WHERE s.currentEmployee = :currentEmployee"),
    @NamedQuery(name = "Sysuser.findByOperationsStaff", query = "SELECT s FROM Sysuser s WHERE s.operationsStaff = :operationsStaff"),
    @NamedQuery(name = "Sysuser.findBySmsAddress", query = "SELECT s FROM Sysuser s WHERE s.smsAddress = :smsAddress"),
    @NamedQuery(name = "Sysuser.findByVersion", query = "SELECT s FROM Sysuser s WHERE s.version = :version")})
public class Sysuser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private Integer userId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "first_name")
    private String firstName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "last_name")
    private String lastName;
    @Size(max = 32)
    @Column(name = "login_id")
    private String loginId;
    @Size(max = 16)
    @Column(name = "nick_name")
    private String nickName;
    @Size(max = 64)
    @Column(name = "department")
    private String department;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 128)
    @Column(name = "email")
    private String email;
    @Column(name = "job")
    private Integer job;
    @Basic(optional = false)
    @NotNull
    @Column(name = "current_employee")
    private boolean currentEmployee;
    @Basic(optional = false)
    @NotNull
    @Column(name = "operations_staff")
    private boolean operationsStaff;
    @Size(max = 64)
    @Column(name = "sms_address")
    private String smsAddress;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "staffMember")
    private List<ShiftStaffMember> shiftStaffMemberList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "reportedBy")
    private List<Event> eventList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "staffMember")
    private List<StaffRole> staffRoleList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "sysuser")
    private List<LogEntry> logEntryList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "user")
    private List<AuthUserRole> authUserRoleList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "user")
    private List<UserPreference> userPreferenceList;
    @OneToMany(mappedBy = "opInCharge")
    private List<Shift> shiftList;
    @OneToMany(mappedBy = "startedBy")
    private List<Shift> shiftList1;

    public Sysuser() {
    }

    public Sysuser(Integer userId) {
        this.userId = userId;
    }

    public Sysuser(Integer userId, String firstName, String lastName, boolean currentEmployee, boolean operationsStaff, int version) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentEmployee = currentEmployee;
        this.operationsStaff = operationsStaff;
        this.version = version;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getJob() {
        return job;
    }

    public void setJob(Integer job) {
        this.job = job;
    }

    public boolean getCurrentEmployee() {
        return currentEmployee;
    }

    public void setCurrentEmployee(boolean currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

    public boolean getOperationsStaff() {
        return operationsStaff;
    }

    public void setOperationsStaff(boolean operationsStaff) {
        this.operationsStaff = operationsStaff;
    }

    public String getSmsAddress() {
        return smsAddress;
    }

    public void setSmsAddress(String smsAddress) {
        this.smsAddress = smsAddress;
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
    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    @XmlTransient
    @JsonIgnore
    public List<StaffRole> getStaffRoleList() {
        return staffRoleList;
    }

    public void setStaffRoleList(List<StaffRole> staffRoleList) {
        this.staffRoleList = staffRoleList;
    }

    @XmlTransient
    @JsonIgnore
    public List<LogEntry> getLogEntryList() {
        return logEntryList;
    }

    public void setLogEntryList(List<LogEntry> logEntryList) {
        this.logEntryList = logEntryList;
    }

    @XmlTransient
    @JsonIgnore
    public List<AuthUserRole> getAuthUserRoleList() {
        return authUserRoleList;
    }

    public void setAuthUserRoleList(List<AuthUserRole> authUserRoleList) {
        this.authUserRoleList = authUserRoleList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserPreference> getUserPreferenceList() {
        return userPreferenceList;
    }

    public void setUserPreferenceList(List<UserPreference> userPreferenceList) {
        this.userPreferenceList = userPreferenceList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Shift> getShiftList() {
        return shiftList;
    }

    public void setShiftList(List<Shift> shiftList) {
        this.shiftList = shiftList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Shift> getShiftList1() {
        return shiftList1;
    }

    public void setShiftList1(List<Shift> shiftList1) {
        this.shiftList1 = shiftList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sysuser)) {
            return false;
        }
        Sysuser other = (Sysuser) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.Sysuser[ userId=" + userId + " ]";
    }
    
}
