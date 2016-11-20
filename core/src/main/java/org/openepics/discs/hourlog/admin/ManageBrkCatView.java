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
import org.openepics.discs.hourlog.ent.BreakdownCategory;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Description: State for Manage Breakdown Categories View
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
public class ManageBrkCatView implements Serializable {
    @EJB
    private HourLogEJB hourLogEJB;
    private static final Logger logger = Logger.getLogger(ManageBrkCatView.class.getName());
    @Inject
    UserSession userSession;

    private List<BreakdownCategory> brkcats;
    private List<BreakdownCategory> filteredBrkcats;
    private BreakdownCategory selectedBrkcat;
    private BreakdownCategory inputBrkcat;
    private InputAction inputAction;

    public ManageBrkCatView() {
    }

    @PostConstruct
    public void init() {
        brkcats = hourLogEJB.findAllBrkCategories();

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
        inputBrkcat = new BreakdownCategory();
        inputAction = InputAction.CREATE;
    }

    public void onEditCommand(ActionEvent event) {
        inputAction = InputAction.UPDATE;
    }

    public void onDeleteCommand(ActionEvent event) {
        inputAction = InputAction.DELETE;
    }

    public void saveBrkcat() {
        try {
            if (inputAction == InputAction.CREATE) {
                hourLogEJB.saveBrkCategory(inputBrkcat);
                brkcats.add(inputBrkcat);
            } else {
                hourLogEJB.saveBrkCategory(selectedBrkcat);
            }
            resetInput();
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Category saved", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not save Category", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    public void deleteBrkcat() {
        try {
            hourLogEJB.deleteBrkCategory(selectedBrkcat);
            brkcats.remove(selectedBrkcat);
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Category deleted", "You may have to refresh the page.");
            resetInput();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not delete Category", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    // ----- Getters/Setters
    public InputAction getInputAction() {
        return inputAction;
    }

    public List<BreakdownCategory> getFilteredBrkcats() {
        return filteredBrkcats;
    }

    public void setFilteredBrkcats(List<BreakdownCategory> filteredBrkcats) {
        this.filteredBrkcats = filteredBrkcats;
    }

    public BreakdownCategory getSelectedBrkcat() {
        return selectedBrkcat;
    }

    public void setSelectedBrkcat(BreakdownCategory selectedBrkcat) {
        this.selectedBrkcat = selectedBrkcat;
    }

    public BreakdownCategory getInputBrkcat() {
        return inputBrkcat;
    }

    public void setInputBrkcat(BreakdownCategory inputBrkcat) {
        this.inputBrkcat = inputBrkcat;
    }

    public List<BreakdownCategory> getBrkcats() {
        return brkcats;
    }
}
