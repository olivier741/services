/*
 * Copyright 2019 olivier.tatsinkou.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viettel.mmserver.scheduler;

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