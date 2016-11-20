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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * RESTful resource for Application State
 *
 * @author vuppala
 */
@Path("/v0.1.0/state")
public class StateResource {

    private static final Logger logger = Logger.getLogger(StateResource.class.getName());

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public StateRep getState(@Context final HttpHeaders headers) {

        logger.log(Level.FINE, "EventResource#getEvents");

        StateRep resource = new StateRep();

        // TODO: Compute the state
        // --- external services
        resource.addServiceStatus("database", StateRep.ServiceStatus.UP);
        resource.addServiceStatus("trouble-reports", StateRep.ServiceStatus.UP);
        resource.addServiceStatus("experiments", StateRep.ServiceStatus.UP);
        resource.addServiceStatus("olog", StateRep.ServiceStatus.UP);

        // --- system
        resource.setSystemstatus(0, 0, 0);

        // -- summary
        resource.setSummary(0);

        return resource;
    }
}
