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
package org.openepics.discs.hourlog.trep;

import org.openepics.discs.hourlog.log.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.openepics.discs.hourlog.ent.TroubleReport;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Lazy model for trouble reports
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class LazyTRModel extends LazyDataModel<TroubleReport> {

    @EJB
    private TroubleReportEJB troubleReportEJB;
    private static final Logger logger = Logger.getLogger(LazyLogModel.class.getName());

    private List<TroubleReport> troubleReports;

    public LazyTRModel() {
    }

    @PostConstruct
    public void init() {
        logger.log(Level.FINE, "LazyTRModel#init: Lazy TR model init");
        if (troubleReportEJB == null) {
            logger.log(Level.FINE, "LazyTRModel#init: Lazy log mode: eventEJB is null");
        }
    }

    /**
     * Get trouble report corresponding to a TR row on the view
     *
     * @param rowKey
     * @return the Trouble Report
     */
    @Override
    public TroubleReport getRowData(String rowKey) {
        logger.log(Level.FINE, "LazyTRModel#getRowData: key {0}", rowKey);
        TroubleReport treport = troubleReportEJB.findTroubleReport(Integer.parseInt(rowKey));
        return treport;
    }

    /**
     * Gets key for a trouble report
     *
     * @param treport
     * @return
     */
    @Override
    public Object getRowKey(TroubleReport treport) {
        logger.log(Level.FINE, "LazyTRModel#getRowkey: ");
        // return log.getEvent().getEventId();
        return treport.getId();
    }

    /**
     * Fetches a page of trouble reports for the given period
     *
     * @param first
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param filters
     * @return
     */
    @Override
    public List<TroubleReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        logger.log(Level.FINE, "LazyTRModel#load: first {0} pageSize {1}", new Object[]{first, pageSize});

        troubleReports = troubleReportEJB.findTroubleReports(first, pageSize, sortField, sortOrder, filters);

        this.setRowCount(troubleReportEJB.countTroubleReports(filters));

        return troubleReports;
    }
}
