/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.user_listener;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="failover")
public class Failover {
    
    @Element(name="failureThreshold",required = true) 
    private int failureThreshold;
    
    @Element(name="resetTimeout",required = true) 
    private int resetTimeout;
    
    @Element(name="receiveTimeout",required = true) 
    private int receiveTimeout;
    
    @Element(name="delayBetweenRetries",required = true) 
    private int delayBetweenRetries;
    
  
    public Failover( @Element(name="failureThreshold",required = true) int failureThreshold, 
                     @Element(name="resetTimeout",required = true) int resetTimeout, 
                     @Element(name="receiveTimeout",required = true) int receiveTimeout,
                     @Element(name="delayBetweenRetries",required = true) int delayBetweenRetries
                    ) {
        this.failureThreshold = failureThreshold;
        this.resetTimeout = resetTimeout;
        this.receiveTimeout = receiveTimeout;
        this.delayBetweenRetries = delayBetweenRetries;
  
    }

    public int getFailureThreshold() {
        return failureThreshold;
    }

    public int getResetTimeout() {
        return resetTimeout;
    }

    public int getReceiveTimeout() {
        return receiveTimeout;
    }

    public int getDelayBetweenRetries() {
        return delayBetweenRetries;
    }

    @Override
    public String toString() {
        return "Failover{" + "failureThreshold=" + failureThreshold + ", resetTimeout=" + resetTimeout + ", receiveTimeout=" + receiveTimeout + ", delayBetweenRetries=" + delayBetweenRetries + '}';
    }

}
