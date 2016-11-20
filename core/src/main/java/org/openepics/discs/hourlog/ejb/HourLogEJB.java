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
import org.openepics.discs.hourlog.ent.Beam;
import org.openepics.discs.hourlog.ent.BeamSystem;
import org.openepics.discs.hourlog.ent.BreakdownCategory;
import org.openepics.discs.hourlog.ent.ControlSignal;
import org.openepics.discs.hourlog.ent.Element;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.ent.Summary;
import org.openepics.discs.hourlog.ent.Mode;
import org.openepics.discs.hourlog.ent.Source;
import org.openepics.discs.hourlog.ent.TrainingRecord;
import org.openepics.discs.hourlog.ent.Vault;
import org.openepics.discs.hourlog.util.SummaryName;

/**
 * Manager of base entities (source, vault, mode etc)
 *
 * @author vuppala
 */
@Stateless
public class HourLogEJB {

    private static final Logger logger = Logger.getLogger(HourLogEJB.class.getName());
    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    // ------------------------ Beam ------------------------
    /**
     * find all beams
     *
     * @return beams
     */
    public List<Beam> findBeams() {
        List<Beam> beams;
        TypedQuery<Beam> query = em.createQuery("SELECT b FROM Beam b ORDER BY b.beamSystem,b.element.symbol, b.massNumber, b.charge, b.energy", Beam.class);
        beams = query.getResultList();
        logger.log(Level.FINE, "HourLogEJB#findBeams: Beam found: {0}", beams.size());
        return beams;
    }

    /**
     * find beams for a given beam system
     *
     * @param bsystem the beam system
     * @return beams
     */
    public List<Beam> findBeams(BeamSystem bsystem) {
        List<Beam> beams;
        TypedQuery<Beam> query = em.createQuery("SELECT b FROM Beam b WHERE b.beamSystem = :bsystem ORDER BY b.element.symbol, b.massNumber, b.charge, b.energy", Beam.class)
                .setParameter("bsystem", bsystem);
        beams = query.getResultList();
        logger.log(Level.FINE, "beams found: {0}", beams.size());
        return beams;
    }

    /**
     * save a given beam in the database
     *
     * @param beam
     */
    public void saveBeam(Beam beam) {
        if (beam.getBeamId() == null) {
            em.persist(beam);
        } else {
            em.merge(beam);
        }
        logger.log(Level.FINE, "HourLogEJB#saveBeam: beam saved - {0}", beam.getBeamId());
    }

    /**
     * delete a beam
     *
     * @param beam
     */
    public void deleteBeam(Beam beam) {
        Beam mbeam = em.find(Beam.class, beam.getBeamId());
        em.remove(mbeam);
    }

    /**
     * find a beam given its id
     *
     * @param id
     * @return the beam
     */
    public Beam findBeam(short id) {
        return em.find(Beam.class, id);
    }

    //------------------------
    // TODO: Remove
//    public List<BreakdownStatus> findBreakdownStatus() {
//        List<BreakdownStatus> bstats;
//        TypedQuery<BreakdownStatus> query = em.createNamedQuery("BreakdownStatus.findAll", BreakdownStatus.class);
//        bstats = query.getResultList();
//        logger.log(Level.FINE, "breakdown status found: {0}", bstats.size());
//        return bstats;
//    }
//
//    // TODO: Remove
//    public BreakdownStatus findBrkStatus(short id) {
//        return em.find(BreakdownStatus.class, id);
//    }

    // ------------------------ Source ------------------------
    /**
     * find all sources
     *
     * @return sources
     */
    public List<Source> findAllSources() {
        List<Source> sources;
        TypedQuery<Source> query = em.createNamedQuery("Source.findAll", Source.class);
        sources = query.getResultList();
        logger.log(Level.FINE, "sources found: {0}", sources.size());
        return sources;
    }
    /**
     * find all sources
     *
     * @return sources
     */
    public List<Source> findSources() {
        List<Source> sources;
        TypedQuery<Source> query = em.createQuery("SELECT s FROM Source s WHERE s.active = TRUE ORDER BY s.name", Source.class);
        sources = query.getResultList();
        logger.log(Level.FINE, "sources found: {0}", sources.size());
        return sources;
    }

