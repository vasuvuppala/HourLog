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
@Table(name = "auth_operation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AuthOperation.findAll", query = "SELECT a FROM AuthOperation a"),
    @NamedQuery(name = "AuthOperation.findByOperationId", query = "SELECT a FROM AuthOperation a WHERE a.operationId = :operationId"),
    @NamedQuery(name = "AuthOperation.findByName", query = "SELECT a FROM AuthOperation a WHERE a.name = :name"),
    @NamedQuery(name = "AuthOperation.findByDescription", query = "SELECT a FROM AuthOperation a WHERE a.description = :description")})
public class AuthOperation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "operation_id")
    private Integer operationId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "operation")
    private List<AuthPermission> authPermissionList;

    public AuthOperation() {
    }

    public AuthOperation(Integer operationId) {
        this.operationId = operationId;
    }

    public AuthOperation(Integer operationId, String name, String description) {
        this.operationId = operationId;
        this.name = name;
        this.description = description;
    }

    public Integer getOperationId() {
        return operationId;
    }

    public void setOperationId(Integer operationId) {
        this.operationId = operationId;
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
        hash += (operationId != null ? operationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AuthOperation)) {
            return false;
        }
        AuthOperation other = (AuthOperation) object;
        if ((this.operationId == null && other.operationId != null) || (this.operationId != null && !this.operationId.equals(other.operationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.AuthOperation[ operationId=" + operationId + " ]";
    }
    
}
