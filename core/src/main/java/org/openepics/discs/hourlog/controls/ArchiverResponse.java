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
package org.openepics.discs.hourlog.controls;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Response from Archiver Appliance
 *
 * @author vuppala
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ArchiverResponse implements Serializable {

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    public static class ArchMeta implements Serializable {

        @XmlElement(name = "name")
        private String name;
        @XmlElement(name = "EGU")
        private String units;
        @XmlElement(name = "PREC")
        private String precision;
        @XmlTransient
        private String description;

        public ArchMeta() {
        }

        public String getName() {
            return name;
        }

        public String getUnits() {
            return units;
        }

        public String getPrecision() {
            return precision;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    public static class ArchData implements Serializable {

        @XmlElement(name = "secs")
        private long seconds;
        @XmlElement(name = "val")
        private double value;
        @XmlElement(name = "nanos")
        private long nanos;
        @XmlElement(name = "severity")
        private int severity;
        @XmlElement(name = "status")
        private int status;

        public ArchData() {

        }

        public long getSeconds() {
            return seconds;
        }

        public double getValue() {
            return value;
        }

        public long getNanos() {
            return nanos;
        }

        public int getSeverity() {
            return severity;
        }

        public int getStatus() {
            return status;
        }
    }

    @XmlElement(name = "meta")
    private ArchMeta meta;
    @XmlElement(name = "data")
    private List<ArchData> data;

    public ArchiverResponse() {
    }

    public ArchMeta getMeta() {
        return meta;
    }

    public List<ArchData> getData() {
        return data;
    }

}
