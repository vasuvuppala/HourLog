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
import java.util.ArrayList;
import java.util.Collections;
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
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

/**
 * State for summary report view
 *
 * TODO" Rewrite from scratch. really bad code.
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class SummaryReportView implements Serializable {

    @EJB
    private FacilityEJB facilityEJB;
    @EJB
    private EventEJB eventEJB;
    @Inject
    private ReportDataGenerator reportDataGenerator;
    @Inject
    private ReportView reportView; //TODO Temporary till everything is conslidate

    private static final Logger logger = Logger.getLogger(SummaryReportView.class.getName());

    private List<ReportItem> reportItemList;

    private PieChartModel pieModel;
    private BarChartModel barModel;
    private List<Facility> facilities;
    private int minYear;
    private int maxYear;
    private Map<Integer, ExperimentReportItem> experimentData = new HashMap<>(); // summary and breakdown data per experiment
    private ExperimentReportItem dataTotals; // total of summary and breakdown data
    private List<ExperimentReportItem> experimentReportItems;
    private Date reportDate; // time reported was generated
    //
    private Facility selectedFacility;
    private Date startDate;
    private Date endDate;
    private Date minDate;
    private Date maxDate;

    private Date reportStartDate;
    private Date reportEndDate;
    
    public SummaryReportView() {
    }

    @PostConstruct
    public void init() {
        facilities = facilityEJB.findFacility();
        if (facilities.isEmpty()) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "No facilities found !", "Empty database?");
            logger.log(Level.SEVERE, "No facilities found in the database!");
            return;
        } else {
            selectedFacility = facilities.get(0);
        }
        minDate = eventEJB.firstEventDate();
        maxDate = Utility.currentDate();
    }

    /**
     * Generates report
     *
     */
