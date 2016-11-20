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
import org.openepics.discs.hourlog.ent.AuthResource;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Description: State for Manage Resource View
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
public class ManageResourceView implements Serializable {

    @EJB
    private AuthEJB authEJB;
    private static final Logger logger = Logger.getLogger(ManageResourceView.class.getName());
    @Inject
    UserSession userSession;

    private List<AuthResource> resources;
    private AuthResource selectedResource;
    private AuthResource inputResource;
    private InputAction inputAction;

    public ManageResourceView() {
    }

    @PostConstruct
    public void init() {
        resources = authEJB.findAuthResources();
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
        inputResource = new AuthResource();
        inputAction = InputAction.CREATE;
    }

    public void onEditCommand(ActionEvent event) {
        inputAction = InputAction.UPDATE;
    }

    public void onDeleteCommand(ActionEvent event) {
        inputAction = InputAction.DELETE;
    }

    public void saveRole() {
        try {
            if (inputAction == InputAction.CREATE) {
                authEJB.saveAuthResource(inputResource);
                resources.add(inputResource);
            } else {
                authEJB.saveAuthResource(selectedResource);
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

    public void deleteRole() {
        try {
            authEJB.deleteAuthResource(selectedResource);
            resources.remove(selectedResource);
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
    public List<AuthResource> getResources() {
        return resources;
    }

    public AuthResource getSelectedResource() {
        return selectedResource;
    }

    public void setSelectedResource(AuthResource selectedResource) {
        this.selectedResource = selectedResource;
    }

    public AuthResource getInputResource() {
        return inputResource;
    }

    public void setInputResource(AuthResource inputResource) {
        this.inputResource = inputResource;
    }

    public InputAction getInputAction() {
        return inputAction;
    }

}
