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

import com.tatsinktechnologic.ussd_gateway.ussdapp.common.UssdMessage;
import java.util.Hashtable;

public class TransactionMgr
{
  private Hashtable<String, Transaction> trans;
  private volatile State beginState;
  private CoreProcessModule coreModule;
  
  public TransactionMgr(int initSize)
  {
    this.trans = new Hashtable(initSize);
  }
  
  public void setBeginState(State beginState)
  {
    this.beginState = beginState;
  }
  
  public void setCoreModule(CoreProcessModule coreModule)
  {
    this.coreModule = coreModule;
  }
  
  public int getNumTransaction()
  {
    return this.trans.size();
  }
  
  public Transaction getTransaction(String transId)
  {
    return (Transaction)this.trans.get(transId);
  }
  
  public Transaction makeTransaction(UssdMessage firstMsg)
  {
    Transaction tran = new Transaction(firstMsg.getTransId(), firstMsg.getMsisdn(), firstMsg.getImsi(), this.beginState, firstMsg.getConnectorId());
    

    tran.setTransMgr(this);
    tran.setCoreModule(this.coreModule);
    
    this.trans.put(tran.getId(), tran);
    
    return tran;
  }
  
  public void removeTrans(String transId)
  {
    this.trans.remove(transId);
  }
}

