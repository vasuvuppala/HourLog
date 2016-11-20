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
package org.openepics.discs.hourlog.status;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.openepics.discs.hourlog.ent.ExternalService;

/**
 * Manager of base entities (source, vault, mode etc)
 *
 * @author vuppala
 */
@Stateless
public class ExternalServiceEJB {

    private static final Logger logger = Logger.getLogger(ExternalServiceEJB.class.getName());
    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    // ------------------------ Beam ------------------------
    /**
     * find all external services
     *
     * @return beams
     */
    public List<ExternalService> findExtService() {
        List<ExternalService> services;
        TypedQuery<ExternalService> query = em.createNamedQuery("ExternalService.findAll", ExternalService.class);
        services = query.getResultList();
        logger.log(Level.FINE, "Ext services found: {0}", services.size());
        return services;
    }

    /**
     * save a given external service in the database
     *
     * @param service
     */
    public void saveService(ExternalService service) {
        if (service.getServiceId() == null) {
            em.persist(service);
        } else {
            em.merge(service);
        }
        logger.log(Level.FINE, "Ext service saved - {0}", service.getServiceId());
    }

    /**
     * delete an ext service
     *
     * @param service
     */
    public void deleteService(ExternalService service) {
        ExternalService mservice = em.find(ExternalService.class, service.getServiceId());
        em.remove(mservice);
    }

    /**
     * find an external service beam given its id
     *
     * @param id
     * @return the service
     */
    public ExternalService findService(int id) {
        return em.find(ExternalService.class, id);
    }

    /**
     *
     * @param serv
     * @return
     */
    public ExternalService findService(ExtServiceName serv) {
        return findService(serv.toString());
    }

    /**
     * find an external service beam given its name
     *
     * @param name
     * @return the service
     */
    public ExternalService findService(String name) {
        TypedQuery<ExternalService> query = em.createNamedQuery("ExternalService.findByName", ExternalService.class).setParameter("name", name);
        List<ExternalService> services = query.getResultList();

        if (services == null || services.isEmpty()) {
            logger.log(Level.FINE, "No ExternalService found with name {0}", name);
            return newService(name);
        }

        if (services.size() > 1) {
            logger.log(Level.WARNING, "There are more than 1 services with the same name {0}", name);
        }
        return services.get(0);
    }

    /**
     * create a new instance of service
     *
     * @param name
     * @return
     */
    private ExternalService newService(String name) {
        ExternalService service = new ExternalService();
        service.setName(name);
        service.setDescription(name);
        service.setStatus(ServiceStatusCode.SERVICE_DOWN.toString());
        service.setStatusMessage("New service");
        return service;
    }

    /**
     * Update status
     *
     * @param service
     * @param upDate
     * @param stat
     * @param message
     * @param successful
     */
//    public void updateStatus(ExternalService service, Date upDate, ServiceStatusCode stat, String message, boolean successful) {        
//        if (successful) {
//            service.setUpdatedAt(upDate);
//        }
//        service.setStatusMessage(message);
//        service.setStatus(stat.toString());     
//    }
}