    /**
     * find all beam sources for a given facility
     *
     * @param facility
     * @return sources
     */
    public List<Source> findSources(Facility facility) {
        List<Source> sources;
        TypedQuery<Source> query = em.createQuery("SELECT s FROM Source s WHERE s.facility = :facility AND s.active = TRUE ORDER BY s.name", Source.class)
                .setParameter("facility", facility);
        sources = query.getResultList();
        logger.log(Level.FINE, "sources found: {0}", sources.size());
        return sources;
    }

    /**
     * save a source
     *
     * @param source
     */
    public void saveSource(Source source) {
        if (source.getSourceId() == null) {
            em.persist(source);
        } else {
            em.merge(source);
        }
        logger.log(Level.FINE, "HourLogEJB#saveSource: source saved - {0}", source.getSourceId());
    }

    /**
     * delete a given source
     *
     * @param source
     */
    public void deleteSource(Source source) {
        Source src = em.find(Source.class, source.getSourceId());
        em.remove(src);
    }

    /**
     * find a source given its id
     *
     * @param id
     * @return the source
     */
    public Source findSource(short id) {
        return em.find(Source.class, id);
    }

    // ------------------------ Vaults ------------------------
    /**
     * Find all vaults
     *
     * @return vaults
     */
    public List<Vault> findAllVaults() {
        List<Vault> vaults;
        TypedQuery<Vault> query = em.createNamedQuery("Vault.findAll", Vault.class);
        vaults = query.getResultList();
        logger.log(Level.FINE, "vaults found: {0}", vaults.size());
        return vaults;
    }
    /**
     * Find active vaults
     *
     * @return vaults
     */
    public List<Vault> findVaults() {
        List<Vault> vaults;
        TypedQuery<Vault> query = em.createQuery("SELECT v FROM Vault v WHERE v.active = TRUE ORDER BY v.name", Vault.class);
        vaults = query.getResultList();
        logger.log(Level.FINE, "vaults found: {0}", vaults.size());
        return vaults;
    }

    /**
     * find all vaults for a given facility
     *
     * @param facility
     * @return vaults
     */
    public List<Vault> findVaults(Facility facility) {
        List<Vault> vaults;
        TypedQuery<Vault> query = em.createQuery("SELECT v FROM Vault v WHERE v.facility = :facility AND v.active = TRUE ORDER BY v.name", Vault.class)
                .setParameter("facility", facility);
        vaults = query.getResultList();
        logger.log(Level.FINE, "vaults found: {0}", vaults.size());
        return vaults;
    }
    
    /**
     * Find a vault given its id
     *
     * @param id
     * @return the vault
     */
    public Vault findVault(short id) {
        return em.find(Vault.class, id);
    }

    /**
     * Save the given vault
     *
     * @param vault
     */
    public void saveVault(Vault vault) {
        if (vault.getVaultId() == null) {
            em.persist(vault);
        } else {
            em.merge(vault);
        }
        logger.log(Level.FINE, "HourLogEJB#saveVault: vault saved - {0}", vault.getVaultId());
    }

    /**
     * Delete the given vault
     *
     * @param vault
     */
    public void deleteVault(Vault vault) {
        Vault mvault = em.find(Vault.class, vault.getVaultId());
        em.remove(mvault);
    }

    // ------------------------ Summaries ------------------------
    /**
     * Find all summaries
     *
     * @return summaries
     */
    public List<Summary> findAllSummary() {
        List<Summary> fstats;
        TypedQuery<Summary> query = em.createQuery("SELECT s FROM Summary s ORDER BY s.name", Summary.class);
        fstats = query.getResultList();
        logger.log(Level.FINE, "facility status found: {0}", fstats.size());
        return fstats;
    }
    /**
     * Find all summaries
     *
     * @return summaries
     */
    public List<Summary> findSummary() {
        List<Summary> fstats;
        TypedQuery<Summary> query = em.createQuery("SELECT s FROM Summary s WHERE s.active = TRUE ORDER BY s.name", Summary.class);
        fstats = query.getResultList();
        logger.log(Level.FINE, "summary found: {0}", fstats.size());
        return fstats;
    }

    /**
     * Find summary given its id
     *
     * @param id
     * @return the summary
     */
    public Summary findSummary(short id) {
        return em.find(Summary.class, id);
    }

