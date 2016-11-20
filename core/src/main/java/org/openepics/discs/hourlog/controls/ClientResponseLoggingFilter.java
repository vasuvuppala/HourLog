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
package org.openepics.discs.hourlog.controls;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

/**
 * To log responses from a REST service. Useful to troubleshooting.
 *
 * @author vuppala
 */
public class ClientResponseLoggingFilter implements ClientResponseFilter {

    @Override
    public void filter(final ClientRequestContext reqCtx,
            final ClientResponseContext resCtx) throws IOException {
        System.out.println("status: " + resCtx.getStatus());
        System.out.println("date: " + resCtx.getDate());
        System.out.println("last-modified: " + resCtx.getLastModified());
        System.out.println("location: " + resCtx.getLocation());
        System.out.println("headers:");
        for (Entry<String, List<String>> header : resCtx.getHeaders()
                .entrySet()) {
            System.out.print("\t" + header.getKey() + " :");
            for (String value : header.getValue()) {
                System.out.print(value + ", ");
            }
            System.out.print("\n");
        }
        System.out.println("media-type: " + resCtx.getMediaType() == null ? "Null" : resCtx.getMediaType().getType());
    }

}
