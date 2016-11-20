/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013, 2014.
 *  
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
package org.openepics.discs.hourlog.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.hourlog.ejb.ShiftEJB;
import org.openepics.discs.hourlog.ent.Facility;
import org.openepics.discs.hourlog.ent.Shift;
import org.openepics.discs.hourlog.ent.ShiftStaffMember;
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.prefs.PreferenceName;
import org.openepics.discs.hourlog.prefs.PreferencesEJB;
import org.openepics.discs.hourlog.util.AppProperties.HourLogProperty;

/**
 * Sends messages to users who want to be notified of facility status changes.
 *
 * @author vuppala
 */
@Named
public class Notifier implements Serializable {

    @EJB
    private PreferencesEJB prefEJB;
    @EJB
    private ShiftEJB shiftEJB;

    @Inject
    private AppProperties appProperties;
    @Inject
    private MailGateway mailGateway;

    private static final Logger logger = Logger.getLogger(Notifier.class.getName());

    public Notifier() {
    }

    /**
     * Notify all those users who have chosen to monitor a facility
     *
     * @author vuppala
     * @param facility
     * @param subject subject
     * @param message Message body
     *
     */
    public void notifyFacilityMonitors(Facility facility, String subject, String message) {

        List<Sysuser> users = prefEJB.findUsers(PreferenceName.MONITOR_FACILITY, facility.getFacilityName());
        List<String> recipients = new ArrayList<>();

        String address;
        for (Sysuser user : users) {
            address = user.getSmsAddress();
            if (address == null || address.isEmpty()) {
                logger.log(Level.WARNING, "Notifier#notifyFacilityMonitors: {0} does not have notification address", user.getLastName());
            } else {
                recipients.add(address);
            }
        }
        
        if (recipients.isEmpty()) {
            logger.log(Level.WARNING, "Notifier#notifyFacilityMonitors: Cannot notify anyeone. No one is monitoring {0}.", facility.getFacilityName());
        } else {
            if (appProperties.inProduction()) {
                mailGateway.sendMail(appProperties.getProperty(HourLogProperty.HOURLOG_FROM_EMAIL), recipients, subject, message);
                logger.log(Level.FINE, "Notifier#notifyFacilityMonitors: Notification sent to {0}: {1}", new Object[] {recipients.toString(), message});
            } else {
                logger.log(Level.WARNING, "Notifier#notifyFacilityMonitors: Not in production so did not send notification to {0}: {1}", new Object[] {recipients.toString(), message});
            }
        }
    }

    /**
     * Notify current shift staff
     *
     * @author vuppala
     * @param subject subject
     * @param message Message body
     *
     */
    public void notifyShiftStaff(String subject, String message) {
        Shift cshift = shiftEJB.currentShift();
        List<ShiftStaffMember> staff = shiftEJB.findShiftStaff(cshift);
        List<String> recipients = new ArrayList<>();
        String address;

        for (ShiftStaffMember ssm : staff) {
            if (ssm.getSendSms()) {
                address = ssm.getStaffMember().getSmsAddress();
                if (address == null || address.isEmpty()) {
                    logger.log(Level.WARNING, "Notifier#notifyShiftStaff: {0} does not have SMS address", ssm.getStaffMember().getLastName());
                } else {
                    recipients.add(address);
                }
            }
        }

        // String testMsg;
        if (recipients.isEmpty()) {
            logger.log(Level.WARNING, "Notifier#notifyShiftStaff: Cannot notify anyeone. No one wants notifications.");
        } else {
            if (appProperties.inProduction()) {
                mailGateway.sendMail(appProperties.getProperty(HourLogProperty.HOURLOG_FROM_EMAIL), recipients, subject, message);
                logger.log(Level.FINE, "Notifier#notifyShiftStaff: Notification sent to {0}: {1}", new Object[] {recipients.toString(), message});
            } else {
                logger.log(Level.WARNING, "Notifier#notifyFacilityMonitors: Not in production so did not send notification to {0}: {1}", new Object[] {recipients.toString(), message});
            }
        }
    }
}
