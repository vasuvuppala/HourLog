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
package org.openepics.discs.hourlog.ejb;

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
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.OperationsRole;
import org.openepics.discs.hourlog.ent.Shift;
import org.openepics.discs.hourlog.ent.ShiftStaffMember;
import org.openepics.discs.hourlog.ent.ShiftStatus;
import org.openepics.discs.hourlog.ent.StaffRole;
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.util.AppProperties;
import org.openepics.discs.hourlog.util.MailGateway;

/**
 * Manager for shifts
 *
 * @author vuppala
 */
@Stateless
public class ShiftEJB {

    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;
    @Inject
    private UserSession userSession;
    @Inject
    private MailGateway mailGateway;
    @Inject
    private AppProperties appProperties;

    private static final Logger logger = Logger.getLogger(ShiftEJB.class.getName());
    private static final String BEAM_COORDINATOR = "Beam Coordinator";

    /**
     * Find all shifts.
     *
     * @return
     */
    public List<Shift> findShifts() {
        List<Shift> shifts;
        TypedQuery<Shift> query = em.createNamedQuery("Shift.findAll", Shift.class);
        shifts = query.getResultList();
        logger.log(Level.FINE, "shifts found: {0}", shifts.size());
        return shifts;
    }

    /**
     * Find beam coordinator role
     *
     * @return the role
     */
    public OperationsRole beeamCoordinatorRole() {
        List<OperationsRole> roles;

        TypedQuery<OperationsRole> query = em.createQuery("SELECT o FROM OperationsRole o WHERE o.name = :name", OperationsRole.class)
                .setParameter("name", BEAM_COORDINATOR)
                .setFirstResult(0)
                .setMaxResults(1);
        roles = query.getResultList();
        if (roles.isEmpty()) {
            logger.log(Level.SEVERE, "No Beam Coordinator Role found in the database!!");
            return null;
        } else {
            return roles.get(0);
        }
    }

    /**
     * Find shifts between a given period for the current facility.
     *
     * @param minDate
     * @param maxDate
     * @return shifts
     */
    public List<Shift> findShifts(Date minDate, Date maxDate) {
        List<Shift> shifts;
        Facility facility = userSession.getFacility();

        TypedQuery<Shift> query = em.createQuery("SELECT s FROM Shift s where s.facility = :facility AND :mindate <= s.updatedAt AND s.updatedAt <= :maxdate ORDER BY s.updatedAt DESC", Shift.class)
                .setParameter("facility", facility)
                .setParameter("mindate", minDate)
                .setParameter("maxdate", maxDate);
        shifts = query.getResultList();

        // get one shift that is just before the min date
        query = em.createQuery("SELECT s FROM Shift s where s.facility = :facility AND s.updatedAt < :mindate ORDER BY s.updatedAt DESC", Shift.class)
                .setParameter("facility", facility)
                .setParameter("mindate", minDate)
                .setFirstResult(0)
                .setMaxResults(1);
        List<Shift> beforeShifts = query.getResultList();
        shifts.addAll(beforeShifts);
        logger.log(Level.FINE, "ShiftEJB.findShifts: shifts between mindate: {0}. maxdate: {1}", new Object[]{minDate, maxDate});
        logger.log(Level.FINE, "ShiftEJB.findShifts: shifts found: {0}", shifts.size());
        logger.log(Level.FINE, "ShiftEJB.findShifts: shifts found: {0}", shifts);
        return shifts;
    }

    /**
     * What is the current shift for a given facility?
     *
     * @author vuppala
     * @param facility The facility
     * @return current shift
     */
    public Shift currentShift(Facility facility) {
        List<Shift> stats;

        TypedQuery<Shift> query = em.createQuery("SELECT s FROM Shift s WHERE s.facility = :facility ORDER BY s.updatedAt DESC", Shift.class)
                .setParameter("facility", facility)
                .setFirstResult(0)
                .setMaxResults(1);
        stats = query.getResultList();

        if (stats.isEmpty()) {
            return null;
        } else {
            return stats.get(0);
        }
    }

