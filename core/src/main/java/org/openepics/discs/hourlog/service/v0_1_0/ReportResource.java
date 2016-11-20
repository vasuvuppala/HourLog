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

import org.openepics.discs.hourlog.exception.HourLogAPIException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.openepics.discs.hourlog.ejb.FacilityEJB;
import org.openepics.discs.hourlog.ejb.ShiftEJB;
import org.openepics.discs.hourlog.ejb.SnapshotEJB;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.SummaryEvent;
import org.openepics.discs.hourlog.ent.Shift;
import org.openepics.discs.hourlog.ent.ShiftStaffMember;
import org.openepics.discs.hourlog.util.AuthService;
import org.openepics.discs.hourlog.log.StatusSnapshot;
import org.openepics.discs.hourlog.report.ExperimentReportItem;
import org.openepics.discs.hourlog.report.ReportDataGenerator;
import org.openepics.discs.hourlog.util.Utility;

/**
 * RESTful report resource
 *
 * @author vuppala
 */
@Path("/v0.1.0/reports")
public class ReportResource {

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    private static class Report {

        private String name;
        private String description;
        private String uri;

        public Report() {
        }

        public Report(String name, String desc, String uri) {
            this.name = name;
            this.description = desc;
            this.uri = uri;
        }

    }

    @EJB
    private FacilityEJB facilityEJB;
    @EJB
    private SnapshotEJB snapshotEJB;
    @EJB
    private ShiftEJB shiftEJB;
    @Inject
    private ReportDataGenerator reportDataGenerator;

    private static final Logger logger = Logger.getLogger(ReportResource.class.getName());
    private static final int MAX_NUM_EVENTS = 100; // maximum number of events to be returned

    private final static List<Report> reports = new ArrayList<>();

    static {
        reports.add(new Report("summary", "Summary of one or more facilities", "/summary"));
    }

    /**
     *
     * Generate reports
     *
     * @param headers
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public List<Report> getReports(@Context final HttpHeaders headers) {
        logger.entering(ReportResource.class.getName(), "getReports");

        AuthService authService = new AuthService(headers);
        if (!authService.isUserInRole("application")) {
            throw new HourLogAPIException(Response.Status.FORBIDDEN, "Not authorized to access this resource");
        }

        return reports;
    }

    /**
     * Generate facility summary
     * 
     * @param facilityName
     * @param headers
     * @return
     * @throws IOException 
     */
    @GET
    @Path("/summary")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public List<SummaryReport> getSummary(
            @DefaultValue("") @QueryParam("facility") String facilityName,
            @Context final HttpHeaders headers) throws IOException {

        AuthService authService = new AuthService(headers);
        if (!authService.isUserInRole("application")) {
            throw new HourLogAPIException(Response.Status.FORBIDDEN, "Not authorized to access this resource");
        }

        List<SummaryReport> summaries = new ArrayList<>();
        List<Facility> facilities = new ArrayList<>();

        logger.entering(ReportResource.class.getName(), "getSummary");

        if ("".equals(facilityName)) {
            facilities = facilityEJB.findFacility();
        } else {
            Facility facility = facilityEJB.findFacility(facilityName);
            if (facility != null) {
                facilities.add(facility);
            }
        }

        if (facilities.isEmpty()) {
            // response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid facility name: " + facilityName);
            throw new HourLogAPIException(Response.Status.NOT_FOUND, "Invalid facility name: " + facilityName);
        }

        for (Facility facility : facilities) {
            SummaryReport fsummary = currentSummary(facility);
            if (fsummary == null) {
                throw new HourLogAPIException(Response.Status.NOT_FOUND, "This is strange. No current status record found for " + facility.getFacilityName());
                // response.sendError(HttpServletResponse.SC_NOT_FOUND, "This is strange. No current status record found for " + facility.getFacilityName());
            } else {
                summaries.add(fsummary);
            }
        }

        return summaries;
    }
    
    /**
     * Generate facility snapshot
     * 
     * @param facilityName
     * @param response
     * @return
     * @throws IOException 
     */
    @GET
    @Path("/snapshot")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public StatusSnapshot getSnapshot(@QueryParam("facility") String facilityName,
            @Context final HttpServletResponse response) throws IOException {

        logger.entering(ReportResource.class.getName(), "getFacility");
        Facility facility = facilityEJB.findFacility(facilityName);
        if (facility == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid facility name: " + facilityName);
            return null;
        }

        StatusSnapshot snapshot = snapshotEJB.snapshotAt(facility, Utility.currentDate());

        if (snapshot == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "This is strange. No current status record found for " + facilityName);
        }

        return snapshot;
    }

    /**
     * Finds current summary for a given faiclity
     *
     * @author vuppala
     * @param facility The facility
     * @return Current summary
     */
    private SummaryReport currentSummary(Facility facility) {
        StatusSnapshot snapshot = snapshotEJB.snapshotAt(facility, Utility.currentDate());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -24); // 24 ours ago
        Date date = cal.getTime();

        List<SummaryEvent> eventList = facilityEJB.summarySince(facility, date, 0, MAX_NUM_EVENTS);
        // logger.info("currentSummary: past date is " + date);
        logger.log(Level.FINE, "currentSummary: number of facility events = {0}", eventList.size());

        logger.log(Level.FINE, "currentSummary: generated current summary for {0}", facility.getFacilityName());
        // SummaryReport summary =  SummaryReport.newInstance(facility, snapshot, eventList);
        Shift shift = shiftEJB.currentShift(facility);
        List<ShiftStaffMember> staff = shiftEJB.findShiftStaff(shift);
        List<AvailabilityRecord> arecords = computeAvailability(facility);
        SummaryReport summary = SummaryReport.newInstance(facility, snapshot, eventList, shift, staff, arecords);

        return summary;
    }

    /**
     * calculates availability information
     *
     * @param facility
     * @return availability records
     */
    private List<AvailabilityRecord> computeAvailability(Facility facility) {
        Map<Integer, ExperimentReportItem> experimentData = new HashMap<>(); // summary and breakdown data per experiment
        ExperimentReportItem dataTotals;
        int days[] = {1, 7, 30, 180};
        Date currentDate = Utility.currentDate();
        List<AvailabilityRecord> arecords = new ArrayList<>();

        for (int day : days) {
            experimentData.clear();
            // ToDO this is too compute intensive (calculates data for every experiment). Improve it.
            dataTotals = reportDataGenerator.experimentReport(facility, null, Utility.addDaysToDate(currentDate, -day), currentDate, experimentData);
            if (dataTotals == null) {
                logger.log(Level.WARNING, "computeAvailability: availability data is null");
                arecords.add(new AvailabilityRecord(day, 0.0D));
            } else {
                arecords.add(new AvailabilityRecord(day, dataTotals.getAvailabilityExcl()));
            }
        }
        return arecords;
    }
}
