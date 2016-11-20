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
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Description: State for Manage User View
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
 */
@Named
@ViewScoped
public class ManageUserView implements Serializable {

    @EJB
    private AuthEJB authEJB;
    private static final Logger logger = Logger.getLogger(ManageUserView.class.getName());
    @Inject
    UserSession userSession;

    private List<Sysuser> users;
    private List<Sysuser> filteredUsers;
    private Sysuser selectedUser;
    private Sysuser inputUser;
    private InputAction inputAction;

    public ManageUserView() {
    }

    @PostConstruct
    public void init() {
        users = authEJB.findUsers();
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
        inputUser = new Sysuser();
        inputAction = InputAction.CREATE;
    }

    public void onEditCommand(ActionEvent event) {
        inputAction = InputAction.UPDATE;
    }

    public void onDeleteCommand(ActionEvent event) {
        inputAction = InputAction.DELETE;
    }

    public void saveUser() {
        try {
            if (!inputValid()) {
                RequestContext.getCurrentInstance().addCallbackParam("success", false);
                return;
            }
            if (inputAction == InputAction.CREATE) {
                authEJB.saveUser(inputUser);
                users.add(inputUser);
            } else {
                authEJB.saveUser(selectedUser);
            }
            resetInput();
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "User saved", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not save User", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    public void deleteUser() {
        try {
            authEJB.deleteUser(selectedUser);
            users.remove(selectedUser);
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "User deleted", "You may have to refresh the page");
            resetInput();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not delete user", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    public boolean inputValid() {
        boolean valid = true;
        Sysuser theuser;
        
        if (inputAction == InputAction.CREATE) {
            theuser = inputUser;
        } else {
            theuser = selectedUser;
        }

        for (Sysuser user : users) {
            if (user == theuser) continue; // If a user is being edited, skip it
            if (user.equals(theuser)) {
                valid = false;
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Duplicate user id: some one already has this user id", "Choose another one");
                break;
            }
            if (theuser.getLoginId().equals(user.getLoginId())) {
                valid = false;
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Duplicate user login id: some one already has this login id", "Choose another one");
                break;
            }
        }

        return valid;
    }

    // ----- Getters/Setters
    public List<Sysuser> getUsers() {
        return users;
    }

    public Sysuser getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(Sysuser selectedUser) {
        this.selectedUser = selectedUser;
    }

    public Sysuser getInputUser() {
        return inputUser;
    }

    public void setInputUser(Sysuser inputUser) {
        this.inputUser = inputUser;
    }

    public List<Sysuser> getFilteredUsers() {
        return filteredUsers;
    }

    public void setFilteredUsers(List<Sysuser> filteredUsers) {
        this.filteredUsers = filteredUsers;
    }

    public InputAction getInputAction() {
        return inputAction;
    }
}
