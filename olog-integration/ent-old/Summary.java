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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "summary")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Summary.findAll", query = "SELECT s FROM Summary s"),
    @NamedQuery(name = "Summary.findBySummaryId", query = "SELECT s FROM Summary s WHERE s.summaryId = :summaryId"),
    @NamedQuery(name = "Summary.findByName", query = "SELECT s FROM Summary s WHERE s.name = :name"),
    @NamedQuery(name = "Summary.findByDescription", query = "SELECT s FROM Summary s WHERE s.description = :description"),
    @NamedQuery(name = "Summary.findByVersion", query = "SELECT s FROM Summary s WHERE s.version = :version")})
public class Summary implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "summary_id")
    private Short summaryId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
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
    @OneToMany(mappedBy = "parent")
    private List<Summary> summaryList;
    @JoinColumn(name = "parent", referencedColumnName = "summary_id")
    @ManyToOne
    private Summary parent;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "summary")
    private List<SummaryEvent> summaryEventList;

    public Summary() {
    }

    public Summary(Short summaryId) {
        this.summaryId = summaryId;
    }

    public Summary(Short summaryId, String name, String description, int version) {
        this.summaryId = summaryId;
        this.name = name;
        this.description = description;
        this.version = version;
    }

    public Short getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(Short summaryId) {
        this.summaryId = summaryId;
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
    public List<Summary> getSummaryList() {
        return summaryList;
    }

    public void setSummaryList(List<Summary> summaryList) {
        this.summaryList = summaryList;
    }

    public Summary getParent() {
        return parent;
    }

    public void setParent(Summary parent) {
        this.parent = parent;
    }

    @XmlTransient
    @JsonIgnore
    public List<SummaryEvent> getSummaryEventList() {
        return summaryEventList;
    }

    public void setSummaryEventList(List<SummaryEvent> summaryEventList) {
        this.summaryEventList = summaryEventList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (summaryId != null ? summaryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Summary)) {
            return false;
        }
        Summary other = (Summary) object;
        if ((this.summaryId == null && other.summaryId != null) || (this.summaryId != null && !this.summaryId.equals(other.summaryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.Summary[ summaryId=" + summaryId + " ]";
    }
    
}
