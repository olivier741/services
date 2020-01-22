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

