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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openepics.discs.hourlog.ent.BreakdownCategory;
import org.openepics.discs.hourlog.ent.Experiment;
import org.openepics.discs.hourlog.ent.Summary;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * Report for an experiment
 *
 * @author vuppala
 */
public class ExperimentReportItem implements Serializable, Comparable<ExperimentReportItem> {

    private static final Logger logger = Logger.getLogger(ExperimentReportView.class.getName());

    public static class Period {

        private final Date startDate;
        private final Date endDate;

        public Period(Date start, Date end) {
            startDate = start;
            endDate = end;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

    }

    private static final String UOF_EXCL = "UOF_EXCL";
    private static final String UOF_EXP = "UOF_EXP";

    private Experiment experiment;
    private Map<String, ReportItem> summaryData;
    private Map<String, ReportItem> breakdownData;
    private boolean partial = false; // does the report contain only a portion of the experiment's time?
    private boolean ongoing = false; // Is the experiment not yet completed?
    private List<Period> runtimes; // periods when the experiment was running
    private double summaryTotal = 0;
    private double breakdownTotal = 0;
    private double availabilityExcl = 0;
    private double availabilityExp = 0;
    private TreeNode summaryTreeRoot;

    private List<ReportItem> summaryList; //TODO: Only for sorting in p:datatable. See if it can be removed.
    private List<ReportItem> breakdownList; //TODO: Only for sorting in p:datatable. See if it can be removed.
    
    private ExperimentReportItem() {

    }

    public static ExperimentReportItem newInstance(Experiment experiment, List<Summary> summaries, List<BreakdownCategory> categories) {
        ExperimentReportItem item = new ExperimentReportItem();

        item.experiment = experiment;
        item.summaryData = new HashMap<>();
        item.breakdownData = new HashMap<>();
        item.runtimes = new ArrayList<>();

        for (Summary summary : summaries) {
            item.summaryData.put(summary.getName(), new ReportItem(summary.getName(), summary.getDescription()));
        }

        item.summaryData.put(UOF_EXCL, new ReportItem(UOF_EXCL, "UOF Excluding Experiment"));
        item.summaryData.put(UOF_EXP, new ReportItem(UOF_EXP, "UOF Experiment Related"));

        for (BreakdownCategory bcat : categories) {
            item.breakdownData.put(bcat.getName(), new ReportItem(bcat.getName(), bcat.getDescription()));
        }

        item.summaryList = new ArrayList<>(item.summaryData.values()); //TODO: Only for sorting in p:datatable. See if it can be removed.
        item.breakdownList = new ArrayList<>(item.breakdownData.values()); //TODO: Only for sorting in p:datatable. See if it can be removed.
        
        return item;
    }

    @Override
    public int compareTo(ExperimentReportItem other) {
//        if (! (object instanceof ExperimentReportItem) ) {
//            return 1;
//        }
//        ExperimentReportItem other = (ExperimentReportItem) object;
        return this.experiment.getNumber().compareTo(other.experiment.getNumber());
    }

    public void addSummaryHours(Summary summary, double hours) {
        summaryData.get(summary.getName()).addHours(hours);
    }

    public void addHoursUOFexcl(double hours) {
        summaryData.get(UOF_EXCL).addHours(hours);
    }

    public void addHoursUOFexp(double hours) {
        summaryData.get(UOF_EXP).addHours(hours);
    }

    public void addBreakHours(BreakdownCategory cat, double hours) {
        breakdownData.get(cat.getName()).addHours(hours);
    }

    public void addBreakHours(String name, double hours) {
        breakdownData.get(name).addHours(hours);
    }

    public void incBreakOccurrence(BreakdownCategory cat) {
        breakdownData.get(cat.getName()).incrementOccurrences();
    }

    public void computeTotals() {
        summaryTotal = 0.0;
        for (ReportItem item : summaryData.values()) {
            if (!UOF_EXCL.equals(item.getName()) && !UOF_EXP.equals(item.getName())) {
                summaryTotal += item.getHours();
            }
        }
        for (ReportItem item : summaryData.values()) {
            item.calculatePercent(summaryTotal);
        }
        double schedOff = summaryData.get(SummaryName.SOF.name()).getHours();
//        summaryRunExpTotal = summaryData.get("XDT").getHours() + summaryData.get("EXR").getHours() + summaryData.get("EXN").getHours();
//        summaryRunDevTotal = summaryData.get("IDT").getHours() + summaryData.get("PDT").getHours() + summaryData.get("SDT").getHours();
        if (Double.compare(summaryTotal, schedOff) == 0) {
            availabilityExcl = 100;
            availabilityExp = 100;
        } else {
            availabilityExcl = (summaryTotal - schedOff - summaryData.get(UOF_EXCL).getHours()) / (summaryTotal - schedOff) * 100.0;
            availabilityExp = (summaryTotal - schedOff - summaryData.get(UOF_EXCL).getHours() - summaryData.get(UOF_EXP).getHours()) / (summaryTotal - schedOff) * 100.0;
        }
        breakdownTotal = 0.0;
        for (ReportItem item : breakdownData.values()) {
            breakdownTotal += item.getHours();
        }
        for (ReportItem item : breakdownData.values()) {
            item.calculatePercent(breakdownTotal);
        }
    }

    public void addItem(ExperimentReportItem another) {
        for (String key : summaryData.keySet()) {
            summaryData.get(key).addTo(another.summaryData.get(key));
        }

        for (String key : breakdownData.keySet()) {
            breakdownData.get(key).addTo(another.breakdownData.get(key));
        }
    }

