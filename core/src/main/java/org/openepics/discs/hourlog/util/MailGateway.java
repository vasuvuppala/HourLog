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
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import javax.inject.Named;
import org.apache.commons.mail.EmailException;
import org.openepics.discs.hourlog.util.AppProperties.HourLogProperty;

/**
 * EMail gateway
 *
 * @author vuppala
 */
@Named
public class MailGateway implements Serializable {

    @Inject
    private AppProperties appProperties;
    
    private static final Logger logger = Logger.getLogger(MailGateway.class.getName());
    private static String SMTPserver;
    private static int SMTPport;

    public MailGateway() {
    }

    @PostConstruct
    public void init() {
        SMTPserver = appProperties.getProperty(HourLogProperty.SMTP_SERVER);
        SMTPport = Integer.parseInt(appProperties.getProperty(HourLogProperty.SMTP_PORT));
    }

    /**
     * Sends an email to given recipients
     *
     * @param from
     * @param recipients
     * @param subject
     * @param message
     */
    public void sendMail(String from, List<String> recipients, String subject, String message) {
        String to[] = recipients.toArray(new String[recipients.size()]);

        try {
            Email email = new SimpleEmail();

            logger.info("MailGateway#sendMail: sending email ...");
            email.setHostName(SMTPserver);
            email.setSmtpPort(SMTPport);
            // email.setSmtpPort(465);
            // email.setAuthenticator(new DefaultAuthenticator("iser", "xxxxxx"));
            // email.setSSLOnConnect(true);
            // Semail.setTLS(true);
            email.setStartTLSEnabled(true);
            // email.setDebug(true);
            email.setFrom(from);
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(to);
            email.send();
        } catch (EmailException e) {
            logger.severe("MailGateway#sendMail: Error while sending email");
            System.out.print(e);
        }
    }
}
