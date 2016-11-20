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
package org.openepics.discs.hourlog.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.openepics.discs.hourlog.auth.AuthManager;
import org.openepics.discs.hourlog.ejb.ShiftEJB;
import org.openepics.discs.hourlog.ent.OperationsRole;
import org.openepics.discs.hourlog.ent.Shift;
import org.openepics.discs.hourlog.ent.ShiftStaffMember;
import org.openepics.discs.hourlog.ent.ShiftStatus;
import org.openepics.discs.hourlog.ent.StaffRole;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.log.LogEntry;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.log.LogbookManager;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;

/**
 * State for Shift view
 *
 * @author vuppala
 *
 */
@Named
@ViewScoped
public class ShiftManager implements Serializable {

    /**
     * Shift staff member
     */
    public static class InputShiftStaffMember implements Serializable {

        private boolean selected;
        private ShiftStaffMember member;

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public ShiftStaffMember getMember() {
            return member;
        }

        public void setMember(ShiftStaffMember member) {
            this.member = member;
        }

    }

    @EJB
    private ShiftEJB shiftEJB;
    @EJB(beanName = "LocalAuthManager")
    private AuthManager authManager;
    @EJB(beanName = "OlogLogbookManager")
    private LogbookManager logbookEJB;

    @Inject
    UserSession userSession;

    private static final Logger logger = Logger.getLogger(ShiftManager.class.getName());
    private List<OperationsRole> operationsRoles;
    private List<ShiftStatus> shiftStatus;
    private Shift currentShift;
    private List<ShiftStaffMember> currentShiftStaff;
    private ShiftStatus defaultStatus;

    private List<StaffRole> staffRoles;
    private Map<OperationsRole, List<InputShiftStaffMember>> shiftMap = new HashMap<>();

    private List<InputShiftStaffMember> operators;
    private InputShiftStaffMember selectedOIC;
    private List<Sysuser> trainedInOIC;

    private List<Shift> filteredShifts;
    private InputShiftStaffMember selectedBeamCoord;
    private List<InputShiftStaffMember> beamCoordinators;

    public ShiftManager() {
    }

    @PostConstruct
    public void init() {
        logger.log(Level.FINE, "ShiftManager:init {0}", "entering");
        operationsRoles = shiftEJB.findOperationsRoles();
        shiftStatus = shiftEJB.findShiftStatus();
        currentShift = shiftEJB.currentShift();
        if (currentShift == null) { // database does not have any shift data
            currentShift = new Shift();
            currentShift.setFacility(userSession.getFacility());
        }

        currentShiftStaff = shiftEJB.findShiftStaff(currentShift);
        staffRoles = shiftEJB.findStaffRoles();
        // newOIC = currentShift.getOpInCharge();
        trainedInOIC = shiftEJB.trainedAsOIC();

        //  ToDo: improve it. get it from db instead?
        for (StaffRole srole : staffRoles) {
            InputShiftStaffMember issm = new InputShiftStaffMember();
            for (ShiftStaffMember ssm : currentShiftStaff) {
                if (srole.getRole().equals(ssm.getRole()) && srole.getStaffMember().equals(ssm.getStaffMember())) {
                    issm.member = ssm;
                    issm.selected = true;
                    break;
                }
            }
            if (issm.member == null) {
                issm.member = new ShiftStaffMember();
                issm.member.setRole(srole.getRole());
                issm.member.setStaffMember(srole.getStaffMember());
                issm.member.setStatus(shiftStatus.get(0)); // default status 
                issm.selected = false;
            }
            if (shiftMap.get(srole.getRole()) == null) {
                shiftMap.put(srole.getRole(), new ArrayList<InputShiftStaffMember>());
            }
            shiftMap.get(srole.getRole()).add(issm);
            if ("Operator".equals(srole.getRole().getName())) {
                operators = shiftMap.get(srole.getRole());
            }
            if ("Beam Coordinator".equals(srole.getRole().getName())) {
                beamCoordinators = shiftMap.get(srole.getRole());
            }
        }
        newOIClistener(); // to set initial value of selectedOIC
    }

