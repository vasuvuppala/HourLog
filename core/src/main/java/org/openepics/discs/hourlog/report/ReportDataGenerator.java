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
package org.openepics.discs.hourlog.report;

import org.openepics.discs.hourlog.util.SummaryName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.openepics.discs.hourlog.auth.AuthManager;
import org.openepics.discs.hourlog.ejb.EventEJB;
import org.openepics.discs.hourlog.ejb.HourLogEJB;
import org.openepics.discs.hourlog.ejb.SnapshotEJB;
import org.openepics.discs.hourlog.ent.BeamEvent;
import org.openepics.discs.hourlog.ent.BeamSystem;
import org.openepics.discs.hourlog.ent.BreakdownCategory;
import org.openepics.discs.hourlog.ent.BreakdownEvent;
import org.openepics.discs.hourlog.ent.Event;
import org.openepics.discs.hourlog.ent.Experiment;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Summary;
import org.openepics.discs.hourlog.util.Utility;

/**
 * Report generator
 *
 * TODO: really really horrible code. rewrite the entire reporting code.
 *
 * @author vuppala
 */
@Named
@RequestScoped
public class ReportDataGenerator implements Serializable {

    @EJB
    private HourLogEJB hourlogEJB;
    @EJB
    private EventEJB eventEJB;
    @EJB
    private SnapshotEJB snapshotEJB;
    @EJB(beanName = "LocalAuthManager")
    private AuthManager authManager;

    private static final Logger logger = Logger.getLogger(ReportDataGenerator.class.getName());

    private static final double MSECS_IN_AN_HOUR = 1000.0 * 3600;

    public Map<String, ReportItem> breakdownHours(Facility facility, Date start, Date end) {

        Date today = Utility.currentDate();
        Map<String, Date> lastFailureDate = new HashMap<>();

        if (end.after(today)) {
            logger.severe("Invalid end date.");
            return null;
        }

        Map<String, ReportItem> report = new HashMap<>();
        List<BreakdownCategory> categories = hourlogEJB.findAllBrkCategories();
        for (BreakdownCategory s : categories) {
            report.put(s.getName(), new ReportItem(s.getName(), s.getDescription()));
            lastFailureDate.put(s.getName(), null);
        }
        List<BreakdownEvent> brkdownEvents = snapshotEJB.breakdownsAt(facility, start, false);
        for (BreakdownEvent bevent : brkdownEvents) {
            if (bevent.getFailed()) {
                lastFailureDate.put(bevent.getCategory().getName(), start);
            }
        }
        brkdownEvents = eventEJB.findBreakdownEvents(facility, start, end);
        logger.log(Level.FINE, "Found breakdown events : {0}", brkdownEvents.size());

        Date beginDate, endDate;
        for (BreakdownEvent bevent : brkdownEvents) {
            BreakdownCategory brkcat = bevent.getCategory();

            if (bevent.getFailed()) {
                if (lastFailureDate.get(brkcat.getName()) == null) {
                    lastFailureDate.put(brkcat.getName(), bevent.getEvent().getOccurredAt());
                } else {
                    logger.log(Level.WARNING, "BreakDownReport: contiguous failure events found for {0}", brkcat.getName());
                }
            } else {
                if (lastFailureDate.get(brkcat.getName()) != null) {
                    ReportItem ri = report.get(brkcat.getName());
                    beginDate = lastFailureDate.get(brkcat.getName());
                    endDate = bevent.getEvent().getOccurredAt();
                    ri.addHours((endDate.getTime() - beginDate.getTime()) / MSECS_IN_AN_HOUR); //ToDo: remove magic numbers
                    ri.incrementOccurrences();
                    lastFailureDate.put(brkcat.getName(), null);
                } else {
                    logger.log(Level.WARNING, "BreakDownReport: contiguous clear events found for {0}", brkcat.getName());
                }
            }
        }
        // for the last status
        for (BreakdownCategory s : categories) {
            ReportItem ri = report.get(s.getName());
            if (lastFailureDate.get(s.getName()) != null) {
                beginDate = lastFailureDate.get(s.getName());
                ri.addHours((end.getTime() - beginDate.getTime()) / MSECS_IN_AN_HOUR); //ToDo: remove magic numbers
            }
        }
        return report;
    }

