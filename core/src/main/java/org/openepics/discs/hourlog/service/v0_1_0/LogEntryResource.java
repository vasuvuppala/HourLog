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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.openepics.discs.hourlog.ejb.FacilityEJB;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.log.LogEntry;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.log.LogbookManager;
import org.openepics.discs.hourlog.util.AuthService;

/**
 * RESTful Event resource
 *
 * @author vuppala
 */
@Path("/v0.1.0/notes")
public class LogEntryResource {

    @EJB
    private FacilityEJB facilityEJB;   
    @EJB(beanName = "OlogLogbookManager")
    private LogbookManager logbookEJB;

    private static final Logger logger = Logger.getLogger(LogEntryResource.class.getName());
    private static final String DEFAULT_SIZE = "100"; // maximum number of events to be returned
    private static final String DEFAULT_START = "0"; // starting from

    /**
     * Serve events matching the given criteria to API clients
     *
     * @param facilityName
     * @param start
     * @param size
     * @param from
     * @param to
     * @param headers
     * @param phrase
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public List<LogEntryRep> getNotes(@QueryParam("facility") String facilityName,
            @DefaultValue(DEFAULT_START) @QueryParam("start") int start,
            @DefaultValue(DEFAULT_SIZE) @QueryParam("size") int size,
            @DefaultValue("") @QueryParam("from") DateParam from,
            @DefaultValue("") @QueryParam("to") DateParam to,
            @DefaultValue("") @QueryParam("phrase") String phrase,
            @Context final HttpHeaders headers) {

        logger.log(Level.FINE, "LogEntryResource#getNotes");

        List<LogEntry> notes;
        List<LogEntryRep> resources = new ArrayList<>();

        AuthService authService = new AuthService(headers);
        if (!authService.isUserInRole("application")) {
            throw new HourLogAPIException(Response.Status.FORBIDDEN, "Not authorized to access this resource");
        }

        Facility facility = facilityEJB.findFacility(facilityName);
        if (facility == null) {
            throw new HourLogAPIException(Response.Status.BAD_REQUEST, "Empty or invalid facility name");
        }
        
        // -- set dates        
        if (from.after(to)) {
            DateParam temp = from;
            from = to;
            to = temp;
        }
        
        List<Logbook> logbooks = new ArrayList<>();
        logbooks.add(facility.getOpsLogbook());
        notes = logbookEJB.findEntries(logbooks, start, size, from.getDate(), to.getDate(), phrase);
        logger.log(Level.FINE, "LogEntryResource#getNotes {0}", notes.size());
        
        LogEntryRep noteRep;
        for (LogEntry note : notes) {
            noteRep = LogEntryRep.newInstance(note);
            resources.add(noteRep);
        }

        return resources;
    }
}
