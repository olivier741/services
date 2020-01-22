/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.scheduler;

/**
 *
 * @author olivier.tatsinkou
 */
public class RepeatedTask
{
  private int repeatedRecurrencePeriod;
  private int repeatedTimes = 0;
  private static final long MILISECOND_PER_MINUTE = 60000L;
  
  public RepeatedTask(int repeatedRecurrenceTimex, int repeatedTimesx)
  {
    this.repeatedRecurrencePeriod = repeatedRecurrenceTimex;
    this.repeatedTimes = repeatedTimesx;
  }
  
  public int getRepeatedRecurrencePeriod()
  {
    return this.repeatedRecurrencePeriod;
  }
  
  public long getRepeatedRecurrencePeriodInMilisecond()
  {
    return this.repeatedRecurrencePeriod * 60000L;
  }
  
  public int getRepeatedTimes()
  {
    return this.repeatedTimes;
  }
  
  public String toString()
  {
    return "Repeated Task: every " + getRepeatedRecurrencePeriodInMilisecond() + " miliseconds util " + this.repeatedTimes + "times";
  }
}

