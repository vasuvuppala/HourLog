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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.openepics.discs.hourlog.ent.AuthPermission;
import org.openepics.discs.hourlog.ent.AuthResource;
import org.openepics.discs.hourlog.ent.AuthUserRole;
import org.openepics.discs.hourlog.ent.AuthOperation;
import org.openepics.discs.hourlog.ent.AuthRole;
import org.openepics.discs.hourlog.ent.Sysuser;

/**
 * Manage authorizations
 * 
 * @author vuppala
 */
@Stateless
public class AuthEJB {

    @Inject
    private UserSession userSession;
    private static final Logger logger = Logger.getLogger(AuthEJB.class.getName());
    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;
    
    /**
     * Find auth roles
     * 
     * @author vuppala 
     * @return all roles
     */
    public List<AuthRole> findRoles() {
        List<AuthRole> roles;
        TypedQuery<AuthRole> query = em.createNamedQuery("AuthRole.findAll", AuthRole.class);
        roles = query.getResultList();
        logger.log(Level.FINE, "roles found: {0}", roles.size());
        return roles;
    }
    
    /**
     * Find user roles
     * 
     * @author vuppala 
     * @return all user roles
     */
    public List<AuthUserRole> findAuthUserRoles() {
        List<AuthUserRole> uroles;
        TypedQuery<AuthUserRole> query = em.createNamedQuery("AuthUserRole.findAll", AuthUserRole.class);
        uroles = query.getResultList();
        logger.log(Level.FINE, "user roles found: {0}", uroles.size());
        return uroles;
    }  
    
     /**
     * Find operations
     * 
     * @author vuppala 
     * @return all operations roles
     */
    public List<AuthOperation> findAuthOperations() {
        List<AuthOperation> opers;
        TypedQuery<AuthOperation> query = em.createNamedQuery("AuthOperation.findAll", AuthOperation.class);
        opers = query.getResultList();
        logger.log(Level.FINE, "Auth operations found: {0}", opers.size());
        return opers;
    }
       
    /**
     * Find resources
     * 
     * @author vuppala 
     * @return all resources
     */
    public List<AuthResource> findAuthResources() {
        List<AuthResource> res;
        TypedQuery<AuthResource> query = em.createNamedQuery("AuthResource.findAll", AuthResource.class);
        res = query.getResultList();
        logger.log(Level.FINE, "resources found: {0}", res.size());
        return res;
    }
    
    /**
     * Find permissions
     * 
     * @author vuppala 
     * @return all resources
     */
    public List<AuthPermission> findAuthPermissions() {
        List<AuthPermission> perms;
        TypedQuery<AuthPermission> query = em.createNamedQuery("AuthPermission.findAll", AuthPermission.class);
        perms = query.getResultList();
        logger.log(Level.FINE, "AuthPermissions found: {0}", perms.size());
        return perms;
    }
    
    /**
     * Save the given role
     * 
     * @param role 
     */
    public void saveAuthRole(AuthRole role) {
        if (role.getRoleId() == null) {
            em.persist(role);
        } else {
            em.merge(role);
        }
        logger.log(Level.FINE, "AuthEJB#saveAuthRole: role saved - {0}", role.getRoleName());
    }
    
    /**
     * Delete the given role
     * 
     * @param role 
     */
    public void deleteAuthRole(AuthRole role) {
        AuthRole authRole = em.find(AuthRole.class, role.getRoleId());
        em.remove(authRole);
    }
    
    /**
     * Save the given resource
     * 
     * @param resource 
     */
    public void saveAuthResource(AuthResource resource) {
        if (resource.getResourceId() == null) {
            em.persist(resource);
        } else {
            em.merge(resource);
        }
        // logger.log(Level.FINE, "AuthEJB#saveAuthResource: role saved - {0}", resource.getName());
    }
    
    /**
     * Delete the given resource
     * 
     * @param resource 
     */
    public void deleteAuthResource(AuthResource resource) {
        AuthResource authResource = em.find(AuthResource.class, resource.getResourceId());
        em.remove(authResource);
    }
    
