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
package org.openepics.discs.hourlog.exception;

/**
 * Exceptions from external services (Olog, Trouble Reports, etc)
 * 
 * @author vuppala
 */
public class HourLogServiceException extends RuntimeException {

    private final String serviceName;
    
    /**
     * Creates a new instance of <code>HourLogServiceException</code>
     * 
     * @param name Service name
     * @param msg  Error message
     */
    public HourLogServiceException(String name, String msg) {       
        super(msg);
        this.serviceName = name;
    }

    /**
     * Creates a new instance of <code>HourLogServiceException</code>
     * 
     * @param name
     * @param msg
     * @param e
     */
    public HourLogServiceException(String name, String msg, Exception e) {       
        super(msg, e);
        this.serviceName = name;
    }
    
    public String getServiceName() {
        return serviceName;
    }
       
}
