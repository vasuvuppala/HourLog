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
package org.openepics.discs.hourlog.service.v0_1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.openepics.discs.hourlog.ent.Sysuser;

/**
 * User as RESTful services
 *
 * @author vuppala
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UserRep {

    private int userId;
    private String loginId;
    private String firstName;
    private String lastName;
    private String email;

    private UserRep() {
    }

    public static UserRep newInstance(Sysuser user) {
        UserRep res = new UserRep();

        res.userId = user.getUserId() == null? 0: user.getUserId();
        res.loginId = user.getLoginId();
        res.firstName = user.getFirstName();
        res.lastName = user.getLastName();
        res.email = user.getEmail();

        return res;
    }
}
