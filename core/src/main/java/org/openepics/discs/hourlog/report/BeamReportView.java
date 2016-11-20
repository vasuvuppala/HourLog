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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.hourlog.ejb.EventEJB;
import org.openepics.discs.hourlog.ejb.HourLogEJB;
import org.openepics.discs.hourlog.ent.BeamSystem;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Summary;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * State for beam report view
 *
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class BeamReportView implements Serializable {

    @EJB
    private HourLogEJB hourLogEJB;
    @EJB
    private EventEJB eventEJB;
    @Inject
    private ReportDataGenerator reportDataGenerator;  

    private static final Logger logger = Logger.getLogger(BeamReportView.class.getName());

    private PieChartModel beamChart;
    private PieChartModel summaryChart;
    //private BarChartModel barModel;

    private List<BeamReportItem> beamSequences;
    private List<BeamReportItem> beamReportItems;
    private BeamReportItem beamTotals;
    private BeamReportItem beamSequenceTotals;
    private Map<ReportBeam, BeamReportItem> beamReport;
    private Date reportDate;
    private BeamSystem selectedBeamSystem;
    private Facility selectedFacility;
    private List<Summary> summaries;
    private Date startDate;
    private Date endDate;
    private Date minDate;
    private Date maxDate;
    boolean includeNone = false; // include 'None' beam in beam report?

    private Date reportStartDate;
    private Date reportEndDate;

    /**
     * Creates a new instance of ReportManager
     */
    public BeamReportView() {
    }

    @PostConstruct
    public void init() {
        summaries = hourLogEJB.findAllSummary();
    }

    private void clearAll() {

    }

    /**
     * generates beam report
     *
     */
//    public void genBeamReport() {       
//        try {
//            selectedFacility = reportView.getSelectedFacility();
//            selectedBeamSystem = reportView.getSelectedBeamSystem();
//            includeNone = reportView.isIncludeNoneBeam(); // include 'None' beam in beam report?
//            if (startDate == null) {
//                startDate = reportView.getMinDate();
//            }
//            if (endDate == null) {
//                endDate = reportView.getMaxDate();
//            }
//            if (selectedFacility == null) {
//                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Could not access selected facility", "Error");
//                logger.log(Level.SEVERE, "Facility from reportView is null!!");
//                return;
//            }
//            if (selectedBeamSystem == null) {
//                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Could not access selected beam system", "Error");
//                logger.log(Level.SEVERE, "Beam system from reportView is null!!");
//                return;
//            }
//            reportView.setReportType("beam");
//            // logger.log(Level.FINE, "generate report. for expr {0}", selectedExperiment.getNumber());
//            clearAll();
//
//            reportStartDate = startDate;
//
//            if (reportStartDate == null) {
//                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "No data was found", "Error");
//                logger.log(Level.WARNING, "No data was foundt");
//                return;
//            }
//
//            reportEndDate = endDate == null ? Utility.currentDate() : endDate;
//
//            if (reportEndDate.before(reportStartDate)) { // swap start and end dates, if necessary
//                Date tempDate = reportEndDate;
//                reportEndDate = reportStartDate;
//                reportStartDate = tempDate;
//            }
//            logger.log(Level.FINE, "start date {0}", reportStartDate);
//            logger.log(Level.FINE, "end  date {0}", reportEndDate);
//
//            beamSequences = reportDataGenerator.beamReport(selectedFacility, selectedBeamSystem, reportStartDate, reportEndDate);
//            if (beamSequences == null || beamSequences.isEmpty()) {
//                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "No data found", "For the beam system in the given period");
//                logger.log(Level.WARNING, "No data found for this beam sytem in the given period.");
//                return;
//            }
//            beamSequenceTotals = BeamReportItem.newInstance(startDate, endDate, null, summaries);
//            for (BeamReportItem bri : beamSequences) {             
//                beamSequenceTotals.addSummaryHours(bri);
//            }
//            beamSequenceTotals.computeTotals(beamSequenceTotals.getSummaryTotal());
//            calculateBeamReport(includeNone);
//            reportDate = Utility.currentDate();
//        } catch (Exception e) {
//            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error while generating report", "Error");
//            logger.log(Level.SEVERE, "Severe error while generating report.", e);
//        }
//    }

    /**
     * ToDO: Temporary. Testing a new way
     * 
     */
    public void generateReport() {       
        try {                      
            
            if (! validInput()) {
                return;
            }
            
            logger.log(Level.FINE, "start date {0}", reportStartDate);
            logger.log(Level.FINE, "end  date {0}", reportEndDate);

            beamSequences = reportDataGenerator.beamReport(selectedFacility, selectedBeamSystem, reportStartDate, reportEndDate);
            if (beamSequences == null || beamSequences.isEmpty()) {
                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "No data found", "For the beam system in the given period");
                logger.log(Level.WARNING, "No data found for this beam sytem in the given period.");
                return;
            }
            beamSequenceTotals = BeamReportItem.newInstance(startDate, endDate, null, summaries);
            for (BeamReportItem bri : beamSequences) {             
                beamSequenceTotals.addSummaryHours(bri);
            }
            beamSequenceTotals.computeTotals(beamSequenceTotals.getSummaryTotal());
            calculateBeamReport(includeNone);
            reportDate = Utility.currentDate();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error while generating report", "Error");
            logger.log(Level.SEVERE, "Severe error while generating report.", e);
        }
    }

    /**
     * Is input valid?
     * 
     * @return 
     */
    private boolean validInput() {      
        if (selectedFacility == null) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Invalid facility", "Error");
            logger.log(Level.SEVERE, "Facility is null!!");
            return false;
        }
        if (selectedBeamSystem == null) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Invalid beam system", "Error");
            logger.log(Level.SEVERE, "Beam system is null!!");
            return false;
        }

        reportStartDate = startDate == null? eventEJB.firstEventDate(selectedFacility): startDate;
        if (reportStartDate == null) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "StartDate is null", "Error");
            logger.log(Level.WARNING, "Could not determine start date; it is null");
            return false;
        }
        reportEndDate = endDate == null ? Utility.currentDate() : endDate;
        if (reportEndDate.before(reportStartDate)) { // swap start and end dates, if necessary
            Date tempDate = reportEndDate;
            reportEndDate = reportStartDate;
            reportStartDate = tempDate;
        }

        return true;
    }
    
    /**
     * Calculate totals for each beam and summary
     *
     */
    private void calculateBeamReport(boolean includeNone) {
        beamReport = new HashMap<>();
        beamTotals = BeamReportItem.newInstance(startDate, endDate, null, summaries);
        ReportBeam tempBeam;

        if (beamSequences == null || beamSequences.isEmpty()) {
            return;
        }

        for (BeamReportItem bri : beamSequences) {
            tempBeam = bri.getBeam();
            if (tempBeam.getElement() == null && includeNone == false) continue; // skip 'None' beam 
            // if (!beamReport.containsKey(tempBeam)) {               
            if (beamReport.get(tempBeam) == null) {               
                beamReport.put(tempBeam, BeamReportItem.newInstance(bri.getStartTime(), bri.getEndTime(), tempBeam, summaries));
            }           
            beamReport.get(tempBeam).addSummaryHours(bri);
            beamTotals.addSummaryHours(bri);
        }
        beamReportItems = new ArrayList<>(beamReport.values());
        for (BeamReportItem bri : beamReportItems) {
            bri.computeTotals(beamTotals.getSummaryTotal());
        }
        beamTotals.computeTotals(beamTotals.getSummaryTotal());
        createBeamChart();
        createSummaryChart();
    }

    /**
     * pie chart for beams
     *
     */
    private void createBeamChart() {
        beamChart = new PieChartModel();

        String name;
        for (BeamReportItem bri : beamReport.values()) {
            // logger.log(Level.FINE, "adding {0} to pie model", ri.getName());
            ReportBeam beam = bri.getBeam();
            if (beam.getElement() == null) {
                name = "None";
            } else { 
                name = emptyString(beam.getMassNumber()) + " " + emptyString(beam.getElement().getSymbol()) 
                       + " " + emptyString(beam.getCharge()) + " " + emptyString(beam.getEnergy());
            }
            beamChart.set(name, bri.getTotalPercentage());
        }
        logger.log(Level.FINE, "Done with making beam chart");
        beamChart.setTitle("Beam Hours");
        beamChart.setLegendPosition("e");
        beamChart.setShowDataLabels(true);
        beamChart.setSliceMargin(2);
        beamChart.setLegendCols(5);
        logger.log(Level.FINE, "Done with beam chart");
    }

    /**
     * Convert null strings to empty ones.
     * 
     * @param string
     * @return 
     */
    private String emptyString(Object object) {
         return object == null? "" : object.toString();
    }
    /**
     * pie chart for beams
     *
     */
    private void createSummaryChart() {
        summaryChart = new PieChartModel();

        if (beamTotals == null || beamTotals.getSummaryData() == null) {
            logger.log(Level.WARNING, "beam totals have not been calculated");
            return;
        }
        for (String sum : beamTotals.getSummaryData().keySet()) {
            if (beamTotals.getSummaryData().get(sum) != null) {
                summaryChart.set(sum, beamTotals.getSummaryData().get(sum).getPercentage());
            }
        }
        logger.log(Level.FINE, "Done with making summary chart");
        summaryChart.setTitle("Summary Hours");
        summaryChart.setLegendPosition("e");
        summaryChart.setShowDataLabels(true);
        summaryChart.setSliceMargin(2);
        // summaryChart.setLegendCols(3);
        logger.log(Level.FINE, "Done with summary chart");
    }

    /**
     * bar chart model
     *
     */
