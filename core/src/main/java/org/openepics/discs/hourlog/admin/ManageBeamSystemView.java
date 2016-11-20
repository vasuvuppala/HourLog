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
import org.openepics.discs.hourlog.ejb.FacilityEJB;
import org.openepics.discs.hourlog.ejb.HourLogEJB;
import org.openepics.discs.hourlog.ent.BeamSystem;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * State for beam systems management view
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
public class ManageBeamSystemView implements Serializable {

    @EJB
    private AuthEJB authEJB;
    @EJB
    private HourLogEJB hourLogEJB;
    @EJB
    private FacilityEJB facilityEJB;
    private static final Logger logger = Logger.getLogger(ManageBeamSystemView.class.getName());
    @Inject
    UserSession userSession;

    private List<BeamSystem> beamsystems;
    private List<BeamSystem> filteredBeamSystems;
    private List<Facility> facilities;
    private BeamSystem selectedBeamSystem;
    private BeamSystem inputBeamSystem;
    private InputAction inputAction;

    public ManageBeamSystemView() {
    }

    /**
     * Initialize the state
     *
     */
    @PostConstruct
    public void init() {
        beamsystems = hourLogEJB.findAllBeamSystems();
        facilities = facilityEJB.findFacility();
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
        inputBeamSystem = new BeamSystem();
        inputAction = InputAction.CREATE;
    }

    public void onEditCommand(ActionEvent event) {
        inputAction = InputAction.UPDATE;
    }

    public void onDeleteCommand(ActionEvent event) {
        inputAction = InputAction.DELETE;
    }

    public void saveBeamSystem() {
        try {
            if (inputAction == InputAction.CREATE) {
                hourLogEJB.saveBeamSystem(inputBeamSystem);
                beamsystems.add(inputBeamSystem);
            } else {
                hourLogEJB.saveBeamSystem(selectedBeamSystem);
            }
            resetInput();
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Beam System  saved", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not save Beam System ", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    public void deleteBeamSystem() {
        try {
            hourLogEJB.deleteBeamSystem(selectedBeamSystem);
            beamsystems.remove(selectedBeamSystem);
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Beam System deleted", "You may have to refresh the page.");
            resetInput();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not delete Beam System ", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    //-- Getters/Setters 
    public InputAction getInputAction() {
        return inputAction;
    }

    public List<BeamSystem> getBeamsystems() {
        return beamsystems;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public List<BeamSystem> getFilteredBeamSystems() {
        return filteredBeamSystems;
    }

    public void setFilteredBeamSystems(List<BeamSystem> filteredBeamSystems) {
        this.filteredBeamSystems = filteredBeamSystems;
    }

    public BeamSystem getSelectedBeamSystem() {
        return selectedBeamSystem;
    }

    public void setSelectedBeamSystem(BeamSystem selectedBeamSystem) {
        this.selectedBeamSystem = selectedBeamSystem;
    }

    public BeamSystem getInputBeamSystem() {
        return inputBeamSystem;
    }

    public void setInputBeamSystem(BeamSystem inputBeamSystem) {
        this.inputBeamSystem = inputBeamSystem;
    }

}
