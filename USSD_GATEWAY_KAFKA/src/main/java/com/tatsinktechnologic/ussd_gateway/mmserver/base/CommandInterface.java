/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.base;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.agent.MMbeanServer;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public class CommandInterface
  extends StandardMBean
  implements CommandInterfaceMBean
{
  private static CommandInterface comInterface;
  
  public static synchronized CommandInterface getInstance()
  {
    Log.info("Registering Command Interface");
    if (comInterface == null) {
      try
      {
        comInterface = new CommandInterface();
      }
      catch (Exception ex)
      {
        Log.error("Critical error when init CommandInterface");
        throw new RuntimeException("Critical error when init CommandInterface!");
      }
    }
    return comInterface;
  }
  
  public CommandInterface()
    throws NotCompliantMBeanException, MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException
  {
    super(CommandInterfaceMBean.class);
    MMbeanServer.getInstance().registerMBean(this, new ObjectName("Tools:name=CommandInterface"));
  }
  
  public void callCommand(String args)
  {
    try
    {
      Runtime rt = Runtime.getRuntime();
      Process proc = rt.exec(args);
      
      StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
      
      StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
      
      errorGobbler.start();
      outputGobbler.start();
      
      int exitVal = proc.waitFor();
      Log.info("ExitValue: " + exitVal);
    }
    catch (Throwable t)
    {
      Log.error("Error in execute remote command");
      Log.error(t);
    }
  }
}

