/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013.
 *  State University (c) Copyright 2013.
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.ent.ControlSignal;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.util.AppProperties;
import org.openepics.discs.hourlog.util.AppProperties.HourLogProperty;

/**
 * Manage data from archiver
 *
 * @author vuppala
 */
@Stateless
public class ArchiverEJB implements Serializable {

//    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss"; // date format used in the results from external TR service 
//    private static final String SERVICE_NAME = "ArchiverAppliance";
    @Inject
    private AppProperties appProperties;
    @Inject
    private UserSession userSession;

    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    private static final Logger logger = Logger.getLogger(ArchiverEJB.class.getName());

    private String remSvcStatus = "up";
    private String remSvcStatusMsg = "";
    //    private final static String SIGNAL_NAME = "Z013L-C";

    public ArchiverEJB() {
    }

    /**
     * Find the Hour Log related control signals for the current facility
     *
     * @return signals
     */
    private List<ControlSignal> findSignals() {
        Facility facility = userSession.getFacility();
        List<ControlSignal> signals;
        TypedQuery<ControlSignal> query = em.createQuery("SELECT s FROM ControlSignal s WHERE s.facility = :facility", ControlSignal.class)
                .setParameter("facility", facility);
        signals = query.getResultList();
        logger.log(Level.FINE, "ArchiverEJB#findSignals: Control Signals found: {0}", signals.size());
        return signals;
    }

    /**
     *
     * Get data from archiver between the given dates for all signals
     *
     * @param from
     * @param to
     * @return data from archiver
     */
    public List<ArchiverResponse> fetchSignals(Date from, Date to) {
        List<ControlSignal> signals = findSignals();
        List<ArchiverResponse> signalData = new ArrayList<>();

        if (signals == null || signals.isEmpty()) {
            logger.log(Level.WARNING, "No signals found");
            return signalData;
        }

        for (ControlSignal signal : signals) {
            logger.log(Level.FINE, "getting  controls data for {0}", signal.getName());
            List<ArchiverResponse> pvdata = null;
            int counter = 0;
            // TODO: Remove after archiver RESTful intertface is fixed.
            // Kludge to workaround bug in archiver appliance's REST interface (content-type is not always set)
            while (pvdata == null && counter < 5) {
                pvdata = getFromArchiver(signal, from, to);
                counter++;
            }
            if (pvdata != null) {
                signalData.addAll(pvdata);
            }
        }

        return signalData;
    }

    /**
     * Fetch data from archiver for a given signal between the given dates
     *
     * @param signal
     * @param from
     * @param to
     * @return data from archiver
     */
    private List<ArchiverResponse> getFromArchiver(ControlSignal signal, Date from, Date to) {
        List<ArchiverResponse> pvdata = null;

        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

            Client client = ClientBuilder.newClient();
            // client.register(ClientResponseLoggingFilter.class);

            String baseUri = appProperties.getProperty(HourLogProperty.EPICS_ARCHIVER_BASE);
            String path = appProperties.getProperty(HourLogProperty.EPICS_ARCHIVER_PATH);

            logger.log(Level.FINE, "ArchiverEJB#getFromArchiver: getting pv data");
            logger.log(Level.FINE, "ArchiverEJB#getFromArchiver: qparam {0} {1} {2}", new Object[]{signal.getName(), fmt.format(from), fmt.format(to)});

            pvdata = client.target(baseUri).path(path).queryParam("pv", signal.getName())
                    .queryParam("from", fmt.format(from)).queryParam("to", fmt.format(to))
                    .request("application/json")
                    .get(new GenericType<List<ArchiverResponse>>() {
                    });

            if (pvdata == null) {
                logger.log(Level.WARNING, "ArchiverEJB#getFromArchiver: null response");
            } else {
                logger.log(Level.FINE, "ArchiverEJB#getFromArchiver: Number of pv records from archiver: {0}", pvdata.size());
                for (ArchiverResponse rec : pvdata) {
                    rec.getMeta().setDescription(signal.getDescription());
                    logger.log(Level.FINE, "ArchiverEJB#getFromArchiver: {0}", rec);
                }
            }
//        } catch (MessageBodyProviderNotFoundException e) {
//            logger.log(Level.SEVERE, "ArchiverEJB#getFromArchiver:: Archiver API did not have content-type set");
        } catch (Exception e) {
            remSvcStatus = "down";
            remSvcStatusMsg = "Remote service appears to be down";
            logger.log(Level.SEVERE, "ArchiverEJB#getFromArchiver:: {0}", remSvcStatusMsg);
            System.out.println(e);
        } finally {
        }

        return pvdata;
    }
}
