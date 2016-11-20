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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.openepics.discs.hourlog.auth.AuthEJB;
import org.openepics.discs.hourlog.ent.Sysuser;

/**
 * State of the User view
 *
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class UserView implements Serializable {

    @EJB
    private AuthEJB authEJB;
    private static final Logger logger = Logger.getLogger(UserView.class.getName());

    private List<Sysuser> users;
    private List<Sysuser> filteredUsers;

    public UserView() {
    }

    @PostConstruct
    public void init() {
        users = authEJB.findUsers();
        logger.log(Level.FINE, "Found number of users: {0}", users.size());
    }

    // --- getters/setters

    public List<Sysuser> getUsers() {
        return users;
    }

    public List<Sysuser> getFilteredUsers() {
        return filteredUsers;
    }

    public void setFilteredUsers(List<Sysuser> filteredUsers) {
        this.filteredUsers = filteredUsers;
    }
    
}
