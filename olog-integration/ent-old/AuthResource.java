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
@Table(name = "auth_resource")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AuthResource.findAll", query = "SELECT a FROM AuthResource a"),
    @NamedQuery(name = "AuthResource.findByResourceId", query = "SELECT a FROM AuthResource a WHERE a.resourceId = :resourceId"),
    @NamedQuery(name = "AuthResource.findByName", query = "SELECT a FROM AuthResource a WHERE a.name = :name"),
    @NamedQuery(name = "AuthResource.findByDescription", query = "SELECT a FROM AuthResource a WHERE a.description = :description"),
    @NamedQuery(name = "AuthResource.findByVersion", query = "SELECT a FROM AuthResource a WHERE a.version = :version")})
public class AuthResource implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "resource_id")
    private Integer resourceId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
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
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "resource")
    private List<AuthPermission> authPermissionList;

    public AuthResource() {
    }

    public AuthResource(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public AuthResource(Integer resourceId, String name, String description, int version) {
        this.resourceId = resourceId;
        this.name = name;
        this.description = description;
        this.version = version;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
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
    public List<AuthPermission> getAuthPermissionList() {
        return authPermissionList;
    }

    public void setAuthPermissionList(List<AuthPermission> authPermissionList) {
        this.authPermissionList = authPermissionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (resourceId != null ? resourceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AuthResource)) {
            return false;
        }
        AuthResource other = (AuthResource) object;
        if ((this.resourceId == null && other.resourceId != null) || (this.resourceId != null && !this.resourceId.equals(other.resourceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.AuthResource[ resourceId=" + resourceId + " ]";
    }
    
}