    /**
     * Calculate the hours spent by a facility in each summary state (EXR, UOF,
     * SOF etc) in a given period.
     *
     * @param facility
     * @param start
     * @param end
     * @return
     */
//    public Map<String, ReportItem> summaryHours(Facility facility, Date start, Date end) {
//
//        Summary summaryAt = snapshotEJB.summaryAt(facility, start, false);
//        Date today = Utility.currentDate();
//        // ToDo: throw exceptions instead
//        if (summaryAt == null) {
//            logger.severe("ReportEJB.summaryHours: Invalid start date.");
//            return null;
//        }
//        if (end.after(today)) {
//            logger.severe("ReportEJB.summaryHours: Invalid end date.");
//            return null;
//        }
//
//        Map<String, ReportItem> report = new HashMap<>();
//        List<Summary> allSummaries = hourlogEJB.findAllSummary();
//        for (Summary s : allSummaries) {
//            report.put(s.getName(), new ReportItem(s.getName(), s.getDescription()));
//        }
//
//        List<SummaryEvent> fstatus = eventEJB.findSummaryEvents(facility, start, end);
//        logger.log(Level.FINE, "ReportEJB.summaryHours: Found facility summary records: {0}", fstatus.size());
//
//        Date prevDate = start;
//        String prevStatus = summaryAt.getName();
//        for (SummaryEvent fs : fstatus) {
//            ReportItem ri = report.get(prevStatus);
//
//            if (ri == null) {
//                logger.log(Level.SEVERE, "ReportEJB.summaryHours: Error: invalid status id {0}", fs.getSummary().getSummaryId());
//                continue;
//            }
//
//            Date currentDate = fs.getEvent().getOccurredAt();
//            if (prevDate != null && currentDate != null) {
//                ri.addHours((currentDate.getTime() - prevDate.getTime()) / MSECS_IN_AN_HOUR); //ToDo: remove magic numbers
//                ri.incrementOccurrences();
//            } else {
//                logger.warning("ReportEJB.summaryHours: Previous date or next date is null. this is strange. check data.");
//            }
//            prevDate = currentDate;
//            prevStatus = fs.getSummary().getName();
//        }
//
//        // for the last status
//        ReportItem ri = report.get(prevStatus);
//        ri.addHours((end.getTime() - prevDate.getTime()) / MSECS_IN_AN_HOUR);
//        ri.incrementOccurrences();
//
//        return report;
//    }
    /**
     * Generates experiment report. This used for both Experiment Report and
     * Summary Report.
     *
     * TODO: Really HORRIBLE! Make it simpler and less verbose
     *
     *
     * @param facility
     * @param selectedExpr
     * @param start start must be less than or equal to end
     * @param end
     * @param experimentData
     * @return experiment report
     */
    public ExperimentReportItem experimentReport(Facility facility, Experiment selectedExpr, Date start, Date end, Map<Integer, ExperimentReportItem> experimentData) {
        List<BreakdownCategory> categories = hourlogEJB.findAllBrkCategories();
        List<Summary> summaries = hourlogEJB.findAllSummary();
        List<Event> events;

        events = eventEJB.findEvents(facility, start, end);

        if (events.isEmpty()) {
            logger.log(Level.WARNING, "ReportDataGenerator#experimentReport: No events found!");
            // return null;
        }
        Double tempHours; // temp variable to hold hours
        // Init experiment 
        Date prevExprDate = start;
        Date prevUOFdate = start;
        Experiment prevExpr = snapshotEJB.experimentAt(facility, start, false);
        if (prevExpr == null) {
            logger.log(Level.FINE, "ReportDataGenerator#experimentReport: No experiments found. Empty database?");
            return null;
        }
        ExperimentReportItem eri = ExperimentReportItem.newInstance(prevExpr, summaries, categories);
        if (!events.isEmpty() && !events.get(0).getOccurredAt().equals(start)) {
            eri.makePartial();
        }
        experimentData.put(prevExpr.getNumber(), eri);
        // init summary
        Date prevSummaryDate = start;
        Summary prevSummary = snapshotEJB.summaryAt(facility, start, false);
        if (prevSummary == null) {
            logger.log(Level.WARNING, "ReportDataGenerator#experimentReport: No summaries found. Empty database?");
            return null;
        }
        // init breakdowns
        Map<String, Date> lastFailureDate = new HashMap<>(); // to keep track of the last failure date of a breakdown category

        for (BreakdownEvent bevent : snapshotEJB.breakdownsAt(facility, start, false)) {
            if (bevent.getFailed()) {
                lastFailureDate.put(bevent.getCategory().getName(), start);
            }
        }

        for (Event event : events) {
//            if (start.equals(event.getOccurredAt())) {
//                continue; // skip first event, if it falls exactly on start date
//            }
            if (selectedExpr != null) { // i.e. data for Experiment Report
                if (!prevExpr.equals(selectedExpr)) {
                    if (!event.getSummaryEventList().isEmpty()) {
                        prevSummary = event.getSummaryEventList().get(0).getSummary();
                    }
                    for (BreakdownEvent bevent : event.getBreakdownEventList()) {
                        if (bevent.getFailed()) {
                            lastFailureDate.put(bevent.getCategory().getName(), event.getOccurredAt());
                        } else {
                            lastFailureDate.put(bevent.getCategory().getName(), null);
                        }
                    }
                    if (!event.getExprEventList().isEmpty()) {
                        prevExpr = event.getExprEventList().get(0).getExperiment();
                        if (experimentData.get(prevExpr.getNumber()) == null) {
                            experimentData.put(prevExpr.getNumber(), ExperimentReportItem.newInstance(prevExpr, summaries, categories));
                        }
                        if (selectedExpr.equals(prevExpr)) {
//                            logger.log(Level.FINE, "ReportDataGenerator.experimentReport: re-start of expr {0} at {1}. resetting counters.", new Object[]{experiment.getNumber(), event.getOccurredAt()});
                            prevExprDate = event.getOccurredAt();
                            prevSummaryDate = event.getOccurredAt();
                            prevUOFdate = prevSummaryDate;
                            // prevSummary = snapshotEJB.summaryAt(facility, start, false);
                            for (Entry<String, Date> brk : lastFailureDate.entrySet()) {
                                if (brk.getValue() != null) {
                                    lastFailureDate.put(brk.getKey(), event.getOccurredAt());
                                }
                            }
                        }
                    }
                    continue;
                }
            }
            for (BreakdownEvent bevent : event.getBreakdownEventList()) {
                if (bevent.getFailed()) {
                    if (lastFailureDate.get(bevent.getCategory().getName()) == null) {
                        lastFailureDate.put(bevent.getCategory().getName(), event.getOccurredAt());
                        experimentData.get(prevExpr.getNumber()).incBreakOccurrence(bevent.getCategory());
                    }
                } else {
                    if (lastFailureDate.get(bevent.getCategory().getName()) == null) {
                        logger.log(Level.FINE, "ReportDataGenerator.experimentReport: Multiple consecutive breakdown clear events for same category {0}", bevent.getCategory().getName());
                    } else {
                        tempHours = (event.getOccurredAt().getTime() - lastFailureDate.get(bevent.getCategory().getName()).getTime()) / MSECS_IN_AN_HOUR;
//                        logger.log(Level.FINE, "ReportDataGenerator.experimentReport: Adding {0} to {1}", new Object[]{tempHours, bevent.getCategory().getName()});
                        experimentData.get(prevExpr.getNumber()).addBreakHours(bevent.getCategory(), tempHours);
                        // experimentData.get(prevExpr.getNumber()).incBreakOccurrence(bevent.getCategory());
                        if (unscheduledOff(prevSummary)) {
                            tempHours = (event.getOccurredAt().getTime() - prevUOFdate.getTime()) / MSECS_IN_AN_HOUR;
                            prevUOFdate = event.getOccurredAt();
                            if (experimentRelatedBreakdown(lastFailureDate)) {
                                experimentData.get(prevExpr.getNumber()).addHoursUOFexp(tempHours);
                            } else {
                                experimentData.get(prevExpr.getNumber()).addHoursUOFexcl(tempHours);
                            }
                        }
                        lastFailureDate.put(bevent.getCategory().getName(), null);
                    }
                }
            }
            if (!event.getSummaryEventList().isEmpty()) {
                // logger.log(Level.FINE,"ReportDataGenerator.experimentReport: Adding Hours {0} for Summary {1}", new Object[] {temp, prevSummary.getName()} );
                tempHours = (event.getOccurredAt().getTime() - prevSummaryDate.getTime()) / MSECS_IN_AN_HOUR;
                experimentData.get(prevExpr.getNumber()).addSummaryHours(prevSummary, tempHours);
                if (unscheduledOff(prevSummary)) {
                    tempHours = (event.getOccurredAt().getTime() - prevUOFdate.getTime()) / MSECS_IN_AN_HOUR;
                    prevUOFdate = event.getOccurredAt();
                    if (experimentRelatedBreakdown(lastFailureDate)) {
                        experimentData.get(prevExpr.getNumber()).addHoursUOFexp(tempHours);
                    } else {
                        experimentData.get(prevExpr.getNumber()).addHoursUOFexcl(tempHours);
                    }
                }
                prevSummary = event.getSummaryEventList().get(0).getSummary();
                prevSummaryDate = event.getOccurredAt();
                if (unscheduledOff(prevSummary)) {
                    prevUOFdate = prevSummaryDate;
                }
            }
            if (event.getOccurredAt().equals(start)) { // if the experiment started on first event, skip it
                continue;
            }
            if (!event.getExprEventList().isEmpty()) { // new experiment

                // adjust summary counters
                if (event.getSummaryEventList().isEmpty()) {
                    tempHours = (event.getOccurredAt().getTime() - prevSummaryDate.getTime()) / MSECS_IN_AN_HOUR;
                    experimentData.get(prevExpr.getNumber()).addSummaryHours(prevSummary, tempHours);
                    prevSummaryDate = event.getOccurredAt();
                    if (unscheduledOff(prevSummary)) {
                        tempHours = (event.getOccurredAt().getTime() - prevUOFdate.getTime()) / MSECS_IN_AN_HOUR;
                        prevUOFdate = event.getOccurredAt();
                        if (experimentRelatedBreakdown(lastFailureDate)) {
                            experimentData.get(prevExpr.getNumber()).addHoursUOFexp(tempHours);
                        } else {
                            experimentData.get(prevExpr.getNumber()).addHoursUOFexcl(tempHours);
                        }
                    }
                }
                // adjust breakdown counters
                // TODO: any breakdowns in this event, they will be added again here but the time amount will be zeros
                for (Entry<String, Date> brk : lastFailureDate.entrySet()) {
                    if (brk.getValue() != null) {
                        tempHours = (event.getOccurredAt().getTime() - brk.getValue().getTime()) / MSECS_IN_AN_HOUR;
                        experimentData.get(prevExpr.getNumber()).addBreakHours(brk.getKey(), tempHours);
                        lastFailureDate.put(brk.getKey(), event.getOccurredAt());
                    }
                }

                // logger.log(Level.FINE,"ReportDataGenerator.experimentReport: Adding entry for experiment {0}", prevExpr.getTitle());
                experimentData.get(prevExpr.getNumber()).addRuntime(prevExprDate, event.getOccurredAt());
                prevExprDate = event.getOccurredAt();
                prevExpr = event.getExprEventList().get(0).getExperiment();
                if (experimentData.get(prevExpr.getNumber()) == null) {
                    experimentData.put(prevExpr.getNumber(), ExperimentReportItem.newInstance(prevExpr, summaries, categories));
                }
            }
        }
        // Add the last segment
        tempHours = (end.getTime() - prevSummaryDate.getTime()) / MSECS_IN_AN_HOUR;
        if (experimentData.get(prevExpr.getNumber()) == null) {
            experimentData.put(prevExpr.getNumber(), ExperimentReportItem.newInstance(prevExpr, summaries, categories));
        }
        experimentData.get(prevExpr.getNumber()).addSummaryHours(prevSummary, tempHours);

        experimentData.get(prevExpr.getNumber()).addRuntime(prevExprDate, end);
        for (BreakdownCategory cat : categories) {
            if (lastFailureDate.get(cat.getName()) != null) {
                tempHours = (end.getTime() - lastFailureDate.get(cat.getName()).getTime()) / MSECS_IN_AN_HOUR;
                experimentData.get(prevExpr.getNumber()).addBreakHours(cat, tempHours);
            }
        }
        if (unscheduledOff(prevSummary)) {
            tempHours = (end.getTime() - prevUOFdate.getTime()) / MSECS_IN_AN_HOUR;
            // prevUOFdate = end;
            if (experimentRelatedBreakdown(lastFailureDate)) {
                experimentData.get(prevExpr.getNumber()).addHoursUOFexp(tempHours);
            } else {
                experimentData.get(prevExpr.getNumber()).addHoursUOFexcl(tempHours);
            }
        }
        //----------------------
        // if selected experiment did occur in the given period, then add a dummy data record
        if (selectedExpr != null && experimentData.get(selectedExpr.getNumber()) == null) {
            experimentData.put(selectedExpr.getNumber(), ExperimentReportItem.newInstance(selectedExpr, summaries, categories));
        }

        //  compute Totals
        Experiment totalExperiment = new Experiment(0); // dummy experiment
        totalExperiment.setTitle("Total");
        totalExperiment.setDescription("Totals");
        ExperimentReportItem dataTotals = ExperimentReportItem.newInstance(totalExperiment, summaries, categories);
        for (ExperimentReportItem item : experimentData.values()) {
            // logger.log(Level.FINE,"ReportDataGenerator.experimentReport: summary total {0}", item.getSummaryTotal());          
            item.computeTotals();
            item.makeSummaryTree();
            dataTotals.addItem(item);
        }
        dataTotals.computeTotals();
        dataTotals.makeSummaryTree();

        logger.log(Level.FINE, "Totals {0}", dataTotals);
        return dataTotals;
    }

