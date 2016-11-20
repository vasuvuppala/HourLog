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
import org.openepics.discs.hourlog.ent.Event;
import org.openepics.discs.hourlog.ent.Shift;

/**
 * A log entry with facility status
 *
 * TODO: Add facility
 *
 * @author vuppala
 */
public class StatusfulLogEntry implements Serializable {

    private boolean newDay = false; // this is the first entry of the day
    private boolean newShift = false;  // this is first entry of a shift
    private boolean logTextChanged = false;  // log text was changed. only for log history.
    private LogEntry logEntry;
    private Event event;
    private Shift shift;
    private StatusSnapshot snapshot;

    public StatusfulLogEntry() {
    }

    public StatusfulLogEntry(LogEntry entry, Event event, StatusSnapshot snapshot) {
        this.logEntry = entry;
        this.event = event;
        this.snapshot = snapshot;
    }

    //TODO: quick fix for lack of JSF components to generate tables dynamically. Replace. This should be done in EL.
//    public List<LogEntryProperty> toProperties() {
//        List<LogEntryProperty> props = new ArrayList<>();
//        SimpleDateFormat dateFmt = new SimpleDateFormat("HH:mm:ss dd-MMM-yy");
//        String lKey, lValue;
//
//        if (logEntry != null) {
//            props.add(new LogEntryProperty("Occurred At", dateFmt.format(logEntry.getOccurredAt())));
//            props.add(new LogEntryProperty("Entered At", dateFmt.format(logEntry.getEnteredAt())));
//        }
//        if (snapshot != null) {
//            for (BeamEvent bevent : snapshot.getBeams()) {
//                lKey = bevent.getBeamSystem().getName();
//                if (bevent.getBeamSystem().getBeamList() != null) {
//                    if (bevent.getBeam() == null) {
//                        lValue = "None";
//                    } else {
//                        lValue = String.format("<SUP>%d</SUP>%s<SUP>%d</SUP> %.2f", 
//                                bevent.getBeam().getMassNumber(), bevent.getBeam().getElement().getSymbol(), bevent.getBeam().getCharge(), bevent.getBeam().getEnergy());
//                    }
//                } else {
//                    if (bevent.getElement() == null) {
//                        lValue = "None";
//                    } else {
//                        lValue = String.format("<SUP>%s</SUP>%s<SUP>%s</SUP>", bevent.getMassNumber() == null ? "" : bevent.getMassNumber(),
//                                bevent.getElement().getSymbol(),
//                                bevent.getCharge() == null ? "" : bevent.getCharge());
//                    }
//                }
//                props.add(new LogEntryProperty(lKey, lValue));
//            }
//        }
//        if (shift != null) {
//            props.add(new LogEntryProperty("Shift Updated At", dateFmt.format(shift.getStartedAt())));
//            props.add(new LogEntryProperty("Shift Updated By", shift.getStartedBy().getFirstName() + " " + shift.getStartedBy().getLastName()));
//            props.add(new LogEntryProperty("Operator-In-Charge", shift.getOpInCharge().getFirstName() + " " + shift.getOpInCharge().getLastName()));
//            props.add(new LogEntryProperty("Experimenter-In-Charge", shift.getExpInCharge()));
//            for (ShiftStaffMember ssm : shift.getShiftStaffMemberList()) {
//                props.add(new LogEntryProperty(ssm.getRole().getName(), ssm.getStaffMember().getFirstName() + " " + ssm.getStaffMember().getLastName()));
//            }
//        }
//        return props;
//    }
    // -- getters/setters
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public StatusSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(StatusSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public boolean isNewDay() {
        return newDay;
    }

    public void setNewDay(boolean newDay) {
        this.newDay = newDay;
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }

    public boolean isNewShift() {
        return newShift;
    }

    public void setNewShift(boolean newShift) {
        this.newShift = newShift;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public void setLogEntry(LogEntry logEntry) {
        this.logEntry = logEntry;
    }

    public boolean isLogTextChanged() {
        return logTextChanged;
    }

    public void setLogTextChanged(boolean logTextChanged) {
        this.logTextChanged = logTextChanged;
    }

}
