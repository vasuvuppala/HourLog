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

package org.openepics.discs.hourlog.admin;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.openepics.discs.hourlog.auth.AuthEJB;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.ejb.HourLogEJB;
import org.openepics.discs.hourlog.ent.Beam;
import org.openepics.discs.hourlog.ent.BeamSystem;
import org.openepics.discs.hourlog.ent.Element;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Description: State for Manage Beams View
 *
 * Methods:
 * <p>
 * Init: to initialize the state
 * <p>
 * resetInput: reset all inputs on the view
 * <p>
 * onRowSelect: things to do when an item is selected
 * <p>
 * onAddCommand: things to do before adding an item
 * <p>
 * onEditCommand: things to do before editing an item
 * <p>
 * onDeleteCommand: things to do before deleting an item
 * <p>
 * saveXXXX: save the input or edited item
 * <p>
 * deleteXXXX: delete the selected item
 *
 * @author vuppala
 *
 */

@Named
@ViewScoped
public class ManageBeamView implements Serializable {
    @EJB
    private AuthEJB authEJB;
    @EJB
    private HourLogEJB hourLogEJB;
    private static final Logger logger = Logger.getLogger(ManageBeamView.class.getName());
    @Inject
    UserSession userSession;
      
    private List<Beam> beams;    
    private List<Beam> filteredBeams;    
    private List<BeamSystem> beamSystems;    
    private List<Element> elements;    
    private Beam selectedBeam;
    private Beam inputBeam;
    private InputAction inputAction;
    
    
    public ManageBeamView() {
    }
    
    @PostConstruct
    public void init() {      
        beams = hourLogEJB.findBeams(); 
        beamSystems = hourLogEJB.findBeamSystems();
        elements = hourLogEJB.findElements();
        resetInput();
    }
    
    private void resetInput() {                
        inputAction = InputAction.READ;
    }
    
    public void onRowSelect(SelectEvent event) {
        // inputRole = selectedRole;
        // Utility.showMessage(FacesMessage.SEVERITY_INFO, "Role Selected", "");
    }
    
    public void onAddCommand(ActionEvent event) {
        inputBeam = new Beam();
        inputAction = InputAction.CREATE;       
    }
    
    public void onEditCommand(ActionEvent event) {
        inputAction = InputAction.UPDATE;
    }
    
    public void onDeleteCommand(ActionEvent event) {
        inputAction = InputAction.DELETE;
    }
    
    public void saveBeam() {
        try {                      
            if (inputAction == InputAction.CREATE) {
                hourLogEJB.saveBeam(inputBeam);
                beams.add(inputBeam);                
            } else {
                hourLogEJB.saveBeam(selectedBeam);
            }
            resetInput();
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Beam saved", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not save beam", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }
    
    public void deleteBeam() {
        try {
            hourLogEJB.deleteBeam(selectedBeam);
            beams.remove(selectedBeam);  
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Beam deleted", "You may have to refresh the page.");
            resetInput();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not delete Beam", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }
    
    //-- Getters/Setters 

    public List<Beam> getBeams() {
        return beams;
    }

    public Beam getSelectedBeam() {
        return selectedBeam;
    }

    public void setSelectedBeam(Beam selectedBeam) {
        this.selectedBeam = selectedBeam;
    }

    public Beam getInputBeam() {
        return inputBeam;
    }

    public void setInputBeam(Beam inputBeam) {
        this.inputBeam = inputBeam;
    }
       
    public InputAction getInputAction() {
        return inputAction;
    }

    public List<BeamSystem> getBeamSystems() {
        return beamSystems;
    }

    public List<Element> getElements() {
        return elements;
    }

    public List<Beam> getFilteredBeams() {
        return filteredBeams;
    }

    public void setFilteredBeams(List<Beam> filteredBeams) {
        this.filteredBeams = filteredBeams;
    }
    
}
