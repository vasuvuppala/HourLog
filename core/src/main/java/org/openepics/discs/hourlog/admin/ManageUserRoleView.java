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
import java.util.Date;
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
import org.openepics.discs.hourlog.ent.AuthUserRole;
import org.openepics.discs.hourlog.ent.AuthRole;
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Description: State for Manage User Role View
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
public class ManageUserRoleView implements Serializable {

    @EJB
    private AuthEJB authEJB;

    @Inject
    UserSession userSession;
    private static final Logger logger = Logger.getLogger(ManageUserRoleView.class.getName());

    private List<AuthUserRole> userRoles;
    private AuthUserRole selectedUserRole;
    private AuthUserRole inputUserRole;
    private InputAction inputAction;

    private List<AuthRole> roles;
    private List<Sysuser> users;

    public ManageUserRoleView() {
    }

    @PostConstruct
    public void init() {
        userRoles = authEJB.findAuthUserRoles();
        roles = authEJB.findRoles();
        users = authEJB.findCurrentUsers();
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
        inputUserRole = new AuthUserRole();
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
                // TODO: following fields not being used/input. change.
                Date currentDate = Utility.currentDate();
                inputUserRole.setCanDelegate(false);
                inputUserRole.setEndTime(currentDate);
                inputUserRole.setStartTime(currentDate);
                inputUserRole.setComment("default comment");
                authEJB.saveAuthUserRole(inputUserRole);
                userRoles.add(inputUserRole);
            } else {
                authEJB.saveAuthUserRole(selectedUserRole);
            }
            resetInput();
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Role saved", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not save role", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    public void deleteEntity() {
        try {
            authEJB.deleteAuthUserRole(selectedUserRole);
            userRoles.remove(selectedUserRole);
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Role deleted", "");
            resetInput();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not delete role", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    // ----- Getters/Setters
    public AuthUserRole getSelectedUserRole() {
        return selectedUserRole;
    }

    public void setSelectedUserRole(AuthUserRole selectedUserRole) {
        this.selectedUserRole = selectedUserRole;
    }

    public AuthUserRole getInputUserRole() {
        return inputUserRole;
    }

    public void setInputUserRole(AuthUserRole inputUserRole) {
        this.inputUserRole = inputUserRole;
    }

    public List<AuthUserRole> getUserRoles() {
        return userRoles;
    }

    public InputAction getInputAction() {
        return inputAction;
    }

    public List<AuthRole> getRoles() {
        return roles;
    }

    public List<Sysuser> getUsers() {
        return users;
    }
}
