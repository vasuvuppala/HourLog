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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * State of an external service
 *
 * @author vuppala
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class StateRep {

    @XmlAccessorType(XmlAccessType.FIELD)
    public static enum ServiceStatus {

        UP, DOWN, UNKNOWN;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    private static class ExternalService {

        private String name;
        private ServiceStatus status;

        private ExternalService() {
        }

        private ExternalService(String name, ServiceStatus stat) {
            this.name = name;
            this.status = stat;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    private static class SystemState {

        private float cpu = 0.0f;
        private float memory = 0.0f;
        private float load = 0.0f;

        public SystemState() {
        }

        private SystemState(float cpu, float memory, float load) {
            this.cpu = cpu;
            this.memory = memory;
            this.load = load;
        }

    }

    private int summary = 0;
    private final SystemState system = new SystemState();
    @XmlElementWrapper(name = "serviceList")
    @XmlElement(name = "service")
    private final List<ExternalService> services = new ArrayList<>();

    public StateRep() {
    }

    public void setSummary(int summary) {
        this.summary = summary;
    }

    public void addServiceStatus(String name, ServiceStatus stat) {
        services.add(new ExternalService(name, stat));
    }

    public void setSystemstatus(float cpu, float memory, float load) {
        system.cpu = cpu;
        system.memory = memory;
        system.load = load;
    }
}
