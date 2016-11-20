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
package org.openepics.discs.hourlog.report;

import org.openepics.discs.hourlog.ent.Beam;
import org.openepics.discs.hourlog.ent.BeamEvent;
import org.openepics.discs.hourlog.ent.BeamSystem;
import org.openepics.discs.hourlog.ent.Element;
import org.openepics.discs.hourlog.util.Utility;

/**
 * Beam for report purposes.
 *
 * We could have used the Entity Beam (org.openepics.discs.hourlog.ent.Beam) but
 * we want different equals() and hashcode() methods and we do not want to
 * modify the entities as they are auto-generated from the data model. Would
 * have been nice to switch to object model, but there is not enough
 * time/budget.
 *
 * @author vuppala
 */
public class ReportBeam implements Comparable<ReportBeam> {

    private BeamSystem beamSystem;
    private Element element;
    private Integer massNumber;
    private Integer charge;
    private Float energy;

    private ReportBeam() {

    }

    /**
     * Construct from a beam event
     *
     * @param beamEvent
     * @return
     */
    public static ReportBeam newInstance(final BeamEvent beamEvent) {

        if (beamEvent == null) {
            return new ReportBeam();
        }

        if (beamEvent.getBeam() != null) {
            return newInstace(beamEvent.getBeam());
        }

        ReportBeam beam = new ReportBeam();
        beam.charge = beamEvent.getCharge();
        beam.energy = beamEvent.getEnergy();
        beam.massNumber = beamEvent.getMassNumber();
        beam.element = beamEvent.getElement();
        beam.beamSystem = beamEvent.getBeamSystem();

        return beam;
    }

    /**
     * Construct a report beam from beam entity
     *
     * @param beam
     * @return
     */
    public static ReportBeam newInstace(final Beam beam) {
        if (beam == null) {
            return null;
        }

        ReportBeam reportBeam = new ReportBeam();

        reportBeam.charge = beam.getCharge();
        reportBeam.energy = beam.getEnergy();
        reportBeam.massNumber = beam.getMassNumber();
        reportBeam.element = beam.getElement();
        reportBeam.beamSystem = beam.getBeamSystem();

        return reportBeam;
    }

    @Override
    public int compareTo(final ReportBeam otherBeam) {
        final int SAME = 0;
        final int SMALLER = -1;
        final int BIGGER = 1;
        int comp;

        if (otherBeam == null) {
            return BIGGER;
        }

        // compare elements
        if (element != otherBeam.element) {
            if (element == null) {
                return SMALLER;
            }
            if (otherBeam.element == null) {
                return BIGGER;
            }
            comp = element.getSymbol().compareToIgnoreCase(otherBeam.element.getSymbol());
            if (comp != 0) {
                return comp;
            }
        }

        if (element == null && otherBeam.element == null) { // 'None' Beam
            return SAME;
        }
        // compare rest of the beam attributes              
        comp = Utility.compare(massNumber, otherBeam.massNumber);
        if (comp != 0) {
            return comp;
        }
        comp = Utility.compare(charge, otherBeam.charge);
        if (comp != 0) {
            return comp;
        }
        comp = Utility.compare(energy, otherBeam.energy);
        if (comp != 0) {
            return comp;
        }

        return SAME;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof ReportBeam)) {
            return false;
        }

        return (this.compareTo((ReportBeam) other) == 0);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (element == null) {
            return hash;
        }
        hash += element == null ? 0 : element.getSymbol().hashCode();
        hash += massNumber == null || massNumber == 0 ? 0 : massNumber.hashCode();
        hash += charge == null || charge == 0 ? 0 : charge.hashCode();
        hash += energy == null || energy == 0.0 ? 0 : energy.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder value = new StringBuilder();
        if (element == null) {
            return "None";
        }

        if (massNumber != null && massNumber != 0) {
            value.append("<SUP>");
            value.append(massNumber);
            value.append("</SUP>");
        }
        value.append(element.getSymbol());
        if (charge != null && charge != 0) {
            value.append("<SUP>");
            value.append(charge);
            value.append("+</SUP>");
        }
        if (energy != null && energy != 0) {
            value.append("&nbsp;");
            value.append(energy);
        }
        return value.toString();
    }
    // -- getters/setters

    public BeamSystem getBeamSystem() {
        return beamSystem;
    }

    public Element getElement() {
        return element;
    }

    public Integer getMassNumber() {
        return massNumber;
    }

    public Integer getCharge() {
        return charge;
    }

    public Float getEnergy() {
        return energy;
    }

}
