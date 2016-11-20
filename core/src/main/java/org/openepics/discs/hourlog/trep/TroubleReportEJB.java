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
package org.openepics.discs.hourlog.trep;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.openepics.discs.hourlog.ent.TroubleReport;
import org.primefaces.model.SortOrder;

/**
 * Manager for trouble reports
 *
 * @author vuppala
 */
@Stateless
public class TroubleReportEJB {

    private static final Logger logger = Logger.getLogger(TroubleReportEJB.class.getName());

    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    /**
     * Find a trouble report given its id
     *
     * @param id
     * @return the TR
     */
    public TroubleReport findTroubleReport(int id) {
        return em.find(TroubleReport.class, id);
    }

    /**
     * Finds all trouble reports
     *
     * @return TRs
     */
    public List<TroubleReport> findTroubleReports() {
        List<TroubleReport> treps;
        TypedQuery<TroubleReport> query = em.createQuery("SELECT t from TroubleReport t ORDER BY t.reportDate DESC", TroubleReport.class);
        treps = query.getResultList();
        logger.log(Level.FINE, "TroubleReportEJB#findTroubleReports: trouible reports found: {0}", treps.size());
        return treps;
    }

    /**
     * Counts the number of trouble reports matching the given filters
     *
     * @param filters
     * @return number of TRs
     */
    public int countTroubleReports(Map<String, Object> filters) {
        StringBuilder qString = new StringBuilder("SELECT count(t) FROM TroubleReport t ");

        String whereClause = whereClause(filters);
        if (!whereClause.isEmpty()) {
            qString.append(" WHERE ");
            qString.append(whereClause);
        }

        logger.log(Level.FINE, "TroubleReportEJB#countTroubleReports: query is {0}", qString);
        TypedQuery<Number> query = em.createQuery(qString.toString(), Number.class);

        int count = query.getSingleResult().intValue();

        return count;
    }

    /**
     * Finds a page of trouble reports for a given period
     *
     * @param first
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param filters
     * @return
     */
    public List<TroubleReport> findTroubleReports(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        StringBuilder qString = new StringBuilder("SELECT t FROM TroubleReport t ");

        String whereClause = whereClause(filters);
        if (!whereClause.isEmpty()) {
            qString.append(" WHERE ");
            qString.append(whereClause);
        }

        qString.append(" ORDER BY ");
        if (sortField == null) {
            qString.append(" t.reportDate DESC");
        } else {
            qString.append(" t.");
            qString.append(sortField);
            qString.append(SortOrder.ASCENDING.equals(sortOrder) ? " ASC " : " DESC ");
        }

        logger.log(Level.FINE, "TroubleReportEJB#findTroubleReports: query is {0}", qString);
        TypedQuery<TroubleReport> query = em.createQuery(qString.toString(), TroubleReport.class)
                .setFirstResult(first)
                .setMaxResults(pageSize);

        List<TroubleReport> treps = query.getResultList();
        return treps;
    }

    /**
     * Generates the where clause from a list of filters
     *
     * @param filters
     * @return where clause
     */
    private String whereClause(Map<String, Object> filters) {
        StringBuilder theClause = new StringBuilder("");

        if (filters != null) {
            // qString.append(" WHERE ");
            Object fieldValue;
            for (String field : filters.keySet()) {
                fieldValue = filters.get(field);
                if (fieldValue != null) {
                    if (theClause.length() != 0) {
                        theClause.append(" AND ");
                    }
                    theClause.append(" t.");
                    theClause.append(field);
                    theClause.append(" LIKE '%");
                    theClause.append(fieldValue.toString().trim());
                    theClause.append("%' ");
                }
            }
        }

        return theClause.toString();
    }

}
