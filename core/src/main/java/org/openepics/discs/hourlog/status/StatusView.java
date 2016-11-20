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
package org.openepics.discs.hourlog.status;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.openepics.discs.hourlog.ent.ExternalService;

/**
 * State for Hour Log Status View
 *
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class StatusView implements Serializable {

    @EJB
    private ExternalServiceEJB extServiceEJB;
    private static final Logger logger = Logger.getLogger(StatusView.class.getName());

    private List<ExternalService> externalService;

    public StatusView() {
    }

    @PostConstruct
    public void init() {
        // logger.log(Level.FINE, "ExperimentManager.init: Init ExperimentManager"); 
        externalService = extServiceEJB.findExtService();        
        logger.log(Level.FINE, "Found number of service records: {0}", externalService.size());
    }

    // --- getters/setters

    public List<ExternalService> getExternalService() {
        return externalService;
    }
}