    /**
     * Generates data for Beam report.
     *
     * TODO: Really HORRIBLE! Make it simpler and less verbose
     *
     *
     * @param facility
     * @param beamSystem
     * @param start start must be less than or equal to end
     * @param end
     * @return experiment report
     */
    public List<BeamReportItem> beamReport(final Facility facility, final BeamSystem beamSystem, final Date start, final Date end) {
        List<BeamReportItem> beamReport = new ArrayList<>();
        List<Summary> summaries = hourlogEJB.findAllSummary();
        List<Event> events;

        events = eventEJB.findEvents(facility, start, end);

        if (events.isEmpty()) {
            logger.log(Level.WARNING, "No events found!");
            // return null;
        }

        BeamEvent beamEvent = snapshotEJB.beamAt(facility, beamSystem, start, false);
        if (beamEvent == null) {
            logger.log(Level.WARNING, "No beams found. Empty database?");            
        }
        
        ReportBeam prevBeam = ReportBeam.newInstance(beamEvent);
        BeamReportItem bri = BeamReportItem.newInstance(start, null, prevBeam, summaries);
        Date prevSummaryDate = start;
        Summary prevSummary = snapshotEJB.summaryAt(facility, start, false);
        if (prevSummary == null) {
            logger.log(Level.WARNING, "No summaries found. Empty database?");
            return null;
        }

        Double tempHours; // temp variable to hold hours
        Double totalHours = 0.0;
        for (Event event : events) {
            if (start.equals(event.getOccurredAt())) {
                continue; // skip first event, if it falls exactly on start time
            }
            if (beamHasChanged(event, beamSystem, prevBeam)) {
                tempHours = (event.getOccurredAt().getTime() - prevSummaryDate.getTime()) / MSECS_IN_AN_HOUR;
                bri.addSummaryHours(prevSummary, tempHours);
                totalHours += tempHours;
                prevSummaryDate = event.getOccurredAt();
                bri.setEndTime(event.getOccurredAt());
                beamReport.add(bri);
                prevBeam =  toBeam(event, beamSystem);
                if (prevBeam == null) {
                    logger.log(Level.SEVERE, "beam should not be null here; something is wrong!");
                }
                bri = BeamReportItem.newInstance(event.getOccurredAt(), null, prevBeam, summaries);
            }

            if (!event.getSummaryEventList().isEmpty()) {
                // logger.log(Level.FINE,"ReportDataGenerator.experimentReport: Adding Hours {0} for Summary {1}", new Object[] {temp, prevSummary.getName()} );
                tempHours = (event.getOccurredAt().getTime() - prevSummaryDate.getTime()) / MSECS_IN_AN_HOUR;
                bri.addSummaryHours(prevSummary, tempHours);
                totalHours += tempHours;
                prevSummary = event.getSummaryEventList().get(0).getSummary();
                prevSummaryDate = event.getOccurredAt();
            }
        }
        // Add the last segment
        tempHours = (end.getTime() - prevSummaryDate.getTime()) / MSECS_IN_AN_HOUR;
        bri.addSummaryHours(prevSummary, tempHours);
        bri.setEndTime(end);
        totalHours += tempHours;
        beamReport.add(bri);

        for (BeamReportItem item : beamReport) {
            // logger.log(Level.FINE,"ReportDataGenerator.experimentReport: summary total {0}", item.getSummaryTotal());          
            item.computeTotals(totalHours);
        }

        logger.log(Level.FINE, "Totals {0}", beamReport);
        return beamReport;
    }

