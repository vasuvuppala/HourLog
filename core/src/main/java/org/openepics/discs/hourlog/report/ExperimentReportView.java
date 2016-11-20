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
import org.openepics.discs.hourlog.ejb.FacilityEJB;
import org.openepics.discs.hourlog.ent.Experiment;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * State for experiment report view
 *
 * TODO" Rewrite from scratch. really bad code.
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class ExperimentReportView implements Serializable {

    @EJB
    private EventEJB eventEJB;
    @EJB
    private FacilityEJB facilityEJB;
    @Inject
    private ReportDataGenerator reportDataGenerator;
    
    private static final Logger logger = Logger.getLogger(ExperimentReportView.class.getName());

    private PieChartModel pieModel;
    private BarChartModel barModel;
    private List<Facility> facilities;
    // private ExperimentReportItem dataTotals; // total of summary and breakdown data
    private Map<Integer, ExperimentReportItem> experimentData = new HashMap<>();
    private List<ExperimentReportItem> experimentReportItems;
    private ExperimentReportItem experimentReport;
    private Date reportDate;

    private Facility selectedFacility;
    private Experiment selectedExperiment;
    private Date startDate;
    private Date endDate;
    private Date minDate;
    private Date maxDate;

    private Date reportStartDate;
    private Date reportEndDate;

    /**
     * Creates a new instance of ReportManager
     */
    public ExperimentReportView() {
    }

    @PostConstruct
    public void init() {
        facilities = facilityEJB.findFacility();
        selectedFacility = facilities.get(0); // TODO: Temporary. Experiment report does not need facility. Once fixed, remove
        minDate = eventEJB.firstEventDate();
        maxDate = Utility.currentDate();
    }

    private void clearAll() {
        experimentData.clear();
    }

    private void resetInput() {
        startDate = null;
        endDate = null;
    }

    /**
     * generates experiment report
     *
     */
//    public void genExperimentReport() {
//
//        try {
//            reportView.setReportType("experiment");
//            logger.log(Level.FINE, "generate report. for expr {0}", selectedExperiment.getNumber());
//            clearAll();
//
//            reportStartDate = startDate == null ? eventEJB.findExperimentBeginDate(selectedFacility, selectedExperiment) : startDate;
//
//            if (reportStartDate == null) {
//                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "No data was found for this experiment", "Error");
//                logger.log(Level.WARNING, "genExperimentReport: No data was found for this experiment");
//                return;
//            }
//
//            reportEndDate = endDate == null ? eventEJB.findExperimentEndDate(selectedFacility, selectedExperiment) : endDate;
//
//            if (reportEndDate.before(reportStartDate)) { // swap start and end dates, if necessary
//                Date tempDate = reportEndDate;
//                reportEndDate = reportStartDate;
//                reportStartDate = tempDate;
//            }
//            logger.log(Level.FINE, "genExperimentReport: start date {0}", reportStartDate);
//            logger.log(Level.FINE, "genExperimentReport: end  date {0}", reportEndDate);
//
//            reportDataGenerator.experimentReport(selectedFacility, selectedExperiment, reportStartDate, reportEndDate, experimentData);
//            experimentReportItems = new ArrayList<>(experimentData.values());
//            experimentReport = experimentData.get(selectedExperiment.getNumber());
//            if (experimentReport == null || experimentReport.getSummaryData() == null || experimentReport.getBreakdownData() == null) {
//                logger.log(Level.WARNING, "genExperimentReport: experiment report data is empty!");
//                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "No data was found for this experiment", "No report will be generated");
//                return;
//            }
//            // reportItemList = new ArrayList<>(dataTotals.getSummaryData().values());
//            createPieModel();
//            createBarModel();
//            reportDate = Utility.currentDate();
////            resetInput();
//
//        } catch (Exception e) {
//            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error while generating report", "Error");
//            logger.severe("Severe error while generating report.");
//            System.err.println(e);
//        }
//    }

    /**
     * generate experiment report
     * 
     */
    public void generateReport() {
        try {         
            logger.log(Level.FINE, "generate report. for expr {0}", selectedExperiment.getNumber());
            if (!validInput()) {
                return;
            }
            clearAll();
           
            logger.log(Level.FINE, "genExperimentReport: start date {0}", reportStartDate);
            logger.log(Level.FINE, "genExperimentReport: end  date {0}", reportEndDate);

            reportDataGenerator.experimentReport(selectedFacility, selectedExperiment, reportStartDate, reportEndDate, experimentData);
            experimentReportItems = new ArrayList<>(experimentData.values());
            experimentReport = experimentData.get(selectedExperiment.getNumber());
            if (experimentReport == null || experimentReport.getSummaryData() == null || experimentReport.getBreakdownData() == null) {
                logger.log(Level.WARNING, "genExperimentReport: experiment report data is empty!");
                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "No data was found for this experiment", "No report will be generated");
                return;
            }
            // reportItemList = new ArrayList<>(dataTotals.getSummaryData().values());
            createPieModel();
            createBarModel();
            reportDate = Utility.currentDate();
//            resetInput();

        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error while generating report", "Error");
            logger.severe("Severe error while generating report.");
            System.err.println(e);
        }
    }
    
    /**
     * is input valid?
     * 
     * @return 
     */
    private boolean validInput() {
        reportStartDate = startDate == null ? eventEJB.findExperimentBeginDate(selectedFacility, selectedExperiment) : startDate;

        if (reportStartDate == null) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "No data was found for this experiment", "Error");
            logger.log(Level.WARNING, "genExperimentReport: No data was found for this experiment");
            return false;
        }

        reportEndDate = endDate == null ? eventEJB.findExperimentEndDate(selectedFacility, selectedExperiment) : endDate;

        if (reportEndDate.before(reportStartDate)) { // swap start and end dates, if necessary
            Date tempDate = reportEndDate;
            reportEndDate = reportStartDate;
            reportStartDate = tempDate;
        }
        
        return true;
    }
    /**
     * pie chart model
     *
     */
    private void createPieModel() {
        pieModel = new PieChartModel();

        for (ReportItem ri : getSummaryList(experimentReport)) {
            logger.log(Level.FINE, "adding {0} to pie model", ri.getName());
            pieModel.set(ri.getName(), ri.getHours());
        }
        logger.log(Level.FINE,"Done with making pie model");
        pieModel.setTitle("Summary");
        pieModel.setLegendPosition("e");
        pieModel.setShowDataLabels(true);
        pieModel.setSliceMargin(3);
        logger.log(Level.FINE,"Done with setting pie model");
    }

    /**
     * bar chart model
     *
     */
    private void createBarModel() {
        barModel = new BarChartModel();

        for (ReportItem ri : getBreakList(experimentReport)) {
            ChartSeries hourSeries = new ChartSeries();
            hourSeries.setLabel(ri.getName());
            hourSeries.set("Category", ri.getHours());
            barModel.addSeries(hourSeries);
        }
        barModel.setTitle("Breakdowns");
        barModel.setLegendPosition("n");
        barModel.setShowDatatip(true);
        barModel.setShowPointLabels(true);
        barModel.setMouseoverHighlight(true);
        barModel.setLegendRows(2);
    }

    // -- getters and setters
    public ExperimentReportItem getExperimentReport() {
        return experimentReport;
    }

    public List<ReportItem> getSummaryList(ExperimentReportItem eri) {
        List<ReportItem> repitems = new ArrayList<>(eri.getSummaryData().values());
        return repitems;
    }

    public List<ReportItem> getBreakList(ExperimentReportItem eri) {
        List<ReportItem> repitems = new ArrayList<>(eri.getBreakdownData().values());
        return repitems;
    }

    public PieChartModel getPieModel() {
        return pieModel;
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

    public BarChartModel getBarModel() {
        return barModel;
    }

    public Facility getSelectedFacility() {
        return selectedFacility;
    }

    public void setSelectedFacility(Facility selectedFacility) {
        this.selectedFacility = selectedFacility;
    }

    public Experiment getSelectedExperiment() {
        return selectedExperiment;
    }

    public void setSelectedExperiment(Experiment selectedExperiment) {
        this.selectedExperiment = selectedExperiment;
    }

    public Map<Integer, ExperimentReportItem> getExperimentData() {
        return experimentData;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public List<ExperimentReportItem> getExperimentReportItems() {
        return experimentReportItems;
    }

    public List<Facility> getFacilities() {
        return facilities;
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

}
