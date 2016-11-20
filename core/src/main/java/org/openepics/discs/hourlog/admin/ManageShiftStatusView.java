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
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.ejb.ShiftEJB;
import org.openepics.discs.hourlog.ent.ShiftStatus;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Description: State for Manage Shift Status View
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
public class ManageShiftStatusView implements Serializable {

    @EJB
    private ShiftEJB shiftEJB;
    private static final Logger logger = Logger.getLogger(ManageShiftStatusView.class.getName());
    @Inject
    UserSession userSession;

    private List<ShiftStatus> shiftStatus;
    private List<ShiftStatus> filteredStatus;
    private ShiftStatus selectedStatus;
    private ShiftStatus inputStatus;
    private InputAction inputAction;

    public ManageShiftStatusView() {
    }

    @PostConstruct
    public void init() {
        shiftStatus = shiftEJB.findShiftStatus();

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
        inputStatus = new ShiftStatus();
        inputAction = InputAction.CREATE;
    }

    public void onEditCommand(ActionEvent event) {
        inputAction = InputAction.UPDATE;
    }

    public void onDeleteCommand(ActionEvent event) {
        inputAction = InputAction.DELETE;
    }

    public void saveStatus() {
        try {
            if (inputAction == InputAction.CREATE) {
                shiftEJB.saveShiftStatus(inputStatus);
                shiftStatus.add(inputStatus);
            } else {
                shiftEJB.saveShiftStatus(selectedStatus);
            }
            resetInput();
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "ShiftStatus saved", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not save ShiftStatus", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    public void deleteStatus() {
        try {
            shiftEJB.deleteShiftStatus(selectedStatus);
            shiftStatus.remove(selectedStatus);
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "ShiftStatus deleted", "You may have to refresh the page.");
            resetInput();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not delete ShiftStatus", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    // ----- Getters/Setters
    public InputAction getInputAction() {
        return inputAction;
    }

    public List<ShiftStatus> getShiftStatus() {
        return shiftStatus;
    }

    public List<ShiftStatus> getFilteredStatus() {
        return filteredStatus;
    }

    public void setFilteredStatus(List<ShiftStatus> filteredStatus) {
        this.filteredStatus = filteredStatus;
    }

    public ShiftStatus getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(ShiftStatus selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public ShiftStatus getInputStatus() {
        return inputStatus;
    }

    public void setInputStatus(ShiftStatus inputStatus) {
        this.inputStatus = inputStatus;
    }

}
