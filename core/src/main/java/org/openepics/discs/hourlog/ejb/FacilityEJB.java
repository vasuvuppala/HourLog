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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.openepics.discs.hourlog.ent.Experiment;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Summary;
import org.openepics.discs.hourlog.ent.SummaryEvent;
import org.openepics.discs.hourlog.util.Utility;

/**
 * Facility Manager
 *
 * @author vuppala
 */
@Stateless
public class FacilityEJB {

    @EJB
    SnapshotEJB snapshotEJB;
   
    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    private static final Logger logger = Logger.getLogger(FacilityEJB.class.getName());

    /**
     * Find facility given its id
     *
     * @param id
     * @return the facility
     */
    public Facility findFacility(short id) {
        return em.find(Facility.class, id);
    }

    /**
     * Find all facilities
     *
     * @author vuppala
     * @return list of facilities
     */
    public List<Facility> findAllFacilities() {
        List<Facility> facs;
        TypedQuery<Facility> query = em.createNamedQuery("Facility.findAll", Facility.class);      

        facs = query.getResultList();
        return facs;
    }
    
    /**
     * Find all facilities that are in operation
     *
     * @author vuppala
     * @return list of facilities
     */
    public List<Facility> findFacility() {
        List<Facility> facs;
        //TypedQuery<Facility> query = em.createNamedQuery("Facility.findAll", Facility.class);
        TypedQuery<Facility> query = em.createQuery("SELECT f FROM Facility f WHERE f.inOperation = TRUE", Facility.class);

        facs = query.getResultList();
        return facs;
    }

    /**
     * Find a facility given its name
     *
     * @author vuppala
     * @param name
     * @return the facility
     */
    public Facility findFacility(String name) {
        List<Facility> facs;
        TypedQuery<Facility> query = em.createNamedQuery("Facility.findByFacilityName", Facility.class)
                .setParameter("facilityName", name);

        facs = query.getResultList();
        if (facs == null || facs.isEmpty()) {
            return null;
        } else {
            return facs.get(0);
        }
    }

    /**
     * Save the given facility in database
     *
     * @param fac the facility
     */
    public void saveFacility(Facility fac) {
        if (fac.getFacilityId() == null) {
            em.persist(fac);
        } else {
            em.merge(fac);
        }
        logger.log(Level.FINE, "HourLogEJB#saveFacility: facility saved - {0}", fac.getFacilityId());
    }

    /**
     * Delete the given facility
     *
     * @param facility
     */
    public void deleteFacility(Facility facility) {
        Facility fac = em.find(Facility.class, facility.getFacilityId());
        em.remove(fac);
    }

    /**
     * What is the current summary status for a given facility?
     *
     * @author vuppala
     * @param facility The facility
     * @return current facility status
     */
    public Summary currentSummary(Facility facility) {
        List<Summary> stats;

        TypedQuery<Summary> query = em.createQuery("SELECT b.summary FROM SummaryEvent b WHERE b.event.facility = :facility ORDER BY b.event.occurredAt DESC", Summary.class)
                .setParameter("facility", facility)
                .setFirstResult(0)
                .setMaxResults(1);
        stats = query.getResultList();

        if (stats.isEmpty()) {
            return null;
        } else {
            return stats.get(0);
        }
    }

    /**
     * Default facility.
     *
     * @author vuppala
     * @return The default facility
     */
    public Facility defaultFacility() {
        List<Facility> facs;
        TypedQuery<Facility> facQuery = em.createQuery("SELECT f FROM Facility f ORDER BY f.facilityId ASC", Facility.class)
                .setFirstResult(0)
                .setMaxResults(1);
        facs = facQuery.getResultList();
        if (facs == null || facs.isEmpty()) {
            logger.log(Level.WARNING, "FacilityEJB#defaultFacility: No facilities defined!");
            return null;
        } else {
            return facs.get(0);
        }
    }

    /**
     * Finds a page summary events in a given facility since a given time.
     *
     * @author vuppala
     * @param facility
     *
     * @param date The time for which status is needed
     * @param start start record
     * @param numOfRecs number of records
     * @return Facility status at the given time
     */
    public List<SummaryEvent> summarySince(Facility facility, Date date, int start, int numOfRecs) {
        Date today = Utility.currentDate();
        List<SummaryEvent> emptyList = new ArrayList<>();

        //ToDo: is this really needed?
        if (date.after(today)) {
            logger.info("Invalid date. It is in future:  " + date);
            return emptyList;
        }

        TypedQuery<SummaryEvent> query = em.createQuery("SELECT f FROM SummaryEvent f WHERE f.event.facility = :facility AND f.event.obsoletedBy IS NULL AND  f.event.occurredAt <= :givenDate ORDER BY f.event.occurredAt DESC", SummaryEvent.class)
                .setParameter("givenDate", date)
                .setParameter("facility", facility)
                .setFirstResult(0)
                .setMaxResults(1);

        SummaryEvent firstEvent;
        List<SummaryEvent> fslist = query.getResultList();
        if (fslist == null || fslist.isEmpty()) {
            // logger.info("No facility status at " + date);
            firstEvent = null;
        } else {
            firstEvent = fslist.get(0);
        }

        query = em.createQuery("SELECT f FROM SummaryEvent f WHERE f.event.facility = :facility AND f.event.obsoletedBy IS NULL AND f.event.occurredAt > :givenDate ORDER BY f.event.occurredAt DESC", SummaryEvent.class)
                .setParameter("givenDate", date)
                .setParameter("facility", facility)
                .setFirstResult(start)
                .setMaxResults(numOfRecs);

        fslist = query.getResultList();
        if (fslist.size() < numOfRecs && firstEvent != null) {
            fslist.add(firstEvent);
        }

        return fslist;
    }

    
    
    /**
     * Finds current facility's summary status at a given time.
     *
     * @author vuppala
     * @param date The time for which status is needed
     * @return Facility status at the given time
     */
//    public Summary summaryAt(Date date) {
//        Date today = Utility.currentDate();
//
//        //ToDo: throws an exception instead
//        if (date.after(today)) {
//            logger.log(Level.WARNING, "FacilityEJB#summaryAt: Invalid date. It is in future: {0} ", date);
//            return null;
//        }
//
//        TypedQuery<SummaryEvent> query = em.createQuery("SELECT f FROM SummaryEvent f WHERE f.event.occurredAt <= :givenDate ORDER BY f.event.occurredAt DESC", SummaryEvent.class)
//                .setParameter("givenDate", date)
//                .setFirstResult(0)
//                .setMaxResults(1);
//
//        List<SummaryEvent> fslist = query.getResultList();
//        if (fslist == null || fslist.isEmpty()) {
//            //ToDo: throws an exception instead
//            logger.info("No facility status at " + date);
//            return null;
//        } else {
//            return fslist.get(0).getSummary();
//        }
//    }
    /**
     * Current Experiment running in a facility.
     *
     * @author vuppala
     * @param facility The facility
     * @return Current experiment
     */
    public Experiment currentExperiment(Facility facility) {
        List<Experiment> exps;
        TypedQuery<Experiment> facQuery = em.createQuery("SELECT f.experiment FROM ExprEvent f WHERE f.event.facility = :facility ORDER BY f.event.occurredAt DESC", Experiment.class)
                .setParameter("facility", facility)
                .setFirstResult(0)
                .setMaxResults(1);

        exps = facQuery.getResultList();
        if (exps == null || exps.isEmpty()) {
            return null;
        } else {
            return exps.get(0);
        }
    }

}
