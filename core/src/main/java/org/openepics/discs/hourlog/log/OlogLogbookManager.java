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
package org.openepics.discs.hourlog.log;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import edu.msu.nscl.olog.api.Attachment;
import edu.msu.nscl.olog.api.Log;
import edu.msu.nscl.olog.api.LogBuilder;
import edu.msu.nscl.olog.api.LogbookBuilder;
import edu.msu.nscl.olog.api.OlogClient;
import edu.msu.nscl.olog.api.OlogClientImpl;
import edu.msu.nscl.olog.api.Property;
import edu.msu.nscl.olog.api.PropertyBuilder;
import edu.msu.nscl.olog.api.XmlLog;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.openepics.discs.hourlog.auth.AuthEJB;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.ent.Sysuser;
import org.openepics.discs.hourlog.util.AppProperties;
import org.openepics.discs.hourlog.util.BlobStore;
import org.openepics.discs.hourlog.exception.HourLogServiceException;
import org.openepics.discs.hourlog.util.AppProperties.HourLogProperty;
import org.openepics.discs.hourlog.util.StopWatch;
import org.openepics.discs.hourlog.util.Utility;

/**
 * Implementation of the logbook system
 *
 * @author vuppala
 */
@Stateless
public class OlogLogbookManager implements LogbookManager {

    @Inject
    private BlobStore blobStore;
    @Inject
    private UserSession userSession;
    @Inject
    private AppProperties appProperties;

    @EJB
    private AuthEJB authEJB;

    private static final Logger logger = Logger.getLogger(OlogLogbookManager.class.getName());
    private static final String HOUR_LOG_PROPERTY = "HourLog"; // olog property name to store Trouble report and notes IDs
    private static final String TR_ATTRIBUTE = "TRnumber"; // attribute to store TR number
    // private static final String NOTES_ATTRIBUTE = "NotesId"; // attribute to store notes id
    private static final String AUTHOR_ATTRIBUTE = "AuthorId"; // attribute to store author's user id

    /**
     * Create a handle to olog
     *
     * @return
     * @throws HourLogServiceException
     */
    private OlogClient ologClient() throws HourLogServiceException {
        LogbookServiceCredential cred = userSession.getLogbookCredential();
        OlogClient client = null;

        try {
            if (cred == null) {
                client = OlogClientImpl.OlogClientBuilder.serviceURL(appProperties.getProperty(HourLogProperty.LOGBOOK_SERVICE_URL)).withHTTPAuthentication(true).create();
            } else {
                client = OlogClientImpl.OlogClientBuilder.serviceURL(appProperties.getProperty(HourLogProperty.LOGBOOK_SERVICE_URL)).withHTTPAuthentication(true)
                        .username(cred.getUserid()).password(cred.getPassword()).create();
            }
        } catch (Exception e) {
            throw new HourLogServiceException("Olog Service", "Olog service appears to be down", e);
        }
        return client;
    }

    @Override
    public void addCredential(String username, char[] password) {
        userSession.setLogbookCredential(new LogbookServiceCredential(username, String.valueOf(password)));
    }

    @Override
    public boolean pingService() throws HourLogServiceException {
        boolean success = false;

        try {
            OlogClient client = OlogClientImpl.OlogClientBuilder.serviceURL(appProperties.getProperty(HourLogProperty.LOGBOOK_SERVICE_URL)).withHTTPAuthentication(true).create();
            client.listLogbooks();
            success = true;
        } catch (Exception e) {
            throw new HourLogServiceException("Olog Service", "Olog service appears to be down", e);
        }

        return success;
    }

    @Override
    public List<Logbook> listLogbooks() {

        OlogClient client = ologClient();
        List<Logbook> logbooks = new ArrayList<>();

        Collection<edu.msu.nscl.olog.api.Logbook> ologbooks;
        ologbooks = client.listLogbooks();
        short id = 0;
        for (edu.msu.nscl.olog.api.Logbook lb : ologbooks) {
            // System.out.format("oLogbook Name: %s, owner: %s %n", lb.getName(), lb.getOwner());
            logbooks.add(new Logbook(id++, lb.getName(), lb.getName(), 0));
        }
        // Sort the entries by occurrence date
        Collections.sort(logbooks, new Comparator<Logbook>() {
            @Override
            public int compare(Logbook l1, Logbook l2) {
                return l1.getLogbookName().compareTo(l2.getLogbookName());
            }
        });
        logger.log(Level.FINE, "Logbooks found: {0}", logbooks.size());
        return logbooks;
    }