    /**
     * Save the given summary
     *
     * @param summary
     */
    public void saveSummary(Summary summary) {
        if (summary.getSummaryId() == null) {
            em.persist(summary);
        } else {
            em.merge(summary);
        }
        logger.log(Level.FINE, "summary saved - {0}", summary.getSummaryId());
    }

    /**
     * Delete the given summary
     *
     * @param summary
     */
    public void deleteSummary(Summary summary) {
        Summary sum = em.find(Summary.class, summary.getSummaryId());
        em.remove(sum);
    }

    /**
     * Gets the summary event for unscheduled off
     *
     * @return the summary event
     */
    public Summary unscheduledOff() {
        
        String queryText = "SELECT e.summary FROM Summary e WHERE e.event.facility = :facility AND e.event.obsoletedBy is NULL ORDER BY e.event.occurredAt DESC";
        TypedQuery<Summary> query = em.createQuery("SELECT s FROM Summary s WHERE s.name = :name", Summary.class)
                .setParameter("name", SummaryName.UOF.toString())
                .setFirstResult(0)
                .setMaxResults(1);
        List<Summary> fstats = query.getResultList();
        if (fstats == null || fstats.isEmpty()) {
            logger.log(Level.SEVERE, "HourLogEJB#unscheduledOff: cannot determine unscheduled Off!");
            return null;
        }
        return fstats.get(0);
    }
    
    // ------------------------ Breakdown Categories ------------------------
    /**
     * Find all breakdown categories
     *
     * @author vuppala
     * @return breakdown categories
     */
    public List<BreakdownCategory> findAllBrkCategories() {
        TypedQuery<BreakdownCategory> query = em.createNamedQuery("BreakdownCategory.findAll", BreakdownCategory.class);
        List<BreakdownCategory> bcats;

        bcats = query.getResultList();
        return bcats;
    }
    /**
     * Find all breakdown categories
     *
     * @author vuppala
     * @return breakdown categories
     */
    public List<BreakdownCategory> findBrkCategories() {
        TypedQuery<BreakdownCategory> query = em.createQuery("SELECT b FROM BreakdownCategory b WHERE b.active = TRUE ORDER BY b.name", BreakdownCategory.class);
        List<BreakdownCategory> bcats;

        bcats = query.getResultList();
        return bcats;
    }
    
    /**
     * Find the breakdown category OTHER
     *
     * @author vuppala
     * @return breakdown categories
     */
    public BreakdownCategory findBrkCategoryOTH() {
        TypedQuery<BreakdownCategory> query = em.createQuery("SELECT b FROM BreakdownCategory b WHERE b.name = 'OTH'", BreakdownCategory.class);
        List<BreakdownCategory> bcats;

        bcats = query.getResultList();
        if (bcats.isEmpty()) {
            logger.log(Level.SEVERE, "Breakdown Category OTH not found!");
            return null;
        } else {
            return bcats.get(0);
        }       
    }

    /**
     * Find a breakdown category given its id
     *
     * @param id
     * @return breakdown category
     */
    public BreakdownCategory findBrkCategory(int id) {
        return em.find(BreakdownCategory.class, id);
    }

    /**
     * Save a given breakdown category
     *
     * @param brkcat the breakdown category
     */
    public void saveBrkCategory(BreakdownCategory brkcat) {
        if (brkcat.getBrkcatId() == null) {
            em.persist(brkcat);
        } else {
            em.merge(brkcat);
        }
        logger.log(Level.FINE, "HourLogEJB#saveBrkCategory: breakdown category saved - {0}", brkcat.getBrkcatId());
    }

    /**
     * Delete the given breakdown category
     *
     * @param brkcat the breakdown category
     */
    public void deleteBrkCategory(BreakdownCategory brkcat) {
        BreakdownCategory cat = em.find(BreakdownCategory.class, brkcat.getBrkcatId());
        em.remove(cat);
    }

    // ------------------- Beam Systems -------------------
    /**
     * Find all beam systems
     *
     * @return beam systems
     */
    public List<BeamSystem> findAllBeamSystems() {
        List<BeamSystem> bsystems;
        TypedQuery<BeamSystem> query = em.createNamedQuery("BeamSystem.findAll", BeamSystem.class);

        bsystems = query.getResultList();
        logger.log(Level.FINE, "beam systems found: {0}", bsystems.size());
        return bsystems;
    }

