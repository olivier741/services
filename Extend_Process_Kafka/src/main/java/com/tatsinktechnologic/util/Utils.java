/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier.tatsinkou
 */
public class Utils {
     private static Logger logger = Logger.getLogger(Utils.class);
     
     public static InetAddress gethostName(){
       
       InetAddress addr = null;

            try
            {
                addr = InetAddress.getLocalHost();
            }
            catch (UnknownHostException ex)
            {
                logger.error("Hostname can not be resolved",ex);
            }
       return addr;
   }
}
