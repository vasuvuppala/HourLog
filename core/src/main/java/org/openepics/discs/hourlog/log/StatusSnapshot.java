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
package org.openepics.discs.hourlog.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import org.openepics.discs.hourlog.ent.BeamEvent;
import org.openepics.discs.hourlog.ent.BreakdownEvent;
import org.openepics.discs.hourlog.ent.Event;
import org.openepics.discs.hourlog.ent.Experiment;
import org.openepics.discs.hourlog.ent.Summary;
import org.openepics.discs.hourlog.ent.Mode;
import org.openepics.discs.hourlog.ent.Source;
import org.openepics.discs.hourlog.ent.Vault;
import org.openepics.discs.hourlog.util.Utility;

/**
 * A snapshot of a facility's events (at a point in time).
 *
 * @author vuppala
 */
@XmlRootElement
public class StatusSnapshot implements Serializable {

    private static final Logger logger = Logger.getLogger(StatusSnapshot.class.getName());

    private Date taken_at;
    private Experiment experiment;
    private Vault vault;
    private Source source;
    private Summary summary;
    private Mode mode;
    private List<BeamEvent> beams;
    private List<BreakdownEvent> breakdowns;

    public StatusSnapshot() {

    }

    /**
     * Makes a shallow copy of the clone
     *
     * TODO: Make sure the shallow copy works for every situation
     *
     * @param clone
     */
    public StatusSnapshot(StatusSnapshot clone) {
        this.taken_at = clone.taken_at;
        this.experiment = clone.experiment;
        this.mode = clone.mode;
        this.vault = clone.vault;
        this.source = clone.source;
        this.summary = clone.summary;
        this.beams = clone.beams;
        this.breakdowns = clone.breakdowns;
    }

    /**
     * A little deeper copy. Beam and breakdown event lists are allocated.
     *
     * @param clone
     * @return deeper clone
     */
    public static StatusSnapshot newInstance(StatusSnapshot clone) {
        StatusSnapshot snapshot = new StatusSnapshot();

        snapshot.taken_at = clone.taken_at;
        snapshot.experiment = clone.experiment;
        snapshot.mode = clone.mode;
        snapshot.vault = clone.vault;
        snapshot.source = clone.source;
        snapshot.summary = clone.summary;

        snapshot.beams = new ArrayList<>();
        BeamEvent bevent;
        for (BeamEvent event : clone.beams) {
            bevent = new BeamEvent();
            bevent.setBeam(event.getBeam());
            bevent.setBeamSystem(event.getBeamSystem());
            bevent.setElement(event.getElement());
            bevent.setCharge(event.getCharge());
            bevent.setEnergy(event.getEnergy());
            bevent.setMassNumber(event.getMassNumber());
            snapshot.beams.add(bevent);
        }

        snapshot.breakdowns = new ArrayList<>();
        BreakdownEvent brkevent;
        for (BreakdownEvent event : clone.breakdowns) {
            brkevent = new BreakdownEvent();
            brkevent.setCategory(event.getCategory());
            brkevent.setFailed(event.getFailed());
            snapshot.breakdowns.add(brkevent);
        }

        return snapshot;
    }

    /**
     * Creates a new snapshot from a given event.
     *
     * @param event
     * @return snapshot
     */
//    public static StatusSnapshot newSnapshot(Event event) {       
//        StatusSnapshot snapshot = new StatusSnapshot();
//        
//        if (event.getExprEventList() != null && !event.getExprEventList().isEmpty()) {            
//            snapshot.experiment = event.getExprEventList().get(0).getExperiment();
//        }
//        if (event.getSourceEventList() != null && !event.getSourceEventList().isEmpty()) {
//            snapshot.source = event.getSourceEventList().get(0).getSource();
//        }
//        if (event.getVaultEventList() != null && !event.getVaultEventList().isEmpty()) {
//            snapshot.vault = event.getVaultEventList().get(0).getVault();
//        }
//        if (event.getSummaryEventList() != null && !event.getSummaryEventList().isEmpty()) {
//            snapshot.summary = event.getSummaryEventList().get(0).getSummary();
//        }
//        if (event.getModeEventList() != null && !event.getModeEventList().isEmpty()) {
//            snapshot.mode = event.getModeEventList().get(0).getMode();
//        }
//        // if (event.getBreakdownEventList() != null && !event.getBreakdownEventList().isEmpty()) {
//            snapshot.breakdowns = event.getBreakdownEventList();           
//        // }
//        // if (event.getBeamEventList() != null && !event.getBeamEventList().isEmpty()) {
//            snapshot.beams = event.getBeamEventList();           
//        // }
//        
//        return snapshot;
//    }
    /**
     * Prints a snapshot.
     *
     * TODO: convert to toString()
     *
     * @param heading
     */
    public void print(String heading) {
        logger.log(Level.FINE, " Snapshot: {0}", heading);
        logger.log(Level.FINE, "   Taken at: {0}", taken_at);
        logger.log(Level.FINE, "   Experiment: {0}", experiment == null? "" : experiment.getNumber());
        logger.log(Level.FINE, "   Mode: {0}", mode == null? "" : mode.getName());
        logger.log(Level.FINE, "   Vault: {0}", vault == null? "" : vault.getName());
        logger.log(Level.FINE, "   Source: {0}", source == null? "" : source.getName());
        logger.log(Level.FINE, "   Status: {0}", summary == null? "" : summary.getName());
        for (BeamEvent bevent : beams) {
            String beamMsg = bevent.getBeamSystem().getName() + ": ";
            if (bevent.getBeam() == null) {
                if (bevent.getElement() != null) {
                    beamMsg += bevent.getElement().getName();
                }
            } else {
                if (bevent.getBeam().getElement() != null) {
                    beamMsg += bevent.getBeam().getElement().getName();
                }
            }
            logger.log(Level.FINE, "   {0}", beamMsg);
        }
        for (BreakdownEvent bevent : breakdowns) {
            logger.log(Level.FINE, "   {0}", bevent.getCategory().getName() + ": " + (bevent.getFailed() ? "Failed" : "Clear"));
        }
    }

