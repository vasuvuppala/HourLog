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
package org.openepics.discs.hourlog.converter;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;
import org.openepics.discs.hourlog.ejb.HourLogEJB;
import org.openepics.discs.hourlog.ent.Logbook;

/**
 * Logbook converter
 *
 * @author vuppala
 */
@Named    // workaround for injecting an EJB in a converter
// @FacesConverter(value = "experimentConverter")
public class LogbookConverter implements Converter {

    @EJB
    private HourLogEJB hourLogEJB;
    private static final Logger logger = Logger.getLogger(LogbookConverter.class.getName());

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Logbook object;

        if (value == null || value.equals("")) {
            logger.log(Level.WARNING, "LogbookConverter: empty id");
            return null;
        } else {
            object = hourLogEJB.findLogbook(Short.parseShort(value));
            // object = new Logbook((short) 0, value, value, 0);
            return object;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            logger.log(Level.WARNING, "LogbookConverter: Null object");
            return "";
        } else {    
            return ((Logbook) value).getLogbookId().toString();
        }
    }
}