    /**
     * Find active beam systems
     *
     * @return beam systems
     */
    public List<BeamSystem> findBeamSystems() {
        List<BeamSystem> bsystems;
        TypedQuery<BeamSystem> query = em.createQuery("SELECT b FROM BeamSystem b WHERE b.active = TRUE ORDER BY b.beamSystemId", BeamSystem.class);

        bsystems = query.getResultList();
        logger.log(Level.FINE, "beam systems found: {0}", bsystems.size());
        return bsystems;
    }
    
    /**
     * Find active beam systems of a facility.
     *
     * @param facility
     * @return beam systems
     */
    public List<BeamSystem> findBeamSystems(Facility facility) {
        List<BeamSystem> bsystems;
        TypedQuery<BeamSystem> query = em.createQuery("SELECT b FROM BeamSystem b WHERE b.facility = :facility AND b.active = TRUE ORDER BY b.beamSystemId", BeamSystem.class)
                .setParameter("facility", facility);
        bsystems = query.getResultList();
        logger.log(Level.FINE, "beam systems found: {0}", bsystems.size());
        return bsystems;
    }
    
    /**
     * Find all beam systems of a facility.
     *
     * @param facility
     * @return beam systems
     */
    public List<BeamSystem> findAllBeamSystems(Facility facility) {
        List<BeamSystem> bsystems;
        TypedQuery<BeamSystem> query = em.createQuery("SELECT b FROM BeamSystem b WHERE b.facility = :facility ORDER BY b.beamSystemId", BeamSystem.class)
                .setParameter("facility", facility);
        bsystems = query.getResultList();
        logger.log(Level.FINE, "beam systems found: {0}", bsystems.size());
        return bsystems;
    }

    /**
     * Find a beam system given its id
     *
     * @param id
     * @return beam system
     */
    public BeamSystem findBeamSystem(short id) {
        return em.find(BeamSystem.class, id);
    }

    /**
     * Save the given beam system
     *
     * @param bsys the beam system
     */
    public void saveBeamSystem(BeamSystem bsys) {
        if (bsys.getBeamSystemId() == null) {
            em.persist(bsys);
        } else {
            em.merge(bsys);
        }
        logger.log(Level.FINE, "HourLogEJB#saveBeamSystem: beam system saved - {0}", bsys.getBeamSystemId());
    }

    /**
     * Delete the given beam system
     *
     * @param bsys the beam system
     */
    public void deleteBeamSystem(BeamSystem bsys) {
        BeamSystem sys = em.find(BeamSystem.class, bsys.getBeamSystemId());
        em.remove(sys);
    }

    // ------------------- Elements -------------------
    /**
     * Find all the elements
     *
     * @return elements
     */
    public List<Element> findElements() {
        List<Element> elements;
        TypedQuery<Element> query = em.createQuery("SELECT e FROM Element e ORDER BY e.name", Element.class);
        elements = query.getResultList();
        logger.log(Level.FINE, "elements found: {0}", elements.size());
        return elements;
    }

    /**
     * Find an element given its id
     *
     * @param id
     * @return the element
     */
    public Element findElement(short id) {
        return em.find(Element.class, id);
    }

    /**
     * Save the given element
     *
     * @param element
     */
    public void saveElement(Element element) {
        if (element.getElementId() == null) {
            em.persist(element);
        } else {
            em.merge(element);
        }
        logger.log(Level.FINE, "HourLogEJB#saveBeamSystem: beam system saved - {0}", element.getElementId());
    }

    /**
     * Delete the given element
     *
     * @param element
     */
    public void deleteElement(Element element) {
        Element elem = em.find(Element.class, element.getElementId());
        em.remove(elem);
    }

    // ------------------- Logbooks -------------------
    /**
     * Find all the log books
     *
     * @return logbooks
     */
    public List<Logbook> findLogbooks() {
        List<Logbook> logbooks;
        TypedQuery<Logbook> query = em.createNamedQuery("Logbook.findAll", Logbook.class);
        logbooks = query.getResultList();
        logger.log(Level.FINE, "Logbooks found: {0}", logbooks.size());
        return logbooks;
    }

