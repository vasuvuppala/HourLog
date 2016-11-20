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
import java.util.Properties;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Named;

/**
 * Hour Log Application properties 
 * TODO: Use values from database, then JNDI, and then defaults
 *
 * @author vuppala
 */
@Named
public class AppProperties implements Serializable {

    private static final Logger logger = Logger.getLogger(AppProperties.class.getName());

    public static enum HourLogProperty {

        EXPSVC_BASE_URI("EXPSVC_BASE_URI", "https://enterprise.nscl.msu.edu/api/experiments"),
        EXPSVC_API_KEY("EXPSVC_API_KEY", "a426e39ee891ee71d365df165f6ce474"),
        EXPSVC_API_APP("EXPSVC_API_APP", "elog"),
        EXPSVC_ENABLED("EXPSVC_ENABLED", "true"),
        BLOBSTORE_ROOT("BLOBSTORE_ROOT", "/srv/hourlog/blobstore"),
        TRSVC_BASE_URI("TRSVC_BASE_URI", "https://enterprise.nscl.msu.edu/troubleReports/api/getTroubleReportsByDate/"),
        TRSVC_ENABLED("TRSVC_ENABLED", "true"),
        TRNGSVC_BASE_URI("TRNGSVC_BASE_URI", "https://enterprise.nscl.msu.edu/troubleReports/api/getOperatorsInCharge/"),
        TRNGSVC_ENABLED("TRNGSVC_ENABLED", "true"),
        EPICS_ARCHIVER_BASE("EPICS_ARCHIVER_BASE", "http://epicsarchiver.nscl.msu.edu"),
        EPICS_ARCHIVER_PATH("EPICS_ARCHIVER_PATH", "/retrieval/data/getData.json"),
        TR_URL("TR_URL", "http://enterprise.nscl.msu.edu/troubleReports/troubleReports/detail"),
        LOG_PAST_WINDOW("LOG_PAST_WINDOW", "2880"),
        RECENT_TIME_WINDOW("RECENT_TIME_WINDOW", "2880"),
        SMTP_SERVER("SMTP_SERVER", "nsclsmtp.nscl.msu.edu"),
        SMTP_PORT("SMTP_PORT", "25"),
        HOURLOG_FROM_EMAIL("HOURLOG_FROM_EMAIL", "hourlog@frib.msu.edu"),
        LOGBOOK_SERVICE_URL("LOGBOOK_SERVICE_URL", "https://dev01-hlc.nscl.msu.edu:8181/Olog/resources"),
        LOGBOOK_ATTACHMENT_URL("LOGBOOK_ATTACHMENT_URL", "https://dev01-hlc.nscl.msu.edu:8181/Olog/resources/attachments"),
        DEPLOYMENT_ENVIRONMENT("DEPLOYMENT_ENVIRONMENT", "development");

        private final String name;
        private final String defaultValue;

        private HourLogProperty(String name, String value) {
            this.name = name;
            this.defaultValue = value;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getName() {
            return name;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

    }

    @Resource(name = "org.openepics.discs.hourlog.props")
    private Properties properties;

    public static final char ARTIFACT_DOC = 'd'; // ToDo: use enum instead
    public static final char ARTIFACT_TR = 't';  // ToDo: use enum instead

    //TODO: move following constant to Facility table in the database
    // Boundaries for changing log occurence date, in minutes
    // public static final int LOG_FUTURE_WINDOW = 15;
    public static final int LOG_FUTURE_WINDOW = 0;  // Setting date into future is not needed anymore but keeping it there just in case.
    public static final int LOG_PAST_WINDOW = 2880;
    // Number of minutes in the past that can be considered as 'recent'
    public static final int RECENT_TIME_WINDOW = 2880;
    // public static final int RECENT_TIME_WINDOW = 5760;

    public AppProperties() {
    }

    /**
     * Fetches value of a given property
     *
     * @param prop
     * @return property value
     */
    public String getProperty(HourLogProperty prop) {
        String value = properties.getProperty(prop.getName());

        if (value == null || value.isEmpty()) {
            value = prop.getDefaultValue();
        }

        return value;
    }

    /**
     * Fetches property value for a given name
     *
     * @param name
     * @return property value
     */
//    public String getProperty(String name) {
//        if (name == null || name.isEmpty()) {
//            return null;
//        }
//
//        return properties.getProperty(name);
//    }

    /**
     * TODO: Redundant. Remove and replace it with getProperty method
     *
     * @return
     */
    public String getTR_URL() {
        return getProperty(HourLogProperty.TR_URL);
    }

    /**
     * TODO: Redundant. Remove and replace it with getProperty method
     *
     * @return
     */
    public String getLOGBOOK_ATTACHMENT_URL() {
        return getProperty(HourLogProperty.LOGBOOK_ATTACHMENT_URL);
    }

    /**
     * Is the application deployed in production environment?
     *
     * @return true if it is
     */
    public boolean inProduction() {
        return "production".equals(getProperty(HourLogProperty.DEPLOYMENT_ENVIRONMENT));
    }

    /**
     * Test for concurrency?
     *
     * @return true if it is
     */
//    public boolean testConcurrency() {
//        return "true".equals(properties.getProperty("test-concurrency"))
//                && ! "production".equals(properties.getProperty("deployment-environment"));
//    }
}
