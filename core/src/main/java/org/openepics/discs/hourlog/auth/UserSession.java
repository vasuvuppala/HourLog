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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.hourlog.ejb.FacilityEJB;
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.ent.AuthRole;
import org.openepics.discs.hourlog.ent.UserPreference;
import org.openepics.discs.hourlog.prefs.PreferenceName;
import org.openepics.discs.hourlog.prefs.PreferencesEJB;
import org.openepics.discs.hourlog.util.AppProperties;
import org.openepics.discs.hourlog.util.Utility;
import org.openepics.discs.hourlog.log.LogbookServiceCredential;

/**
 * The session.
 * 
 * @author vuppala
 */
@Named
@SessionScoped
public class UserSession implements Serializable {
    @EJB
    private FacilityEJB facilityEJB;   
    @EJB
    private AuthEJB authEJB;
    @EJB
    private PreferencesEJB prefEJB;
    
    @Inject 
    private AppProperties appProperties;
    
    private static final Logger logger = Logger.getLogger(UserSession.class.getName());

    private String userId; // user unique login name
    private String token;   // auth token
    private AuthRole role; // current role   
    private Sysuser user; // the user record. ToDo:  keep this in the session?
    private Facility facility; // current facility: ToDo: keep this in the session?    
    private List<Logbook> selectedLogbooks;;  // logbooks selected by the user
    private String currentTheme; // current GUI theme
    private LogbookServiceCredential logbookCredential; // user credetials to access the logbook service

    public UserSession() {
        this.selectedLogbooks = new ArrayList<>();
    }
    
    public void UserSession() {
    }

    /**
     * Initialize user session.
     *
     * @author vuppala
     *
     */
    @PostConstruct
    public void init() {
        try {
            facility = facilityEJB.defaultFacility(); // Default facility and logbook  for users who are not logged in
            // logbook = facility.getOpsLogbook();
            selectedLogbooks.add(facility.getOpsLogbook());
            currentTheme = prefEJB.defaultThemeName();
            logger.log(Level.SEVERE, "UserSession: Deployment evnironment is {0}", appProperties.inProduction()? "Production": "Non-Production");           
        } catch (Exception e) {
            logger.log(Level.SEVERE, "UserSession: Can not initialize: {0}", e.getMessage());
        }

    }

    /**
     * Start user session
     *
     * @author vuppala
     * @param userId User login id
     * @param role User role
     *
     */
    public void start(String userId, AuthRole role)  {
        this.userId = userId;
        this.role = role;
        user = authEJB.findUser(userId);
        
        if (user != null) {
            UserPreference pref = prefEJB.findPreference(user, PreferenceName.DEFAULT_THEME);
            if (pref != null) {
                currentTheme = pref.getPrefValue();
            }
            pref = prefEJB.findPreference(user, PreferenceName.DEFAULT_FACILITY);
            if (pref != null) {
                facility = facilityEJB.findFacility(pref.getPrefValue());
            }
        } else {
            logger.log(Level.WARNING, "User not defined in the Hour Log database: {0}", userId);
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "You are not registered as Hour Log user", "Please contact Hour Log administrator.");
        }
        // facility = facilityEJB.defaultFacility(user);      
        //logbook = facility.getOpsLogbook();
        if (currentTheme == null) {
            currentTheme = prefEJB.defaultThemeName();
        }
        if (facility == null) {
            facility = facilityEJB.defaultFacility(); 
        }
        
        selectedLogbooks.clear();
        selectedLogbooks.add(facility.getOpsLogbook());
        // loggedIn = true;
    }

    /**
     * Switch to a new facility
     *
     * @param facility
     */
    public void switchFacility(Facility facility) {
        if (facility == null) {
            logger.log(Level.WARNING, "Null facility given");
            return;
        }

        this.facility = facility;
        Logbook lb = facility.getOpsLogbook();

        if (lb == null) {
            logger.log(Level.SEVERE, "No operations logbook defined for {0})", facility.getFacilityName());
            return;
        }
        selectedLogbooks.clear();
        selectedLogbooks.add(lb);       
    }
    
    /**
     * end user session
     *
     * @author vuppala
     *
     */
    public void end() {
        userId = null;
        user = null;
        role = null;
        // loggedIn = false;
        token = null;
    }

    // -- getters/setters 
    
    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Sysuser getUser() {
        return user;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;      
    }
    
    public AuthRole getRole() {
        return role;
    }

    public void setRole(AuthRole role) {
        this.role = role;
    }
   
    public List<Logbook> getSelectedLogbooks() {
        return selectedLogbooks;
    }

    public void setSelectedLogbooks(List<Logbook> selectedLogbooks) {
        this.selectedLogbooks = selectedLogbooks;
    }

    public String getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(String currentTheme) {
        this.currentTheme = currentTheme;
    }

    public LogbookServiceCredential getLogbookCredential() {
        return logbookCredential;
    }

    public void setLogbookCredential(LogbookServiceCredential logbookCredential) {
        this.logbookCredential = logbookCredential;
    }
       
}
