/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013.
 *  State University (c) Copyright 2013.
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
package org.openepics.discs.hourlog.ologmig;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import edu.msu.nscl.olog.api.OlogClient;
import edu.msu.nscl.olog.api.OlogClientImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.openepics.discs.hourlog.ent.Artifact;
import org.openepics.discs.hourlog.ent.LogEntry;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.exception.HourLogServiceException;
import org.openepics.discs.hourlog.util.AppProperties;
import org.openepics.discs.hourlog.util.Utility;

/**
 * Sync the approved experiments cache
 *
 * @author vuppala
 */
@Stateless
public class OlogImportEJB implements Serializable {

    // private static final String DATE_FORMAT = "yyyy-MM-dd"; // date format used in the results from external Experiment service
    private static final Logger logger = Logger.getLogger(OlogImportEJB.class.getName());

    private static final String HOUR_LOG_PROPERTY = "HourLog"; // olog property name to store Trouble report and notes IDs
    private static final String TR_ATTRIBUTE = "TRnumber"; // attribute to store TR number
    private static final String NOTES_ATTRIBUTE = "NotesId"; // attribute to store notes id
    private static final String AUTHOR_ATTRIBUTE = "AuthorId"; // attribute to store author's user id
    
    private static final String OLOG_BASE_URI = "https://svcsdev2-hlc.nscl.msu.edu:8181/Olog/resources/";
    private static final String OLOG_IMPORT_URI = "https://svcsdev2-hlc.nscl.msu.edu:8181/Olog/resources/logs/import";
    private static final String ATTACH_BASE_URI = "https://svcsdev2-hlc.nscl.msu.edu:8181/Olog/resources/attachments";
    private final TrustManager[] trustManager = new TrustManager[]{new DummyX509TrustManager()};

    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    private static final class OlogLogbook {

        private String name;
        private String owner;
        private Integer id;
        private String state;
        
        public OlogLogbook() {

        }

        public static OlogLogbook valueOf(Logbook logbook) {
            OlogLogbook ologbook = new OlogLogbook();

            ologbook.name = logbook.getLogbookName();
            ologbook.owner = "owner"; // is this needed?
            ologbook.state = "Active"; // is this needed?
            ologbook.id = null; // is this needed?
            return ologbook;
        }

        public String getName() {
            return name;
        }

        public String getOwner() {
            return owner;
        }

        public Integer getId() {
            return id;
        }

        public String getState() {
            return state;
        }
        
        
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    private static final class OlogTag {

        private String name;
        private String state;

        public OlogTag() {

        }

        public String getName() {
            return name;
        }

        public String getState() {
            return state;
        }
        
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    private static final class OlogAttachment {

        private String fileName;
        private String contentType;
        private Boolean thumbnail;
        private Long fileSize;

        public OlogAttachment() {

        }

        public String getFileName() {
            return fileName;
        }

        public String getContentType() {
            return contentType;
        }

        public Boolean getThumbnail() {
            return thumbnail;
        }

        public Long getFileSize() {
            return fileSize;
        }
        
        
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    private static final class OlogProperty {

        private String name;
        private Integer id;

        @XmlJavaTypeAdapter(MapAdapter.class)
        private Map<String, String> attributes;

        public OlogProperty() {

        }

        public static OlogProperty HourLogProperty(String name, String value) {
            OlogProperty prop = new OlogProperty();

            prop.name = HOUR_LOG_PROPERTY;
            prop.attributes = new HashMap<>();
            prop.id = null;
            prop.attributes.put(name, value);
            return prop;
        }

        public static List<OlogProperty> valueOf(LogEntry entry) {
            List<OlogProperty> props = new ArrayList<>();
            
            props.add(OlogProperty.HourLogProperty(AUTHOR_ATTRIBUTE, String.valueOf(entry.getSysuser().getUserId())));
            if (entry.getObsoletedBy() == null) {
                props.add(OlogProperty.HourLogProperty(NOTES_ATTRIBUTE, String.valueOf(entry.getLogEntryId())));
            }

            List<Artifact> artifacts = entry.getArtifactList();
            if (artifacts != null && !artifacts.isEmpty()) {
                for (Artifact art : artifacts) {
                    if (art.getType() == 't') {
                        props.add(OlogProperty.HourLogProperty(TR_ATTRIBUTE, art.getResourceId()));
                    }
                }
            }
            return props;
        }

        public String getName() {
            return name;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public Integer getId() {
            return id;
        }
        
        
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    private static final class OlogLog {

        private Long id;
        private String owner;
        private String description;
        private String level;
        private String source;
        private String state;
        private long createdDate;
        private long modifiedDate;
        private long eventStart;
        private long eventEnd;
        private String version;

        private List<OlogTag> tags;
        private List<OlogLogbook> logbooks;
        private List<OlogAttachment> attachments;
        private List<OlogProperty> properties;

        public OlogLog() {

        }

        public LogEntry toLogEntry() {
            return new LogEntry();
        }

        public static OlogLog valueOf(LogEntry entry, Long logId, Long createdDate) {
            OlogLog olog = new OlogLog();

            olog.id = logId;
            olog.owner = entry.getSysuser().getLoginId() == null? entry.getSysuser().getLastName(): entry.getSysuser().getLoginId();
            olog.description = "Imported: " + entry.getLogText();
            olog.level = "Info";
            olog.source = "0:0:0:0:0:0:0:1";
            olog.state = "Active";
            olog.createdDate =  createdDate == null? entry.getEnteredAt().getTime(): createdDate; // workaround. created date must be same as the original entry
            olog.modifiedDate = entry.getEnteredAt().getTime();
            olog.eventStart = entry.getOccurredAt().getTime() > entry.getEnteredAt().getTime()? entry.getEnteredAt().getTime(): entry.getOccurredAt().getTime(); // event start cannot be in future
            if (olog.eventStart > olog.createdDate) { // Workaround. Created date cannot be graeter than eventStart for edited entries
                olog.createdDate = olog.eventStart;
            }
            olog.eventEnd = 38742530400000L; //  eternity 
            olog.version = "1";

            olog.logbooks = new ArrayList<>();
            olog.logbooks.add(OlogLogbook.valueOf(entry.getLogbook()));

            olog.properties = OlogProperty.valueOf(entry);
            olog.tags = new ArrayList<>();
            olog.attachments = new ArrayList<>();

            return olog;
        }

        @Override
        public String toString() {
            return String.format("id: %d%n owner: %s%n description: %s%n Created: %s%n Modified: %s%n Start: %s%n End: %s%n Version: %s%n",
                    id, owner, description, createdDate, modifiedDate, eventStart, eventEnd, version);
        }

        public Long getId() {
            return id;
        }

        public String getOwner() {
            return owner;
        }

        public String getDescription() {
            return description;
        }

        public String getLevel() {
            return level;
        }

        public String getSource() {
            return source;
        }

        public String getState() {
            return state;
        }

        public long getCreatedDate() {
            return createdDate;
        }

        public long getModifiedDate() {
            return modifiedDate;
        }

        public long getEventStart() {
            return eventStart;
        }

        public long getEventEnd() {
            return eventEnd;
        }

        public String getVersion() {
            return version;
        }

        public List<OlogTag> getTags() {
            return tags;
        }

        public List<OlogLogbook> getLogbooks() {
            return logbooks;
        }

        public List<OlogAttachment> getAttachments() {
            return attachments;
        }

        public List<OlogProperty> getProperties() {
            return properties;
        }
        
        
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    private static final class OlogRawData {

        private Integer count;
        private List<OlogLog> log;

        public OlogRawData() {

        }

        
        public static OlogRawData valueOf(LogEntry entry, Long logId, Long createdDate) {
            OlogRawData data = new OlogRawData();

            data.count = null;
            data.log = new ArrayList<>();
            data.log.add(OlogLog.valueOf(entry, logId, createdDate));

            return data;
        }

        @Override
        public String toString() {
            try {
                Map<String, Object> jaxbProperties = new HashMap<>(2);
                jaxbProperties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
                jaxbProperties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, false);
                JAXBContext jc = JAXBContext.newInstance(new Class[]{OlogRawData.class}, jaxbProperties);
                Marshaller marshaller = jc.createMarshaller();

                StringWriter writer = new StringWriter();
                marshaller.marshal(this, writer);
                return writer.toString();
            } catch (JAXBException e) {
                throw new ClassCastException("Error in converting OlogRawData to String: " + e.getMessage());
            }
            // return String.format("count: %d%n logs: %s%n", count, log);           
        }

        public Integer getCount() {
            return count;
        }

        public List<OlogLog> getLog() {
            return log;
        }

    }

    @Inject
    private AppProperties appProps;

    @PersistenceContext(unitName = "org.openepics.discs.hourlog")
    private EntityManager em;

    public OlogImportEJB() {
    }

    /**
     * perform the sync
     *
     */
    public void importAll() {
        logger.log(Level.INFO, "import started");

        logger.log(Level.INFO, "import done");
    }

    private String attachFile(WebResource resource, Long logId, File file) {
    
            FormDataMultiPart form = new FormDataMultiPart();
            // form.field("file", file.getName());
//            FormDataBodyPart fdp = new FormDataBodyPart("file",
//                    new FileInputStream(file),
//                    MediaType.APPLICATION_OCTET_STREAM_TYPE);
            FormDataBodyPart fdp = new FileDataBodyPart("file",file);
            form.bodyPart(fdp);

            String response = resource.path("attachments")
                    .path(logId.toString()).type(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_XML)
                    .post(String.class, form);
            return response;
        
    }
    
    private WebResource clientCreate(String user, String password) {

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
            sslContext.init(null, this.trustManager, null);
        } catch (NoSuchAlgorithmException e) {
            throw new HourLogServiceException("Olog import", "SSL");
        } catch (KeyManagementException e) {
            throw new HourLogServiceException("Olog import", "Key");
        }
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getProperties().put(
                HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                new HTTPSProperties(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname,
                            SSLSession session) {
                        return true;
                    }
                }, sslContext));

        clientConfig.getClasses().add(MultiPartWriter.class);
        Client client = Client.create(clientConfig);

        client.addFilter(new HTTPBasicAuthFilter(user, password));

        // client.addFilter(new RawLoggingFilter(logger));
        client.addFilter(new RawLoggingFilter(logger));
        client.setFollowRedirects(true);
        WebResource resource = client.resource(UriBuilder.fromUri(OLOG_BASE_URI).build());
        return resource;
    }

    /**
     * get the experiments from the remote service
     *
     * @param entry log entry to be imported
     * @return log entry with id
     */
    private OlogLog importRawData(OlogRawData request) {

        logger.log(Level.INFO, "importing raw data");
//        Client client = Utility.gullibleRestClient();
//        client.register(new LoggingFilter(logger, 10000));
        // HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("user", "pass");
        // client.register(feature);

        // client.register(MultiPartFeature.class);
        WebResource importRes = clientCreate("vuppala", "Hiavgp$03");
//        File f = new File("C:/temp/test.txt");
//        attachFile(importRes, 16024L, f);

//        String response = client.target(BASE_URI)
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(request, MediaType.APPLICATION_JSON), String.class);
//        logger.log(Level.INFO, "import to Olog {0}", response);
//        return response;
        OlogRawData response = importRes.path("logs").path("import")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(OlogRawData.class, request);

        List<OlogLog> logs = response.getLog();

        if (logs == null || logs.isEmpty()) {
            logger.log(Level.WARNING, "No log entries in raw data. Cannot convert to Log Entry");
            return null;
        }

        // client.close();
        return logs.get(0);
    }

    private OlogLog importHistory(LogEntry entry) {
        OlogRawData rawData;
        OlogLog olog = null;
        Long ologId = null; // entry id of olog
        Long createdDate = null;
        List<LogEntry> editedEntries = entry.getLogEntryList();

        if (editedEntries != null && !editedEntries.isEmpty()) {
            // sort the history in order of entry
            Collections.sort(editedEntries, new Comparator<LogEntry>() {
                @Override
                public int compare(LogEntry l1, LogEntry l2) {
                    return l2.getEnteredAt().compareTo(l1.getEnteredAt());
                }
            });
            for (LogEntry hist : editedEntries) {
                rawData = OlogRawData.valueOf(hist, ologId, createdDate);
                olog = importRawData(rawData);
                if (ologId == null) {
                    ologId = olog.id;
                    createdDate = olog.createdDate;
                }
            }
        }

        return olog;
    }

    public Long importEntry(LogEntry entry) {
        if (appProps.inProduction()) {
            logger.log(Level.SEVERE, "Cannot import in production environment!!");
            return null;
        }

        // Client client = ClientBuilder.newClient();
        // String BASE_URI = appProps.getProperty(AppProperties.LOGBOOK_SERVICE_URL);        
        // Form form = new Form();
        logger.log(Level.INFO, "importing entry: {0} {1}", new Object[] {entry.getLogEntryId(), entry.getLogText()});
        OlogLog olog = importHistory(entry);

        // finally the current/valid entry
        OlogRawData rawData;
        if (olog == null) {
             rawData = OlogRawData.valueOf(entry, null, null );
        } else {
             rawData = OlogRawData.valueOf(entry, olog.getId(), olog.getCreatedDate() );
        }
        olog = importRawData(rawData);
        Long ologId = olog.id;
        logger.log(Level.INFO, "Log id is {0}", ologId);
        // update event notes 

        return ologId;
    }
}
