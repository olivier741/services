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
import java.util.Date;

public abstract interface SchedulerMBean
{
  public abstract void start();
  
  public abstract void stop();
  
  public abstract void restart();
  
  public abstract int removeTask(int paramInt);
  
  public abstract int addTask(String paramString1, String paramString2, String paramString3, Date paramDate1, Date paramDate2, Date paramDate3, int paramInt1, String paramString4, int paramInt2, String paramString5, String paramString6, String paramString7, boolean paramBoolean, int paramInt3, int paramInt4);
  
  public abstract int editTask(int paramInt1, String paramString1, String paramString2, String paramString3, Date paramDate1, Date paramDate2, Date paramDate3, int paramInt2, String paramString4, int paramInt3, String paramString5, String paramString6, String paramString7, boolean paramBoolean, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void print();
}
