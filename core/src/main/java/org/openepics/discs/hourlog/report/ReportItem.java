/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013.
 *  State University (c) Copyright 2013.
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

import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An item in the report
 *
 * @author vuppala
 */
@XmlRootElement
public class ReportItem implements Serializable {

    private String name; // name of the item: status or system
    private String description;
    private int occurrences; // number of occurrences of name
    private double hours; // hours: status or breakdown
    private double percentage; // 

    public ReportItem() {

    }

    public ReportItem(String givenName, String desc) {
        name = givenName;
        description = desc;
        occurrences = 0;
        hours = 0.0;
        percentage = 0.0;
    }

    public void incrementOccurrences() {
        occurrences++;
    }

    public void addHours(double hrs) {
        hours += hrs;
    }

    public void calculatePercent(double total) {
        BigDecimal ntotal = new BigDecimal(total);

        if (ntotal.compareTo(BigDecimal.ZERO) != 0) {
            percentage = hours / Math.abs(total) * 100.0;
        }
    }

    public void addTo(ReportItem another) {
        hours += another.hours;
        occurrences += another.occurrences;
    }

    @Override
    public String toString() {
        return String.format("Report Item:%n Name: %s%n Description: %s%n Occurrences: %d%n Hours %f%n Percentage: %f%n", name, description, occurrences, hours, percentage);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public double getHours() {
        return hours;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

}
