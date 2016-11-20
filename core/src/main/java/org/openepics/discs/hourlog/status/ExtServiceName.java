/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2014, 2015.
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
package org.openepics.discs.hourlog.status;

/**
 * Names of external services. 
 * 
 * Note: Names must match those  in the database.
 * 
 * @author vuppala
 */
public enum ExtServiceName {
    TROUBLE_REPORT("TroubleReport"),
    TRAINING("Training"),
    EXPERIMENTS("ApprovedExperiments"),
    LOGBOOK("Olog");
    
    private final String name;
    
    private ExtServiceName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