    /**
     * Find a logbook given its id
     *
     * @param id
     * @return
     */
    public Logbook findLogbook(short id) {
        return em.find(Logbook.class, id);
    }

    /**
     * Save the given logbook
     *
     * @param logbook
     */
    public void saveLogbook(Logbook logbook) {
        if (logbook.getLogbookId() == null) {
            em.persist(logbook);
        } else {
            em.merge(logbook);
        }
        logger.log(Level.FINE, "HourLogEJB#saveLogbook: Logbook saved - {0}", logbook.getLogbookId());
    }

    /**
     * Delete the given logbook
     *
     * @param logbook
     */
    public void deleteLogbook(Logbook logbook) {
        Logbook lb = em.find(Logbook.class, logbook.getLogbookId());
        em.remove(lb);
    }

    // ------------------- Modes -------------------
    /**
     * Find all the modes
     *
     * @return modes
     */
    public List<Mode> findAllModes() {
        List<Mode> modes;
        TypedQuery<Mode> query = em.createNamedQuery("Mode.findAll", Mode.class);
        modes = query.getResultList();
        logger.log(Level.FINE, "modes found: {0}", modes.size());
        return modes;
    }
    
    /**
     * Find active modes
     *
     * @return modes
     */
    public List<Mode> findModes() {
        List<Mode> modes;
        TypedQuery<Mode> query = em.createQuery("SELECT m FROM Mode m WHERE m.active = TRUE ORDER BY m.name", Mode.class);
        modes = query.getResultList();
        logger.log(Level.FINE, "modes found: {0}", modes.size());
        return modes;
    }

    /**
     * Find a mode given its id
     *
     * @param id
     * @return the mode
     */
    public Mode findMode(short id) {
        return em.find(Mode.class, id);
    }

    /**
     * Save a given mode
     *
     * @param mode
     */
    public void saveMode(Mode mode) {
        if (mode.getModeId() == null) {
            em.persist(mode);
        } else {
            em.merge(mode);
        }
        logger.log(Level.FINE, "HourLogEJB#saveMode: Mode saved - {0}", mode.getModeId());
    }

    /**
     * Delete a given mode
     *
     * @param mode
     */
    public void deleteMode(Mode mode) {
        Mode lb = em.find(Mode.class, mode.getModeId());
        em.remove(lb);
    }

    // ------------------- Control Signals -------------------
    /**
     * Find all the controls signals related to current facility
     *
     * @return signals
     */
    public List<ControlSignal> findSignals() {
        List<ControlSignal> signals;
        TypedQuery<ControlSignal> query = em.createNamedQuery("ControlSignal.findAll", ControlSignal.class);
        signals = query.getResultList();
        logger.log(Level.FINE, "HourLogEJB#findSignals: ControlSignal found: {0}", signals.size());
        return signals;
    }

    /**
     * Find control signal given its id
     *
     * @param id
     * @return the signal
     */
    public ControlSignal findSignal(short id) {
        return em.find(ControlSignal.class, id);
    }

    /**
     *
     * @param signal
     */
    public void saveSignal(ControlSignal signal) {
        if (signal.getSignalId() == null) {
            em.persist(signal);
        } else {
            em.merge(signal);
        }
        logger.log(Level.FINE, "HourLogEJB#saveSignal: Control Signal saved - {0}", signal.getName());
    }

    /**
     * Delete the given signal
     *
     * @param signal
     */
    public void deleteSignal(ControlSignal signal) {
        ControlSignal sig = em.find(ControlSignal.class, signal.getSignalId());
        em.remove(sig);
    }
    
    // ------------------- Training Records -------------------
    /**
     * Find all training records 
     *
     * @return training records
     */
    public List<TrainingRecord> findTrainingRecord() {
        List<TrainingRecord> trecords;
        TypedQuery<TrainingRecord> query = em.createNamedQuery("TrainingRecord.findAll", TrainingRecord.class);
        trecords = query.getResultList();
        logger.log(Level.FINE, "HourLogEJB#TrainingRecord: Training Records found: {0}", trecords.size());
        
        return trecords;
    }

}
