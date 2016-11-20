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
package org.openepics.discs.hourlog.service.v0_1_0;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.openepics.discs.hourlog.ent.BeamEvent;
import org.openepics.discs.hourlog.ent.BreakdownEvent;
import org.openepics.discs.hourlog.ent.Event;
import org.openepics.discs.hourlog.ent.ExprEvent;
import org.openepics.discs.hourlog.ent.ModeEvent;
import org.openepics.discs.hourlog.ent.SourceEvent;
import org.openepics.discs.hourlog.ent.SummaryEvent;
import org.openepics.discs.hourlog.ent.VaultEvent;

/**
 * Event (as seen by API clients)
 * 
 * @author vuppala
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="event")
public class EventRep {
    @XmlAccessorType(XmlAccessType.FIELD)
    private static enum EventType {
        BEAM, BREAKDOWN, OTHER;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    private static enum ExprEventType {
        EXPERIMENT, MODE, SOURCE, SUMMARY, VAULT;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Status {

        private EventType type;
        private String name;
        private String value;

        private Status() {
        }

        public Status(EventType type, String name, String value) {
            this.type = type;
            this.name = name;
            this.value = value;
        }
    }
    
    private int eventId;
    private Date occurredAt;
    private Date enteredAt;
    private String logText;
    private UserRep author;
    
    private List<Status> status;

    private EventRep() {
    }

    /**
     * Event representation from event entity
     * 
     * 
     * TODO: FInd a generic way to gather all event types rather than listing each one
     * 
     * @param event
     * @param logText
     * @return 
     */
    public static EventRep newInstance(Event event, String logText) {
        EventRep resource = new EventRep();
        resource.eventId = event.getEventId();
        resource.enteredAt = event.getEventEnteredAt();
        resource.occurredAt = event.getOccurredAt();
        resource.author = UserRep.newInstance(event.getReportedBy());
        resource.status = new ArrayList<>();
        
        // --- Beams
        String beamName;
        for (BeamEvent eve : event.getBeamEventList()) {
            beamName = "";
            if (eve.getBeam() == null) {
                if (eve.getElement() != null) {
                    beamName = String.format("%d %s %d %f", eve.getMassNumber(), eve.getElement().getSymbol(), eve.getCharge(), eve.getEnergy());
                }
            } else {
                if (eve.getBeam().getElement() != null) {
                    beamName = String.format("%d %s %d %f", eve.getBeam().getMassNumber(), eve.getBeam().getElement().getSymbol(), eve.getBeam().getCharge(), eve.getBeam().getEnergy());
                }
            }
            resource.status.add(new Status(EventType.BEAM, eve.getBeamSystem().getName(), beamName));
        }
        
        // breakdowns
        for(BreakdownEvent eve: event.getBreakdownEventList()) {
            resource.status.add(new Status(EventType.BREAKDOWN, eve.getCategory().getName(), eve.getFailed()? "down": "up"));
        }
        
        // summary
        for(SummaryEvent eve: event.getSummaryEventList()) {
            resource.status.add(new Status(EventType.OTHER, ExprEventType.SUMMARY.toString(), eve.getSummary().getName()));
        }
        
        // experiment
        for(ExprEvent eve: event.getExprEventList()) {
            resource.status.add(new Status(EventType.OTHER, ExprEventType.EXPERIMENT.toString(), eve.getExperiment().getNumber().toString()));
        }
        
        // source
        for(SourceEvent eve: event.getSourceEventList()) {
            resource.status.add(new Status(EventType.OTHER, ExprEventType.SOURCE.toString(), eve.getSource().getName()));
        }
        
        // Vault
        for(VaultEvent eve: event.getVaultEventList()) {
            resource.status.add(new Status(EventType.OTHER, ExprEventType.VAULT.toString(), eve.getVault().getName()));
        }
        
        // Mode
        for(ModeEvent eve: event.getModeEventList()) {
            resource.status.add(new Status(EventType.OTHER, ExprEventType.MODE.toString(), eve.getMode().getName()));
        }
        
        resource.logText = logText;
        
        return resource;
    }

    // getters and setters
    
    public List<Status> getEvents() {
        return status;
    }      
}
