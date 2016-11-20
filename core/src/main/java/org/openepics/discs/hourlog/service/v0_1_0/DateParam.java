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
package org.openepics.discs.hourlog.service.v0_1_0;

import org.openepics.discs.hourlog.exception.HourLogAPIException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.core.Response.Status;

/**
 * Date Parameter 
 *
 * @author vuppala
 */
public class DateParam {

    private Date date;
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DateParam(String string) throws HourLogAPIException {
        if (string == null || string.isEmpty()) {
            // date = Utility.currentDate();
            date = null;
            return;
        }
        try {
            date = dateFormat.parse(string);
        } catch (ParseException e) {
            throw new HourLogAPIException(Status.BAD_REQUEST, "Invalid date format");
        }
    }

    /**
     * Is date after current date?
     *
     * @param other
     * @return true if it does
     */
    public boolean after(DateParam other) {
        if (date == null || other == null || other.date == null) {
            return false;
        }
        return date.after(other.date);
    }

    /**
     * Is date empty?
     *
     * @return true if it does
     */
    public boolean isEmpty() {       
        return date == null;
    }
    
    // --- getters/setters
    
    public Date getDate() {
        return date;
    }    
}
