/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.transaction;

/**
 *
 * @author olivier.tatsinkou
 */
import java.util.TimerTask;

public class UssdTimeoutTask
  extends TimerTask
{
  private String transId;
  private TransactionMgr transMgr;
  
  public UssdTimeoutTask(String transId, TransactionMgr transMgr)
  {
    this.transId = transId;
    this.transMgr = transMgr;
  }
  
  public void run()
  {
    this.transMgr.removeTrans(this.transId);
  }
}

