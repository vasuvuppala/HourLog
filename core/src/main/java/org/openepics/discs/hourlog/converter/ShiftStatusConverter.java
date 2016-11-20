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
import org.openepics.discs.hourlog.ejb.ShiftEJB;
import org.openepics.discs.hourlog.ent.ShiftStatus;

/**
 * Shift Converter
 *
 * @author vuppala
 */
@Named    // workaround for injecting an EJB in a converter
// @FacesConverter(value = "experimentConverter")
public class ShiftStatusConverter implements Converter {

    @EJB
    private ShiftEJB shiftEJB;
    private static final Logger logger = Logger.getLogger(ShiftStatusConverter.class.getName());

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ShiftStatus object;

        if (value == null || value.equals("")) {
            logger.log(Level.FINE, "Shift status converter: empty id");
            return null;
        } else {
            object = shiftEJB.findShiftStatus(Short.parseShort(value));
            return object;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            logger.log(Level.FINE, "BeamConverter: Null object");
            return "";
        } else {
            // logger.log(Level.FINE, "Exp number: " + ((Experiment) value).getId().toString());
            return ((ShiftStatus) value).getStatusId().toString();
        }
    }
}
