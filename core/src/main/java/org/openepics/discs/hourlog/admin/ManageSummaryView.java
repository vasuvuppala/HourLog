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
import org.openepics.discs.hourlog.ent.Summary;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Description: State for Manage Summary View
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
public class ManageSummaryView implements Serializable {
    @EJB
    private HourLogEJB hourLogEJB;
    private static final Logger logger = Logger.getLogger(ManageSummaryView.class.getName());
    @Inject
    UserSession userSession;

    private List<Summary> summaries;
    private List<Summary> filteredSummaries;
    private Summary selectedSummary;
    private Summary inputSummary;
    private InputAction inputAction;

    public ManageSummaryView() {
    }

    @PostConstruct
    public void init() {
        summaries = hourLogEJB.findAllSummary();

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
        inputSummary = new Summary();
        inputAction = InputAction.CREATE;
    }

    public void onEditCommand(ActionEvent event) {
        inputAction = InputAction.UPDATE;
    }

    public void onDeleteCommand(ActionEvent event) {
        inputAction = InputAction.DELETE;
    }

    public void saveSummary() {
        try {
            if (inputAction == InputAction.CREATE) {
                hourLogEJB.saveSummary(inputSummary);
                summaries.add(inputSummary);
            } else {
                hourLogEJB.saveSummary(selectedSummary);
            }
            resetInput();
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Summary saved", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not save Summary", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    public void deleteSummary() {
        try {
            hourLogEJB.deleteSummary(selectedSummary);
            summaries.remove(selectedSummary);
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Summary deleted", "You may have to refresh the page.");
            resetInput();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not delete Summary", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    // ----- Getters/Setters
    public InputAction getInputAction() {
        return inputAction;
    }

    public List<Summary> getFilteredSummaries() {
        return filteredSummaries;
    }

    public void setFilteredSummaries(List<Summary> filteredSummaries) {
        this.filteredSummaries = filteredSummaries;
    }

    public Summary getSelectedSummary() {
        return selectedSummary;
    }

    public void setSelectedSummary(Summary selectedSummary) {
        this.selectedSummary = selectedSummary;
    }

    public Summary getInputSummary() {
        return inputSummary;
    }

    public void setInputSummary(Summary inputSummary) {
        this.inputSummary = inputSummary;
    }

    public List<Summary> getSummaries() {
        return summaries;
    }
}
