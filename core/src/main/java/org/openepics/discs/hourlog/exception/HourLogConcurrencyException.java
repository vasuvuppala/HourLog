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
package org.openepics.discs.hourlog.exception;

import javax.ejb.ApplicationException;

/**
 * Exception to indicate that there have been multiple concurrent updates to an entity.
 * 
 * @author vuppala
 */
@ApplicationException
public class HourLogConcurrencyException extends RuntimeException {

    /**
     * Creates a new instance of <code>HourLogConcurrencyException</code>
     * without detail message.
     */
    public HourLogConcurrencyException() {
    }

    /**
     * Constructs an instance of <code>HourLogConcurrencyException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public HourLogConcurrencyException(String msg) {
        super(msg);
    }
}
