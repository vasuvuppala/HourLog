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
package org.openepics.discs.hourlog.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.management.relation.Role;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.openepics.discs.hourlog.ent.AuthOperation;
import org.openepics.discs.hourlog.ent.AuthPermission;
import org.openepics.discs.hourlog.ent.AuthResource;
import org.openepics.discs.hourlog.log.LogEntry;
import org.openepics.discs.hourlog.ent.AuthUserRole;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.util.AppProperties;
import org.openepics.discs.hourlog.util.Utility;

/**
 * An implementation for the AuthManager interface using local entities 
 * 
 * 
 * @author vuppala
 */
@Stateless
public class LocalAuthManager implements AuthManager {

    @Inject
    private UserSession userSession;

    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;
    private static final Logger logger = Logger.getLogger(LocalAuthManager.class.getName());

    @Override
    public boolean canCreateLog() {
        logger.log(Level.FINE, "AuthEJB: checking create log auth for {0}", userSession.getUserId());

        List<Operation> operations = Arrays.asList(Operation.CREATE);
        return canPerformOnLogbook(operations);
    }

    @Override
    public boolean isValidUser() {     
        return (userSession.getUser() != null);
    }
    
    /**
     * Rules:
     *    1. Someone with 'EDIT_ANY' priv can edit any entry
     *    2. A user can edit his/her entry as long as it is recent
     *    3. Someone with 'EDIT_RECNT' can edit any entry that is recent
     * 
     * @param entry
     * @return 
     */
    @Override
    public boolean canEditLog(LogEntry entry) {
        Sysuser user = userSession.getUser();
        List<Operation> operations = Arrays.asList(Operation.EDIT_ANY);

        if (user == null || entry == null) {
            return false;
        }
        if (user.equals(entry.getSysuser()) && isRecent(entry.getOccurredAt())) {
            return true;
        }
        if (canPerformOnLogbook(operations)) {
            return true;
        }
        operations = Arrays.asList(Operation.EDIT_RECENT);

        return canPerformOnLogbook(operations) && isRecent(entry.getOccurredAt());
    }

    @Override
    public boolean canSetAnyDate() {      
        List<Operation> operations = Arrays.asList(Operation.EDIT_ANY);
        return canPerformOnLogbook(operations);
    }
    
    @Override
    public boolean canOperateShift() {
        List<Operation> operations = Arrays.asList(Operation.OPERATE_SHIFT);
        return canPerformOnFacility(operations);
    }

    @Override
    public boolean canUpdateStatus() {
        List<Operation> operations = Arrays.asList(Operation.UPDATE_STATUS);
        return canPerformOnFacility(operations);
    }

    @Override
    public boolean canManageFacility() {
        List<Operation> operations = Arrays.asList(Operation.MANAGE);
        return canPerformOnFacility(operations);
    }

    @Override
    public boolean canManageRole(Sysuser user, Role role) {
        TypedQuery<AuthUserRole> query = em.createQuery("SELECT a FROM AuthUserRole a WHERE a.user = :user AND a.role = :role AND a.isRoleManager = TRUE", AuthUserRole.class);
        List<AuthUserRole> userRoles = query.getResultList();
        
        return !userRoles.isEmpty();
    }

    
    @Override
    public boolean canGenerateReports() {
        Sysuser user = userSession.getUser();
        AuthResource resource = findResource("Application:HourLog");
        List<Operation> operations = Arrays.asList(Operation.GEN_REPORTS);
        List<AuthOperation> authOps = toAuthOperations(operations);
        
        return canPerform(user, authOps, resource);
    }
    
    @Override
    public boolean canExecuteApp() {
        return true;
    }
   
    /**
     * can the current user perform the given operations on the current facility
     * 
     * @param operations
     * @return true if she can
     */
    private boolean canPerformOnFacility(List<Operation> operations) {
        AuthResource resource = toResource(userSession.getFacility());
        AuthResource allFacs = findResource("Facility:*"); // TODO: improve
        Sysuser user = userSession.getUser();
        List<AuthOperation> authOps = toAuthOperations(operations);

        return canPerform(user, authOps, resource) || canPerform(user, authOps, allFacs);
    }