    /**
     * Save the given permission
     * 
     * @param perm 
     */
    public void saveAuthPermission(AuthPermission perm) {
        if (perm.getPrivilegeId() == null) {
            em.persist(perm);
        } else {
            em.merge(perm);
        }
        // logger.log(Level.FINE, "AuthEJB#saveAuthResource: role saved - {0}", resource.getName());
    }
    
    /**
     * Delete the given permission
     * 
     * @param perm 
     */
    public void deleteAuthPermission(AuthPermission perm) {
        AuthPermission authPerm = em.find(AuthPermission.class, perm.getPrivilegeId());
        em.remove(authPerm);
    }
    
    /**
     * save the given user-role 
     * 
     * @param urole 
     */
    public void saveAuthUserRole(AuthUserRole urole) {
        if (urole.getUserRoleId() == null) {
            em.persist(urole);
        } else {
            em.merge(urole);
        }
        // logger.log(Level.FINE, "AuthEJB#saveAuthResource: role saved - {0}", resource.getName());
    }
    
    /**
     * delete the given auth-role
     * 
     * @param urole 
     */
    public void deleteAuthUserRole(AuthUserRole urole) {
        AuthUserRole authUserRole = em.find(AuthUserRole.class, urole.getUserRoleId());
        em.remove(authUserRole);
    }
    
    /**
     * find a role given its id
     * 
     * @param id
     * @return the role
     */
    public AuthRole findAuthRole(int id) {
        return em.find(AuthRole.class, id);
    }
    
    /**
     * find a resource given its id
     * 
     * @param id
     * @return the resource 
     */
    public AuthResource findAuthResource(int id) {
        return em.find(AuthResource.class, id);
    }
    
    /**
     * find an operation given its id
     * 
     * @param id
     * @return the operation
     */
    public AuthOperation findAuthOperation(int id) {
        return em.find(AuthOperation.class, id);
    }
    
    // ------------------- Users -------------------
    /**
     * find all users in the system
     * 
     * @return list of users
     */
    public List<Sysuser> findUsers() {
        List<Sysuser> users;
        TypedQuery<Sysuser> query = em.createQuery("SELECT u FROM Sysuser u ORDER BY u.lastName, u.firstName", Sysuser.class);
        users = query.getResultList();
        logger.log(Level.FINE, "AuthEJB#findUsers:  found users {0}", users.size());
        return users;
    }

    /**
     * find all users who are current employees
     * 
     * @return list of users
     */
    public List<Sysuser> findCurrentUsers() {
        List<Sysuser> users;
        TypedQuery<Sysuser> query = em.createQuery("SELECT u FROM Sysuser u WHERE u.currentEmployee = TRUE ORDER BY u.lastName, u.firstName", Sysuser.class);
        users = query.getResultList();
        logger.log(Level.FINE, "AuthEJB#findCurrentUsers:  found users {0}", users.size());
        return users;
    }
    
    /** 
     * find a user given its id
     * 
     * @param id
     * @return the user 
     */
    public Sysuser findUser(int id) {
        return em.find(Sysuser.class, id);
    }

    /**
     * Finds user given its login id.
     * 
     *
     * @param loginId  Id of the desired user
     * @return User for the given login id
     */
    public Sysuser findUser(String loginId) {
        TypedQuery<Sysuser> query = em.createNamedQuery("Sysuser.findByLoginId", Sysuser.class).setParameter("loginId", loginId);
        List<Sysuser> emps = query.getResultList();

        if (emps == null || emps.isEmpty()) {
            logger.log(Level.WARNING, "UserEJB: No user found with id {0}", loginId);
            return null;
        }

        if (emps.size() > 1) {
            logger.log(Level.WARNING, "UserEJB: there are more than 1 employee with the same login id {0}", loginId);
        }
        return emps.get(0);
    }
    
    /**
     * save the given user
     * 
     * @param user 
     */
    public void saveUser(Sysuser user) {
        if (user == null) {
            return;
        }
        
        if (user.getUserId() == null) {
            em.persist(user);
        } else {
            em.merge(user);
        }
        logger.log(Level.FINE, "HourLogEJB#saveUser: User saved - {0}", user.getUserId());
    }

    /**
     * delete the given user
     * 
     * @param user 
     */
    public void deleteUser(Sysuser user) {
        Sysuser muser = em.find(Sysuser.class, user.getUserId());
        em.remove(muser);
    }
     
}
