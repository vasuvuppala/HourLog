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
import org.openepics.discs.hourlog.ejb.HourLogEJB;
import org.openepics.discs.hourlog.ent.ExternalService;
import org.openepics.discs.hourlog.ent.TrainingRecord;
import org.openepics.discs.hourlog.status.ExtServiceName;
import org.openepics.discs.hourlog.status.ExternalServiceEJB;

/**
 * State for Trainings View
 *
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class TrainingView implements Serializable {

    @EJB
    private HourLogEJB hourlogEJB;
    @EJB
    private TrainingServiceEJB trainingServiceEJB;
    @EJB
    private ExternalServiceEJB extServiceEJB;
    
    private static final Logger logger = Logger.getLogger(TrainingView.class.getName());

    private List<TrainingRecord> trainingRecords;
    private List<TrainingRecord> filteredTrecords;
    private ExternalService trainingService;
    
    public TrainingView() {
    }

    @PostConstruct
    public void init() {
        // logger.log(Level.FINE, "ExperimentManager.init: Init ExperimentManager"); 
        trainingRecords = hourlogEJB.findTrainingRecord();
        logger.log(Level.FINE, "Found number of training records: {0}", trainingRecords.size());
        trainingService = extServiceEJB.findService(ExtServiceName.TRAINING);
    }

    /**
     * Refresh training records
     * 
     */
    public void refreshTrainingCache() {
        logger.log(Level.FINE, "Refresh Training Cache");
        trainingServiceEJB.syncCache();
        init();
    }
    
    // --- getters/setters

    public List<TrainingRecord> getTrainingRecords() {
        return trainingRecords;
    }

    public List<TrainingRecord> getFilteredTrecords() {
        return filteredTrecords;
    }

    public void setFilteredTrecords(List<TrainingRecord> filteredTrecords) {
        this.filteredTrecords = filteredTrecords;
    }

    public ExternalService getTrainingService() {
        return trainingService;
    }

}
