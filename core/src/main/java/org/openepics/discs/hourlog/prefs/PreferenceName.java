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

/**
 * User Preference
 *
 * @author vuppala
 */
public enum PreferenceName {

    DEFAULT_THEME("default-theme"),
    DEFAULT_FACILITY("default-facility"),
    MONITOR_FACILITY("monitor-facility"); // facility a user wants to monitor. multi-valued. used for otifying user when a faiclity goes into UOF

    private final String name;

    private PreferenceName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
