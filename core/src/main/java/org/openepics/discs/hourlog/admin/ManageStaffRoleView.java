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
import org.openepics.discs.hourlog.ejb.ShiftEJB;
import org.openepics.discs.hourlog.ent.OperationsRole;
import org.openepics.discs.hourlog.ent.StaffRole;
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Description: State for Manage Staff Role View
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
public class ManageStaffRoleView implements Serializable {

    @EJB
    private ShiftEJB shiftEJB;
    @EJB
    private AuthEJB authEJB;

    private static final Logger logger = Logger.getLogger(ManageStaffRoleView.class.getName());
    @Inject
    UserSession userSession;

    private List<StaffRole> staffRoles;
    private List<StaffRole> filteredRoles;
    private StaffRole selectedRole;
    private StaffRole inputRole;
    private InputAction inputAction;

    private List<Sysuser> users;
    private List<OperationsRole> operationsRoles;

    public ManageStaffRoleView() {
    }

    @PostConstruct
    public void init() {
        staffRoles = shiftEJB.findStaffRoles();
        users = authEJB.findCurrentUsers();
        operationsRoles = shiftEJB.findOperationsRoles();

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
        inputRole = new StaffRole();
        inputAction = InputAction.CREATE;
    }

    public void onEditCommand(ActionEvent event) {
        inputAction = InputAction.UPDATE;
    }

    public void onDeleteCommand(ActionEvent event) {
        inputAction = InputAction.DELETE;
    }

    public void saveEntity() {
        try {
            if (inputAction == InputAction.CREATE) {
                shiftEJB.saveStaffRole(inputRole);
                staffRoles.add(inputRole);
            } else {
                shiftEJB.saveStaffRole(selectedRole);
            }
            resetInput();
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Staff Role saved", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not save Staff Role", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    public void deleteEntity() {
        try {
            shiftEJB.deleteStaffRole(selectedRole);
            staffRoles.remove(selectedRole);
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Staff Role deleted", "You may have to refresh the page.");
            resetInput();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not delete Staff Role", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    // ----- Getters/Setters
    public InputAction getInputAction() {
        return inputAction;
    }

    public List<StaffRole> getStaffRoles() {
        return staffRoles;
    }

    public List<Sysuser> getUsers() {
        return users;
    }

    public List<OperationsRole> getOperationsRoles() {
        return operationsRoles;
    }

    public List<StaffRole> getFilteredRoles() {
        return filteredRoles;
    }

    public void setFilteredRoles(List<StaffRole> filteredRoles) {
        this.filteredRoles = filteredRoles;
    }

    public StaffRole getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(StaffRole selectedRole) {
        this.selectedRole = selectedRole;
    }

    public StaffRole getInputRole() {
        return inputRole;
    }

    public void setInputRole(StaffRole inputRole) {
        this.inputRole = inputRole;
    }
}
