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
import org.openepics.discs.hourlog.ent.OperationsRole;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Description: State for Manage auth ROles View
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
public class ManageOpRolesView implements Serializable {

    @EJB
    private ShiftEJB shiftEJB;
    private static final Logger logger = Logger.getLogger(ManageOpRolesView.class.getName());
    @Inject
    UserSession userSession;

    private List<OperationsRole> oproles;
    private List<OperationsRole> filteredOproles;
    private OperationsRole selectedOprole;
    private OperationsRole inputOprole;
    private InputAction inputAction;

    public ManageOpRolesView() {
    }

    @PostConstruct
    public void init() {
        oproles = shiftEJB.findOperationsRoles();

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
        inputOprole = new OperationsRole();
        inputAction = InputAction.CREATE;
    }

    public void onEditCommand(ActionEvent event) {
        inputAction = InputAction.UPDATE;
    }

    public void onDeleteCommand(ActionEvent event) {
        inputAction = InputAction.DELETE;
    }

    public void saveOprole() {
        try {
            if (inputAction == InputAction.CREATE) {
                shiftEJB.saveOprole(inputOprole);
                oproles.add(inputOprole);
            } else {
                shiftEJB.saveOprole(selectedOprole);
            }
            resetInput();
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Operations Role saved", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not save Operations Role", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    public void deleteOprole() {
        try {
            shiftEJB.deleteOprole(selectedOprole);
            oproles.remove(selectedOprole);
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Operations Role deleted", "You may have to refresh the page.");
            resetInput();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not delete Operations Role", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    // ----- Getters/Setters
    public InputAction getInputAction() {
        return inputAction;
    }

    public List<OperationsRole> getOproles() {
        return oproles;
    }

    public List<OperationsRole> getFilteredOproles() {
        return filteredOproles;
    }

    public void setFilteredOproles(List<OperationsRole> filteredOproles) {
        this.filteredOproles = filteredOproles;
    }

    public OperationsRole getSelectedOprole() {
        return selectedOprole;
    }

    public void setSelectedOprole(OperationsRole selectedOprole) {
        this.selectedOprole = selectedOprole;
    }

    public OperationsRole getInputOprole() {
        return inputOprole;
    }

    public void setInputOprole(OperationsRole inputOprole) {
        this.inputOprole = inputOprole;
    }

}
