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
@Table(name = "api_client")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ApiClient.findAll", query = "SELECT a FROM ApiClient a"),
    @NamedQuery(name = "ApiClient.findById", query = "SELECT a FROM ApiClient a WHERE a.id = :id"),
    @NamedQuery(name = "ApiClient.findByName", query = "SELECT a FROM ApiClient a WHERE a.name = :name"),
    @NamedQuery(name = "ApiClient.findByApiKey", query = "SELECT a FROM ApiClient a WHERE a.apiKey = :apiKey"),
    @NamedQuery(name = "ApiClient.findByApiPassword", query = "SELECT a FROM ApiClient a WHERE a.apiPassword = :apiPassword"),
    @NamedQuery(name = "ApiClient.findByDescription", query = "SELECT a FROM ApiClient a WHERE a.description = :description"),
    @NamedQuery(name = "ApiClient.findByContactName", query = "SELECT a FROM ApiClient a WHERE a.contactName = :contactName"),
    @NamedQuery(name = "ApiClient.findByContactEmail", query = "SELECT a FROM ApiClient a WHERE a.contactEmail = :contactEmail"),
    @NamedQuery(name = "ApiClient.findByComments", query = "SELECT a FROM ApiClient a WHERE a.comments = :comments")})
public class ApiClient implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "api_key")
    private String apiKey;
    @Size(max = 255)
    @Column(name = "api_password")
    private String apiPassword;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "contact_name")
    private String contactName;
    @Size(max = 128)
    @Column(name = "contact_email")
    private String contactEmail;
    @Size(max = 255)
    @Column(name = "comments")
    private String comments;

    public ApiClient() {
    }

    public ApiClient(Integer id) {
        this.id = id;
    }

    public ApiClient(Integer id, String name, String apiKey, String description, String contactName) {
        this.id = id;
        this.name = name;
        this.apiKey = apiKey;
        this.description = description;
        this.contactName = contactName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
        if (!(object instanceof ApiClient)) {
            return false;
        }
        ApiClient other = (ApiClient) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.ApiClient[ id=" + id + " ]";
    }
    
}