    @Override
    public Logbook toLogbook(String name) {
        return new Logbook((short) 0, name, name, 0);
    }

    /**
     * Convert from olog entry to hourlog log entry
     *
     * @param olog
     * @return
     */
    private LogEntry toLogEntry(Log olog) {
        long ologId = olog.getId();
        if (ologId < Integer.MIN_VALUE || ologId > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Log ID too big to be cast to integer: " + ologId);
        }

        int logId = (int) ologId;
        LogEntry entry = new LogEntry(logId, olog.getDescription(), olog.getOwner(),
                Utility.truncateToSeconds(olog.getEventStart()),
                Utility.truncateToSeconds(olog.getModifiedDate()), 0);

        if (olog.getLogbookNames().isEmpty()) {
            logger.log(Level.WARNING, "Got a log entry (id: {0}) from olog that is not in any logbook!", olog.getId());
        } else {
            entry.setLogbook(toLogbook(olog.getLogbookNames().iterator().next()));
        }

        // artifacts: attachments dn trouble reports
        List<Artifact> artifacts = new ArrayList<>();
        entry.setArtifactList(artifacts);

        Collection<Attachment> attachments = olog.getAttachments();
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                Artifact artifact = new Artifact(0, 'd', attachment.getFileName(), attachment.getFileName(), 0);
                artifacts.add(artifact);
            }
        }
        // Trouble reports & author
        Sysuser author = authEJB.findUser(olog.getOwner());
        Collection<Property> properties = olog.getProperties();
        if (properties != null) {
            for (Property property : properties) {
                if (HOUR_LOG_PROPERTY.equals(property.getName())) {
                    if (property.getAttributeValue(TR_ATTRIBUTE) != null) {
                        Artifact artifact = new Artifact(0, 't', "TR:" + property.getAttributeValue(TR_ATTRIBUTE), property.getAttributeValue(TR_ATTRIBUTE), 0);
                        artifacts.add(artifact);
                    }
                    if (property.getAttributeValue(AUTHOR_ATTRIBUTE) != null) {
                        author = authEJB.findUser(Integer.valueOf(property.getAttributeValue(AUTHOR_ATTRIBUTE)));
                    }
                }
            }
        }
        if (author == null) {
            author = new Sysuser(null, "Unknown", "Unknown", false, false, 0);
            author.setLoginId("Unknown (" + olog.getOwner() + ")");
            author.setEmail("unknown");
            entry.setAuthor(author.getLoginId());
            //throw new ClassCastException("Cannot convert olog owner to hourlog user: " + olog.getOwner());
        }
        entry.setSysuser(author);

        return entry;
    }

    /**
     * Convert from olog entries to sorted hourlog log entries. Olog entries are
     * a collection of entries; it includes the current log entries and their
     * history (old edited version of the entries).
     *
     * We need to match each log entry with its history.
     *
     * @param ologs
     * @return
     */
    private List<LogEntry> toLogEntries(Collection<Log> ologs) {
        SortedMap<Long, SortedMap<Integer, Log>> ologMap = new TreeMap<>();
        SortedMap<Integer, Log> ologEntries;

        for (Log olog : ologs) {
            ologEntries = ologMap.get(olog.getId());
            if (ologEntries == null) {
                ologEntries = new TreeMap<>();
                ologMap.put(olog.getId(), ologEntries);
            }
            ologEntries.put(olog.getVersion(), olog);
        }

        Log ol_log; // olog log entry
        LogEntry hl_log; // hourlog log entry
        List<LogEntry> hl_entries = new ArrayList<>(); // hourlog log entries
        for (SortedMap<Integer, Log> olEntryMap : ologMap.values()) {
            if (olEntryMap == null) {
                logger.log(Level.WARNING, "Strange. Map entry with no log entries!");
                continue;
            }
            ol_log = olEntryMap.get(olEntryMap.lastKey()); // the last entry is the valid entry
            hl_log = toLogEntry(ol_log);
            if (olEntryMap.size() > 1) { // there are edits
                List<LogEntry> hl_editedLogs = new ArrayList<>();
                for (Log editedLog : olEntryMap.values()) {
                    if (!editedLog.equals(ol_log)) { // skip the last entry 
                        hl_editedLogs.add(toLogEntry(editedLog));
                    }
                }
                hl_log.setLogEntryList(hl_editedLogs);
            }
            hl_entries.add(hl_log);
        }

        return hl_entries;
    }

    /**
     * Add edits to log entries.
     *
     * @param entries
     */
