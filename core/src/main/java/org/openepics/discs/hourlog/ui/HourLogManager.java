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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.openepics.discs.hourlog.ejb.HourLogEJB;
import org.openepics.discs.hourlog.ent.Beam;
import org.openepics.discs.hourlog.ent.BeamSystem;
import org.openepics.discs.hourlog.ent.Element;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Summary;
import org.openepics.discs.hourlog.ent.Mode;
import org.openepics.discs.hourlog.ent.Source;
import org.openepics.discs.hourlog.ent.Vault;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.ejb.FacilityEJB;
import org.openepics.discs.hourlog.ent.BreakdownCategory;
import org.openepics.discs.hourlog.ent.ControlSignal;
import org.openepics.discs.hourlog.ent.Logbook;

/**
 * Hour Log base data: vaults, sources, beams, facilities, elements etc
 *
 * @author vuppala
 *
 */
// ToDo: make it request scoped?
@Named
@ViewScoped
public class HourLogManager implements Serializable {

    @EJB
    private HourLogEJB hourLogEJB;
    @EJB
    private FacilityEJB facilityEJB;
    private static final Logger logger = Logger.getLogger(HourLogManager.class.getName());
    @Inject
    UserSession userSession;

    private List<Mode> modes;
    private List<Mode> allModes;
    private List<Vault> vaults;
    private List<Vault> allVaults;
    private List<Source> sources;
    private List<Source> allSources;
    private List<Summary> summaries;
    private List<Summary> allSummaries;
    private List<Facility> facilities;
    private List<BeamSystem> beamSystems;
    private List<BeamSystem> allBeamSystems;
    private List<Beam> beams;
    private List<Element> elements;
    private List<Logbook> logbooks;
    private List<ControlSignal> signals;
    private List<BreakdownCategory> breakdownCatgories;

    public HourLogManager() {
    }

    @PostConstruct
    public void init() {
        modes = hourLogEJB.findModes();
        allModes = hourLogEJB.findAllModes();
        
        vaults = hourLogEJB.findVaults(userSession.getFacility());
        allVaults = hourLogEJB.findAllVaults();
        
        sources = hourLogEJB.findSources(userSession.getFacility());
        allSources = hourLogEJB.findSources();
        
        summaries = hourLogEJB.findSummary();
        allSummaries = hourLogEJB.findAllSummary();
        
        beamSystems = hourLogEJB.findBeamSystems(userSession.getFacility());
        allBeamSystems = hourLogEJB.findAllBeamSystems();
        
        elements = hourLogEJB.findElements();
        breakdownCatgories = hourLogEJB.findBrkCategories();
        facilities = facilityEJB.findAllFacilities();
        beams = hourLogEJB.findBeams();
        logbooks = hourLogEJB.findLogbooks();
        signals = hourLogEJB.findSignals();
    }

    public List<Beam> findBeams(BeamSystem bsystem) {
        logger.log(Level.FINE, "HourLogManager.findBeams: entering");
        return hourLogEJB.findBeams(bsystem);
    }

    // --- getters and setters
    public List<Mode> getModes() {
        return modes;
    }

    public List<Vault> getVaults() {
        return vaults;
    }

    public List<Source> getSources() {
        // sources = hourLogEJB.findSources(userSession.getFacility()); // TODO: Not good idea. Move it
        return sources;
    }

    public List<Summary> getSummaries() {
        return summaries;
    }

    public List<Summary> getAllSummaries() {
        return allSummaries;
    }

    public List<BeamSystem> getBeamSystems() {
        // beamSystems = hourLogEJB.findBeamSystems(userSession.getFacility());  // TODO: Not good idea. Move it
        return beamSystems;
    }

    public List<Element> getElements() {
        return elements;
    }

    public List<BreakdownCategory> getBreakdownCatgories() {
        return breakdownCatgories;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public List<Beam> getBeams() {
        return beams;
    }

    public List<Source> getAllSources() {
        return allSources;
    }

    public List<BeamSystem> getAllBeamSystems() {
        return allBeamSystems;
    }

    public List<Logbook> getLogbooks() {
        return logbooks;
    }

    public List<ControlSignal> getSignals() {
        return signals;
    }

    public List<Vault> getAllVaults() {
        return allVaults;
    }

    public List<Mode> getAllModes() {
        return allModes;
    }

}
