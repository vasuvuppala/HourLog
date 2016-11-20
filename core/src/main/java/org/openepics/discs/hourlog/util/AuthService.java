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
package org.openepics.discs.hourlog.util;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.openepics.discs.hourlog.exception.HourLogAPIException;

/**
 * Authentication and Authorization service
 *
 * TODO: interface with RBAC
 *
 * @author vuppala
 */
public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private static String AUTH_HEADER_NAME = "DISCS-Authorization";
    String identity;
    String passcode;
    boolean authenticated = false;

    public AuthService() {

    }

    public AuthService(@Context HttpHeaders headers) {
        if (headers == null) {
            return;
        }

        List<String> authValues = headers.getRequestHeader(AUTH_HEADER_NAME);
        if (authValues == null || authValues.isEmpty()) {
            throw new HourLogAPIException(Response.Status.UNAUTHORIZED, "Requires DISCS Authentication");
        }
        String authId = headers.getRequestHeader(AUTH_HEADER_NAME).get(0);

        logger.log(Level.FINE, "AuthService#: auth string is {0}", authId);
        String[] authIdParts = authId.split(":", 2);

        if (authIdParts.length != 2) {
            throw new HourLogAPIException(Response.Status.FORBIDDEN, "Invalid DISCS Authentication");
        }

        identity = authIdParts[0];
        passcode = authIdParts[1];

        if (identity == null || identity.isEmpty()) {
            throw new HourLogAPIException(Response.Status.UNAUTHORIZED, "Invalid DISCS Authentication");
        }

        logger.log(Level.FINE, "getFacility: auth identity is " + identity);
        // TODO: Authenticate
        authenticated = true;
    }

    public boolean isValid() {
        return authenticated;
    }

    public boolean isUserInRole(String role) {
        //TODO: Check role membership
        return authenticated;
    }
}