    /**
     * What is the current shift for the current facility?
     *
     * @author vuppala
     * @return current shift
     */
    public Shift currentShift() {
        return currentShift(userSession.getFacility());
    }

    /**
     * FInd shift at a given instance.
     *
     * @author vuppala
     * @param instance
     * @return shift
     */
    public Shift findShift(Date instance) {
        List<Shift> shifts;
        Facility facility = userSession.getFacility();

        TypedQuery<Shift> query = em.createQuery("SELECT s FROM Shift s WHERE s.facility = :facility AND s.updatedAt <= :instance ORDER BY s.updatedAt DESC", Shift.class)
                .setParameter("facility", facility)
                .setParameter("instance", instance)
                .setFirstResult(0)
                .setMaxResults(1);
        shifts = query.getResultList();

        if (shifts.isEmpty()) {
            return null;
        } else {
            return shifts.get(0);
        }
    }

    /**
     * Who are on current shift?
     *
     * @author vuppala
     * @param shift given shift
     * @return list of staff member
     */
    public List<ShiftStaffMember> findShiftStaff(Shift shift) {
        List<ShiftStaffMember> staff;

        TypedQuery<ShiftStaffMember> query = em.createQuery("SELECT s FROM ShiftStaffMember s WHERE s.shift = :shift", ShiftStaffMember.class)
                .setParameter("shift", shift);
        staff = query.getResultList();

        return staff;
    }

    /**
     * get all people who are trained as Operator-in-charge
     *
     * @return
     */
    public List<Sysuser> trainedAsOIC() {
        List<Sysuser> users;

        TypedQuery<Sysuser> query = em.createQuery("SELECT u FROM Sysuser u, TrainingRecord t WHERE u.userId = t.employeeNumber AND t.oic = true ORDER BY u.lastName", Sysuser.class);

        users = query.getResultList();
        logger.log(Level.FINE, "ShiftEJB.trainedAsOIC: Found  {0} users", users.size());

        return users;
    }

    /**
     * staff members and their assigned roles
     *
     * @author vuppala
     * @param newShift new shift
     *
     */
    //public void newShift(List<ShiftStaffMember> shiftStaff, Sysuser OIC, String experimenter ) {
    public void newShift(Shift newShift) {
        /*
         for (ShiftStaffMember ssm: shiftStaff) {
         ssm.setShift(shift);
         }
         */
        for (ShiftStaffMember ssm : newShift.getShiftStaffMemberList()) {
            em.merge(ssm.getStaffMember()); // save the SMS addresses
        }

        em.persist(newShift);
    }

    // ----------------------- operation role ------------------
    /**
     * Find all operations roles (not auth roles)
     *
     * @return operations roles
     */
    public List<OperationsRole> findOperationsRoles() {
        List<OperationsRole> oproles;
        TypedQuery<OperationsRole> query = em.createNamedQuery("OperationsRole.findAll", OperationsRole.class);
        oproles = query.getResultList();
        logger.log(Level.FINE, "ShiftEJB.findOperationsRoles: Operations Roles found: {0}", oproles.size());
        return oproles;
    }

    /**
     * Find operations role given its id
     *
     * @param id
     * @return the role
     */
    public OperationsRole findOprole(short id) {
        return em.find(OperationsRole.class, id);
    }

    /**
     * Save the given operations role
     *
     * @param oprole the role
     */
    public void saveOprole(OperationsRole oprole) {
        if (oprole.getOpRoleId() == null) {
            em.persist(oprole);
        } else {
            em.merge(oprole);
        }
        logger.log(Level.FINE, "ShiftEJB#saveOprole: Control Signal saved - {0}", oprole.getName());
    }