//    private void createBarModel() {
//        barModel = new BarChartModel();
//        for (ReportItem ri : getBreakList(experimentReport)) {
//            ChartSeries hourSeries = new ChartSeries();
//            hourSeries.setLabel(ri.getName());
//            hourSeries.set("Category", ri.getHours());
//            barModel.addSeries(hourSeries);
//        }
//        barModel.setTitle("Breakdowns");
//        barModel.setLegendPosition("n");
//        barModel.setShowDatatip(true);
//        barModel.setShowPointLabels(true);
//        barModel.setMouseoverHighlight(true);
//        barModel.setLegendRows(2);
//    }
    // -- getters and setters
    
    public List<BeamReportItem> getBeamSequences() {
        return beamSequences;
    }

    public PieChartModel getBeamChart() {
        return beamChart;
    }

    public PieChartModel getSummaryChart() {
        return summaryChart;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Facility getSelectedFacility() {
        return selectedFacility;
    }

    public void setSelectedFacility(Facility selectedFacility) {
        this.selectedFacility = selectedFacility;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public Date getReportStartDate() {
        return reportStartDate;
    }

    public Date getReportEndDate() {
        return reportEndDate;
    }

    public List<Summary> getSummaries() {
        return summaries;
    }

    public Map<ReportBeam, BeamReportItem> getBeamReport() {
        return beamReport;
    }

    public List<BeamReportItem> getBeamReportItems() {
        return beamReportItems;
    }

    public BeamReportItem getBeamTotals() {
        return beamTotals;
    }

    public BeamReportItem getBeamSequenceTotals() {
        return beamSequenceTotals;
    }

    public BeamSystem getSelectedBeamSystem() {
        return selectedBeamSystem;
    }

    public void setSelectedBeamSystem(BeamSystem selectedBeamSystem) {
        this.selectedBeamSystem = selectedBeamSystem;
    }

    public boolean isIncludeNone() {
        return includeNone;
    }

    public void setIncludeNone(boolean includeNone) {
        this.includeNone = includeNone;
    }
    
}
