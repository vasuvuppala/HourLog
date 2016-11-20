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
import java.util.Date;
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
import org.openepics.discs.hourlog.ejb.HourLogEJB;
import org.openepics.discs.hourlog.ent.BreakdownCategory;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.model.TreeNode;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

/**
 * State for breakdown report view
 *
 * TODO" Rewrite from scratch. really bad code.
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class BreakdownReportView implements Serializable {
    @EJB
    private FacilityEJB facilityEJB;
    @EJB
    private EventEJB eventEJB;
    @EJB
    private HourLogEJB hourLogEJB;
    @Inject
    private ReportDataGenerator reportDataGenerator;   

    private static final Logger logger = Logger.getLogger(BreakdownReportView.class.getName());

    private List<ReportItem> reportItemList;
    private Map<String, ReportItem> reportHash;
    private TreeNode root;
    private PieChartModel pieModel;
    private BarChartModel barModel;
    private List<Facility> facilities;
    private int minYear;
    private int maxYear;
    private List<BreakdownCategory> selectedCategories;
    private List<BreakdownCategory> categories;

    private Facility selectedFacility;
    private Date startDate;
    private Date endDate;
    private Date minDate;
    private Date maxDate;

    private Date reportStartDate;
    private Date reportEndDate;
    private Date reportDate;

    
    public BreakdownReportView() {
    }

    @PostConstruct
    public void init() {
        facilities = facilityEJB.findFacility();
      
        minDate = eventEJB.firstEventDate();
        maxDate = Utility.currentDate();
        categories = hourLogEJB.findBrkCategories();
        selectedCategories = new ArrayList<>(categories);
    }

    /**
     * Generates report
     *
     */
//    public void genReport() {
//
//        try {
//            reportView.setReportType("breakdown");
//            logger.log(Level.FINE, "Generate report. start: ");
//            // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
//            reportHash = reportDataGenerator.breakdownHours(selectedFacility, reportStartDate, reportEndDate);
//            reportItemList = new ArrayList<>(reportHash.values());
//
//            calculatePercents();
//
//            createPieModel();
//            createBarModel();
//            reportDate = Utility.currentDate();
//        } catch (Exception e) {
//            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error during report generation", " ");
//            logger.severe("Severe error while setting up tree");
//            System.err.println(e);
//        }
//    }

    /**
     * Generate the breakdown report
     * 
     */
    public void generateReport() {
        try {
            logger.log(Level.FINE, "Generate report. start: ");

            if (!validInput()) {
                logger.log(Level.WARNING, "Invalid input");
                return;
            }

            reportHash = reportDataGenerator.breakdownHours(selectedFacility, reportStartDate, reportEndDate);
            reportItemList = new ArrayList<>(reportHash.values());

            calculatePercents();

            createPieModel();
            createBarModel();
            reportDate = Utility.currentDate();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error during report generation", " ");
            logger.log(Level.SEVERE, "Severe error while setting up tree", e);
        }
    }

    /**
     * is input valid?
     * 
     * @return
     */
    private boolean validInput() {
        Date currentDate = Utility.currentDate();

        if (selectedFacility == null) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Invalid facility", "Error");
            logger.log(Level.SEVERE, "Facility is null!!");
            return false;
        }
        reportStartDate = startDate == null ? eventEJB.firstEventDate(selectedFacility) : startDate;
        if (reportStartDate == null) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "StartDate is null", "Error");
            logger.log(Level.WARNING, "Could not determine start date; it is null");
            return false;
        }
        if (reportStartDate.after(currentDate)) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Start date is in the future", " ");
            return false;
        }

        reportEndDate = endDate == null ? Utility.currentDate() : endDate;
        if (reportEndDate.before(reportStartDate)) { // swap start and end dates, if necessary
            Date tempDate = reportEndDate;
            reportEndDate = reportStartDate;
            reportStartDate = tempDate;
        }
        if (reportEndDate.after(currentDate)) {
            reportEndDate = currentDate;
        }

        return true;
    }

    /**
     * calculates percents in the report
     *
     */
    public void calculatePercents() {
        double total = 0.0;

        for (ReportItem ri : reportItemList) {
            total += ri.getHours();
        }

        for (ReportItem ri : reportItemList) {
            ri.calculatePercent(total);
        }
    }

    /**
     * pie model
     *
     */
    private void createPieModel() {
        pieModel = new PieChartModel();

        for (ReportItem ri : reportItemList) {
            pieModel.set(ri.getName(), ri.getHours());
        }
        pieModel.setTitle("Percentage");
        pieModel.setLegendPosition("e");
        pieModel.setShowDataLabels(true);
        pieModel.setFill(false);
        pieModel.setSliceMargin(2);
    }

    /**
     * Generate bar chart model
     */
    private void createBarModel() {
        barModel = new BarChartModel();

        for (ReportItem ri : reportItemList) {
            ChartSeries hourSeries = new ChartSeries();
            hourSeries.setLabel(ri.getName());
            hourSeries.set("Category", ri.getHours());
            barModel.addSeries(hourSeries);
        }
        barModel.setTitle("Hours");
        barModel.setLegendPosition("n");
        barModel.setShowDatatip(true);
        barModel.setShowPointLabels(true);
        barModel.setMouseoverHighlight(true);
        barModel.setLegendRows(2);
    }

    //- getters/setters
    

    public List<ReportItem> getReportItemList() {
        return reportItemList;
    }

    public TreeNode getRoot() {
        return root;
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

    public BarChartModel getBarModel() {
        return barModel;
    }

    public int getMinYear() {
        return minYear;
    }

    public int getMaxYear() {
        return maxYear;
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

    public Date getReportDate() {
        return reportDate;
    }

    public List<BreakdownCategory> getCategories() {
        return categories;
    }

    public List<BreakdownCategory> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(List<BreakdownCategory> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

}
