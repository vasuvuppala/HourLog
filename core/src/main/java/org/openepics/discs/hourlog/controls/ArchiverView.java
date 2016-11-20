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
package org.openepics.discs.hourlog.controls;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.openepics.discs.hourlog.log.StatusfulLogEntry;
import org.openepics.discs.hourlog.controls.ArchiverResponse.ArchData;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;

/**
 * State of channel graph page
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class ArchiverView implements Serializable {

    @EJB
    private ArchiverEJB archiverEJB;

    private static final Logger logger = Logger.getLogger(ArchiverView.class.getName());

    private List<ArchiverResponse> pvDataList;
    // private ArchiverResponse pvData;
    private List<LineChartModel> archiverCharts = new ArrayList<>();
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yy-MM-dd'T'HH:mm:ss");
    private static final int ARCHIVE_WINDOW = 5;
    private static final long ARCHIVE_WINDOW_SECONDS = 1000L;
    private Date fromDate;
    private StringBuilder signalMessage = new StringBuilder();

    public ArchiverView() {
    }

    /**
     * Initialize
     *
     * @author vuppala
     */
    @PostConstruct
    public void init() {
        logger.log(Level.FINE, "ArchiverView#init: initializing PVs");
    }

    public void onGetArchData(StatusfulLogEntry logent) {
        logger.log(Level.FINE, "ArchiverView#onGetArchData: Getting signals from archiver");
        Date current = logent.getLogEntry().getOccurredAt();
        fromDate = Utility.addMinutesToDate(current, -ARCHIVE_WINDOW);
        Date to = Utility.addMinutesToDate(current, ARCHIVE_WINDOW);
        pvDataList = archiverEJB.fetchSignals(fromDate, to);
        logger.log(Level.FINE, "ArchiverView#onGetArchData: Got signals from archiver");

        makeCharts();
    }

    private void makeCharts() {
        logger.log(Level.FINE, "ArchiverView#makeChart: making chart");

        if (pvDataList.isEmpty()) {
            logger.log(Level.WARNING, "ArchiverView#makeChart: No signals found");
            return;
        }

        archiverCharts.clear();
        signalMessage.setLength(0); // clear it
        for (ArchiverResponse pvData : pvDataList) {
            if (pvData.getData().isEmpty()) {
                signalMessage.append(String.format("No data found for %s\n", pvData.getMeta().getName()));
                continue;
            }
            LineChartModel chart = new LineChartModel();
            chart.setShowPointLabels(false);
            // chart.setLegendPosition("e");
            chart.getAxis(AxisType.X).setLabel("Seconds");
            chart.setTitle(String.format("%s (%s)", pvData.getMeta().getDescription(), pvData.getMeta().getName()));

            ChartSeries pv = new ChartSeries();
            logger.log(Level.FINE, "ArchiverView#makeChart: adding PV {0}", pvData.getMeta().getName());
            // pv.setLabel(pvData.getMeta().getName());

            for (ArchData archData : pvData.getData()) {
                pv.set(archData.getSeconds() % ARCHIVE_WINDOW_SECONDS, archData.getValue());
                // pv.set(epochSecsToDate(archData.getSeconds()), archData.getValue());
                // logger.log(Level.FINE, "ArchiverView#makeChart: chart set {0}, {1}", new Object[]{epochSecsToDate(archData.getSeconds()), archData.getValue()});
            }
            chart.addSeries(pv);
            archiverCharts.add(chart);
        }

        logger.log(Level.FINE, "ArchiverView#makeChart: Made charts");
    }

    private String epochSecsToDate(long secs) {
        Date date = new Date(secs * 1000L);
        return fmt.format(date);
    }

    public List<ArchiverResponse> getPvdata() {
        return pvDataList;
    }

    public List<ArchiverResponse> getPvDataList() {
        return pvDataList;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public StringBuilder getSignalMessage() {
        return signalMessage;
    }

    public List<LineChartModel> getArchiverCharts() {
        return archiverCharts;
    }

}
