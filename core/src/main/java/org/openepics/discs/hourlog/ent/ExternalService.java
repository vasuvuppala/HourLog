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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "external_service")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExternalService.findAll", query = "SELECT e FROM ExternalService e"),
    @NamedQuery(name = "ExternalService.findByServiceId", query = "SELECT e FROM ExternalService e WHERE e.serviceId = :serviceId"),
    @NamedQuery(name = "ExternalService.findByName", query = "SELECT e FROM ExternalService e WHERE e.name = :name"),
    @NamedQuery(name = "ExternalService.findByDescription", query = "SELECT e FROM ExternalService e WHERE e.description = :description"),
    @NamedQuery(name = "ExternalService.findByStatus", query = "SELECT e FROM ExternalService e WHERE e.status = :status"),
    @NamedQuery(name = "ExternalService.findByStatusMessage", query = "SELECT e FROM ExternalService e WHERE e.statusMessage = :statusMessage"),
    @NamedQuery(name = "ExternalService.findByStatusUpdatedAt", query = "SELECT e FROM ExternalService e WHERE e.statusUpdatedAt = :statusUpdatedAt"),
    @NamedQuery(name = "ExternalService.findByLastServicedAt", query = "SELECT e FROM ExternalService e WHERE e.lastServicedAt = :lastServicedAt")})
public class ExternalService implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "service_id")
    private Integer serviceId;
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
    @Lob
    @Size(max = 65535)
    @Column(name = "base_url")
    private String baseUrl;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "status")
    private String status;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "status_message")
    private String statusMessage;
    @Column(name = "status_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusUpdatedAt;
    @Column(name = "last_serviced_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastServicedAt;

    public ExternalService() {
    }

    public ExternalService(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public ExternalService(Integer serviceId, String name, String description, String status, String statusMessage) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.statusMessage = statusMessage;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
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

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Date getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(Date statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    public Date getLastServicedAt() {
        return lastServicedAt;
    }

    public void setLastServicedAt(Date lastServicedAt) {
        this.lastServicedAt = lastServicedAt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceId != null ? serviceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExternalService)) {
            return false;
        }
        ExternalService other = (ExternalService) object;
        if ((this.serviceId == null && other.serviceId != null) || (this.serviceId != null && !this.serviceId.equals(other.serviceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.ExternalService[ serviceId=" + serviceId + " ]";
    }
    
}
