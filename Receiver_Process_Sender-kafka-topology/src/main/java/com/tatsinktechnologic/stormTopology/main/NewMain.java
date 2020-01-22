/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.stormTopology.main;

import com.tatsinktechnologic.beans.Notification_Conf;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
import com.fasterxml.uuid.Generators;

/**
 *
 * @author olivier.tatsinkou
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    
    private static HashMap<String,Notification_Conf> SET_NOTIFICATION = null;
    
    public static void main(String[] args) {
        // TODO code application logic here
        
//         Calendar now = Calendar.getInstance();
//                int hour = now.get(Calendar.HOUR);
//                int minute = now.get(Calendar.MINUTE);
//                int seconde = now.get(Calendar.SECOND);
//
//                Calendar date1 = Calendar.getInstance();
//                date1.set(Calendar.HOUR_OF_DAY, date1.getTime().getHours() );
//                date1.set(Calendar.MINUTE, date1.getTime().getMinutes() );
//                date1.set(Calendar.SECOND, date1.getTime().getSeconds());
//
//                String hour_value = "08:15:03";
//                String[] parts = hour_value.split(":");
//                Calendar date2 = Calendar.getInstance();
//                date2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
//                date2.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
//                date2.set(Calendar.SECOND, Integer.parseInt(parts[2]));
//
//                if (date1.before(date2)) {
//                    System.out.println("current time is :"+hour+" : "+minute+" : "+seconde);
//                    System.out.println("Date 1 before time is :"+date1);
//                    System.out.println("Date 2 before time is :"+date2);
//                }else{
//                    System.out.println("current time is :"+hour+" : "+minute+" : "+seconde);
//                    System.out.println("Date 1  after time is :"+date1);
//                    System.out.println("Date 2  after time is :"+date2);
//                }
//                Timestamp paid_time = new Timestamp(System.currentTimeMillis());
//                String validity = "D15";
//                System.out.println("expirere time day  :"+getExpiret_Time(validity,paid_time));
//                
//                String validity1 = "H15";
//                System.out.println("expirere tim  :"+getExpiret_Time(validity1,paid_time));
//                
//                String validity2 = "H50";
//                System.out.println("expirere tim  :"+getExpiret_Time(validity2,paid_time));
//                
//                String validity3 = "H72";
//                System.out.println("expirere tim  :"+getExpiret_Time(validity3,paid_time));
//                
//                SET_NOTIFICATION = ConnectionPool.getSET_NOTIFICATION();
//                
//                ConnectionPool.getCOMMAND_CONF_HASH();
                
//                int num = 4;
//               generateRandomUuid(num);
               generateRandomUuid();
                
                
    }
    
    
     private static Timestamp getExpiret_Time(String validity,Timestamp paid_time){
            Timestamp result = new Timestamp(System.currentTimeMillis());
            Calendar cal = Calendar.getInstance();
            cal.setTime(paid_time);
            if (validity.contains("D")){
                int numberDay= Integer.parseInt(validity.replace("D", ""));
                cal.add(Calendar.DAY_OF_WEEK, numberDay);
            }else if (validity.contains("H")){
                int numberHour = Integer.parseInt(validity.replace("H", ""));
                if (numberHour < 24){
                   cal.add(Calendar.HOUR_OF_DAY, numberHour);  
                }else if (numberHour >24){
                    int numberDay = numberHour/24;
                    cal.add(Calendar.DAY_OF_WEEK, numberDay);
                    int numberHour1 = numberHour%24;
                    cal.add(Calendar.HOUR_OF_DAY, numberHour1);
                }else{
                   cal.add(Calendar.HOUR_OF_DAY, 1); 
                }
                
            }
            result = new Timestamp(cal.getTime().getTime());
            
            return result;
        }
     
     
      public static void generateRandomUuid(int num) {
 
        /***** Generate Version 4 UUID - Randomly Generated UUID *****/
        UUID uid = null;
        for(int i=1; i<num; i++) {
 
            /***** Generating Random UUID's *****/
            uid= UUID.randomUUID();
            System.out.println("Generated UUID?= " + uid.toString() + ", UUID Version?= " + uid.version() + ", UUID Variant?= " + uid.variant() + "\n");
        }
    }
      
      
    public static void generateRandomUuid() {
 
        /***** Generate Version 1 UUID - Time-Based UUID *****/
        UUID uuid1 = Generators.timeBasedGenerator().generate();
        
        System.out.println("Generated Version 1 UUID?= " + uuid1.toString() + ", UUID Version?= " + uuid1.version() + ", UUID Variant?= " + uuid1.version() + "\n");
 
        /***** Generate Version 4 UUID - Randomly Generated UUID *****/
        UUID uuid2 = Generators.randomBasedGenerator().generate();
        System.out.println("Generated Version 4 UUID?= " + uuid2.toString() + ", UUID Version?= " + uuid2.version() + ", UUID Variant?= " + uuid2.version());
    }
}
