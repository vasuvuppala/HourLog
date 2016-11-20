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
package org.openepics.discs.hourlog.cache;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.openepics.discs.hourlog.ejb.ExperimentEJB;
import org.openepics.discs.hourlog.ent.Experiment;
import org.openepics.discs.hourlog.ent.ExternalService;
import org.openepics.discs.hourlog.status.ExtServiceName;
import org.openepics.discs.hourlog.status.ExternalServiceEJB;

/**
 * Cached approved experiments.
 *
 * TODO: Make it request scoped or load lazily?
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class ExperimentManager implements Serializable {

    @EJB
    private ExperimentEJB experimentEJB;
    @EJB
    private ExperimentServiceEJB experimentServiceEJB;
    @EJB
    private ExternalServiceEJB extServiceEJB;
    
    private static final Logger logger = Logger.getLogger(ExperimentManager.class.getName());

    private List<Experiment> experiments;
    private List<Experiment> filteredExperiments;
    private ExternalService expService;

    public ExperimentManager() {
    }

    @PostConstruct
    public void init() {
        // logger.log(Level.FINE, "ExperimentManager.init: Init ExperimentManager"); 
        // experimentServiceEJB.syncCache(); // ToDo: may not be a good idea to refresh cache everytime but users want it
        experiments = experimentEJB.findExperiments();
        logger.log(Level.FINE, "Found number of experiments: {0}", experiments.size());
        expService = extServiceEJB.findService(ExtServiceName.EXPERIMENTS);
    }

    /**
     * Refresh the experiments cache.
     * 
     */
    public void refreshExperiments() {
        logger.log(Level.FINE, "Refreshing Experiments");
        experimentServiceEJB.syncCache();
        init();
    }
    
    // --- getters/setters
    public List<Experiment> getExperiments() {
        return experiments;
    }

    public List<Experiment> getFilteredExperiments() {
        return filteredExperiments;
    }

    public void setFilteredExperiments(List<Experiment> filteredExperiments) {
        this.filteredExperiments = filteredExperiments;
    }

    public ExternalService getExpService() {
        return expService;
    }
}
