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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "event")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e"),
    @NamedQuery(name = "Event.findByEventId", query = "SELECT e FROM Event e WHERE e.eventId = :eventId"),
    @NamedQuery(name = "Event.findByOccurredAt", query = "SELECT e FROM Event e WHERE e.occurredAt = :occurredAt"),
    @NamedQuery(name = "Event.findByEventEnteredAt", query = "SELECT e FROM Event e WHERE e.eventEnteredAt = :eventEnteredAt"),
    @NamedQuery(name = "Event.findByNote", query = "SELECT e FROM Event e WHERE e.note = :note"),
    @NamedQuery(name = "Event.findByLogEnteredAt", query = "SELECT e FROM Event e WHERE e.logEnteredAt = :logEnteredAt"),
    @NamedQuery(name = "Event.findByVersion", query = "SELECT e FROM Event e WHERE e.version = :version")})
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "event_id")
    private Integer eventId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "occurred_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date occurredAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "event_entered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventEnteredAt;
    @Column(name = "note")
    private Integer note;
    @Basic(optional = false)
    @NotNull
    @Column(name = "log_entered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logEnteredAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    @Version
    private int version;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "event")
    private List<ExprEvent> exprEventList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "event")
    private List<BreakdownEvent> breakdownEventList;
    @JoinColumn(name = "reported_by", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Sysuser reportedBy;
    @JoinColumn(name = "facility", referencedColumnName = "facility_id")
    @ManyToOne(optional = false)
    private Facility facility;
    @OneToMany(mappedBy = "obsoletedBy")
    private List<Event> eventList;
    @JoinColumn(name = "obsoleted_by", referencedColumnName = "event_id")
    @ManyToOne
    private Event obsoletedBy;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "event")
    private List<BeamEvent> beamEventList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "event")
    private List<VaultEvent> vaultEventList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "event")
    private List<SourceEvent> sourceEventList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "event")
    private List<ModeEvent> modeEventList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "event")
    private List<SnapshotEvent> snapshotEventList;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "event")
    private List<SummaryEvent> summaryEventList;

    public Event() {
    }

    public Event(Integer eventId) {
        this.eventId = eventId;
    }

    public Event(Integer eventId, Date occurredAt, Date eventEnteredAt, Date logEnteredAt, int version) {
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.eventEnteredAt = eventEnteredAt;
        this.logEnteredAt = logEnteredAt;
        this.version = version;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Date getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Date occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Date getEventEnteredAt() {
        return eventEnteredAt;
    }

    public void setEventEnteredAt(Date eventEnteredAt) {
        this.eventEnteredAt = eventEnteredAt;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public Date getLogEnteredAt() {
        return logEnteredAt;
    }

    public void setLogEnteredAt(Date logEnteredAt) {
        this.logEnteredAt = logEnteredAt;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExprEvent> getExprEventList() {
        return exprEventList;
    }

    public void setExprEventList(List<ExprEvent> exprEventList) {
        this.exprEventList = exprEventList;
    }

    @XmlTransient
    @JsonIgnore
    public List<BreakdownEvent> getBreakdownEventList() {
        return breakdownEventList;
    }

    public void setBreakdownEventList(List<BreakdownEvent> breakdownEventList) {
        this.breakdownEventList = breakdownEventList;
    }

    public Sysuser getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(Sysuser reportedBy) {
        this.reportedBy = reportedBy;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    @XmlTransient
    @JsonIgnore
    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public Event getObsoletedBy() {
        return obsoletedBy;
    }

    public void setObsoletedBy(Event obsoletedBy) {
        this.obsoletedBy = obsoletedBy;
    }

    @XmlTransient
    @JsonIgnore
    public List<BeamEvent> getBeamEventList() {
        return beamEventList;
    }

    public void setBeamEventList(List<BeamEvent> beamEventList) {
        this.beamEventList = beamEventList;
    }

    @XmlTransient
    @JsonIgnore
    public List<VaultEvent> getVaultEventList() {
        return vaultEventList;
    }

    public void setVaultEventList(List<VaultEvent> vaultEventList) {
        this.vaultEventList = vaultEventList;
    }

    @XmlTransient
    @JsonIgnore
    public List<SourceEvent> getSourceEventList() {
        return sourceEventList;
    }

    public void setSourceEventList(List<SourceEvent> sourceEventList) {
        this.sourceEventList = sourceEventList;
    }

    @XmlTransient
    @JsonIgnore
    public List<ModeEvent> getModeEventList() {
        return modeEventList;
    }

    public void setModeEventList(List<ModeEvent> modeEventList) {
        this.modeEventList = modeEventList;
    }

    @XmlTransient
    @JsonIgnore
    public List<SnapshotEvent> getSnapshotEventList() {
        return snapshotEventList;
    }

    public void setSnapshotEventList(List<SnapshotEvent> snapshotEventList) {
        this.snapshotEventList = snapshotEventList;
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
        hash += (eventId != null ? eventId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.eventId == null && other.eventId != null) || (this.eventId != null && !this.eventId.equals(other.eventId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.hourlog.ent.Event[ eventId=" + eventId + " ]";
    }
    
}
