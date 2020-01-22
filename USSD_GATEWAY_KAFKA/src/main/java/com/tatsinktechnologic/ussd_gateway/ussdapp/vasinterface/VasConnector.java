/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.vasinterface;

import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasRequest;
import com.tatsinktechnologic.ussd_gateway.ussdapp.vasinterface.tcp.Vas_TCPConnector;
import com.tatsinktechnologic.ussd_gateway.utils.Rule;

/**
 *
 * @author olivier.tatsinkou
 */


public class VasConnector  extends Vas_TCPConnector
{
  private Rule rule;
  
  public VasConnector(int id, String name, String cfgFile, String rule)
    throws Exception
  {
    super(id, name, cfgFile);
    
    this.rule = new Rule();
    this.rule.setExpression(rule);
  }
  
  public boolean isRegisted(VasRequest req)
  {
    synchronized (this.rule)
    {
      this.rule.setVariable("MSISDN", req.getMsisdn());
      this.rule.setVariable("BIZID", Integer.valueOf(req.getBizId()));
      this.rule.setVariable("REQID", req.getId());
      return this.rule.parser();
    }
  }
  
  public void setRule(String expression)
  {
    synchronized (this.rule)
    {
      this.rule.setExpression(expression);
    }
  }
  
  public String getRule()
  {
    synchronized (this.rule)
    {
      return this.rule.getExpression();
    }
  }
}
