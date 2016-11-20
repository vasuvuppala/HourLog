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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "breakdown_event")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BreakdownEvent.findAll", query = "SELECT b FROM BreakdownEvent b"),
    @NamedQuery(name = "BreakdownEvent.findById", query = "SELECT b FROM BreakdownEvent b WHERE b.id = :id"),
    @NamedQuery(name = "BreakdownEvent.findByFailed", query = "SELECT b FROM BreakdownEvent b WHERE b.failed = :failed")})
public class BreakdownEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "failed")
    private boolean failed;
    @JoinColumn(name = "category", referencedColumnName = "brkcat_id")
    @ManyToOne(optional = false)
    private BreakdownCategory category;
    @JoinColumn(name = "event", referencedColumnName = "event_id")
    @ManyToOne(optional = false)
    private Event event;

    public BreakdownEvent() {
    }

    public BreakdownEvent(Integer id) {
        this.id = id;
    }

    public BreakdownEvent(Integer id, boolean failed) {
        this.id = id;
        this.failed = failed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public BreakdownCategory getCategory() {
        return category;
    }

    public void setCategory(BreakdownCategory category) {
        this.category = category;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
        if (!(object instanceof BreakdownEvent)) {
            return false;
        }
        BreakdownEvent other = (BreakdownEvent) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.BreakdownEvent[ id=" + id + " ]";
    }
    
}
