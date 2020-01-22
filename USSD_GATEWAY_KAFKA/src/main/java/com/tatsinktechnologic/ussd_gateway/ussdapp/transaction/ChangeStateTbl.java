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
import com.tatsinktechnologic.ussd_gateway.utils.Rule;
import java.util.ArrayList;

public class ChangeStateTbl
{
  private ArrayList<String> parterns;
  private ArrayList<State> states;
  private Rule rule;
  private int size;
  
  public ChangeStateTbl()
  {
    this.parterns = new ArrayList();
    this.states = new ArrayList();
    this.rule = new Rule();
    this.size = 0;
  }
  
  public void put(String partern, State state)
  {
    this.parterns.add(partern);
    this.states.add(state);
    
    this.size += 1;
  }
  
  public synchronized State get(String input)
  {
    this.rule.setVariable("INPUT", input);
    for (int i = 0; i < this.size; i++)
    {
      this.rule.setExpression("INPUT==" + (String)this.parterns.get(i));
      if (this.rule.parser()) {
        return (State)this.states.get(i);
      }
    }
    return null;
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < this.size; i++)
    {
      buf.append((String)this.parterns.get(i));
      buf.append(" --> ");
      buf.append(this.states.get(i));
      buf.append("\r\n");
    }
    return buf.toString();
  }
  
  public boolean isEmpty()
  {
    return this.size == 0;
  }
}