    /**
     * Updates the snapshot with an event
     *
     * @param event
     */
    public void update(Event event) {

        if (event.getExprEventList() != null && !event.getExprEventList().isEmpty()) {
            // logger.log(Level.FINE,"LayLogMOdel: snapshotatevent: adding experiment number " + event.getExprEventList().get(0).getExperiment().getNumber());
            this.setExperiment(event.getExprEventList().get(0).getExperiment());
        }
        if (event.getSourceEventList() != null && !event.getSourceEventList().isEmpty()) {
            this.setSource(event.getSourceEventList().get(0).getSource());
        }
        if (event.getVaultEventList() != null && !event.getVaultEventList().isEmpty()) {
            this.setVault(event.getVaultEventList().get(0).getVault());
        }
        if (event.getSummaryEventList() != null && !event.getSummaryEventList().isEmpty()) {
            this.setSummary(event.getSummaryEventList().get(0).getSummary());
        }
        if (event.getModeEventList() != null && !event.getModeEventList().isEmpty()) {
            this.setMode(event.getModeEventList().get(0).getMode());
        }
        if (event.getBreakdownEventList() != null && !event.getBreakdownEventList().isEmpty()) {
            this.setBreakdowns(Utility.mergeBreakdowns(this.getBreakdowns(), event.getBreakdownEventList()));
        }
        if (event.getBeamEventList() != null && !event.getBeamEventList().isEmpty()) {
            this.setBeams(Utility.mergeBeams(this.getBeams(), event.getBeamEventList()));
        }
        // return new StatusSnapshot(this);
    }

    /**
     * Compare two status snapshots
     *
     * @param other
     * @return true if they are the same
     */
    public boolean equals(StatusSnapshot other) {
        boolean same;
        
        // first check if references are the same. This is to check for equality of null values
        if ( this.experiment == other.experiment
                && this.summary == other.summary
                && this.vault == other.vault
                && this.source == other.source
                && this.mode == other.mode
                && breakdownsEqual(this.breakdowns, other.breakdowns)
                && beamsEqual(this.beams, other.beams)) {
            return true;
        }
        
//        same = this.experiment.equals(other.experiment)
//                && this.summary.equals(other.summary)
//                && this.vault.equals(other.vault)
//                && this.source.equals(other.source)
//                && this.mode.equals(other.mode)
//                && breakdownsEqual(this.breakdowns, other.breakdowns)
//                && beamsEqual(this.beams, other.beams);
        
        same = Utility.areSame(this.experiment, other.experiment)
                && Utility.areSame(this.summary, other.summary)
                && Utility.areSame(this.vault, other.vault)
                && Utility.areSame(this.source, other.source)
                && Utility.areSame(this.mode, other.mode)
                && breakdownsEqual(this.breakdowns, other.breakdowns)
                && beamsEqual(this.beams, other.beams);

        return same;
    }  
    
    /**
     * Check if two lists of beams are the same.
     *
     * TODO: It is assumed that the list element names match
     *
     * @param list1
     * @param list2
     * @return
     */
    private boolean beamsEqual(List<BeamEvent> list1, List<BeamEvent> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        Boolean same;
        BeamEvent event1, event2;
        for (int i = 0; i < list1.size(); i++) {
            event1 = list1.get(i);
            event2 = list2.get(i);
            same = Utility.areSame(event1.getBeam(), event2.getBeam())
                    && Utility.areSame(event1.getElement(), event2.getElement())
                    && Utility.areSame(event1.getMassNumber(), event2.getMassNumber())
                    && Utility.areSame(event1.getCharge(), event2.getCharge())
                    && Utility.areSame(event1.getEnergy(), event2.getEnergy());
                    
            if (!same) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if two lists of breakdown events are the same.
     *
     * TODO: It is assumed that the list element names match
     *
     * @param list1
     * @param list2
     * @return
     */
    private boolean breakdownsEqual(List<BreakdownEvent> list1, List<BreakdownEvent> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i).getFailed() != list2.get(i).getFailed()) {
                return false;
            }
        }
        return true;
    }

    //--- getters/setters
    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public Vault getVault() {
        return vault;
    }

    public void setVault(Vault vault) {
        this.vault = vault;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public List<BeamEvent> getBeams() {
        return beams;
    }

    public void setBeams(List<BeamEvent> beams) {
        this.beams = beams;
    }

    public List<BreakdownEvent> getBreakdowns() {
        return breakdowns;
    }

    public void setBreakdowns(List<BreakdownEvent> breakdowns) {
        this.breakdowns = breakdowns;
    }

    public Date getTaken_at() {
        return taken_at;
    }

    public void setTaken_at(Date taken_at) {
        this.taken_at = taken_at;
    }
}
