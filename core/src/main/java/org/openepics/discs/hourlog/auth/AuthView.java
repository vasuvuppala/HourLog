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

package org.openepics.discs.hourlog.auth;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * State for the Authorization view
 * 
 * @author vuppala
 */
@Named
@RequestScoped
public class AuthView implements Serializable {
    
    @EJB(beanName = "LocalAuthManager")
    private AuthManager authManager;
    
    public boolean canGenerateReports() {
        return authManager.canGenerateReports();
    }
    
    public boolean canManageFacility() {
        return authManager.canManageFacility();
    }
    
    public boolean canOperateShift() {
        return authManager.canOperateShift();
    }
    
    public boolean isValidUser() {
        return authManager.isValidUser();
    }
}
