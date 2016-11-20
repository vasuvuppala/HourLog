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
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "snapshot")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Snapshot.findAll", query = "SELECT s FROM Snapshot s"),
    @NamedQuery(name = "Snapshot.findBySnapshotId", query = "SELECT s FROM Snapshot s WHERE s.snapshotId = :snapshotId"),
    @NamedQuery(name = "Snapshot.findByTakenAt", query = "SELECT s FROM Snapshot s WHERE s.takenAt = :takenAt"),
    @NamedQuery(name = "Snapshot.findByReferenceId", query = "SELECT s FROM Snapshot s WHERE s.referenceId = :referenceId")})
public class Snapshot implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "snapshot_id")
    private Integer snapshotId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "taken_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date takenAt;
    @Column(name = "reference_id")
    private BigInteger referenceId;
    @JoinColumn(name = "facility", referencedColumnName = "facility_id")
    @ManyToOne(optional = false)
    private Facility facility;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "snapshot")
    private List<SnapshotEvent> snapshotEventList;

    public Snapshot() {
    }

    public Snapshot(Integer snapshotId) {
        this.snapshotId = snapshotId;
    }

    public Snapshot(Integer snapshotId, Date takenAt) {
        this.snapshotId = snapshotId;
        this.takenAt = takenAt;
    }

    public Integer getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(Integer snapshotId) {
        this.snapshotId = snapshotId;
    }

    public Date getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(Date takenAt) {
        this.takenAt = takenAt;
    }

    public BigInteger getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(BigInteger referenceId) {
        this.referenceId = referenceId;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    @XmlTransient
    @JsonIgnore
    public List<SnapshotEvent> getSnapshotEventList() {
        return snapshotEventList;
    }

    public void setSnapshotEventList(List<SnapshotEvent> snapshotEventList) {
        this.snapshotEventList = snapshotEventList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (snapshotId != null ? snapshotId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Snapshot)) {
            return false;
        }
        Snapshot other = (Snapshot) object;
        if ((this.snapshotId == null && other.snapshotId != null) || (this.snapshotId != null && !this.snapshotId.equals(other.snapshotId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.Snapshot[ snapshotId=" + snapshotId + " ]";
    }
    
}
