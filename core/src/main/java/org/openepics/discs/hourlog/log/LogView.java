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
package org.openepics.discs.hourlog.log;

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
import org.openepics.discs.hourlog.ejb.HourLogEJB;
import org.openepics.discs.hourlog.ejb.ShiftEJB;
import org.openepics.discs.hourlog.ejb.SnapshotEJB;
import org.openepics.discs.hourlog.ent.BreakdownCategory;
import org.openepics.discs.hourlog.ent.BreakdownEvent;
import org.openepics.discs.hourlog.ent.Logbook;
import org.openepics.discs.hourlog.ent.Shift;
import org.openepics.discs.hourlog.ent.TroubleReport;
import org.openepics.discs.hourlog.trep.TroubleReportsView;
import org.openepics.discs.hourlog.util.AppProperties;
import org.openepics.discs.hourlog.util.BlobStore;
import org.openepics.discs.hourlog.exception.HourLogConcurrencyException;
import org.openepics.discs.hourlog.exception.HourLogServiceException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 * State for the (main) log page. S
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class LogView implements Serializable {

    @EJB
    private EventEJB eventEJB;
    @EJB(beanName = "LocalAuthManager")
    private AuthManager authManager;
    @EJB
    private SnapshotEJB snapshotEJB;
    @EJB
    private HourLogEJB hourlogEJB;
    @EJB(beanName = "OlogLogbookManager")
    private LogbookManager logbookEJB;

    @Inject
    private UserSession userSession;
    @Inject
    private LazyLogModel lazyLogModel;
    @Inject
    private BlobStore blobStore;
    @Inject
    private TroubleReportsView troubleReportsView;

    private static final Logger logger = Logger.getLogger(LogView.class.getName());
    private static final int CHANGE_HOURS = 9;
    private static final int FAST_CHANGE_HOURS = 24;

//    private static final String NEW_ENTRY_FORM = "newEntry"; // Name of the ne wlog entry input panel
    private Logbook operationsLogbook;
    private LazyDataModel<StatusfulLogEntry> lazyLog;
    private StatusSnapshot currentSnapshot;
    private List<String> breakdowns;
    private List<StatusfulLogEntry> editedLogEntries;
    private List<BreakdownCategory> breakdownCategories;

    // entities selected for edits
    private StatusfulLogEntry selectedEntry;
    // private StatusSnapshot selectedSnapshot;
    private Shift selectedShift;

    // for filtering. 
    private Date minDate, maxDate; // minimum and max event dates. for min/max in calendar widget
    private Date startDate = null, endDate = null; // input from the user

    // For new log and status entries    
    private String newNote;
    private String oldNote; // for saving old note in cases of reset
    // private boolean createTR = false;

    private String uploadedFileName;
    private Artifact downloadArtifact;

    private List<Artifact> inputArtifacts;
    private List<Artifact> savedArtifacts; // in case input is reset
    // private TroubleReport inputTroubleReport;
    private Date occurredAt;
    private StatusSnapshot inputSnapshot;
    private boolean statusInputLocked = true; // is ability to enter facility status enabled? 

    private boolean editorSwitchState = false; // switch state. useful just to save the state
    private boolean richEditor = false; // default is plain text editor

    private Date latestLogDate; // time of the latest log entry

    public LogView() {
    }

    @PostConstruct
    public void init() {
        logger.log(Level.FINE, "Initializing Log Manager");

        inputArtifacts = new ArrayList<>();
        savedArtifacts = new ArrayList<>();
        editedLogEntries = new ArrayList<>();
        operationsLogbook = userSession.getFacility().getOpsLogbook();
        // latestLogDate = logbookEJB.latestLogDate(operationsLogbook);
        latestLogDate = Utility.currentDate();
        breakdownCategories = hourlogEJB.findBrkCategories();
        moveOtherToEnd(breakdownCategories);

        resetInput();
        lazyLog = lazyLogModel;

        if (lazyLog == null) {
            logger.log(Level.SEVERE, "Lazy log model is null");
        }
    }

    /**
     * Move Breakdown Category OTHER to end of the breakdown categories list
     *
     * @param bcats
     */
    private void moveOtherToEnd(List<BreakdownCategory> bcats) {
        BreakdownCategory oth = hourlogEJB.findBrkCategoryOTH();
        BreakdownCategory othInList = null;

        if (oth != null && oth.getName() != null) {
            for (BreakdownCategory cat : bcats) {
                if (oth.getName().equals(cat.getName())) {
                    othInList = cat;
                    break;
                }
            }
            if (othInList != null) {
                bcats.remove(othInList);
                bcats.add(othInList);
            }
        }

    }

    /**
     * Resets input variables
     *
     */
    private void resetInput() {
        // Date currentDate = Utility.currentDate();

        resetFacilityStatus();

        newNote = oldNote;
        endDate = null;
        // createTR = false;
        // fileUploaded = false;
        inputArtifacts.clear();
        inputArtifacts.addAll(savedArtifacts);
        uploadedFileName = null;
        //minDate = eventEJB.firstEventDate();
        minDate = logbookEJB.earliestLogDate(operationsLogbook);
        maxDate = Utility.currentDate();
        statusInputLocked = true;
        occurredAt = null;
        // selectedComponents = null;          
    }

    /**
     * reset facility status
     *
     */
    public void resetFacilityStatus() {
        currentSnapshot = snapshotEJB.snapshotAt(Utility.currentDate());
        inputSnapshot = StatusSnapshot.newInstance(currentSnapshot); // a slightly deeper copy of the current snapshot 
        if (currentSnapshot == null) {
            logger.log(Level.WARNING, "currentSnapshot is null");
        }
        logger.log(Level.FINE, "checking breakdowns");
        breakdowns = breakdownsAt(currentSnapshot);
    }

    /**
     * Stores breakdowns from the input
     *
     */
    private void recordBreakdowns() {
        try {
            for (BreakdownEvent bevent : inputSnapshot.getBreakdowns()) {
                bevent.setFailed(false);
                logger.log(Level.FINE, "event category {0}", bevent.getCategory().getName());
                for (String catname : breakdowns) {
                    if (catname.equals(bevent.getCategory().getName())) {
                        bevent.setFailed(true);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "LogView.recordBreakdowns {0}", e.getMessage());
            System.out.println(e);
        }
    }

    /**
     * Adds a new log entry
     *
     * TODO: Check for authorization
     *
     * @return next view (for PRG pattern)
     */
    public String addNewEntry() {
        try {
//            if (newNote == null || newNote.isEmpty()) {
//                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Note is empty", "Please enter a note");
//                // return null;
//            }
            if (validEntry(false)) {
                // checkChanges();
                recordBreakdowns();
                eventEJB.addEvent(currentSnapshot, occurredAt, newNote, inputArtifacts, inputSnapshot);
                resetSavedInput();
                resetInput();
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Log entry added", " ");
//                hourLogSingleton.newLogUpdateID();
            } else {
                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Please fix validation errors.", "");
            }
        } catch (HourLogServiceException e) {
            logger.log(Level.SEVERE, "Error while adding log entry", e);
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error while adding log entry", String.format("%s %s", e.getServiceName(), e.getMessage()));
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Something went wrong", e.getMessage());
            logger.log(Level.SEVERE, "LogView.addNewEntry {0}", e.getMessage());
            System.out.println(e);
        }

        return "?faces-redirect=true";
    }

    /**
     * Prepares for adding an entry
     *
     *
     */
    public void prepareforNewEntry() {
        try {
            //TODO: optimize. no need to reset eveything.
            resetSavedInput();
            resetInput();
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Something has gone awry", e.getMessage());
            logger.log(Level.SEVERE, "LogView#prepareforNewEntry Something has gone awry: {0}", e.getMessage());
            System.out.println(e);
        }
    }

    /**
     * Prepares the given entry for editing
     *
     * @param entry
     */
    public void prepareforEdit(StatusfulLogEntry entry) {
        try {
            // logger.log(Level.FINE, "LogView.prepareforEdit: Entry Id {0}", entry.getLogEntry().getLogEntryId());
            selectedEntry = entry;

            inputSnapshot = StatusSnapshot.newInstance(entry.getSnapshot());
            breakdowns = breakdownsAt(inputSnapshot);
            newNote = entry.getLogEntry().getLogText();
            oldNote = newNote; // save in case of input reset
            occurredAt = entry.getLogEntry().getOccurredAt();

            inputArtifacts.clear();
            if (entry.getLogEntry().getArtifactList() != null) {
                inputArtifacts.addAll(entry.getLogEntry().getArtifactList());
            }
            savedArtifacts.clear();
            savedArtifacts.addAll(inputArtifacts); // saved them in case input is reset
            statusInputLocked = false; // unlock status inputs

        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "LogView.prepareforEdit: Something went wrong", e.getMessage());
            logger.log(Level.SEVERE, "LogView.prepareforEdit {0}", e.getMessage());
            System.out.println(e);
        }
    }

    /**
     * Edits a log entry
     *
     * TODO: Check for authorization
     *
     * @return next view (for PRG)
     */
    public String editLogEntry() {
        try {
            recordBreakdowns();
            if (validEntry(true)) {
                // checkChanges();
                // recordBreakdowns();
                eventEJB.editStatusfulEntry(selectedEntry, occurredAt, newNote, inputArtifacts, inputSnapshot);
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Log entry modified", "");
                RequestContext.getCurrentInstance().addCallbackParam("success", true); // To inform the form
                // input reset not needed as onInputReset will be called whenver the edit dialog is closed             
            } else {
                RequestContext.getCurrentInstance().addCallbackParam("success", false); // To inform the form
            }
        } catch (HourLogConcurrencyException e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Someone has updated this entry while you were editing it.", "Please refresh the page and try again.");
            logger.log(Level.SEVERE, "LogView#editLogEntry: Optimistic Lock Exception - {0}", e.getMessage());
            // System.out.println(e);
        } catch (HourLogServiceException e) {
            logger.log(Level.SEVERE, "Error while editing log entry", e);
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Error while editing log entry", String.format("%s %s", e.getServiceName(), e.getMessage()));
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong", "Log entry was not updated");
            logger.log(Level.SEVERE, "LogView#editLogEntry: caught exception. check logs.");
            // System.out.println(e);
        } finally {

        }

        return "?faces-redirect=true";
    }

    /**
     * Resets the values that were saved
     */
    private void resetSavedInput() {
        savedArtifacts.clear();
        oldNote = null;
    }

    /**
     * Checks if there are any changes. For debugging
     *
     */
//    private void checkChanges() {
//        logger.log(Level.FINE, "checkChanges: entering");
//        if (inputSnapshot.getExperiment() == currentSnapshot.getExperiment()) {
//            logger.log(Level.FINE, "Experiment ref not changed");
//        } else {
//            logger.log(Level.FINE, "Experiment ref changed");
//        }
//        if (inputSnapshot.getExperiment().getNumber().equals(currentSnapshot.getExperiment().getNumber())) {
//            logger.log(Level.FINE, "Experiment value not changed");
//        } else {
//            logger.log(Level.FINE, "Experiment value  changed");
//        }
//        if (inputSnapshot.getVault() == currentSnapshot.getVault()) {
//            logger.log(Level.FINE, "vault ref not changed");
//        } else {
//            logger.log(Level.FINE, "vault ref changed");
//        }
//        if (inputSnapshot.getVault().getName().equals(currentSnapshot.getVault().getName())) {
//            logger.log(Level.FINE, "vault value not changed");
//        } else {
//            logger.log(Level.FINE, "vault value changed");
//        }
//        if (inputSnapshot.getBeams().get(0) == currentSnapshot.getBeams().get(0)) {
//            logger.log(Level.FINE, "beam event ref not changed");
//        } else {
//            logger.log(Level.FINE, "beam ref changed");
//        }
//        if (inputSnapshot.getBeams().get(0).getBeam() != null) {
//            if (currentSnapshot.getBeams().get(0).getBeam() != null) {
//                logger.log(Level.FINE, "beam 0 input: {0}. Current: {1}", new Object[] {inputSnapshot.getBeams().get(0).getBeam().getBeamId(), currentSnapshot.getBeams().get(0).getBeam().getBeamId()});
//            }
//            if (inputSnapshot.getBeams().get(0).getBeam() == currentSnapshot.getBeams().get(0).getBeam()) {
//                logger.log(Level.FINE, "beam ref not changed");
//            } else {
//                logger.log(Level.FINE, "beam ref changed");
//            }
//            if (inputSnapshot.getBeams().get(0).getBeam().equals(currentSnapshot.getBeams().get(0).getBeam())) {
//                logger.log(Level.FINE, "beam val not changed");
//            } else {
//                logger.log(Level.FINE, "beam val changed");
//            }
//        }
//    }
    /**
     * Uploads files
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        // Utility.showMessage(FacesMessage.SEVERITY_INFO, "Succesful", msg);
        logger.log(Level.FINE, "Entering handleFileUpload");
        InputStream istream;

        try {
            UploadedFile uploadedFile = event.getFile();
            uploadedFileName = uploadedFile.getFileName();
            istream = uploadedFile.getInputstream();
            // logger.log(Level.FINE,"Uploaded file name {0}", uploadedFileName);
            // Utility.showMessage(FacesMessage.SEVERITY_INFO, "File ", "Name: " + uploadedFileName);
            logger.log(Level.FINE, "calling blobstore");
            String fileId = blobStore.storeFile(istream);

            // create an artifact
            Artifact artifact = new Artifact();
            artifact.setName(uploadedFileName);
            artifact.setType(AppProperties.ARTIFACT_DOC);
            artifact.setResourceId(fileId);
            inputArtifacts.add(artifact);

            istream.close();
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "File uploaded", "Name: " + uploadedFileName);
            // ileUploaded = true;
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error Uploading file", e.getMessage());
            logger.log(Level.SEVERE, "LogView.handleFileUpload {0}", e.getMessage());
            System.out.println(e);
            // fileUploaded = false;
        } finally {

        }
    }

    /**
     * downloads file
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
            logger.log(Level.SEVERE, "LogView.getDownloadedFile {0}", e.getMessage());
            System.out.println(e);
        }

        return file;
    }

    /**
     * Adds a trouble report to the log entry
     *
     */
    public void addTR() {
        try {
            TroubleReport inputTroubleReport = troubleReportsView.getSelectedTroubleReport();
            // create an artifact
            Artifact artifact = new Artifact();
            artifact.setName("TR:" + inputTroubleReport.getId());
            artifact.setType(AppProperties.ARTIFACT_TR);
            //artifact.setResourceId(Integer.toString(inputTR));
            artifact.setResourceId(Integer.toString(inputTroubleReport.getId()));
            inputArtifacts.add(artifact);

            RequestContext.getCurrentInstance().addCallbackParam("success", true);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Trouble Report Added", "");
        } catch (Exception e) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error Adding TR", e.getMessage());
            logger.log(Level.SEVERE, "LogView.addTR: Error Adding Trouble Report {0}", e.getMessage());
            System.out.println(e);
        }
    }

    /**
     * Removes an artifact from the log entry
     *
     * @param artifact
     */
    public void removeArtifact(Artifact artifact) {
        logger.log(Level.FINE, "LogView.removeArtifact: to remove {0}", artifact.getName());
        // inputArtifacts.remove(artifact);
        int size = inputArtifacts.size();
        for (int i = 0; i < size; i++) {
            if (inputArtifacts.get(i).getResourceId().equals(artifact.getResourceId())) {
                inputArtifacts.remove(i);
                logger.log(Level.FINE, "LogView.removeArtifact: removed artifact at index {0}", i);
                break;
            }
        }
        logger.log(Level.FINE, "LogView.removeArtifact: number of artifacts {0}", inputArtifacts.size());
    }

    /**
     * COnverts current breakdowns to a list of strings (used by GUI widgets)
     *
     * @param snapshot
     * @return breakdowns
     */
    public List<String> breakdownsAt(StatusSnapshot snapshot) {
        try {
            if (snapshot == null) {
                logger.log(Level.WARNING, "LogView.BreakdownsAt: strange. snapshot is null");
                return null;
            }

            List<String> brks = new ArrayList<>();
            // BreakdownStatus failed = hourLogView.failBreakdownStatus();

            for (BreakdownEvent bevent : snapshot.getBreakdowns()) {
                // logger.log(Level.FINE,"LogMnager.breakdownsAt: break down " + bevent.getCategory().getName() + "  " + bevent.getStatus().getName());
                if (bevent.getFailed()) {
                    brks.add(bevent.getCategory().getName());
                }
            }

            return brks;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "LogView.breakdownsAt:  {0}", e.getMessage());
            System.out.println(e);
        }
        return null;
    }

    /**
     * Filters log entries
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
        lazyLogModel.filterEvents(startDate, endDate);

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
        lazyLogModel.filterEvents(startDate, endDate);
    }

    /**
     * Is the input valid?
     *
     * @param editOperation - validate input when editing a log entry entry
     * @return true if it is
     */
    private boolean validEntry(boolean editOperation) {
        boolean valid = true;

        if (newNote == null || newNote.isEmpty()) {
            valid = false;
            Utility.showMessage(FacesMessage.SEVERITY_FATAL, "Sorry, you must write a note, even if it's just a status change.", "");
        }

        // check for valid occurrence date
        Date oldDate = null;
        if (editOperation) { // when editing an entry
            oldDate = selectedEntry.getLogEntry().getOccurredAt();
        }
        if (occurredAt != null && !occurredAt.equals(oldDate)) { // if occurrence date was changed
            if (!authManager.canSetAnyDate()) {
                Date currentDate = Utility.currentDate();
                Date maxLogDate = Utility.addMinutesToDate(currentDate, AppProperties.LOG_FUTURE_WINDOW);
                Date minLogDate = Utility.addMinutesToDate(currentDate, -AppProperties.LOG_PAST_WINDOW);
                if (occurredAt != null && (occurredAt.before(minLogDate) || occurredAt.after(maxLogDate))) {
                    valid = false;
                    Utility.showMessage(FacesMessage.SEVERITY_FATAL, String.format("Sorry, the occurrence date must be between %1$td-%1$tb-%1$tY %1$tH:%1tM and %2$td-%2$tb-%2$tY %2$tH:%2tM", minLogDate, maxLogDate), "");
                }
            }
        }

        if (editOperation) {
            logger.log(Level.FINE, "ViewLog.validEntry: Checking for change");
            LogEntry logEntry = selectedEntry.getLogEntry();

            if (logEntry.getOccurredAt().equals(occurredAt)
                    && logEntry.getLogText().equals(newNote)
                    && inputArtifacts.equals(logEntry.getArtifactList())
                    && inputSnapshot.equals(selectedEntry.getSnapshot())) {
                valid = false;
                Utility.showMessage(FacesMessage.SEVERITY_FATAL, "You must change something: status, note, attachments, or occurrence date", "");
            }
        }

        return valid;
    }

    /**
     * Find the history of a log entry, plus their the status information
     *
     * @param entry
     */
    public void findLogHistory(StatusfulLogEntry entry) {
        try {
            logger.log(Level.FINE, "LogView.findLogHistory: entering");
            editedLogEntries.clear();
            snapshotEJB.getHistoryWithStatus(entry.getLogEntry().getLogEntryId(), editedLogEntries);
            logger.log(Level.FINE, "LogView.findLogHistory: number of log entries found: {0}", editedLogEntries.size());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "LogView.findLogHistory: error {0}", e);
        }
    }

    /**
     * Can the current user edit the given log entry
     *
     * @param entry
     * @return true if she can
     */
    public boolean canEditLog(StatusfulLogEntry entry) {
        return authManager.canEditLog(entry.getLogEntry());
    }

    /**
     * Moves a period in the filter
     *
     * @param hours
     */
    private void moveEndDate(int hours) {
        logger.log(Level.FINE, "LogView.moveEndDate: Moving end date by {0} hours", hours);
        if (hours > 0) {
            if (endDate == null || endDate.equals(maxDate)) {
                return;
            }
            Date newDate = Utility.addHoursToDate(endDate == null ? maxDate : endDate, hours);
            logger.log(Level.FINE, "LogView.moveEndDate: new end date {0}", newDate);
            endDate = newDate.after(maxDate) ? maxDate : newDate;
        } else if (hours < 0) {
            Date newDate = Utility.addHoursToDate(endDate == null ? maxDate : endDate, hours);
            logger.log(Level.FINE, "LogView.moveEndDate: new end date {0}", newDate);
            if (newDate.equals(minDate)) {
                return;
            }
            endDate = newDate.before(minDate) ? minDate : newDate;
        }
        startDate = null;
        lazyLogModel.filterEvents(startDate, endDate);
    }

    /**
     * Switches editor mode: rich or plain
     *
     */
    public void onEditorSwitch() {
        richEditor = !richEditor;
    }

    /**
     * Resets for new log entry
     *
     */
    public void onInputReset() {
        logger.log(Level.FINE, "LogView#onInputReset: Input reset");
        resetSavedInput();
        resetInput();
    }

    /**
     * Resets all changes when editing a log entry
     *
     */
    public void onEditReset() {
        newNote = oldNote;
        inputArtifacts.clear();
        inputArtifacts.addAll(savedArtifacts);
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

    // Auth
    public boolean canNotCreateLog() {
        return !authManager.canCreateLog();
    }

    public boolean canNotUpdateStatus() {
        return !authManager.canUpdateStatus();
    }

    public boolean canNotInputStatus() {
        return statusInputLocked || !authManager.canUpdateStatus();
    }

    /**
     * If there are any new log entries in the system, updates the log view.
     *
     */
    // private static final String LOGFORM = "logform"; // Name of the log table component
    private final String LOGFORM = "logform";
    public void checkNewData() {
        try {
            // final String LOGFORM = "logform"; // Name of the log table component (in log.xhtml). DO NOT change it!
            
            if (latestLogDate == null) {
                logger.log(Level.WARNING, "latestLogDate is null. Setting it to curent date");
                latestLogDate = Utility.currentDate();
            }
            
            Date ldate = logbookEJB.latestLogDate(operationsLogbook);
            if (!latestLogDate.equals(ldate)) {
                // logger.log(Level.FINE, "State has changed. updating {0}", LOGFORM);
                RequestContext.getCurrentInstance().update(LOGFORM);
                latestLogDate = ldate;
            } else {
                // logger.log(Level.FINE, "No changes");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Problem while refreshing log entries", e);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Problem while refreshing log entries", "This is mostly normal and can be ignored");
        }
    }

    // -------------- Getters and setters
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

    public String getNewNote() {
        return newNote;
    }

    public void setNewNote(String note) {
        // logger.log(Level.SEVERE, "LogView.setting Note to {0}", note);
        this.newNote = note;
    }

    public boolean isRichEditor() {
        return richEditor;
    }

    public void setRichEditor(boolean richEditor) {
        this.richEditor = richEditor;
    }

    public LazyDataModel<StatusfulLogEntry> getLazyLog() {
        return lazyLog;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public List<Artifact> getInputArtifacts() {
        return inputArtifacts;
    }

    public StatusSnapshot getInputSnapshot() {
        return inputSnapshot;
    }

    public void setInputSnapshot(StatusSnapshot inputSnapshot) {
        this.inputSnapshot = inputSnapshot;
    }

    public List<String> getBreakdowns() {
        return breakdowns;
    }

    public void setBreakdowns(List<String> breakdowns) {
        this.breakdowns = breakdowns;
    }

    public void setDownloadArtifact(Artifact downloadArtifact) {
        this.downloadArtifact = downloadArtifact;
    }

    public Artifact getDownloadArtifact() {
        return downloadArtifact;
    }

    public List<StatusfulLogEntry> getEditedLogEntries() {
        return editedLogEntries;
    }

    public Date getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Date occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Date getMinDate() {
        return minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public Shift getSelectedShift() {
        return selectedShift;
    }

    public StatusSnapshot getCurrentSnapshot() {
        return currentSnapshot;
    }

    public boolean isStatusInputLocked() {
        return statusInputLocked;
    }

    public void setStatusInputLocked(boolean statusInputLocked) {
        this.statusInputLocked = statusInputLocked;
    }

    public boolean isEditorSwitchState() {
        return editorSwitchState;
    }

    public void setEditorSwitchState(boolean editorSwitchState) {
        this.editorSwitchState = editorSwitchState;
    }

    public Logbook getOperationsLogbook() {
        return operationsLogbook;
    }

    public List<BreakdownCategory> getBreakdownCategories() {
        return breakdownCategories;
    }

}
