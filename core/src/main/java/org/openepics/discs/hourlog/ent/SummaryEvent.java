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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "summary_event")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SummaryEvent.findAll", query = "SELECT s FROM SummaryEvent s"),
    @NamedQuery(name = "SummaryEvent.findById", query = "SELECT s FROM SummaryEvent s WHERE s.id = :id")})
public class SummaryEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "event", referencedColumnName = "event_id")
    @ManyToOne(optional = false)
    private Event event;
    @JoinColumn(name = "summary", referencedColumnName = "summary_id")
    @ManyToOne(optional = false)
    private Summary summary;

    public SummaryEvent() {
    }

    public SummaryEvent(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
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
        if (!(object instanceof SummaryEvent)) {
            return false;
        }
        SummaryEvent other = (SummaryEvent) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.SummaryEvent[ id=" + id + " ]";
    }
    
}
