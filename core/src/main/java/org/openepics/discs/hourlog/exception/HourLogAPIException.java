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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *  Hour Log REST API Exception
 * 
 * @author vuppala
 */
public class HourLogAPIException extends WebApplicationException {
    public HourLogAPIException(Response.Status status, String message) {
        // response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        super(Response.status(status).entity(message).type(MediaType.TEXT_HTML).build());
    }
}
