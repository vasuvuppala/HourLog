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

/**
 * The various operations that can be performed on resources by roles
 * 
 * @author vuppala
 */
public enum Operation {
    CREATE ("Create"),
    EDIT_ANY ("EditAny"),
    EDIT_RECENT ("EditRecent"),
    UPDATE_STATUS ("UpdateStatus"),
    OPERATE_SHIFT ("OperateShift"),
    MANAGE ("Manage"),
    GEN_REPORTS ("GenerateReports"),
    EXECUTE ("Execute");
    
    private final String name;
    
    Operation(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
