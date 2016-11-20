/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2012.
 * 
 * You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *       http://www.gnu.org/licenses/gpl.txt
 * 
 * Contact Information:
 *   Facilitty for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 * 
 */
package org.openepics.discs.hourlog.prefs;

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
import org.openepics.discs.hourlog.auth.AuthEJB;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.ejb.FacilityEJB;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.ent.UserPreference;
import org.openepics.discs.hourlog.util.Utility;

/**
 * State of user preferences view
 *
 * @author Vasu V <vuppala@frib.msu.org>
 */
@Named
@SessionScoped
public class PreferencesView implements Serializable {

    @EJB
    private FacilityEJB facilityEJB;
    @EJB
    private PreferencesEJB prefEJB;
    @EJB
    private AuthEJB authEJB;
    
    @Inject
    private UserSession userSession;

    private static final Logger logger = Logger.getLogger(PreferencesView.class.getName());
    private String preferredTheme;
    private String defaultTheme;
    private List<Theme> themes;
    private List<String> selectedFacNames;
    private String inputNotificationAddress;
    
    // private Facility defaultFacility;
    private Facility preferredFacility;
    private List<Facility> facilities;
    private Sysuser user;

    public PreferencesView() {
    }

    @PostConstruct
    public void init() {
        facilities = facilityEJB.findFacility();
        themes = prefEJB.findTheems();
        defaultTheme = prefEJB.defaultThemeName();
        preferredFacility = facilityEJB.defaultFacility();
        preferredTheme = defaultTheme;

        user = userSession.getUser();

        if (user != null) {
            // -- themse
            UserPreference pref = prefEJB.findPreference(user, PreferenceName.DEFAULT_THEME);
            if (pref != null) {
                preferredTheme = pref.getPrefValue();
            }
            logger.log(Level.FINE, "preferred theme: {0}", preferredTheme);

            //-- facility
            pref = prefEJB.findPreference(user, PreferenceName.DEFAULT_FACILITY);
            if (pref != null) {
                preferredFacility = facilityEJB.findFacility(pref.getPrefValue());
            }
            
            // Monitoring
            List<UserPreference> prefs = prefEJB.findPreferences(user, PreferenceName.MONITOR_FACILITY);
            selectedFacNames = new ArrayList<>();
            for(UserPreference up: prefs) {
               selectedFacNames.add(up.getPrefValue());
            }
            
            // Notification address
            inputNotificationAddress = user.getSmsAddress();
        }

        userSession.setCurrentTheme(preferredTheme);
    }

    /**
     * Saves user preferences
     *
     */
    public void savePreferences() {
        try {
            user = userSession.getUser();
            if (user == null) {
                Utility.showMessage(FacesMessage.SEVERITY_WARN, "You must be logged in to set preferences", "");
                return;
            }
            prefEJB.savePreference(user, PreferenceName.DEFAULT_THEME, preferredTheme);
            userSession.setCurrentTheme(preferredTheme);
            prefEJB.savePreference(user, PreferenceName.DEFAULT_FACILITY, preferredFacility.getFacilityName());
            userSession.setFacility(preferredFacility);
            prefEJB.savePreference(user, PreferenceName.MONITOR_FACILITY, selectedFacNames);
            user.setSmsAddress(inputNotificationAddress);
            authEJB.saveUser(user);
            logger.log(Level.FINE, "PreferencesView#saveTheme: saved preferences");
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Preferences Saved", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Something has gone amiss", "Preferences not saved");
            System.out.println(e);
        }
    }

    // -------------- getters/setters
    public String getDefaultTheme() {
        return defaultTheme;
    }

    public String getPreferredTheme() {
        return preferredTheme;
    }

    public void setPreferredTheme(String preferredTheme) {
        this.preferredTheme = preferredTheme;
    }

    public Facility getPreferredFacility() {
        return preferredFacility;
    }

    public void setPreferredFacility(Facility preferredFacility) {
        this.preferredFacility = preferredFacility;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public List<Theme> getThemes() {
        return (themes);
    }

    public List<String> getSelectedFacNames() {
        return selectedFacNames;
    }

    public void setSelectedFacNames(List<String> selectedFacNames) {
        this.selectedFacNames = selectedFacNames;
    }

    public String getInputNotificationAddress() {
        return inputNotificationAddress;
    }

    public void setInputNotificationAddress(String inputNotificationAddress) {
        this.inputNotificationAddress = inputNotificationAddress;
    }

}
