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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.openepics.discs.hourlog.ent.BeamEvent;
import org.openepics.discs.hourlog.ent.Experiment;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Summary;
import org.openepics.discs.hourlog.ent.SummaryEvent;
import org.openepics.discs.hourlog.ent.Mode;
import org.openepics.discs.hourlog.ent.OperationsRole;
import org.openepics.discs.hourlog.ent.Shift;
import org.openepics.discs.hourlog.ent.ShiftStaffMember;
import org.openepics.discs.hourlog.ent.Source;
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.ent.Vault;
import org.openepics.discs.hourlog.log.StatusSnapshot;

/**
 * Summary report of a facility
 *
 * @author vuppala
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class SummaryReport {

    /**
     * Source
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class FacSource {

        private String name;
        private String description;

        public FacSource() {
        }

        ;
        
        public FacSource(Source source) {
            if (source != null) {
                this.name = source.getName();
                this.description = source.getDescription();
            }
        }
    }

    /**
     * Beam
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class FacBeam {

        private String system = "";
        private String elementName = "";
        private String symbol = "";
        private Integer massNumber = 0;
        private Integer elementCharge = 0;
        private Float energy = 0.0f;

        public FacBeam() {
        }

        ;
        
        public FacBeam(BeamEvent bevent) {
            this.system = bevent.getBeamSystem().getName();
            if (bevent.getElement() != null) {
                this.elementName = bevent.getElement().getName();
                this.symbol = bevent.getElement().getSymbol();
                this.massNumber = bevent.getMassNumber();
                this.elementCharge = bevent.getCharge();
                this.energy = bevent.getEnergy();
            }
            if (bevent.getBeam() != null) {
                this.elementName = bevent.getBeam().getElement().getName();
                this.symbol = bevent.getBeam().getElement().getSymbol();
                this.massNumber = bevent.getBeam().getMassNumber();
                this.energy = bevent.getBeam().getEnergy();
                this.elementCharge = bevent.getBeam().getCharge();
            }
        }
    }

    /**
     * Summary status
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class FacStatus {

        private Date timeStamp;
        private String name;
        private String description;

        public FacStatus() {
        }

        ;
        
        public FacStatus(Date date, Summary fstatus) {
            this.timeStamp = date;
            this.name = fstatus.getName();
            this.description = fstatus.getDescription();
        }
    }

    /**
     * Staff
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class FacStaff {

        private Sysuser employee;
        private OperationsRole role;

        public FacStaff() {
        }
    ;

    }
    
    /**
     * Shift
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class FacShift {

        private Date startTime;
        private Sysuser operatorInCharge; //operator in charge
        private String experimenterInCharge;  // experiment in charge
        @XmlElementWrapper(name = "staffList")
        private List<FacStaff> shiftStaff;

        public FacShift() {
        }

        ;
        
        public FacShift(Shift shift, List<ShiftStaffMember> staffList) {
            if (shift != null) {
                this.startTime = shift.getUpdatedAt();
                this.operatorInCharge = shift.getOpInCharge();
                this.experimenterInCharge = shift.getExpInCharge();
            }
            this.shiftStaff = new ArrayList<>();
            for (ShiftStaffMember ssm : staffList) {
                FacStaff member = new FacStaff();
                member.employee = ssm.getStaffMember();
                member.role = ssm.getRole();
                this.shiftStaff.add(member);
            }
        }

    }

    private Facility facility;
    private Experiment experiment; // current experiment   
    private Mode mode;
    private FacSource source;
    private Vault vault;

    @XmlElementWrapper(name = "beamList")
    @XmlElement(name = "beam")
    private List<FacBeam> beams; // current beam
    @XmlElementWrapper(name = "statusList")
    private List<FacStatus> status;
    @XmlElementWrapper(name = "availabilityList")
    private List<AvailabilityRecord> availability;
    private FacShift shift;

    public SummaryReport() {

    }

    /**
     * Generates new summary report for the given facility from given snapshot,
     * status events, shift information
     *
     * @param facility
     * @param snapshot
     * @param fsevents
     * @param shift
     * @param staff
     * @param arecords
     * @return
     */
    public static SummaryReport newInstance(Facility facility, StatusSnapshot snapshot, List<SummaryEvent> fsevents,
            Shift shift, List<ShiftStaffMember> staff, List<AvailabilityRecord> arecords) {
        SummaryReport fsummary = new SummaryReport();

        fsummary.facility = facility;
        fsummary.experiment = snapshot.getExperiment();
        fsummary.vault = snapshot.getVault();

        fsummary.status = new ArrayList<>();
        for (SummaryEvent event : fsevents) {
            fsummary.status.add(new FacStatus(event.getEvent().getOccurredAt(), event.getSummary()));
        }

        fsummary.availability = arecords;
        fsummary.beams = new ArrayList<>();
        for (BeamEvent event : snapshot.getBeams()) {
            fsummary.beams.add(new FacBeam(event));
        }

        fsummary.mode = snapshot.getMode();
        fsummary.source = new FacSource(snapshot.getSource());

        if (shift != null) {
            fsummary.shift = new FacShift(shift, staff);
        }
        return fsummary;
    }

}
