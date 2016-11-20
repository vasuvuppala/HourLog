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
@Table(name = "vault")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vault.findAll", query = "SELECT v FROM Vault v"),
    @NamedQuery(name = "Vault.findByVaultId", query = "SELECT v FROM Vault v WHERE v.vaultId = :vaultId"),
    @NamedQuery(name = "Vault.findByName", query = "SELECT v FROM Vault v WHERE v.name = :name"),
    @NamedQuery(name = "Vault.findByDescription", query = "SELECT v FROM Vault v WHERE v.description = :description"),
    @NamedQuery(name = "Vault.findByActive", query = "SELECT v FROM Vault v WHERE v.active = :active"),
    @NamedQuery(name = "Vault.findByVersion", query = "SELECT v FROM Vault v WHERE v.version = :version")})
public class Vault implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "vault_id")
    private Short vaultId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "vault")
    private List<VaultEvent> vaultEventList;

    public Vault() {
    }

    public Vault(Short vaultId) {
        this.vaultId = vaultId;
    }

    public Vault(Short vaultId, String name, String description, boolean active, int version) {
        this.vaultId = vaultId;
        this.name = name;
        this.description = description;
        this.active = active;
        this.version = version;
    }

    public Short getVaultId() {
        return vaultId;
    }

    public void setVaultId(Short vaultId) {
        this.vaultId = vaultId;
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

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @XmlTransient
    @JsonIgnore
    public List<VaultEvent> getVaultEventList() {
        return vaultEventList;
    }

    public void setVaultEventList(List<VaultEvent> vaultEventList) {
        this.vaultEventList = vaultEventList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vaultId != null ? vaultId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vault)) {
            return false;
        }
        Vault other = (Vault) object;
        if ((this.vaultId == null && other.vaultId != null) || (this.vaultId != null && !this.vaultId.equals(other.vaultId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.Vault[ vaultId=" + vaultId + " ]";
    }
    
}
