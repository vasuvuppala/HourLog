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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
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
import org.joda.time.LocalDate;
import org.openepics.discs.hourlog.auth.AuthManager;
import org.openepics.discs.hourlog.ejb.EventEJB;
import org.openepics.discs.hourlog.ejb.FacilityEJB;
import org.openepics.discs.hourlog.ejb.HourLogEJB;
import org.openepics.discs.hourlog.ent.BeamSystem;
import org.openepics.discs.hourlog.ent.BreakdownCategory;
import org.openepics.discs.hourlog.ent.Experiment;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.util.Utility;

/**
 * State for reports related views
 *
 * TODO: Consolidate all report views into this one.
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class ReportView implements Serializable {

    @EJB
    private HourLogEJB hourLogEJB;
    @EJB
    private EventEJB eventEJB;
    @EJB
    private FacilityEJB facilityEJB;

    @EJB(beanName = "LocalAuthManager")
    private AuthManager authManager;

    @Inject
    private ReportDataGenerator reportDataGenerator;

    private static final Logger logger = Logger.getLogger(ReportView.class.getName());

    private List<Facility> facilities;
    private List<BeamSystem> beamSystems;
    private BeamSystem selectedBeamSystem;
    private Date minDate;
    private Date maxDate;

    private Facility selectedFacility;
    private Date selectedStartDate;
    private Date selectedEndDate;
    private Experiment selectedExperiment;
    private int selectedWeek;
    private int selectedQuarter;
    private int selectedYear;
    private int selectedMonth;
    private int selectedRepType;
    private boolean includeNoneBeam = false;
    private String reportType;

    private List<BreakdownCategory> selectedCategories;
    private List<BreakdownCategory> categories;
    
    private Date reportStartDate;
    private Date reportEndDate;

    private static final Map<String, Integer> months;

    static {
        months = new LinkedHashMap<>();
        months.put("January", 1);
        months.put("February", 2);
        months.put("March", 3);
        months.put("April", 4);
        months.put("May", 5);
        months.put("June", 6);
        months.put("July", 7);
        months.put("August", 8);
        months.put("September", 9);
        months.put("October", 10);
        months.put("November", 11);
        months.put("December", 12);
    }

    private static final Map<String, Integer> reports;

    static {
        reports = new LinkedHashMap<>();
        reports.put("Weekly", 1);
        reports.put("Quarterly", 2);
        reports.put("Monthly", 3);
        reports.put("Yearly", 4);
        reports.put("Period", 5);
    }

    private static final Map<String, Integer> weeks;

    static {
        weeks = new LinkedHashMap<>();
        for (int i = 1; i <= 52; i++) {
            weeks.put(Integer.toString(i), i);
        }
    }

    private static final Map<String, Integer> quarters;

    static {
        quarters = new LinkedHashMap<>();
        quarters.put("First", 0);
        quarters.put("Second", 1);
        quarters.put("Third", 2);
        quarters.put("Fourth", 3);
    }

    private static final Map<String, Integer> years = new LinkedHashMap<>();

    /**
     * Creates a new instance of ReportManager
     */
    public ReportView() {
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
            changeFacility();
        }
        selectedRepType = reports.get("Weekly");
        categories = hourLogEJB.findAllBrkCategories();
        selectedCategories = new ArrayList<>(categories);
    }

    /**
     * Calculates min/max dates and year
     */
    public void changeFacility() {
        Calendar calendar = Calendar.getInstance();
        minDate = eventEJB.firstEventDate(selectedFacility);
        if (minDate == null) {
            logger.warning("Min or max year is 0. No data in the db?");
        } else {
            calendar.setTime(minDate);
            int minYear = calendar.get(Calendar.YEAR);
            calendar.setTime(eventEJB.lastEventDate());
            int maxYear = calendar.get(Calendar.YEAR);
            logger.log(Level.FINE, "min year " + minYear + "Max " + maxYear);
            for (int i = maxYear; i >= minYear; i--) {
                years.put(Integer.toString(i), i);
            }
        }
        maxDate = Utility.currentDate();
        beamSystems = hourLogEJB.findBeamSystems(selectedFacility);
    }

    private void computeDates() {
        Date currentDate = Utility.currentDate();
        LocalDate tempdate;
        switch (selectedRepType) {
            case 1: // weekly
                tempdate = new LocalDate().withWeekyear(selectedYear).withWeekOfWeekyear(selectedWeek).dayOfWeek().withMinimumValue();
                reportStartDate = tempdate.toDateTimeAtStartOfDay().toDate();
                tempdate = tempdate.dayOfWeek().withMaximumValue().plusDays(1);
                reportEndDate = tempdate.toDateTimeAtStartOfDay().toDate();
                break;
            case 2:
                tempdate = new LocalDate().withYear(selectedYear).withMonthOfYear(selectedQuarter * 3 + 1).withDayOfMonth(1);
                reportStartDate = tempdate.toDateTimeAtStartOfDay().toDate();
                tempdate = new LocalDate().withYear(selectedYear).withMonthOfYear(selectedQuarter * 3 + 3).dayOfMonth().withMaximumValue().plusDays(1);
                reportEndDate = tempdate.toDateTimeAtStartOfDay().toDate();
                break;
            case 3:
                tempdate = new LocalDate().withYear(selectedYear).withMonthOfYear(selectedMonth).dayOfMonth().withMinimumValue();
                reportStartDate = tempdate.toDateTimeAtStartOfDay().toDate();
                tempdate = tempdate.dayOfMonth().withMaximumValue().plusDays(1);
                reportEndDate = tempdate.toDateTimeAtStartOfDay().toDate();
                break;
            case 4:
                tempdate = new LocalDate().withYear(selectedYear).monthOfYear().withMinimumValue().dayOfMonth().withMinimumValue();
                reportStartDate = tempdate.toDateTimeAtStartOfDay().toDate();
                tempdate = tempdate.monthOfYear().withMaximumValue().dayOfMonth().withMaximumValue().plusDays(1);
                reportEndDate = tempdate.toDateTimeAtStartOfDay().toDate();
                break;
            case 5:
                reportStartDate = selectedStartDate == null ? minDate : selectedStartDate;
                reportEndDate = selectedEndDate == null ? maxDate : selectedEndDate;
                if (reportEndDate.before(reportStartDate)) {
                    Date tempDate = reportEndDate;
                    reportEndDate = reportStartDate;
                    reportStartDate = tempDate;
                }
                break;
            default:
                logger.log(Level.SEVERE, "Invalid report type value {0}", selectedRepType);
                break;
        }
        if (reportStartDate.after(currentDate)) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Start date is in the future", " ");
            return;
        }
        if (reportEndDate.after(currentDate)) {
            reportEndDate = currentDate;
        }

    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public boolean canNotGenerateReports() {
        return !authManager.canGenerateReports();
    }

    /**
     * generate a report
     * 
     * @param reportType
     * @return 
     */
    public String generateReport(String reportType) {     
        String resource = "";
        StringBuilder url = new StringBuilder("faces-redirect=true");

        reportStartDate = selectedStartDate;
        reportEndDate = selectedEndDate;
        if ("beam".equals(reportType)) {
            resource = "beam/index.xhtml";
            url.append("&facility=");
            url.append(selectedFacility.getFacilityId());
            url.append("&beamsystem=");
            url.append(selectedBeamSystem.getBeamSystemId());
            url.append("&none=");
            url.append(includeNoneBeam ? "true" : "false");
        }
        if ("breakdown".equals(reportType)) {
            computeDates();
            resource = "breakdown/index.xhtml";
            url.append("&facility=");
            url.append(selectedFacility.getFacilityId());
        }
        if ("summary".equals(reportType)) {
            computeDates();
            resource = "summary/index.xhtml";
            url.append("&facility=");
            url.append(selectedFacility.getFacilityId());
        }
        if ("experiment".equals(reportType)) {
            resource = "experiment/index.xhtml";
            url.append("&number=");
            url.append(selectedExperiment.getNumber());
        }
        
        if (reportStartDate != null) {
            url.append("&start=");
            url.append(new SimpleDateFormat("yyyyMMddHHmmss").format(reportStartDate));
        }
        if (reportEndDate != null) {
            url.append("&end=");
            url.append(new SimpleDateFormat("yyyyMMddHHmmss").format(reportEndDate));
        }
        
        if (url.length() != 0 ) {
            url.insert(0,"?");
        }
        url.insert(0,resource);
       
        logger.log(Level.INFO, "generate report url {0}", url);
        return (url.toString());
    }

    public boolean isWeeklyReport() {
        return reports.get("Weekly") == selectedRepType;
    }

    public boolean isMonthlyReport() {
        return reports.get("Monthly") == selectedRepType;
    }

    public boolean isQuarterlyReport() {
        return reports.get("Quarterly") == selectedRepType;
    }

    public boolean isYearlyReport() {
        return reports.get("Yearly") == selectedRepType;
    }

    public boolean isPeriodReport() {
        return reports.get("Period") == selectedRepType;
    }

    // -----
    public List<Facility> getFacilities() {
        return facilities;
    }

    public Map<String, Integer> getMonths() {
        return months;
    }

    public Map<String, Integer> getReports() {
        return reports;
    }

    public Map<String, Integer> getWeeks() {
        return weeks;
    }

    public Map<String, Integer> getQuarters() {
        return quarters;
    }

    public Date getMinDate() {
        return minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public Facility getSelectedFacility() {
        return selectedFacility;
    }

    public void setSelectedFacility(Facility selectedFacility) {
        this.selectedFacility = selectedFacility;
    }

    public Map<String, Integer> getYears() {
        return years;
    }

    public List<BeamSystem> getBeamSystems() {
        return beamSystems;
    }

    public BeamSystem getSelectedBeamSystem() {
        return selectedBeamSystem;
    }

    public void setSelectedBeamSystem(BeamSystem selectedBeamSystem) {
        this.selectedBeamSystem = selectedBeamSystem;
    }

    public Date getSelectedStartDate() {
        return selectedStartDate;
    }

    public void setSelectedStartDate(Date selectedStartDate) {
        this.selectedStartDate = selectedStartDate;
    }

    public Date getSelectedEndDate() {
        return selectedEndDate;
    }

    public void setSelectedEndDate(Date selectedEndDate) {
        this.selectedEndDate = selectedEndDate;
    }

    public boolean isIncludeNoneBeam() {
        return includeNoneBeam;
    }

    public void setIncludeNoneBeam(boolean includeNoneBeam) {
        this.includeNoneBeam = includeNoneBeam;
    }

    public Experiment getSelectedExperiment() {
        return selectedExperiment;
    }

    public void setSelectedExperiment(Experiment selectedExperiment) {
        this.selectedExperiment = selectedExperiment;
    }

    public int getSelectedWeek() {
        return selectedWeek;
    }

    public void setSelectedWeek(int selectedWeek) {
        this.selectedWeek = selectedWeek;
    }

    public int getSelectedQuarter() {
        return selectedQuarter;
    }

    public void setSelectedQuarter(int selectedQuarter) {
        this.selectedQuarter = selectedQuarter;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    public int getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(int selectedMonth) {
        this.selectedMonth = selectedMonth;
    }

    public int getSelectedRepType() {
        return selectedRepType;
    }

    public void setSelectedRepType(int selectedRepType) {
        this.selectedRepType = selectedRepType;
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