    /**
     * Update shift information
     *
     */
    public void newShift() {
        logger.log(Level.FINE, "ShiftManager:newShift ", "entering");
        logger.log(Level.FINE, "ShiftManager:newShift new oic is {0}", currentShift.getOpInCharge().getLastName());

        if (!validateInput()) {
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            return;
        }

        try {
            List<ShiftStaffMember> newShiftStaff = new ArrayList<>();
            for (Map.Entry<OperationsRole, List<InputShiftStaffMember>> entry : shiftMap.entrySet()) {
                for (InputShiftStaffMember ssm : entry.getValue()) {
                    if (ssm.selected) {
                        logger.log(Level.FINE, "ShiftManager#newShift: set" + ssm.getMember().getStaffMember().getLastName() + " as " + ssm.getMember().getRole().getName());
                        ssm.member.setShift(currentShift);
                        newShiftStaff.add(ssm.member);
                    }
                }
            }
            currentShift.setShiftStaffMemberList(newShiftStaff);
            Date currentDate = Utility.currentDate();
            Sysuser currentUser = userSession.getUser();
            Logbook logbook = userSession.getFacility().getOpsLogbook();
            
            currentShift.setUpdatedBy(currentUser);
            currentShift.setUpdatedAt(currentDate);

            // create an auto log entry. 
            // TODO: Move this to shiftEJB
            LogEntry logEntry = logbookEJB.addEntry(logbook, currentUser, shiftLogText(), currentDate, null);
            currentShift.setNote(logEntry.getLogEntryId());
            shiftEJB.newShift(currentShift);

            // inputUnlocked = false;
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Shift Information Updated", " ");
            RequestContext.getCurrentInstance().addCallbackParam("success", true);
        } catch (Exception e) {
            RequestContext.getCurrentInstance().addCallbackParam("success", false);
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "There was an error while updaing shift information", e.getMessage());
            logger.log(Level.SEVERE, "ShiftManager:newShift {0}", e);
            System.out.print(e);
        }
    }

    /**
     * Validate input shift information
     *
     * @return true if no errors
     */
    private boolean validateInput() {
        OperationsRole beamCoord = shiftEJB.beeamCoordinatorRole();
        int counter = 0;
        Shift latestShift = shiftEJB.currentShift(); // to check if someone has changed shift in the meantime
        
        if ( latestShift != null ) {
           if (! latestShift.equals(currentShift)) {
               String updatedBy = latestShift.getUpdatedBy() == null? "someone" : latestShift.getUpdatedBy().getFirstName() + " " + latestShift.getUpdatedBy().getLastName();
               Utility.showMessage(FacesMessage.SEVERITY_ERROR, "While you were updating the shift, " + updatedBy + " modified it.","Please refresh the page, or start over." );
               return false;
           }
        }
        for (InputShiftStaffMember ssm : shiftMap.get(beamCoord)) {
            if (ssm.selected) {
                counter++;
            }
            if (counter > 1) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "You cannot select more than one Beam Coordinator", "");
                return false;
            }
        }

        return true;
    }

    /**
     * Auto-generated log entry text when shift is updated
     *
     * @return shift update text
     */
    private String shiftLogText() {
        String note = String.format("%s shift information updated at %tc by %s %s",
                currentShift.getFacility().getFacilityName(), currentShift.getUpdatedAt(), currentShift.getUpdatedBy().getFirstName(),
                currentShift.getUpdatedBy().getLastName());
        return note;
    }

    /*
     * when a new operator in charge is chosen
     */
    public void newOIClistener() {
        if (selectedOIC != null) {
            selectedOIC.selected = false;
        }
        for (InputShiftStaffMember ssm : operators) {
            if (currentShift != null && ssm.member.getStaffMember().equals(currentShift.getOpInCharge())) {
                ssm.selected = true;
                selectedOIC = ssm;
                break;
            }
        }
    }

    public boolean canStartShift() {
        return authManager.canOperateShift();
    }

    public boolean canChangeShift() {
        // return authManager.canOperateShift() && inputUnlocked;
        return authManager.canOperateShift();
    }

    public List<OperationsRole> getOperationsRoles() {
        return operationsRoles;
    }

    public List<ShiftStatus> getShiftStatus() {
        return shiftStatus;
    }

    public Shift getCurrentShift() {
        return currentShift;
    }

    /**
     * Index of beam coordinator from staff list
     *
     * @param ssm
     * @return
     */
    public int beamCoordIdx(InputShiftStaffMember ssm) {
        int i = beamCoordinators.indexOf(ssm);
        logger.log(Level.FINE, "ShiftManager#beamCoordIdx: beam coord index is {0}", i);
        return beamCoordinators.indexOf(ssm);
    }

    /**
     * Fetches staff members that can fill the given role
     *
     * @param role
     * @return staff members
     */
    public List<InputShiftStaffMember> shiftStaffAs(OperationsRole role) {
        return shiftMap.get(role);
    }

    // ----------- getters/setters
    public InputShiftStaffMember getSelectedOIC() {
        return selectedOIC;
    }

    public List<Sysuser> getTrainedInOIC() {
        return trainedInOIC;
    }

    public List<Shift> getFilteredShifts() {
        return filteredShifts;
    }

    public void setFilteredShifts(List<Shift> filteredShifts) {
        this.filteredShifts = filteredShifts;
    }

    public InputShiftStaffMember getSelectedBeamCoord() {
        return selectedBeamCoord;
    }

    public void setSelectedBeamCoord(InputShiftStaffMember selectedBeamCoord) {
        this.selectedBeamCoord = selectedBeamCoord;
    }

    public List<InputShiftStaffMember> getBeamCoordinators() {
        return beamCoordinators;
    }
}
