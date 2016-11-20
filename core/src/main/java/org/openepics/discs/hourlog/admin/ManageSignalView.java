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
import org.openepics.discs.hourlog.ent.ControlSignal;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Description: State for Manage Signal View
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
public class ManageSignalView implements Serializable {
    @EJB
    private HourLogEJB hourLogEJB;
    @EJB
    private FacilityEJB facilityEJB;
    private static final Logger logger = Logger.getLogger(ManageSignalView.class.getName());
    @Inject
    UserSession userSession;

    private List<ControlSignal> signals;
    private List<ControlSignal> filteredSignals;
    private List<Facility> facilities;
    private ControlSignal selectedSignal;
    private ControlSignal inputSignal;
    private InputAction inputAction;

    public ManageSignalView() {
    }

    @PostConstruct
    public void init() {
        signals = hourLogEJB.findSignals();
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
        inputSignal = new ControlSignal();
        inputAction = InputAction.CREATE;
    }

    public void onEditCommand(ActionEvent event) {
        inputAction = InputAction.UPDATE;
    }

    public void onDeleteCommand(ActionEvent event) {
        inputAction = InputAction.DELETE;
    }

    public void saveSignal() {
        try {
            if (inputAction == InputAction.CREATE) {
                hourLogEJB.saveSignal(inputSignal);
                signals.add(inputSignal);
            } else {
                hourLogEJB.saveSignal(selectedSignal);
            }
            resetInput();
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Control Signal saved", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not save Control Signal", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    public void deleteSignal() {
        try {
            hourLogEJB.deleteSignal(selectedSignal);
            signals.remove(selectedSignal);
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Control Signal deleted", "You may have to refresh the page.");
            resetInput();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not delete Control Signal", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    // ----- Getters/Setters
    public InputAction getInputAction() {
        return inputAction;
    }

    public List<ControlSignal> getSignals() {
        return signals;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public List<ControlSignal> getFilteredSignals() {
        return filteredSignals;
    }

    public void setFilteredSignals(List<ControlSignal> filteredSignals) {
        this.filteredSignals = filteredSignals;
    }

    public ControlSignal getSelectedSignal() {
        return selectedSignal;
    }

    public void setSelectedSignal(ControlSignal selectedSignal) {
        this.selectedSignal = selectedSignal;
    }

    public ControlSignal getInputSignal() {
        return inputSignal;
    }

    public void setInputSignal(ControlSignal inputSignal) {
        this.inputSignal = inputSignal;
    }

}
