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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.openepics.discs.hourlog.ent.BeamEvent;
import org.openepics.discs.hourlog.ent.BreakdownEvent;

/**
 * Utility methods
 *
 * @author Vasu V <vuppala@frib.msu.org>
 */
public class Utility {

    private static final Logger logger = Logger.getLogger(Utility.class.getName());
    
    public static void showMessage(FacesMessage.Severity severity, String summary, String message) {
        FacesContext context = FacesContext.getCurrentInstance();

        context.addMessage(null, new FacesMessage(severity, summary, message));
        // FacesMessage n = new FacesMessage();
    }

    /**
     * Checks if two dates fall on the same day
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    /**
     * Check if a string is blank. Not using StringUtils from apache to limit
     * dependencies.
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * Convert String to Integer
     *
     * @param str
     * @return Integer
     */
    public static Integer parseNumber(String str) {
        if (isBlank(str)) {
            return null;
        }

        return Integer.parseInt(str);
    }

    /**
     * Convert string to date with given date format
     *
     * @param dateFormat
     * @param str
     * @return
     * @throws Exception
     */
    public static Date parseDate(String dateFormat, String str) throws Exception {
        DateFormat df = new SimpleDateFormat(dateFormat);

        if (isBlank(str)) {
            return null;
        }

        return df.parse(str);
    }

    /**
     * Method that generates a jax-rs client without checking for valid CA
     * certificates.
     *
     * Important: Only for development/test servers!! Must not be used for
     * production environments
     *
     * @return
     */
    public static Client gullibleRestClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

            }};
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            ClientBuilder clientBuilder = ClientBuilder.newBuilder();
            // ClientConfig config = new ClientConfig(); 
            return clientBuilder.sslContext(sslContext).hostnameVerifier(allHostsValid).build();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block 
            logger.log(Level.SEVERE, "Problem with REST client: no algorithm", e);
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block 
            logger.log(Level.SEVERE, "Problem with REST client: key management", e);
        }
        return null;
    }
    
    /**
     * Superimpose List2 onto List1 of breakdowns
     *
     * @param list1
     * @param list2
     * @return
     */
    public static List<BreakdownEvent> mergeBreakdowns(List<BreakdownEvent> list1, List<BreakdownEvent> list2) {
        List<BreakdownEvent> newList = new ArrayList<>();
        BreakdownEvent event;
        // very small lists for no need for optimization
        for (BreakdownEvent b1 : list1) {
            event = b1;
            for (BreakdownEvent b2 : list2) {
                if (b1.getCategory().equals(b2.getCategory())) {
                    event = b2;
                    break;
                }
            }
            newList.add(event);
        }
        return newList;
    }

    /**
     * Compare two beam event lists
     *
     * @param list1
     * @param list2
     * @return true if the lists are equal, false otherwise
     */
//    public static boolean compareBeams(List<BeamEvent> list1, List<BeamEvent> list2) {
//        if (list1 == null && list2 == null) {
//            return true;
//        }
//        if (list1 == null || list2 == null) {
//            return false;
//        }
//        if (list1.size() != list2.size()) {
//            return false;
//        }
//
//        BeamEvent event;
//        for (BeamEvent b1 : list1) {
//            event = null;
//            for (BeamEvent b2 : list2) {
//                if (b1.getBeamSystem().equals(b2.getBeamSystem())) {
//                    event = b2;
//                }
//            }
//            if (!compareBeamEvents(b1, event)) {
//                return false;
//            }
//        }
//
//        return true;
//    }

    /**
     * Compares two beam events
     *
     * @param event1
     * @param event2
     * @return true if they are equal, false otherwise
     */
//    public static boolean compareBeamEvents(BeamEvent event1, BeamEvent event2) {
//        if (event1 == null && event2 == null) {
//            return true;
//        }
//        if (event1 == null || event2 == null) {
//            return false;
//        }
//
//        if (!event1.getBeamSystem().equals(event2.getBeamSystem())) {
//            return false;
//        }
//
//        // one beam is null and other is not
//        if ((event1.getBeam() == null && event2.getBeam() != null) || (event1.getBeam() != null && event2.getBeam() == null)) {
//            return false;
//        }
//
//        if (event1.getBeam() == null) { // both beams are null
//            return compareElements(event1.getElement(), event2.getElement()) && event1.getMassNumber() == event2.getMassNumber();
//        } else { // both beams are not null
//            return event1.getBeam().equals(event2.getBeam());
//        }
//    }

    /**
     * Check for equality of two beams
     *
     * @param b1
     * @param b2
     * @return true if they are same
     */
//    public static boolean sameBeam(Beam b1, Beam b2) {
//        if (b1 == null && b2 == null) {
//            return true;
//        }
//        if (b1 == null || b2 == null) {
//            return false;
//        }
//        boolean flag = compareElements(b1.getElement(), b2.getElement()) && 
//                       Double.compare(b1.getEnergy(), b2.getEnergy()) == 0 && 
//                       b1.getCharge() == b2.getCharge() && 
//                       b1.getMassNumber() == b2.getMassNumber() && 
//                       areSame(b1.getBeamSystem(), b2.getBeamSystem());        
//        
//        return flag;
//    }
    
    /**
     * Compare two elements
     *
     * @param e1
     * @param e2
     * @return true if they are same
     */
//    public static boolean compareElements(Element e1, Element e2) {
//        if (e1 == null && e2 == null) {
//            return true;
//        }
//        if (e1 == null || e2 == null) {
//            return false;
//        }
//        return e1.equals(e2);
//    }

    /**
     * Superimpose List2 onto List1 of beams
     *
     * @param list1
     * @param list2
     * @return beams
     */
    public static List<BeamEvent> mergeBeams(List<BeamEvent> list1, List<BeamEvent> list2) {
        List<BeamEvent> newList = new ArrayList<>();
        BeamEvent event;
        // very small lists so no need for optimization
        for (BeamEvent b1 : list1) {
            event = b1;
            for (BeamEvent b2 : list2) {
                if (b1.getBeamSystem().equals(b2.getBeamSystem())) {
                    event = b2;
                    break;
                }
            }
            newList.add(event);
        }
        return newList;
    }

    /**
     * Objects are the same
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean areSame(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
    
    /**
     * Objects are the same
     *
     * @param <T>
     * @param a
     * @param b
     * @return
     */
    public static <T extends Comparable <? super T>> int compare(T a, T b) {
        final int SAME = 0;
        final int SMALLER = -1;
        final int BIGGER = 1;

        if (a == b) {
            return SAME;
        }
        if (a == null) {
            return SMALLER;
        }
        if (b == null) {
            return BIGGER;
        }

        return a.compareTo(b);
    }

    /**
     * Add days to given date
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDaysToDate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * Add hours to given date
     *
     * @param date
     * @param hours
     * @return
     */
    public static Date addHoursToDate(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hours);
        return cal.getTime();
    }

    /**
     * Add minutes to given date
     *
     * @param date
     * @param minutes
     * @return
     */
    public static Date addMinutesToDate(Date date, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }

    /**
     * Truncate date so that it has only year, month, date, hour, minute, 
     *  and seconds but not milliseconds or smaller time units 
     *
     * @param date
     * @return
     */
    public static Date truncateToSeconds(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTime();
    }
    
    /**
     * Return current date
     *
     * @return
     */
    public static Date currentDate() {        
        return truncateToSeconds(new Date());
    }
    
    /**
     * Are two strings equal?
     *
     * @param s1
     * @param s2
     * @return true if they are equal
     */
    public static boolean equalStrings(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }

        return (s1 != null && s1.equals(s2));
    }
}
