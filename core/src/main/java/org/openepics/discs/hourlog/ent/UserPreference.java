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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "user_preference")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserPreference.findAll", query = "SELECT u FROM UserPreference u"),
    @NamedQuery(name = "UserPreference.findByUserPreferenceId", query = "SELECT u FROM UserPreference u WHERE u.userPreferenceId = :userPreferenceId"),
    @NamedQuery(name = "UserPreference.findByName", query = "SELECT u FROM UserPreference u WHERE u.name = :name"),
    @NamedQuery(name = "UserPreference.findByPrefValue", query = "SELECT u FROM UserPreference u WHERE u.prefValue = :prefValue"),
    @NamedQuery(name = "UserPreference.findByVersion", query = "SELECT u FROM UserPreference u WHERE u.version = :version")})
public class UserPreference implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "user_preference_id")
    private Integer userPreferenceId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Size(max = 64)
    @Column(name = "pref_value")
    private String prefValue;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @JoinColumn(name = "user", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Sysuser user;

    public UserPreference() {
    }

    public UserPreference(Integer userPreferenceId) {
        this.userPreferenceId = userPreferenceId;
    }

    public UserPreference(Integer userPreferenceId, String name, int version) {
        this.userPreferenceId = userPreferenceId;
        this.name = name;
        this.version = version;
    }

    public Integer getUserPreferenceId() {
        return userPreferenceId;
    }

    public void setUserPreferenceId(Integer userPreferenceId) {
        this.userPreferenceId = userPreferenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefValue() {
        return prefValue;
    }

    public void setPrefValue(String prefValue) {
        this.prefValue = prefValue;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Sysuser getUser() {
        return user;
    }

    public void setUser(Sysuser user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userPreferenceId != null ? userPreferenceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserPreference)) {
            return false;
        }
        UserPreference other = (UserPreference) object;
        if ((this.userPreferenceId == null && other.userPreferenceId != null) || (this.userPreferenceId != null && !this.userPreferenceId.equals(other.userPreferenceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.UserPreference[ userPreferenceId=" + userPreferenceId + " ]";
    }
    
}