    public void makePartial() {
        this.partial = true;
    }

    public void makeOngoing() {
        this.ongoing = true;
    }

    /**
     * Get accumulated summary hours
     * 
     * @param name Summary Name
     * @return 
     */
    private double summaryHours(SummaryName name) {
        if (summaryData.get(name.toString()) == null) {
            logger.log(Level.WARNING, "Invalid summary name {0}", name.toString());
            return 0;
        }
        
        return summaryData.get(name.toString()).getHours();
    }
    
    //TODO: make it generic (remove specific names like "XDT". "EXR" etc)
    /**
     * Make summary tree
     * 
     */
    public void makeSummaryTree() {
        summaryTreeRoot = new DefaultTreeNode("root", null);
        double total;

        ReportItem totalTime = new ReportItem("Total", "Total Time");
        ReportItem schedTime = new ReportItem("Scheduled", "Scheduled Time");
        ReportItem devTime = new ReportItem("Development", "Development Time");
        ReportItem exprTime = new ReportItem("Experiment", "Experiment Time");
        ReportItem unschedTime = new ReportItem("Unscheduled", "Unscheduled Time");
        ReportItem runTime = new ReportItem("Running", "Running Time");

        // add hours
        exprTime.addHours(summaryHours(SummaryName.XDT) + summaryHours(SummaryName.EXR) + summaryHours(SummaryName.EXN));
        devTime.addHours(summaryHours(SummaryName.IDT) + summaryHours(SummaryName.SDT) + summaryHours(SummaryName.PDT));
        runTime.addHours(devTime.getHours() + exprTime.getHours());
        schedTime.addHours(runTime.getHours() + summaryHours(SummaryName.SOF));
        unschedTime.addHours(summaryHours(SummaryName.UOF));
        totalTime.addHours(schedTime.getHours() + unschedTime.getHours());

        total = totalTime.getHours();

        // percents
        exprTime.calculatePercent(total);
        devTime.calculatePercent(total);
        runTime.calculatePercent(total);
        schedTime.calculatePercent(total);
        unschedTime.calculatePercent(total);
        totalTime.calculatePercent(total);

        // craete nodes
        TreeNode totalNode = new DefaultTreeNode(totalTime, summaryTreeRoot);
        TreeNode schedNode = new DefaultTreeNode(schedTime, totalNode);
        TreeNode unschedNode = new DefaultTreeNode(unschedTime, totalNode);

        TreeNode sofNode = new DefaultTreeNode(summaryData.get(SummaryName.SOF.toString()), schedNode);
        TreeNode runNode = new DefaultTreeNode(runTime, schedNode);

        TreeNode devNode = new DefaultTreeNode(devTime, runNode);
        TreeNode exprNode = new DefaultTreeNode(exprTime, runNode);

        TreeNode idtNode = new DefaultTreeNode(summaryData.get(SummaryName.IDT.toString()), devNode);
        TreeNode pdtNode = new DefaultTreeNode(summaryData.get(SummaryName.PDT.toString()), devNode);
        TreeNode sdtNode = new DefaultTreeNode(summaryData.get(SummaryName.SDT.toString()), devNode);

        TreeNode xdtNode = new DefaultTreeNode(summaryData.get(SummaryName.XDT.toString()), exprNode);
        TreeNode exrNode = new DefaultTreeNode(summaryData.get(SummaryName.EXR.toString()), exprNode);
        TreeNode exnNode = new DefaultTreeNode(summaryData.get(SummaryName.EXN.toString()), exprNode);

        TreeNode uofexlNode = new DefaultTreeNode(summaryData.get(UOF_EXCL), unschedNode);
        TreeNode uofincNode = new DefaultTreeNode(summaryData.get(UOF_EXP), unschedNode);

        expandTree(summaryTreeRoot);          
    }

    /**
     * Expand the tree widget
     * 
     * @param node 
     */
    private void expandTree(TreeNode node) {
        if (node == null) {
            logger.warning("strange root is null");
            return;
        }

        node.setExpanded(true);
        for (TreeNode tn : node.getChildren()) {
            expandTree(tn);
        }
        // logger.info("Expanded report tree");
    }   
    
    @Override
    public String toString() {
        return String.format(" Experiment Report Item:%n  Total Summary: %f%n  Total Breaks: %f%n", summaryTotal, breakdownTotal);
    }
    // ----- accessors ----

    public static String getUOF_EXCL() {
        return UOF_EXCL;
    }

    public static String getUOF_EXP() {
        return UOF_EXP;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public Map<String, ReportItem> getSummaryData() {
        return summaryData;
    }

    public Map<String, ReportItem> getBreakdownData() {
        return breakdownData;
    }

    public void addRuntime(Date start, Date end) {
        runtimes.add(new Period(start, end));
    }

    public boolean isPartial() {
        return partial;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public List<Period> getRuntimes() {
        return runtimes;
    }

    public double getSummaryTotal() {
        return summaryTotal;
    }

    public double getBreakdownTotal() {
        return breakdownTotal;
    }

    public TreeNode getSummaryTreeRoot() {
        return summaryTreeRoot;
    }

    public double getAvailabilityExcl() {
        return availabilityExcl;
    }

    public double getAvailabilityExp() {
        return availabilityExp;
    }

    public List<ReportItem> getSummaryList() {
        return summaryList;
    }

    public List<ReportItem> getBreakdownList() {
        return breakdownList;
    }
}
