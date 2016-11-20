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
package org.openepics.discs.hourlog.util;

/**
 * Stop watch to measure elapsed time
 * 
 * @author vuppala
 */
public final class StopWatch {
    
    private static final long NANO_IN_SECS = 1000000000L;
    
    private long startTime = System.nanoTime();
    private long lapTime = System.nanoTime();
    private long duration = 0;
    private long lapDuration = 0;
    private boolean stopped = false;
    
    public StopWatch() {       
    }
    
    /**
     * start watch
     */
    public void start() {
        startTime = System.nanoTime();
        lapTime = System.nanoTime();
        stopped = false;
    }
    
    /**
     * stop watch
     */
    public void  stop() {
        duration = System.nanoTime() - startTime;
        stopped = true;
    }
    
    /**
     * get time
     * 
     * @return 
     */
    public long getTime() {
        return stopped ? duration: System.nanoTime() - startTime;
    }
    
    /**
     * stop after a lap
     * 
     * @return 
     */
    public long lapStop() {
        long oldLapTime = lapTime;
        lapTime = System.nanoTime();
        lapDuration = lapTime - oldLapTime;
        return lapDuration;
    }
    
    /**
     * reset stopwatch
     * 
     */
    public void reset() {
        startTime = 0;
        duration = 0;
        stopped = true;
    }
    
    /**
     * check if stopped
     * 
     * @return 
     */
    public boolean isStopped() {
        return stopped;
    }
    
    /**
     * display stopwatch reading
     * 
     * @param dur
     * @return 
     */
    private String reading(long dur) {
        long seconds = dur / NANO_IN_SECS;
        long remainder = dur % NANO_IN_SECS;
        return String.valueOf(seconds) + "." + String.format("%09d",remainder) + " Seconds";
    }
    
    /**
     * display laptime
     * 
     * @return 
     */
    public String lapReading() {
        return reading(lapDuration);
    }
    
    @Override
    public String toString() {      
        return reading(getTime());
    }
}
