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
package org.openepics.discs.hourlog.prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.ent.UserPreference;

/**
 * Manager for User Preferences
 *
 * @author vuppala
 */
@Stateless
public class PreferencesEJB {

    private static final Logger logger = Logger.getLogger(PreferencesEJB.class.getName());
    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    private static final String DEFAULT_THEME = "aristo";

    /**
     * Fetches all GUI themes
     *
     * @return themes
     */
    public List<Theme> findTheems() {
        List<Theme> themes;

        themes = new ArrayList<>();
        themes.add(new Theme(0, "Afterdark", "afterdark"));
        themes.add(new Theme(1, "Afternoon", "afternoon"));
        themes.add(new Theme(2, "Afterwork", "afterwork"));
        themes.add(new Theme(3, "Aristo", "aristo"));
        themes.add(new Theme(4, "Black-Tie", "black-tie"));
        themes.add(new Theme(5, "Blitzer", "blitzer"));
        themes.add(new Theme(6, "Bluesky", "bluesky"));
        themes.add(new Theme(7, "Bootstrap", "bootstrap"));
        themes.add(new Theme(8, "Casablanca", "casablanca"));
        themes.add(new Theme(9, "Cupertino", "cupertino"));
        themes.add(new Theme(10, "Cruze", "cruze"));
        themes.add(new Theme(11, "Dark-Hive", "dark-hive"));
        themes.add(new Theme(12, "Delta", "delta"));
        themes.add(new Theme(13, "Dot-Luv", "dot-luv"));
        themes.add(new Theme(14, "Eggplant", "eggplant"));
        themes.add(new Theme(15, "Excite-Bike", "excite-bike"));
        themes.add(new Theme(16, "Flick", "flick"));
        themes.add(new Theme(17, "Glass-X", "glass-x"));
        themes.add(new Theme(18, "Home", "home"));
        themes.add(new Theme(19, "Hot-Sneaks", "hot-sneaks"));
        themes.add(new Theme(20, "Humanity", "humanity"));
        themes.add(new Theme(21, "Le-Frog", "le-frog"));
        themes.add(new Theme(22, "Midnight", "midnight"));
        themes.add(new Theme(23, "Mint-Choc", "mint-choc"));
        themes.add(new Theme(24, "Overcast", "overcast"));
        themes.add(new Theme(25, "Pepper-Grinder", "pepper-grinder"));
        themes.add(new Theme(26, "Redmond", "redmond"));
        themes.add(new Theme(27, "Rocket", "rocket"));
        themes.add(new Theme(28, "Sam", "sam"));
        themes.add(new Theme(29, "Smoothness", "smoothness"));
        themes.add(new Theme(30, "South-Street", "south-street"));
        themes.add(new Theme(31, "Start", "start"));
        themes.add(new Theme(32, "Sunny", "sunny"));
        themes.add(new Theme(33, "Swanky-Purse", "swanky-purse"));
        themes.add(new Theme(34, "Trontastic", "trontastic"));
        themes.add(new Theme(35, "UI-Darkness", "ui-darkness"));
        themes.add(new Theme(36, "UI-Lightness", "ui-lightness"));
        themes.add(new Theme(37, "Vader", "vader"));

        return themes;
    }

    /**
     * Default GUI theme
     *
     * @return the theme
     */
    public String defaultThemeName() {
        return DEFAULT_THEME;
    }

    /**
     * Finds a preference given its id
     *
     * @param id
     * @return preference
     */
    public UserPreference findPreference(int id) {
        return em.find(UserPreference.class, id);
    }

    /**
     * Gets preferences for a user
     *
     * @param user
     * @return preferences
     */
    public List<UserPreference> findPreferences(Sysuser user) {
        List<UserPreference> prefs;
        TypedQuery<UserPreference> query = em.createQuery("SELECT p FROM UserPreference p WHERE p.user = :user", UserPreference.class)
                .setParameter("user", user);
        prefs = query.getResultList();
        logger.log(Level.FINE, "PreferencesEJB#findPreferences: Preferences found: {0}", prefs.size());
        return prefs;
    }

