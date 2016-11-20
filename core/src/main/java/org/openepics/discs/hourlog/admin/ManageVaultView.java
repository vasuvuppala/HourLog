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
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Vault;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Description: State for Manage Vault View
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
public class ManageVaultView implements Serializable {
    @EJB
    private HourLogEJB hourLogEJB;
    @EJB
    private FacilityEJB facilityEJB;
    
    private static final Logger logger = Logger.getLogger(ManageVaultView.class.getName());
    @Inject
    UserSession userSession;

    private List<Vault> vaults;
    private List<Facility> facilities;
    private List<Vault> filteredVaults;
    private Vault selectedVault;
    private Vault inputVault;
    private InputAction inputAction;

    public ManageVaultView() {
    }

    @PostConstruct
    public void init() {
        vaults = hourLogEJB.findAllVaults();
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
        inputVault = new Vault();
        inputAction = InputAction.CREATE;
    }

    public void onEditCommand(ActionEvent event) {
        inputAction = InputAction.UPDATE;
    }

    public void onDeleteCommand(ActionEvent event) {
        inputAction = InputAction.DELETE;
    }

    public void saveVault() {
        try {
            if (inputAction == InputAction.CREATE) {
                hourLogEJB.saveVault(inputVault);
                vaults.add(inputVault);
            } else {
                hourLogEJB.saveVault(selectedVault);
            }
            resetInput();
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Vault saved", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not save vault", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    public void deleteVault() {
        try {
            hourLogEJB.deleteVault(selectedVault);
            vaults.remove(selectedVault);
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Vault deleted", "You may have to refresh the page.");
            resetInput();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Could not delete Vault", e.getMessage());
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            System.out.println(e);
        }
    }

    // ----- Getters/Setters
    public InputAction getInputAction() {
        return inputAction;
    }

    public List<Vault> getFilteredVaults() {
        return filteredVaults;
    }

    public void setFilteredVaults(List<Vault> filteredVaults) {
        this.filteredVaults = filteredVaults;
    }

    public Vault getSelectedVault() {
        return selectedVault;
    }

    public void setSelectedVault(Vault selectedVault) {
        this.selectedVault = selectedVault;
    }

    public Vault getInputVault() {
        return inputVault;
    }

    public void setInputVault(Vault inputVault) {
        this.inputVault = inputVault;
    }

    public List<Vault> getVaults() {
        return vaults;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }
}
