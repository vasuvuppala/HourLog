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
package org.openepics.discs.hourlog.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.openepics.discs.hourlog.ent.Experiment;

/**
 * Experiment manager
 *
 * @author vuppala
 */
@Stateless
public class ExperimentEJB {

    private static final Logger logger = Logger.getLogger(ExperimentEJB.class.getName());
    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    public List<Experiment> findExperiments() {
        List<Experiment> experiments;
        TypedQuery<Experiment> query = em.createNamedQuery("Experiment.findAll", Experiment.class);
        experiments = query.getResultList();
        logger.log(Level.FINE, "experiments found: " + experiments.size());
        return experiments;
    }

    public Experiment findExperiment(int number) {
        return em.find(Experiment.class, number);
    }
}
