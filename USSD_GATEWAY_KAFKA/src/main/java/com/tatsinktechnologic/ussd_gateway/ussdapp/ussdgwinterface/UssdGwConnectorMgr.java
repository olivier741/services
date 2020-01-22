/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.ussdgwinterface;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.ussdapp.config.DBConfig;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;
import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class UssdGwConnectorMgr
{
  private DBConfig dbConfig;
  private HashMap<Integer, Connector> connectors;
  
  public UssdGwConnectorMgr(DBConfig dbConfig)
    throws Exception
  {
    this.dbConfig = dbConfig;
    

    ResultSet result = dbConfig.getData("select * from USSDGW");
    
    this.connectors = new HashMap();
    try
    {
      while (result.next())
      {
        int id = result.getInt("USSDGW_ID");
        if (this.connectors.containsKey(Integer.valueOf(id))) {
          throw new Exception("same connector id: " + id);
        }
        String name = result.getString("NAME");
        

        String configFile = result.getString("CONFIG_FILE");
        

        String implClass = result.getString("IMPLEMENT_CLASS");
        

        Class cfClass = Class.forName(implClass);
        
        Constructor conster = cfClass.getConstructor(new Class[] { Integer.TYPE, String.class, String.class });
        
        Connector con = (Connector)conster.newInstance(new Object[] { Integer.valueOf(id), name, configFile });
        
        this.connectors.put(Integer.valueOf(id), con);
      }
      try
      {
        result.close();
      }
      catch (SQLException ex) {}
      if (!this.connectors.isEmpty()) {
        return;
      }
    }
    finally
    {
      try
      {
        result.close();
      }
      catch (SQLException ex) {}
    }
    throw new Exception("No Ussdgw connector!");
  }
  
  public void setStoreQueue(BlockQueue storeQ)
  {
    for (Connector con : this.connectors.values()) {
      con.setQueue(storeQ);
    }
  }
  
  public Connector getConnector(int connectorId)
  {
    return (Connector)this.connectors.get(Integer.valueOf(connectorId));
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder("connector list: ");
    buf.append(this.connectors.size());
    buf.append(" connector(s)\r\n");
    for (Connector con : this.connectors.values())
    {
      buf.append(con);
      buf.append("\r\n\r\n");
    }
    return buf.toString();
  }
  
  public void start()
  {
    for (Connector con : this.connectors.values()) {
      con.start();
    }
  }
  
  public void stop()
  {
    for (Connector con : this.connectors.values()) {
      con.stop();
    }
  }
}
