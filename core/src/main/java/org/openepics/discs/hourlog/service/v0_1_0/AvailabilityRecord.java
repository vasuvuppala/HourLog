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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Availability record
 *
 * @author vuppala
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AvailabilityRecord {

    private int days;
    private double percentage;

    private AvailabilityRecord() {
    }

    public AvailabilityRecord(int days, double percentage) {
        this.days = days;
        this.percentage = percentage;
    }

    public int getDays() {
        return days;
    }

    public double getPercentage() {
        return percentage;
    }
}
