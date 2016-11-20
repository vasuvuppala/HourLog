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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.hourlog.util.AppProperties.HourLogProperty;

/**
 * System for storing blobs
 *
 * @author vuppala
 */
@Named
public class BlobStore implements Serializable {

    private static final Logger logger = Logger.getLogger(BlobStore.class.getName());
    private static String blobStoreRoot = "/var/hourlog";
    private boolean validStore = true; // is the blob store valid?

    @Inject
    private AppProperties appProps;

    public void BlobStore() {
    }

    @PostConstruct
    public void init() {
        String storeRoot = appProps.getProperty(HourLogProperty.BLOBSTORE_ROOT); // Todo: either move it to AppProperties or inject it

        if (storeRoot != null && !storeRoot.isEmpty()) {
            blobStoreRoot = storeRoot;
        }

        File folder = new File(blobStoreRoot);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                logger.log(Level.SEVERE, "Could not create blob store root {0}", blobStoreRoot);
                validStore = false;
            }
        }
    }

    /**
     * Copies files
     *
     * @param is
     * @param os
     * @throws IOException
     */
    private void copyFile(InputStream is, OutputStream os) throws IOException {
        int len;
        byte[] buffer = new byte[1024];

        while ((len = is.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
    }

    /**
     * Generates new blob identifier
     *
     * @return blob id
     */
    private String newBlobId() {
        String fileName = UUID.randomUUID().toString();
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // zero based
        int day = now.get(Calendar.DAY_OF_MONTH);

        String separator = File.separator;

        String dirName = year + separator + month + separator + day;
        String pathName = blobStoreRoot + separator + dirName;

        File folder = new File(pathName);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                logger.log(Level.SEVERE, "Could not create blob directory {0}", pathName);
                return null;
            }
        }

        String blobId = dirName + separator + fileName;
        String fullPath = blobStoreRoot + separator + blobId;
        File newFile = new File(fullPath);
        if (newFile.exists()) {
            logger.log(Level.SEVERE, "Blob already exists! Name collision {0}", fullPath);
            return null;
        }

        logger.log(Level.FINE, "New Blob Id {0}", blobId);

        return blobId;
    }

    /**
     * Save file into blob store.
     *
     * @param istream
     * @return
     * @throws IOException
     */
    public String storeFile(InputStream istream) throws IOException {
        String fileId = this.newBlobId();
        OutputStream ostream;

        if (istream == null) {
            throw new IOException("istream is null");
        }

        File ofile = new File(blobStoreRoot + File.separator + fileId);
        ostream = new FileOutputStream(ofile);
        copyFile(istream, ostream);
        // repBean.putFile(folderName, fname, istream);           
        ostream.close();

        return fileId;
    }

    /**
     * Retrieve file from blob store
     *
     * @param fileId
     * @return file
     * @throws IOException
     */
    public InputStream retreiveFile(String fileId) throws IOException {
        InputStream istream;

        istream = new FileInputStream(blobStoreRoot + File.separator + fileId);

        return istream;
    }
    
    /**
     * Retrieve file from blob store
     *
     * @param fileId
     * @param fileName
     * @return file
     * @throws IOException
     */
    public File retreiveFile(String fileId, String fileName) throws IOException {      
        Path path = Paths.get(blobStoreRoot + File.separator + fileId);
        Path newPath = Paths.get(path.getParent() + File.separator + fileName);
        Files.copy(path, newPath, StandardCopyOption.REPLACE_EXISTING);
        
        return newPath.toFile();
    }

    public void deleteFile(File file) throws IOException {
        Path path = Paths.get(file.getPath());
        Files.delete(path);
    }

    public String getBlobStoreRoot() {
        return blobStoreRoot;
    }

    public boolean isValidStore() {
        return validStore;
    }
}
