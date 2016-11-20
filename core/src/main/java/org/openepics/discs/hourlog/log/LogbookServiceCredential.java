/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2014, 2015.
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

/**
 * Credentials for Olog service
 * 
 * @author vuppala
 */
public class LogbookServiceCredential {
    private final String userid; // user's unique login name
    
    /**
     * TODO: Change this implementation.
     * 
     * KEEPING PASSWORD HERE IS BAD! 
     * However, there no good alternative. Encoding it is no better.
     * Encryption is not all that better either as the key has to be kept somewhere, in the code or the database.
     * Till a better alternative is implemented in Olog, we are stuck with this.
     * 
     * Note this is only for Olog Service.
     */
    private final String password; //
    
    /**
     * 
     * @param userid
     * @param pass 
     */
    public LogbookServiceCredential(String userid, String pass) {
        this.userid = userid;
        this.password = pass;
    }

    protected String getUserid() {
        return userid;
    }

    protected String getPassword() {
        return password;
    }
}