    /**
     * Are the current breakdowns due to the user/experiment (and not the
     * facility)?
     *
     * TODO: get rid of DET, DAQ constants.
     *
     * @param lastFailureDate
     * @return true if they are
     */
    private boolean experimentRelatedBreakdown(Map<String, Date> lastFailureDate) {
        if (lastFailureDate.get(BreakCategoryName.DET.toString()) == null && lastFailureDate.get(BreakCategoryName.DAQ.toString()) == null) {
            return false;
        }

        for (Entry<String, Date> brk : lastFailureDate.entrySet()) {
            if (BreakCategoryName.DET.toString().equals(brk.getKey()) || BreakCategoryName.DAQ.toString().equals(brk.getKey())) {
                continue;
            }
            if (brk.getValue() != null) { // if anything else is broken besides DET/DAQ it is not experiment related
                return false;
            }
        }
        return true;
    }

    /**
     * Convert a beam event to beam
     *
     * @param beamEvent
     * @return
     */
//    private Beam toBeam(BeamEvent beamEvent) {
//        if (beamEvent.getBeam() != null) {
//            return beamEvent.getBeam();
//        }
//
//        Beam beam = new Beam();
//
//        if (beamEvent.getCharge() != null) {
//            beam.setCharge(beamEvent.getCharge());
//        }
//        if (beamEvent.getEnergy() != null) {
//            beam.setEnergy(beamEvent.getEnergy());
//        }
//        if (beamEvent.getMassNumber() != null) {
//            beam.setMassNumber(beamEvent.getMassNumber());
//        }
//        beam.setElement(beamEvent.getElement());
//        beam.setBeamSystem(beamEvent.getBeamSystem());
//
//        return beam;
//    }

    /**
     * Get the beam from an event for the given beam system
     *
     * @param event
     * @param beamSystem
     * @return
     */
    private ReportBeam toBeam(Event event, BeamSystem beamSystem) {
        for (BeamEvent bevent : event.getBeamEventList()) {
            if (beamSystem.equals(bevent.getBeamSystem())) {
                return ReportBeam.newInstance(bevent);
            }
        }
        return null;
    }

    /**
     * Check if there is new beam.
     *
     * @param event
     * @param beamSystem
     * @param prevBeam
     * @return
     */
    private boolean beamHasChanged(Event event, BeamSystem beamSystem, ReportBeam prevBeam) {
        for (BeamEvent bevent : event.getBeamEventList()) {
            if (beamSystem.equals(bevent.getBeamSystem())) {
                if (!Utility.areSame(ReportBeam.newInstance(bevent), prevBeam)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Is it UOF summary event?
     *
     * TODO: Replace with similar method in Hour Log EJB.
     *
     * @param summary
     * @return true if it is
     */
    private boolean unscheduledOff(Summary summary) {
        return SummaryName.UOF.toString().equals(summary.getName());
    }
}