    /**
     * Find the preference value of a given preference name of a user
     *
     * @param user
     * @param name
     * @return preference value
     */
    public UserPreference findPreference(Sysuser user, PreferenceName name) {
        List<UserPreference> prefs;
        TypedQuery<UserPreference> query = em.createQuery("SELECT p FROM UserPreference p WHERE p.user = :user AND p.name = :name", UserPreference.class)
                .setParameter("user", user)
                .setParameter("name", name.toString());
        prefs = query.getResultList();
        logger.log(Level.FINE, "PreferencesEJB#findPreference: Preferences found: {0}", prefs.size());
        if (prefs.isEmpty()) {
            return null;
        } else {
            return prefs.get(0);
        }
    }
    
    /**
     * Find the preference values of a given preference name of a user (for multi-valued preferences)
     *
     * @param user
     * @param name
     * @return preference value list
     */
    public List<UserPreference> findPreferences(Sysuser user, PreferenceName name) {
        List<UserPreference> prefs;
        TypedQuery<UserPreference> query = em.createQuery("SELECT p FROM UserPreference p WHERE p.user = :user AND p.name = :name", UserPreference.class)
                .setParameter("user", user)
                .setParameter("name", name.toString());
        prefs = query.getResultList();
        logger.log(Level.FINE, "PreferencesEJB#findPreferences: Preferences found: {0}", prefs.size());
        
        return prefs;
    }

    /**
     * Find users with the given preference-value
     *
     * 
     * @param name
     * @param value
     * @return list of users
     */
    public List<Sysuser> findUsers(PreferenceName name, String value) {
        List<Sysuser> users;
        TypedQuery<Sysuser> query = em.createQuery("SELECT p.user FROM UserPreference p WHERE p.prefValue = :value AND p.name = :name", Sysuser.class)
                .setParameter("value", value)
                .setParameter("name", name.toString());
        
        users = query.getResultList();
        logger.log(Level.FINE, "PreferencesEJB#findUsers: Users found: {0}", users.size());
        
        return users;
    }
    
    /**
     * Saves preference for a user
     *
     * @param user
     * @param name
     * @param value
     */
    public void savePreference(Sysuser user, PreferenceName name, String value) {
        UserPreference pref = findPreference(user, name);
        if (pref == null) {
            pref = new UserPreference();
            pref.setName(name.toString());
            pref.setUser(user);
        }
        pref.setPrefValue(value);
        savePrefernce(pref);
    }
    
    /**
     * Saves multi-valued preferences for a user
     *
     * @param user
     * @param name
     * @param values
     */
    public void savePreference(Sysuser user, PreferenceName name, List<String> values) {
        
        // first remove current values
        List<UserPreference> prefs = findPreferences(user, name);
        for(UserPreference pref: prefs) {
            em.remove(pref);
        }
        
        // then add the new one
        UserPreference newPref;
        for(String value: values) {
            newPref = new UserPreference();
            newPref.setName(name.toString());
            newPref.setUser(user);
            newPref.setPrefValue(value);
            em.persist(newPref);
        }     
    }

    /**
     * Saves a given preference
     *
     * @param pref the preference
     */
    public void savePrefernce(UserPreference pref) {
        if (pref.getUserPreferenceId() == null) {
            em.persist(pref);
        } else {
            em.merge(pref);
        }
        logger.log(Level.FINE, "PreferencesEJB#findPreferences: Preference saved - {0}", pref.getName());
    }

    /**
     * Saves a bunch of preferences
     *
     * @param preferences
     */
    public void savePrefernces(List<UserPreference> preferences) {
        for (UserPreference pref : preferences) {
            if (pref.getUserPreferenceId() == null) {
                em.persist(pref);
            } else {
                em.merge(pref);
            }
        }
        // logger.log(Level.FINE, "PreferencesEJB#findPreferences: Preference saved - {0}", pref.getName());
    }

    /**
     * Deletes a preference
     *
     * @param pref
     */
    public void deletePreference(UserPreference pref) {
        UserPreference upref = em.find(UserPreference.class, pref.getUserPreferenceId());
        if (upref != null) {
            em.remove(upref);
        }
    }
}
