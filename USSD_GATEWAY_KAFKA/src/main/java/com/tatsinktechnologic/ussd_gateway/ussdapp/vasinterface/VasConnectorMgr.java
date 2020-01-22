/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.vasinterface;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasRequest;
import com.tatsinktechnologic.ussd_gateway.ussdapp.config.DBConfig;
import com.tatsinktechnologic.ussd_gateway.ussdapp.mina.tcp.TCPConnector;
import com.tatsinktechnologic.ussd_gateway.ussdapp.vasinterface.tcp.Vas_TCPConnector;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VasConnectorMgr
{
  private DBConfig dbConfig;
  private HashMap<Integer, VasConnector> connectors;
  
  public VasConnectorMgr(DBConfig dbConfig)
    throws Exception
  {
    this.dbConfig = dbConfig;
    

    ResultSet result = dbConfig.getData("select * from CP");
    
    Iterator  i$ = null;
    this.connectors = new HashMap();
    try
    {
      while (result.next())
      {
        int id = result.getInt("ID");
        if (this.connectors.containsKey(Integer.valueOf(id))) {
          throw new Exception("same connector id: " + id);
        }
        String name = result.getString("NAME");
        

        String configFile = result.getString("CONFIG_FILE");
        

        String rule = result.getString("RULE");
        if (rule == null) {
          rule = "";
        }
        VasConnector con = new VasConnector(id, name, configFile, rule);
        
        this.connectors.put(Integer.valueOf(id), con);
      }
      try
      {
        result.close();
      }
      catch (SQLException ex) {}
    i$ = this.connectors.values().iterator();
    }
    finally
    {
      try
      {
        result.close();
      }
      catch (SQLException ex) {}
    }

    while (i$.hasNext())
    {
      VasConnector conn = (VasConnector)i$.next();
      if ((conn.haveBkConnector()) && 
        (!this.connectors.containsKey(Integer.valueOf(conn.getBakConId())))) {
        throw new Exception("Connector " + conn.getId() + " have invalid backup id: " + conn.getBakConId());
      }
    }
  }
  
  public void setStoreQueue(BlockQueue storeQ)
  {
    for (Vas_TCPConnector con : this.connectors.values()) {
      con.setQueue(storeQ);
    }
  }
  
  public Vas_TCPConnector getConnector(int connectorId)
  {
    return (Vas_TCPConnector)this.connectors.get(Integer.valueOf(connectorId));
  }
  
  public Vas_TCPConnector getConnector(VasRequest req)
  {
    if (req.getConnectorId() > 0) {
      return (Vas_TCPConnector)this.connectors.get(Integer.valueOf(req.getConnectorId()));
    }
    for (VasConnector con : this.connectors.values()) {
      if (con.isRegisted(req)) {
        return con;
      }
    }
    return null;
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder("connector list: ");
    buf.append(this.connectors.size());
    buf.append(" connector(s)\r\n");
    for (Map.Entry<Integer, VasConnector> entry : this.connectors.entrySet())
    {
      buf.append(entry.getValue());
      buf.append("\r\n___Rule: ");
      buf.append(((VasConnector)entry.getValue()).getRule());
      buf.append("\r\n\r\n");
    }
    return buf.toString();
  }
  
  public void start()
  {
    for (Vas_TCPConnector con : this.connectors.values()) {
      con.start();
    }
  }
  
  public void stop()
  {
    for (Vas_TCPConnector con : this.connectors.values()) {
      con.stop();
    }
  }
  
  public void reloadConnectorRules()
    throws Exception
  {
    synchronized (this.dbConfig.getLock())
    {
      this.dbConfig.reconnectDb();
      try
      {
        ResultSet result = this.dbConfig.getData("select * from CP");
        try
        {
          while (result.next())
          {
            int id = result.getInt("ID");
            VasConnector con = (VasConnector)this.connectors.get(Integer.valueOf(id));
            if (con != null)
            {
              String rule = result.getString("RULE");
              if (rule == null) {
                rule = "";
              }
              con.setRule(rule);
            }
          }
          try
          {
            result.close();
          }
          catch (SQLException ex) {}
        }
        finally
        {
          try
          {
            result.close();
          }
          catch (SQLException ex) {}
        }
      }
      finally
      {
        this.dbConfig.disconnect();
      }
    }
  }
}