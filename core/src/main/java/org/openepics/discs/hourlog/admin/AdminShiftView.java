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
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.openepics.discs.hourlog.auth.AuthEJB;
import org.openepics.discs.hourlog.ent.OperationsRole;
import org.openepics.discs.hourlog.ent.ShiftStatus;
import org.openepics.discs.hourlog.ent.StaffRole;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.ejb.ShiftEJB;

/**
 * The state of Administrative Shift View
 *
 * @author vuppala
 *
 */
// ToDo: make it request scoped?
@Named
@ViewScoped
public class AdminShiftView implements Serializable {

    @EJB
    private AuthEJB authEJB;
    @EJB
    private ShiftEJB shiftEJB;

    @Inject
    UserSession userSession;

    private static final Logger logger = Logger.getLogger(AdminShiftView.class.getName());
    private List<OperationsRole> operationsRoles;
    private List<StaffRole> staffRoles;
    private List<ShiftStatus> shiftStatus;

    public AdminShiftView() {
    }

    @PostConstruct
    public void init() {
        operationsRoles = shiftEJB.findOperationsRoles();
        staffRoles = shiftEJB.findStaffRoles();
        shiftStatus = shiftEJB.findShiftStatus();
    }

    //-- Getters/Setters 
    public List<OperationsRole> getOperationsRoles() {
        return operationsRoles;
    }

    public List<StaffRole> getStaffRoles() {
        return staffRoles;
    }

    public List<ShiftStatus> getShiftStatus() {
        return shiftStatus;
    }
}
