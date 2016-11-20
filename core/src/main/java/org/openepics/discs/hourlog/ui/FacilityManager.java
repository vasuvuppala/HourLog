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
package org.openepics.discs.hourlog.ui;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.hourlog.ejb.FacilityEJB;
import org.openepics.discs.hourlog.ent.Experiment;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Summary;
import org.openepics.discs.hourlog.auth.UserSession;

/**
 * State for facilities view
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class FacilityManager implements Serializable {

    @EJB
    private FacilityEJB facilityEJB;

    @Inject
    private UserSession userSession;

    private static final Logger logger = Logger.getLogger(FacilityManager.class.getName());

    private Summary currentSummary;
    private Experiment currentExperiment;

    public FacilityManager() {
    }

    @PostConstruct
    public void init() {
        Facility facility = userSession.getFacility();

        if (facility == null) {
            logger.log(Level.WARNING, "No facility defined!");
        } else {
            currentSummary = facilityEJB.currentSummary(facility);
            currentExperiment = facilityEJB.currentExperiment(facility);
        }
    }

    public Summary getCurrentSummary() {
        return currentSummary;
    }

    public Experiment getCurrentExperiment() {
        return currentExperiment;
    }

}