    /**
     * Can the current user perform the given operations on the current facility's Operations Logbook
     * 
     * @param operations
     * @return true if she can
     */
    private boolean canPerformOnLogbook(List<Operation> operations) {
        AuthResource resource = toResource(userSession.getFacility().getOpsLogbook());
        Sysuser user = userSession.getUser();
        List<AuthOperation> authOps = toAuthOperations(operations);

        return canPerform(user, authOps, resource);
    }

    /**
     * Can the user perform any of the operations on the resource
     *
     * @param user
     * @param operations
     * @param resource
     * @return
     */
    private boolean canPerform(Sysuser user, List<AuthOperation> operations, AuthResource resource) {
        if (user == null || resource == null || operations == null || operations.isEmpty()) {
            return false;
        }
        
        TypedQuery<AuthPermission> query = em.createQuery("SELECT p FROM AuthPermission p JOIN p.role.authUserRoleList r WHERE r.user = :user AND p.resource = :resource AND p.operation IN :operations", AuthPermission.class)
                .setParameter("user", user)
                .setParameter("operations", operations)
                .setParameter("resource", resource);
        List<AuthPermission> permissions = query.getResultList();
        return !permissions.isEmpty();
    }

    /**
     * check if the given time falls within the valid time window
     * 
     * @param date
     * @return true if it does
     */
    private boolean validTimeWindow(Date date) {
        Date currentDate = Utility.currentDate();
        Date maxLogDate = Utility.addMinutesToDate(currentDate, AppProperties.LOG_FUTURE_WINDOW);
        Date minLogDate = Utility.addMinutesToDate(currentDate, -AppProperties.LOG_PAST_WINDOW);

        if (date == null || date.before(minLogDate) || date.after(maxLogDate)) {
            return false;
        }

        return true;
    }
    
    /**
     * check if the given time si 'recent'
     * 
     * A date/time is recent if it falls within last RECENT_TIME_WINDOW hours.
     * 
     * @param date
     * @return true if it is recent
     */
    private boolean isRecent(Date date) {
        Date currentDate = Utility.currentDate();
        // Date maxLogDate = Utility.addMinutesToDate(currentDate, AppProperties.LOG_FUTURE_WINDOW);
        Date minLogDate = Utility.addMinutesToDate(currentDate, -AppProperties.RECENT_TIME_WINDOW);

        if (date == null || date.before(minLogDate)) {
            return false;
        }

        return true;
    }

    /**
     * Convert the given facility to a resource
     * 
     * @param facility
     * @return corresponding resource
     */
    private AuthResource toResource(Facility facility) {
        return findResource("Facility:" + facility.getFacilityName());
    }

    /**
     * Convert a logbook to a resource
     * 
     * @param logbook
     * @return corresponding resource
     */
    private AuthResource toResource(Logbook logbook) {
        return findResource("Logbook:" + logbook.getLogbookName());
    }
    
    /**
     * Find a resource given its name
     * 
     * @param name
     * @return the resource
     */
    private AuthResource findResource(String name) {
        TypedQuery<AuthResource> query = em.createQuery("SELECT r FROM AuthResource r WHERE r.name = :name", AuthResource.class)
                .setParameter("name", name);
        List<AuthResource> resources = query.getResultList();
        if (resources == null || resources.isEmpty()) {
            logger.log(Level.WARNING, "#findResource: cannot determine resource {0}", name);
            return null;
        }
        return resources.get(0);
    }

    /**
     * Convert a list of operation names to a list of AuthOperation entities
     * 
     * @param operations
     * @return list of AuthOperation records
     */
    private List<AuthOperation> toAuthOperations(List<Operation> operations) {
        List<String> names = new ArrayList<>();

        for (Operation op : operations) {
            names.add(op.toString());
        }

        TypedQuery<AuthOperation> query = em.createQuery("SELECT r FROM AuthOperation r WHERE r.name IN :names", AuthOperation.class)
                .setParameter("names", names);
        List<AuthOperation> resources = query.getResultList();
        if (resources == null || resources.isEmpty()) {
            logger.log(Level.WARNING, "#findOperation: cannot find opertions: {0}", names);
            return null;
        }
        return resources;
    }

}
