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
package org.openepics.discs.hourlog.trep;

import org.openepics.discs.hourlog.ent.*;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.hourlog.cache.TroubleReportServiceEJB;
import org.openepics.discs.hourlog.status.ExtServiceName;
import org.openepics.discs.hourlog.status.ExternalServiceEJB;

/**
 * State for Trouble Report view
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class TroubleReportsView implements Serializable {

    @Inject
    private LazyTRModel lazyTRModel;

    @EJB
    private TroubleReportServiceEJB troubleReportServiceEJB;

    @EJB
    private ExternalServiceEJB extServiceEJB;
    
    private static final Logger logger = Logger.getLogger(TroubleReportsView.class.getName());
//    private LazyDataModel<TroubleReport> lazyModel;

//    private List<TroubleReport> troubleReports;   
    private List<TroubleReport> filteredReports;
    private TroubleReport selectedTroubleReport;
    private ExternalService TRservice;
    
    public TroubleReportsView() {
    }

    @PostConstruct
    public void init() {
        logger.log(Level.FINE, "TroubleReportsView#init: Init log Trouble Reports View");
        TRservice = extServiceEJB.findService(ExtServiceName.TROUBLE_REPORT);
    }

    public void refreshTRs() {
        logger.log(Level.FINE, "TroubleReportsView.refreshTRs: Refresh Trouble Reports");
        troubleReportServiceEJB.syncCache();
        init();
    }

    // --- getters/setters
//    public ServiceStatus getServiceStatus() {
//        return troubleReportServiceEJB.getServiceStatus();
//    }

    public ExternalService getTRservice() {
        return TRservice;
    }

    public List<TroubleReport> getFilteredReports() {
        return filteredReports;
    }

    public void setFilteredReports(List<TroubleReport> filteredReports) {
        this.filteredReports = filteredReports;
    }

    public TroubleReport getSelectedTroubleReport() {
        return selectedTroubleReport;
    }

    public void setSelectedTroubleReport(TroubleReport selectedTroubleReport) {
        this.selectedTroubleReport = selectedTroubleReport;
    }

    public LazyTRModel getLazyTRModel() {
        return lazyTRModel;
    }

}
