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
public class State
{
  public static final int BEGIN_STATE = 0;
  public static final int END_STATE_WITHOUT_VAS = 1;
  public static final int NOT_END_STATE_WITHOUT_VAS = 2;
  public static final int END_STATE_WITH_VAS = 3;
  public static final int NOT_END_STATE_WITH_VAS = 4;
  public static final int WAIT_VAS_RSP = 5;
  private static final int USSDGW_TIMEOUT = 45;
  private static final int VAS_TIMEOUT = 30;
  private int id;
  private int stateType;
  private String content;
  private int timeout;
  private int cpId;
  private int bizId;
  private boolean needEncrypt;
  private ChangeStateTbl changeStateTbl;
  private State defaultState;
  private int defaultStateId;
  private int charSet;
  
  public State(int id, int type, int cpId, int bizId, String content, int timeout, int defaultSate, int encrypted, int charSet)
  {
    this.id = id;
    this.stateType = type;
    this.cpId = cpId;
    this.bizId = bizId;
    this.content = content;
    if (timeout == 0) {
      switch (type)
      {
      case 2: 
      case 4: 
        this.timeout = 45;
        break;
      case 5: 
        this.timeout = 30;
        break;
      case 3: 
      default: 
        this.timeout = timeout;
        break;
      }
    } else {
      this.timeout = timeout;
    }
    this.defaultStateId = defaultSate;
    this.needEncrypt = (encrypted == 1);
    
    this.changeStateTbl = null;
    
    this.charSet = charSet;
  }
  
  public boolean notEndState()
  {
    return (this.stateType != 1) && (this.stateType != 3);
  }
  
  public State getDefaultState()
  {
    return this.defaultState;
  }
  
  public int getBizId()
  {
    return this.bizId;
  }
  
  public int getCpId()
  {
    return this.cpId;
  }
  
  public int getDefaultStateId()
  {
    return this.defaultStateId;
  }
  
  public boolean isNeedEncrypt()
  {
    return this.needEncrypt;
  }
  
  public void setChangeStateTbl(ChangeStateTbl changeStateTbl)
  {
    this.changeStateTbl = changeStateTbl;
  }
  
  public void setDefaultState(State defaultState)
  {
    this.defaultState = defaultState;
  }
  
  public State changeState(String input)
  {
    if (this.changeStateTbl == null) {
      return this.defaultState;
    }
    State next = this.changeStateTbl.get(input);
    if (next == null) {
      next = this.defaultState;
    }
    return next;
  }
  
  public int getStateType()
  {
    return this.stateType;
  }
  
  public int getTimeout()
  {
    return this.timeout;
  }
  
  public String getContent()
  {
    return this.content;
  }
  
  public int getId()
  {
    return this.id;
  }
  
  public String toString()
  {
    return "state " + this.id;
  }
  
  public String printChangeStateTbl()
  {
    StringBuilder buf = new StringBuilder("State ");
    buf.append(this.id);
    buf.append(":\r\n");
    if (this.changeStateTbl != null) {
      buf.append(this.changeStateTbl.toString());
    }
    buf.append("default --> ");
    buf.append(this.defaultState);
    
    return buf.toString();
  }
  
  public int getCharSet()
  {
    return this.charSet;
  }
}