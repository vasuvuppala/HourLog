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
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.openepics.discs.hourlog.ejb.ShiftEJB;
import org.openepics.discs.hourlog.ent.OperationsRole;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

/**
 * State for shift view
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class ShiftDashboardView implements Serializable {

    @EJB
    private ShiftEJB shiftEJB;

    private List<OperationsRole> operRoles;

    public ShiftDashboardView() {
    }

    private DashboardModel model;

    @PostConstruct
    public void init() {
        model = new DefaultDashboardModel();
        DashboardColumn column1 = new DefaultDashboardColumn();
        DashboardColumn column2 = new DefaultDashboardColumn();
        DashboardColumn column3 = new DefaultDashboardColumn();
        operRoles = shiftEJB.findOperationsRoles();

        column1.addWidget("exprimenterIC");
        int i = 0;
        for (OperationsRole role : operRoles) {
            switch (i++ % 3) {
                case 0:
                    column1.addWidget("role-" + role.getOpRoleId());
                    break;
                case 1:
                    column2.addWidget("role-" + role.getOpRoleId());
                    break;
                default:
                    column3.addWidget("role-" + role.getOpRoleId());
                    break;
            }
        }

        model.addColumn(column1);
        model.addColumn(column2);
        model.addColumn(column3);
    }

//    public void handleReorder(DashboardReorderEvent event) {
//        FacesMessage message = new FacesMessage();
//        message.setSeverity(FacesMessage.SEVERITY_INFO);
//        message.setSummary("Reordered: " + event.getWidgetId());
//        message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());
//         
//        addMessage(message);
//    }
//    public void handleClose(CloseEvent event) {
//        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Panel Closed", "Closed panel id:'" + event.getComponent().getId() + "'");
//         
//        addMessage(message);
//    }
//     
//    public void handleToggle(ToggleEvent event) {
//        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, event.getComponent().getId() + " toggled", "Status:" + event.getVisibility().name());
//         
//        addMessage(message);
//    }
//    private void addMessage(FacesMessage message) {
//        FacesContext.getCurrentInstance().addMessage(null, message);
//    }
    public DashboardModel getModel() {
        return model;
    }
}
