/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013.
 *  State University (c) Copyright 2013.
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
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.ejb.FacilityEJB;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.util.Utility;
import org.primefaces.context.RequestContext;

/**
 * State of facility switch dialog
 *
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class FacilitySwitchView implements Serializable {

    @EJB
    private FacilityEJB facilityEJB;
    @Inject
    UserSession userSession;
    private static final Logger logger = Logger.getLogger(FacilitySwitchView.class.getName());
   
    private List<Facility> facilities;
    private Facility newFacility; // facility to  switch to (input by user)
    
    public FacilitySwitchView() {
    }

    @PostConstruct
    public void init() {
        facilities = facilityEJB.findFacility();
        newFacility = userSession.getFacility();
    }

    public String switchFacility() {
        userSession.switchFacility(newFacility);
        String nextView = "home?faces-redirect=true";
        FacesContext context = FacesContext.getCurrentInstance();

        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Facility Switched", "New facility: " + newFacility.getFacilityName());
        nextView = context.getViewRoot().getViewId() + "?faces-redirect=true"; // redirect to the current page
        context.getExternalContext().getFlash().setKeepMessages(true); // Keep messages in Flash
        RequestContext.getCurrentInstance().addCallbackParam("success", true);
        
        return nextView;
    }

   
    //  getters/setters

    public List<Facility> getFacilities() {
        return facilities;
    }

    public Facility getNewFacility() {
        return newFacility;
    }

    public void setNewFacility(Facility newFacility) {
        this.newFacility = newFacility;
    }      
}
