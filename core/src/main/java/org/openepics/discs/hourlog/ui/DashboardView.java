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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import org.openepics.discs.hourlog.ejb.ShiftEJB;
import org.openepics.discs.hourlog.ejb.SnapshotEJB;
import org.openepics.discs.hourlog.ent.Shift;
import org.openepics.discs.hourlog.log.StatusSnapshot;
import org.openepics.discs.hourlog.util.Utility;

/**
 * State for dashboard
 *
 * TODO: DashboardView is for the dashboard at the top of each page. It is
 * request scoped so that it can update data as it gets changed from
 * other instances of Hour Log. Make it more efficient by checking if status has changed.
 *
 * @author vuppala
 */
@Named
@RequestScoped
public class DashboardView implements Serializable {

    @EJB
    private ShiftEJB shiftEJB;
    @EJB
    private SnapshotEJB snapshotEJB;

    private static final Logger logger = Logger.getLogger(DashboardView.class.getName());

    private Shift currentShift;
    private StatusSnapshot currentSnapshot;

    public DashboardView() {
    }

    @PostConstruct
    public void init() {
        try {
            logger.log(Level.FINE, "initializing dashboard");
            currentSnapshot = snapshotEJB.snapshotAt(Utility.currentDate());
            currentShift = shiftEJB.currentShift();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while initializing dashboard", e);
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error while initializing dashboard", e.getMessage());
        }
    }

    public Shift getCurrentShift() {
        return currentShift;
    }

    public StatusSnapshot getCurrentSnapshot() {
        return currentSnapshot;
    }
}