//    private void addEditHistory(List<LogEntry> entries) {
//        List<LogEntry> edits;
//        for (LogEntry entry : entries) {
//            edits = getEntryWithHistory(entry.getLogEntryId());
//            if (edits != null && edits.size() > 1) {
//                entry.setLogEntryList(edits);
//            }
//        }
//    }
    @Override
    public LogEntry getEntry(int logEntryId) {
        OlogClient client = ologClient();
        Map<String, String> map = new HashMap<>();

        map.put("id", Integer.toString(logEntryId));
        Collection<Log> logs = client.findLogs(map);
        if (logs.isEmpty()) {
            return null;
        }

        Iterator<Log> iter = logs.iterator();
        Log olog = iter.next();

        LogEntry entry = toLogEntry(olog);
        return entry;
    }

    @Override
    public Date latestLogDate(Logbook logbook) {
        if (logbook == null) {
            logger.log(Level.WARNING, "logbook is null!");
            return null;
        }

        List<Logbook> logbooks = new ArrayList<>();
        logbooks.add(logbook);
        return latestLogDate(logbooks);
    }

    @Override
    public Date latestLogDate(List<Logbook> logbooks) {
        if (logbooks == null || logbooks.isEmpty()) {
            logger.log(Level.WARNING, "No logbooks specified!");
            return null;
        }

        // StopWatch stopWatch = new StopWatch();
        OlogClient client = ologClient();
        // logger.log(Level.FINE, "latestLogDate: After creating client: {0}", stopWatch);

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        for (Logbook logbook : logbooks) {
            map.add("logbook", logbook.getLogbookName());
            // logger.log(Level.FINE, " find entries log book {0}", logbook.getLogbookName());
        }

        map.add("sort", "modified"); // sort by entry modified time
        map.add("limit", "1"); // get the latest

        // map.add("history", "audit"); // get history of every entry
        Collection<Log> ologs = client.findLogs(map);

        if (ologs == null || ologs.isEmpty()) {
            logger.log(Level.WARNING, "No log entries returned from service!");
            return Utility.currentDate();
        } else {
            return ologs.iterator().next().getModifiedDate();
        }
    }

    /**
     * ToDo; Remove once Olog API provides this information Temporary. Returns
     * an date from past.
     *
     * @return
     */
    private Date anOldDate() {
        Date date = Utility.currentDate();
        try {
            String DATE_FORMAT = "yyyy-MM-dd"; // date format used in the results from external Experiment service
            date = Utility.parseDate(DATE_FORMAT, "2003-01-01");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error", e);
        }

        return date;
    }

    @Override
    public Date earliestLogDate(Logbook logbook) {
        if (logbook == null) {
            logger.log(Level.WARNING, "logbook is null!");
            return null;
        }

        // ToDO: Replace with olog API calls
        return anOldDate();
    }

    @Override
    public Date earliestLogDate(List<Logbook> logbooks) {
        if (logbooks == null || logbooks.isEmpty()) {
            logger.log(Level.WARNING, "No logbooks specified!");
            return null;
        }

        // ToDO: Replace with olog API calls
        return anOldDate();
    }

    @Override
    public List<LogEntry> getEntryWithHistory(int logId) {
        OlogClient client = ologClient();

        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(logId));
        map.put("history", "audit");
        Collection<Log> ologs = client.findLogs(map);

        if (ologs == null || ologs.isEmpty()) {
            logger.log(Level.SEVERE, "No logs with id {0}", logId);
            return null;
        }

        List<LogEntry> logEntries = new ArrayList<>();
        for (Log olog : ologs) {
            logEntries.add(toLogEntry(olog));
        }

        // Sort the entries by occurrence date
        Comparator<LogEntry> comparator = new Comparator<LogEntry>() {
            @Override
            public int compare(LogEntry l1, LogEntry l2) {
                return l2.getEnteredAt().compareTo(l1.getEnteredAt());
            }
        };
        Collections.sort(logEntries, comparator);
        // logEntries.add(0, logEntry); // add the given event at the beginning

        logger.log(Level.FINE, "Log history entries found: {0}", logEntries.size());
        return logEntries;
    }

    @Override
    public List<LogEntry> findEntries(List<Logbook> logbooks, int start, int size, Date beginDate, Date endDate, String phrase) {
        StopWatch stopWatch = new StopWatch();
        OlogClient client = ologClient();
        logger.log(Level.FINE, "After creating client: {0}", stopWatch);

        logger.log(Level.FINE, "Finding olog entries");
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        for (Logbook logbook : logbooks) {
            map.add("logbook", logbook.getLogbookName());
            logger.log(Level.FINE, " find entries log book {0}", logbook.getLogbookName());
        }

        if (beginDate != null) {
            map.add("start", String.valueOf(beginDate.getTime() / 1000L));
        }
        if (endDate != null) {
            map.add("end", String.valueOf(endDate.getTime() / 1000L));
        }
        map.add("limit", String.valueOf(size));
        map.add("page", String.valueOf(start / size + 1));
        if (phrase != null) {
            map.add("search", "*" + phrase + "*");
        }
        map.add("history", "audit"); // get history of every entry
        map.add("sort", "entryStart"); // sort by entry start time

        Collection<Log> ologs = client.findLogs(map);
        logger.log(Level.FINE, "After getting logs from service: {0}", stopWatch);
        List<LogEntry> logEntries = toLogEntries(ologs);
        logger.log(Level.FINE, "After converting logs to HourLog logs: {0}", stopWatch);
        // Sort the entries by occurrence date
        Collections.sort(logEntries, new Comparator<LogEntry>() {
            @Override
            public int compare(LogEntry l1, LogEntry l2) {
                return l2.getOccurredAt().compareTo(l1.getOccurredAt());
            }
        });
        logger.log(Level.FINE, "After sorting log entries: {0}", stopWatch);
        // addEditHistory(logEntries);
        logger.log(Level.FINE, "Log entries found: {0}", logEntries.size());

        return logEntries;
    }

    @Override
    public LogEntry addEntry(Logbook logbook, Sysuser author, String logText, Date occurredAt, List<Artifact> artifacts) {

        StopWatch stopWatch = new StopWatch();
        OlogClient client = ologClient();
        logger.log(Level.FINE, "After creating client: {0}", stopWatch);
        LogEntry entry = null;

        try {
            LogBuilder logBuilder = LogBuilder.log()
                    .description(logText).level("Info")
                    .appendToLogbook(LogbookBuilder.logbook(logbook.getLogbookName()))
                    .onDate(occurredAt);

            logger.log(Level.FINE, "After log builder: {0}", stopWatch);
            // Add trouble reports
            if (artifacts != null) {
                for (Artifact artifact : artifacts) {
                    if (artifact.getType() == 't') { //TODO: add type method to artifact
                        PropertyBuilder property = PropertyBuilder.property(HOUR_LOG_PROPERTY).attribute(TR_ATTRIBUTE);
                        logBuilder.appendProperty(property.attribute(TR_ATTRIBUTE, artifact.getResourceId()));
                    }
                }
            }
            Log olog = client.set(logBuilder);
            logger.log(Level.FINE, "After adding log entry: {0}", stopWatch);

            logger.log(Level.FINE, "Log entry added to {0}", logbook.getLogbookName());

            entry = toLogEntry(olog);

            // attach documents
            if (artifacts != null) {
                for (Artifact artifact : artifacts) {
                    if (artifact.getType() == 'd') { //TODO: add type method to artifact
                        File file = blobStore.retreiveFile(artifact.getResourceId(), artifact.getName());
                        client.add(file, olog.getId());
                        // blobStore.deleteFile(file);
                    }
                }
            }
            logger.log(Level.FINE, "After everything: {0}", stopWatch);
        } catch (IOException e) {
            throw new HourLogServiceException("Olog Service", "Could not get file from BlobStore", e);
        } catch (Exception e) {
            throw new HourLogServiceException("Olog Service", "Problem with Olog Service", e);
        }

        return entry;
    }

    @Override
    public LogEntry editEntry(LogEntry entry, Sysuser author, String logText, Date occurredAt, List<Artifact> artifacts) {
        OlogClient client = ologClient();
        LogEntry newEntry = null;

        try {
            // Log log = client.getLog((long) entry.getLogEntryId());

            LogBuilder logBuilder = LogBuilder.log().id((long) entry.getLogEntryId())
                    .appendToLogbook(LogbookBuilder.logbook(entry.getLogbook().getLogbookName()))
                    .description(logText).level("Info")
                    .onDate(occurredAt);

            if (artifacts != null) {
                for (Artifact artifact : artifacts) {
                    if (artifact.getType() == 't') { //TODO: add type method to artifact
                        PropertyBuilder property = PropertyBuilder.property(HOUR_LOG_PROPERTY).attribute(TR_ATTRIBUTE);
                        logBuilder.appendProperty(property.attribute(TR_ATTRIBUTE, artifact.getResourceId()));
                    }
                }
            }

            Log olog = client.set(logBuilder);
            logger.log(Level.FINE, "Log entry updated");

            newEntry = toLogEntry(olog);

            // add new  documents
            Collection<Attachment> attachments = olog.getAttachments();
            if (artifacts != null) {
                for (Artifact artifact : artifacts) {
                    if (artifact.getType() == 'd' && newArtifact(artifact.getName(), attachments)) { //TODO: add type method to artifact                       
                        File file = blobStore.retreiveFile(artifact.getResourceId(), artifact.getName());
                        client.add(file, olog.getId());
                        // blobStore.deleteFile(file);
                    }
                }
            }
            // remove old ones
            if (attachments != null) {
                for (Attachment attachment : attachments) {
                    if (deletedAttachment(attachment, artifacts)) {
                        client.delete(attachment.getFileName(), olog.getId());
                    }
                }
            }

        } catch (IOException e) {
            throw new HourLogServiceException("Olog Service", "Could not update log entry", e);
        } catch (Exception e) {
            throw new HourLogServiceException("Olog Service", "Problem with Olog Service", e);
        }

        return newEntry;
    }

    /**
     * is it an old attachment?
     *
     * @param attachment
     * @param artifacts
     * @return
     */
    private boolean deletedAttachment(Attachment attachment, List<Artifact> artifacts) {
        if (artifacts == null) {
            return true;
        }
        if (attachment.getFileName() == null) {
            return true;
        }
        for (Artifact artifact : artifacts) {
            if (artifact.getType() == 'd' && attachment.getFileName().equals(artifact.getName())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Is filename already attached?
     *
     * @param artifact
     * @param attachments
     * @return
     */
    private boolean newArtifact(String filename, Collection<Attachment> attachments) {
        if (attachments == null) {
            return true;
        }

        if (filename == null) {
            return false;
        }

        for (Attachment attachment : attachments) {
            if (filename.equals(attachment.getFileName())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int countEntries(List<Logbook> logbooks, Date beginDate, Date endDate, String phrase) {
        StopWatch stopWatch = new StopWatch();
        int count = 0;
        Client client = null;
        try {
            //client = appProperties.inProduction() ? ClientBuilder.newClient() : Utility.gullibleRestClient();
            client = ClientBuilder.newClient();

            String BASE_URI = appProperties.getProperty(HourLogProperty.LOGBOOK_SERVICE_URL);
            WebTarget target = client.target(BASE_URI).path("logs");
            logger.log(Level.FINE, "Count entries");
            // MultivaluedMap<String, String> map = new MultivaluedMapImpl();
            for (Logbook logbook : logbooks) {
                // map.add("logbook", logbook.getLogbookName());
                target = target.queryParam("logbook", logbook.getLogbookName());
                logger.log(Level.FINE, " count entries log book {0}", logbook.getLogbookName());
            }

            if (beginDate != null) {
                // map.add("start", String.valueOf(beginDate.getTime() / 1000L));
                target = target.queryParam("start", String.valueOf(beginDate.getTime() / 1000L));
            }
            if (endDate != null) {
                //map.add("end", String.valueOf(endDate.getTime() / 1000L));
                target = target.queryParam("end", String.valueOf(endDate.getTime() / 1000L));
            }
            if (phrase != null) {
                // map.add("search", "*" + phrase + "*");
                target = target.queryParam("search", "*" + phrase + "*");
            }

            target = target.queryParam("limit", "1");
            OlogEntries entries = target.request(MediaType.APPLICATION_XML).get(OlogEntries.class);
            count = entries.getCount();
        } catch (Exception e) {
            // throw new HourLogServiceException("Olog Service", "Could not get result count from Olog", e);
            logger.log(Level.WARNING, "Could not get result count from Olog", e);
        } finally {
            if (client != null) {
                client.close();
            }
        }

        logger.log(Level.FINE, "Log Entries count: {0}", count);
        logger.log(Level.FINE, "After counting entries: {0}", stopWatch);
        return count;
    }

    /**
     * TODO: This is temporary till Olog API provides a way to get result count
     * for a query.
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "logs")
    private static class OlogEntries implements Serializable {

        @XmlAttribute(name = "count")
        private Integer count;
        @XmlTransient
        private Collection<XmlLog> logs = new ArrayList<>();

        public Integer getCount() {
            return count;
        }
    }
}
