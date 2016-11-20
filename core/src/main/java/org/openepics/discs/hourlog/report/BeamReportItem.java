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

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openepics.discs.hourlog.ent.Summary;

/**
 * An item (record) of a beam report.
 * 
 * @author vuppala
 */
public class BeamReportItem {
    private Date startTime;
    private Date endTime;
    private ReportBeam beam;
    private Map<String, ReportItem> summaryData;
    private double summaryTotal = 0.0; // total of all the hours in summaryData
    private double totalPercentage = 0.0; // percentage of summaryTotal compared to overall total hours (of all report items)
    
    private BeamReportItem() {
        
    }
    
    /**
     * Constructor
     * 
     * @param start
     * @param end
     * @param beam
     * @param summaries
     * @return A 
     */
    public static BeamReportItem newInstance(final Date start, final Date end, final ReportBeam beam, final List<Summary> summaries) {
        BeamReportItem bri = new BeamReportItem();
        
        bri.startTime = start;
        bri.endTime = end;
        bri.beam = beam;
        bri.summaryData = new HashMap<>();
        
        for(Summary sum: summaries) {
           bri.summaryData.put(sum.getName(), new ReportItem(sum.getName(), sum.getDescription()));          
        }
        
        return bri;
    }
    
    /**
     * Add hours to a summary data
     * 
     * @param summary
     * @param hours 
     */
    public void addSummaryHours(Summary summary, double hours) {
        summaryData.get(summary.getName()).addHours(hours);
    }
    
    /**
     * Add hours from another report beam item's summary data
     *
     * @param bri
     */
    public void addSummaryHours(BeamReportItem bri) {
        if (bri == null) {
            return;
        }
        Map<String, ReportItem> otherSumData = bri.getSummaryData();
        if (otherSumData == null) {
            return;
        }
        summaryTotal += bri.summaryTotal;
        totalPercentage += bri.totalPercentage;
        
        for (String sum : otherSumData.keySet()) {
            ReportItem rep = otherSumData.get(sum);
            if (rep != null && summaryData.get(sum) != null) {
                summaryData.get(sum).addHours(rep.getHours());              
            }
        }
    }
    
    /**
     * Calculate total summary hours and percentages for each summary
     * 
     * @param totalHours Total against which the percentage of this items is to be calculated
     */
    public void computeTotals(Double totalHours) {
        summaryTotal = 0.0;
        for (ReportItem item : summaryData.values()) {
            summaryTotal += item.getHours();
        }
        for (ReportItem item : summaryData.values()) {
            item.calculatePercent(summaryTotal);
        }
        
        BigDecimal ntotal = new BigDecimal(totalHours);
        if (ntotal.compareTo(BigDecimal.ZERO) != 0) {
            totalPercentage = summaryTotal / Math.abs(totalHours) * 100.0;
        }     
    }
    
    // ---------------------------------

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public ReportBeam getBeam() {
        return beam;
    }

    public Map<String, ReportItem> getSummaryData() {
        return summaryData;
    }

    public double getSummaryTotal() {
        return summaryTotal;
    }

    public double getTotalPercentage() {
        return totalPercentage;
    }
    
    
}
