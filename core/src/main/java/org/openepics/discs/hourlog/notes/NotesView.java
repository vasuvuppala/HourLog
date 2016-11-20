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
package org.openepics.discs.hourlog.notes;

import org.openepics.discs.hourlog.log.*;
import java.io.InputStream;
import org.openepics.discs.hourlog.util.Utility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.hourlog.auth.AuthManager;
import org.openepics.discs.hourlog.auth.UserSession;
import org.openepics.discs.hourlog.ejb.EventEJB;
import org.openepics.discs.hourlog.ejb.SnapshotEJB;
import org.openepics.discs.hourlog.log.Artifact;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.util.BlobStore;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;

/**
 * State for notes view. Note: Initially it was planned to create log entries in
 * any logbook using Notes view but later it was decided not to do so.
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class NotesView implements Serializable {

    @EJB
    private EventEJB eventEJB;
    @EJB(beanName = "LocalAuthManager")
    private AuthManager authManager;
    @EJB(beanName = "OlogLogbookManager")
    private LogbookManager logbookEJB;
    @EJB
    private SnapshotEJB snapshotEJB;

    @Inject
    private UserSession userSession;
    @Inject
    private LazyNotesModel lazyNotesModel;
    @Inject
    private BlobStore blobStore;

    private static final Logger logger = Logger.getLogger(LogView.class.getName());
    private static final int CHANGE_HOURS = 9;
    private static final int FAST_CHANGE_HOURS = 24;
    private LazyDataModel<StatusfulLogEntry> lazyLog;

    private List<StatusfulLogEntry> obsoleteLogEntries;
    private List<Logbook> logbooks;
    private List<Logbook> selectedLogbooks;

    String searchPhrase;

    // for filtering. 
    private Date minDate, maxDate; // minimum and max event dates. for min/max in calendar widget
    private Date startDate = null, endDate = null; // input from the user

    private Date latestLogDate = Utility.currentDate(); // time of the latest log entry

    private Artifact downloadArtifact;

    public NotesView() {
    }

    @PostConstruct
    public void init() {
        logger.log(Level.FINE, "Initializing Log Manager");

//        inputArtifacts = new ArrayList<>();
        obsoleteLogEntries = new ArrayList<>();
        // logbooks = hourLogEJB.findLogbooks();
        logbooks = logbookEJB.listLogbooks();
        selectedLogbooks = new ArrayList<>();
        for (Logbook logbook : logbooks) {
            if (logbook.getLogbookName().equals(userSession.getFacility().getOpsLogbook().getLogbookName())) {
                selectedLogbooks.add(logbook);
            }
        }

        resetInput();
        lazyLog = lazyNotesModel;

        if (lazyLog == null) {
            logger.log(Level.SEVERE, "Lazy log model is null");
        }

    }

    /**
     * Resets input variables
     *
     */
    private void resetInput() {
        logger.log(Level.FINE, "reset input variables");

        endDate = null;

        minDate = logbookEJB.earliestLogDate(selectedLogbooks);
        maxDate = Utility.currentDate();
    }

    /**
     * Fetches attached file
     *
     * @return file
     */
    public StreamedContent getDownloadedFile() {
        StreamedContent file = null;

        try {
            // return downloadedFile;
            logger.log(Level.FINE, "Opening stream from repository: {0}", downloadArtifact.getResourceId());
            logger.log(Level.FINE, "download file name: {0}", downloadArtifact.getName());
            InputStream istream = blobStore.retreiveFile(downloadArtifact.getResourceId());
            file = new DefaultStreamedContent(istream, "application/octet-stream", downloadArtifact.getName());

            // InputStream stream = new FileInputStream(pathName);                       
            // downloadedFile = new DefaultStreamedContent(stream, "application/octet-stream", "file.jpg"); //ToDo" replace with actual filename
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error Downloading file", e.getMessage());
            logger.log(Level.SEVERE, "NotesView.getDownloadedFile {0}", e.getMessage());
            System.out.println(e);
        }

        return file;
    }

    /**
     * Applies the filter
     *
     * @return next view
     */
    public String filterLog() {
        // swap if start is after end
        if (startDate != null && endDate != null) {
            if (startDate.after(endDate)) {
                Date temp = endDate;
                endDate = startDate;
                startDate = temp;
            }
        }

        lazyNotesModel.filterEvents(selectedLogbooks, startDate, endDate, searchPhrase);

        return null;
    }

    /**
     * Resets filter data
     *
     * @param e
     */
    public void resetFilter(ActionEvent e) {
        startDate = null;
        endDate = null;
        searchPhrase = null;
        lazyNotesModel.filterEvents(selectedLogbooks, startDate, endDate, searchPhrase);
    }

    /**
     * Find the history of a log entry, plus their the status information
     *
     * @param entry
     */
    public void findLogHistory(StatusfulLogEntry entry) {
        try {
            logger.log(Level.FINE, "NotesView.findLogHistory: entering");
            obsoleteLogEntries.clear();
            snapshotEJB.getLogHistory(entry.getLogEntry().getLogEntryId(), obsoleteLogEntries);
            logger.log(Level.FINE, "NotesView.findLogHistory: number of log entries found: {0}", obsoleteLogEntries.size());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "NotesView.findLogHistory: error {0}", e.getMessage());
            System.err.println(e);
        }
    }

    /**
     * Moves the fileter's end date by given hours
     *
     * @param hours
     */
    private void moveEndDate(int hours) {
        logger.log(Level.FINE, "NotesView.moveEndDate: Moving end date by {0} hours", hours);
        long difference = 0;
        if (startDate != null && endDate != null) {
            difference = endDate.getTime() - startDate.getTime();
        }
        if (hours > 0) {
            if (endDate == null || endDate.equals(maxDate)) {
                return;
            }
            Date newDate = Utility.addHoursToDate(endDate == null ? maxDate : endDate, hours);
            logger.log(Level.FINE, "NotesView.moveEndDate: new end date {0}", newDate);
            endDate = newDate.after(maxDate) ? maxDate : newDate;
        } else if (hours < 0) {
            Date newDate = Utility.addHoursToDate(endDate == null ? maxDate : endDate, hours);
            logger.log(Level.FINE, "NotesView.moveEndDate: new end date {0}", newDate);
            if (newDate.equals(minDate)) {
                return;
            }
            endDate = newDate.before(minDate) ? minDate : newDate;
        }
        if (startDate != null) {
            startDate.setTime(endDate.getTime() - difference);
        }
        lazyNotesModel.filterEvents(selectedLogbooks, startDate, endDate, searchPhrase);
    }

    public void forwardEndDate() {
        moveEndDate(CHANGE_HOURS);
    }

    public void fastForwardEndDate() {
        moveEndDate(FAST_CHANGE_HOURS);
    }

    public void reverseEndDate() {
        moveEndDate(-CHANGE_HOURS);
    }

    public void fastReverseEndDate() {
        moveEndDate(-FAST_CHANGE_HOURS);
    }

    private final String NOTESFORM = "notesForm"; // Name of the log table component. Do not change!
    /**
     * If there are any new log entries in the system, updates the notes view.
     *
     */
    public void checkNewData() {
        // TODO: change logbooks to selected log books
        try {
            // final String NOTESFORM = "notesForm"; // Name of the log table component           

            if (latestLogDate == null) {
                logger.log(Level.WARNING, "NotesView#checkNewData: latestLogDate is null. Setting it to curent date");
                latestLogDate = Utility.currentDate();
            }
            Date ldate = logbookEJB.latestLogDate(logbooks);
            if (!latestLogDate.equals(ldate)) {
                // logger.log(Level.FINE, "NotesView#checkNewData: State has changed. updating {0}", NOTESFORM);
                RequestContext.getCurrentInstance().update(NOTESFORM);
                latestLogDate = ldate;
            } else {
                // logger.log(Level.FINE, "NotesView#checkNewData: No changes");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Problem while refreshing log entries", e);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Problem while refreshing log entries", "This is mostly normal and can be ignored");
        }
    }

    // Getters and setters
    public boolean isInputEnabled() {
        return authManager.canCreateLog();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public LazyDataModel<StatusfulLogEntry> getLazyLog() {
        return lazyLog;
    }

    public void setDownloadArtifact(Artifact downloadArtifact) {
        this.downloadArtifact = downloadArtifact;
    }

    public Artifact getDownloadArtifact() {
        return downloadArtifact;
    }

    public List<StatusfulLogEntry> getEditedLogEntries() {
        return obsoleteLogEntries;
    }

    public Date getMinDate() {
        return minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
    }

    public List<Logbook> getSelectedLogbooks() {
        return selectedLogbooks;
    }

    public void setSelectedLogbooks(List<Logbook> selectedLogbooks) {
        this.selectedLogbooks = selectedLogbooks;
    }

    public List<Logbook> getLogbooks() {
        return logbooks;
    }

}