    /**
     * Delete the operations role
     *
     * @param oprole the role
     */
    public void deleteOprole(OperationsRole oprole) {
        OperationsRole role = em.find(OperationsRole.class, oprole.getOpRoleId());
        em.remove(role);
    }

    // ----------------------- shift status ------------------
    /**
     * Find all shift status records
     *
     * @return status
     */
    public List<ShiftStatus> findShiftStatus() {
        List<ShiftStatus> stats;
        TypedQuery<ShiftStatus> query = em.createNamedQuery("ShiftStatus.findAll", ShiftStatus.class);
        stats = query.getResultList();
        logger.log(Level.FINE, "ShiftEJB#findShiftStatus: shift status found: {0}", stats.size());
        return stats;
    }

    /**
     * Find shift status given its id
     *
     * @param id
     * @return the status
     */
    public ShiftStatus findShiftStatus(short id) {
        return em.find(ShiftStatus.class, id);
    }

    /**
     * Save given shift status
     *
     * @param stat the status
     */
    public void saveShiftStatus(ShiftStatus stat) {
        if (stat.getStatusId() == null) {
            em.persist(stat);
        } else {
            em.merge(stat);
        }
        logger.log(Level.FINE, "ShiftEJB#saveShiftStatus: Control Signal saved - {0}", stat.getName());
    }

    /**
     * Delete the given status
     *
     * @param status
     */
    public void deleteShiftStatus(ShiftStatus status) {
        ShiftStatus stat = em.find(ShiftStatus.class, status.getStatusId());
        em.remove(stat);
    }

    // ----------------------- Staff Roles ------------------
    /**
     * staff members and their assigned roles
     *
     * @author vuppala
     *
     * @return list of staff member
     */
    public List<StaffRole> findStaffRoles() {
        List<StaffRole> sroles;
        TypedQuery<StaffRole> query = em.createQuery("SELECT s FROM StaffRole s ORDER BY s.staffMember.lastName, s.staffMember.firstName", StaffRole.class);
        sroles = query.getResultList();
        logger.log(Level.FINE, "ShiftEJB.findStaffRoles: staff roles found: {0}", sroles.size());
        return sroles;
    }

    /**
     * Find all operations staff
     *
     * @return operations staff members
     */
    public List<Sysuser> findStaff() {
        List<Sysuser> staff;
        TypedQuery<Sysuser> query = em.createQuery("SELECT DISTINCT s.staffMember FROM StaffRole s", Sysuser.class);
        staff = query.getResultList();
        logger.log(Level.FINE, "ShiftEJB#findStaff: number of operations staff found: {0}", staff.size());
        return staff;
    }

    /**
     * Find operations staff that are assigned the given role
     *
     * @param role
     * @return operations staff members
     */
    public List<Sysuser> findStaff(OperationsRole role) {
        List<Sysuser> staff;
        TypedQuery<Sysuser> query = em.createQuery("SELECT s.staffMember FROM StaffRole s WHERE s.role = :role", Sysuser.class)
                .setParameter("role", role);
        staff = query.getResultList();
        logger.log(Level.FINE, "ShiftEJB#findStaff: staff for given role found: {0}", staff.size());
        return staff;
    }

    /**
     * Find operations staff role given its id
     *
     * @param id
     * @return the staff role
     */
    public StaffRole findStaffRole(short id) {
        return em.find(StaffRole.class, id);
    }

    /**
     * Save operations staff role given its id
     *
     * @param role
     */
    public void saveStaffRole(StaffRole role) {
        if (role.getStaffRoleId() == null) {
            em.persist(role);
        } else {
            em.merge(role);
        }
        logger.log(Level.FINE, "ShiftEJB#saveShiftStatus: Control Signal saved - {0}", role.getStaffRoleId());
    }

    /**
     * Delete a staff role given its id
     *
     * @param srole
     */
    public void deleteStaffRole(StaffRole srole) {
        StaffRole role = em.find(StaffRole.class, srole.getStaffRoleId());
        em.remove(role);
    }
}
