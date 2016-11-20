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
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.openepics.discs.hourlog.ejb.FacilityEJB;
import org.openepics.discs.hourlog.ejb.SnapshotEJB;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.util.AuthService;
import org.openepics.discs.hourlog.log.StatusSnapshot;
import org.openepics.discs.hourlog.util.Utility;

/**
 * RESTful facility resource
 *
 * @author vuppala
 */
@Path("/v0.1.0/facilities")
public class FacilityResource {

    @EJB
    private FacilityEJB facilityEJB;
    @EJB
    private SnapshotEJB snapshotEJB;

    private static final Logger logger = Logger.getLogger(FacilityResource.class.getName());

    /**
     * List of all facilities
     *
     * @param headers
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public List<Facility> getFacilities(@Context final HttpHeaders headers) {
        List<Facility> facilities;

        logger.entering(FacilityResource.class.getName(), "getFacility");

        AuthService authService = new AuthService(headers);
        if (!authService.isUserInRole("application")) {
            throw new HourLogAPIException(Response.Status.FORBIDDEN, "Not authorized to access this resource");
        }
        facilities = facilityEJB.findAllFacilities();
        return facilities;
    }

    /**
     * get a snapshot
     *
     * @param facilityName
     * @param headers
     * @return
     * @throws IOException
     */
    @GET
    @Path("/{facility}/snapshot")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public StatusSnapshot getSnapshot(@PathParam("facility") String facilityName,
            @Context final HttpHeaders headers) throws IOException {

        logger.entering(FacilityResource.class.getName(), "getFacility");

        AuthService authService = new AuthService(headers);
        if (!authService.isUserInRole("application")) {
            throw new HourLogAPIException(Response.Status.FORBIDDEN, "Not authorized to access this resource");
        }

        Facility facility = facilityEJB.findFacility(facilityName);
        if (facility == null) {
            throw new HourLogAPIException(Response.Status.NOT_FOUND, " Invalid facility name: " + facilityName);
        }

        StatusSnapshot snapshot = snapshotEJB.snapshotAt(facility, Utility.currentDate());

        if (snapshot == null) {
            throw new HourLogAPIException(Response.Status.NOT_FOUND, " This is strange. No current status record found for " + facilityName);
        }

        return snapshot;
    }
}