//    public void genReport() {
//
//        try {
//            reportView.setReportType("status");
//            logger.log(Level.FINE, "generate report. start: ");
//            LocalDate tempdate;
//            Date currentDate = Utility.currentDate();
//
//            switch (selectedRepType) {
//                case 1: // weekly
//                    tempdate = new LocalDate().withWeekyear(selectedYear).withWeekOfWeekyear(selectedWeek).dayOfWeek().withMinimumValue();
//                    reportStartDate = tempdate.toDateTimeAtStartOfDay().toDate();
//                    tempdate = tempdate.dayOfWeek().withMaximumValue().plusDays(1);
//                    reportEndDate = tempdate.toDateTimeAtStartOfDay().toDate();
//                    break;
//                case 2:
//                    tempdate = new LocalDate().withYear(selectedYear).withMonthOfYear(selectedQuarter * 3 + 1).withDayOfMonth(1);
//                    reportStartDate = tempdate.toDateTimeAtStartOfDay().toDate();
//                    tempdate = new LocalDate().withYear(selectedYear).withMonthOfYear(selectedQuarter * 3 + 3).dayOfMonth().withMaximumValue().plusDays(1);
//                    reportEndDate = tempdate.toDateTimeAtStartOfDay().toDate();
//                    break;
//                case 3:
//                    tempdate = new LocalDate().withYear(selectedYear).withMonthOfYear(selectedMonth).dayOfMonth().withMinimumValue();
//                    reportStartDate = tempdate.toDateTimeAtStartOfDay().toDate();
//                    tempdate = tempdate.dayOfMonth().withMaximumValue().plusDays(1);
//                    reportEndDate = tempdate.toDateTimeAtStartOfDay().toDate();
//                    break;
//                case 4:
//                    tempdate = new LocalDate().withYear(selectedYear).monthOfYear().withMinimumValue().dayOfMonth().withMinimumValue();
//                    reportStartDate = tempdate.toDateTimeAtStartOfDay().toDate();
//                    tempdate = tempdate.monthOfYear().withMaximumValue().dayOfMonth().withMaximumValue().plusDays(1);
//                    reportEndDate = tempdate.toDateTimeAtStartOfDay().toDate();
//                    break;
//                case 5:
//                    reportStartDate = startDate == null ? currentDate : startDate;
//                    reportEndDate = endDate == null ? currentDate : endDate;
//                    if (reportEndDate.before(reportStartDate)) {
//                        Date tempDate = reportEndDate;
//                        reportEndDate = reportStartDate;
//                        reportStartDate = tempDate;
//                    }
//                    break;
//                default:
//                    logger.log(Level.SEVERE, "Invalid report type value {0}", selectedRepType);
//                    break;
//            }
//            if (reportStartDate.after(currentDate)) {
//                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Start date is in the future", " ");
//                return;
//            }
//            if (reportEndDate.after(currentDate)) {
//                reportEndDate = currentDate;
//            }
//
//            logger.log(Level.FINE, "genExperimentReport: start date {0}", reportStartDate);
//            logger.log(Level.FINE, "genExperimentReport: end  date {0}", reportEndDate);
//
//            experimentData.clear();
//            dataTotals = reportDataGenerator.experimentReport(selectedFacility, null, reportStartDate, reportEndDate, experimentData);
//            experimentReportItems = new ArrayList<>(experimentData.values());
//            Collections.sort(experimentReportItems);
//            reportItemList = new ArrayList<>(dataTotals.getSummaryData().values());
//            createPieModel();
//            createBarModel();
//            reportDate = Utility.currentDate();
//        } catch (Exception e) {
//            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error during report generation", " ");
//            logger.severe("Severe error while generating report.");
//            System.err.println(e);
//        }
//    }

    /**
     * generate summary report
     * 
     */
    public void generateReport() {

        try {
            logger.log(Level.FINE, "generate report. start: ");

            if (!validInput()) {
                return;
            }

            logger.log(Level.FINE, "genSummaryReport: start date {0}", reportStartDate);
            logger.log(Level.FINE, "genSummaryReport: end  date {0}", reportEndDate);

            experimentData.clear();
            dataTotals = reportDataGenerator.experimentReport(selectedFacility, null, reportStartDate, reportEndDate, experimentData);
            experimentReportItems = new ArrayList<>(experimentData.values());
            if (dataTotals == null || dataTotals.getSummaryData() == null || dataTotals.getBreakdownData() == null) {
                logger.log(Level.WARNING, "No data found!");
                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "No data was found for this period", "No report will be generated");
                return;
            }
            Collections.sort(experimentReportItems);
            reportItemList = new ArrayList<>(dataTotals.getSummaryData().values());
            createPieModel();
            createBarModel();
            reportDate = Utility.currentDate();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error during report generation", " ");
            logger.severe("Severe error while generating report.");
            System.err.println(e);
        }
    }

    /**
     * Is the input valid?
     * 
     * @return 
     */
    private boolean validInput() {
        
        if (selectedFacility == null) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Invalid facility", "Error");
            logger.log(Level.SEVERE, "Facility is null!!");
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
     * calculates summary for the given report
     *
     * @param eri the report
     * @return
     */
    public List<ReportItem> getSummaryList(ExperimentReportItem eri) {
        List<ReportItem> repitems = new ArrayList<>(eri.getSummaryData().values());
        return repitems;
    }

    /**
     * Get breakdown list from given report
     *
     * @param eri the report
     * @return report items
     */
    public List<ReportItem> getBreakList(ExperimentReportItem eri) {
        List<ReportItem> repitems = new ArrayList<>(eri.getBreakdownData().values());
        return repitems;
    }

    /**
     * pie chart
     */
    private void createPieModel() {
        pieModel = new PieChartModel();

        for (ReportItem ri : reportItemList) {
            pieModel.set(ri.getName(), ri.getHours());
        }
        pieModel.setTitle("Summary");
        pieModel.setLegendPosition("e");
        pieModel.setShowDataLabels(true);
        pieModel.setSliceMargin(3);
    }

    /**
     * bar chart
     */
    private void createBarModel() {
        barModel = new BarChartModel();

        for (ReportItem ri : getBreakList(dataTotals)) {
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

    // ----------- getters/setters

    public List<ReportItem> getReportItemList() {
        return reportItemList;
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

    public Facility getInFacility() {
        return selectedFacility;
    }

    public void setInFacility(Facility inFacility) {
        this.selectedFacility = inFacility;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public Facility getSelectedFacility() {
        return selectedFacility;
    }

    public void setSelectedFacility(Facility selectedFacility) {
        this.selectedFacility = selectedFacility;
    }

    public ExperimentReportItem getDataTotals() {
        return dataTotals;
    }

    public Map<Integer, ExperimentReportItem> getExperimentData() {
        return experimentData;
    }

    public List<ExperimentReportItem> getExperimentReportItems() {
        return experimentReportItems;
    }

    public BarChartModel getBarModel() {
        return barModel;
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

}
