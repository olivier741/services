/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao;

import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.beans.Command_Param;
import com.tatsinktechnologic.beans.Command_Request;
import com.tatsinktechnologic.beans.Notification_Conf;
import com.tatsinktechnologic.beans.Product;
import com.tatsinktechnologic.beans.Sub_Register;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier.tatsinkou
 */
public class ConnectionPool {
    
    private static Logger logger = Logger.getLogger(ConnectionPool.class);
    private static HashMap<String,Process_Request> COMMAND_CONF_HASH = new HashMap<String,Process_Request>() ;
    private static HashMap<String,Product> SET_PRODUCT = new HashMap<String,Product>() ;
    private static List<Command_Request> LIST_COMMAND_REQUEST = new ArrayList<Command_Request>();
    private static List<Command_Param> LIST_COMMAND_PARAM = new ArrayList<Command_Param>();
    private static HashMap<String,Notification_Conf> SET_NOTIFICATION = new HashMap<String,Notification_Conf>();
    
    private final static String SQL_SELECT_COMMAND_CONF = " SELECT a.command_id, \n" +
                                                        "       a.command_code, \n" +
                                                        "       a.param_pattern_separate, \n" +
                                                        "       a.channel,  \n"+
                                                        "       a.product_name, \n" +
                                                        "       b.syntax_id, \n" +
                                                        "       c.action_id, \n" +
                                                        "       c.action_type, \n" +
                                                        "       c.description, \n" +
                                                        "       d.param_id, \n" +
                                                        "       d.param_name, \n" +
                                                        "       d.param_length, \n" +
                                                        "       d.param_pattern \n" +
                                                        " FROM command_request a \n" +
                                                        " LEFT JOIN command_syntax b on a.command_id=b.command_id \n" +
                                                        " LEFT JOIN command_action c on b.action_id=c.action_id \n" +
                                                        " LEFT JOIN comamd_param d on b.param_id=d.param_id ";

    private final static String SQL_SELECT_COMMAND_REQUEST = "SELECT command_id,command_code, param_pattern_separate, channel FROM command_request ";
    
    private final static String SQL_SELECT_COMMAND_PARAM = " SELECT param_id,param_name,param_length,param_pattern FROM comamd_param ";
    
    private final static String SQL_INSERT_MO_HIS= " INSERT INTO mo_his (msisdn,content_receive,channel,command,param,receive_time,action_type,process_time,err_code,fee,Processing_Bolt) VALUES (?,?,?,?,?,?,?,sysdate(),?,?,?) ";

    private final static String SQL_SELECT_PRODUCT = " SELECT product_id,product_name,reg_fee,start_time,end_time,register_time,restrict_product,register_day,isextend,extend_fee,list_allow,pending_time,validity FROM product_config ORDER BY product_id";
    
    private final static String SQL_SELECT_CHECK_ALLOWLIST = " SELECT msisdn FROM ? WHERE msisdn = ? ";
    
    private final static String SQL_SELECT_REGISTER = " SELECT id,msisdn,product_name,fee,start_time,expire_time,end_time,paid_time,validity,status,auto_extend,restrict_product FROM register WHERE status = 1 AND msisdn = ? ORDER BY id ";
    
    private final static String SQL_INSERT_REGISTER = " INSERT INTO register (msisdn,product_name,fee,register_time,start_time,expire_time,end_time,paid_time,validity,status,auto_extend,restrict_product) VALUES (?,?,?,sysdate(),?,?,?,?,?,?,?,?) ";
    
    private final static String SQL_DELETE_REGISTER = " DELETE FROM register WHERE msisdn = ? AND product_name = ? AND status = 1 ";
    
    private final static String SQL_UPDATE_REGISTER = " UPDATE register SET status = -1, delete_time=sysdate() WHERE msisdn = ? AND product_name = ? AND status = 1 ";
    
    private final static String SQL_SELECT_NOTIFICATION= " SELECT notification_id,language, param_name, param_value FROM notification_config ";
    
    private final static String SQL_INSERT_MT_HIS= " INSERT INTO mt_his (msisdn,content_sent,channel,sent_time) VALUES (?,?,?,?) ";
    
    public static HashMap<String, Process_Request> getCOMMAND_CONF_HASH() {
        loadCommand_Conf();
        return COMMAND_CONF_HASH;
    }

    public static List<Command_Request> getLIST_COMMAND_REQUEST() {
        loadCommand_request();
        return LIST_COMMAND_REQUEST;
    }

    public static List<Command_Param> getLIST_COMMAND_PARAM() {
        loadCommand_param();
        return LIST_COMMAND_PARAM;
    }

    public static HashMap<String, Product> getSET_PRODUCT() {
        loadCommand_Product();
        return SET_PRODUCT;
    }

    public static HashMap<String, Notification_Conf> getSET_NOTIFICATION() {
        loadNotification_Conf();
        return SET_NOTIFICATION;
    }
    
    
    
    /*
    get all param
    */
    
   private static void loadCommand_param(){
        Connection connect = null;  
        PreparedStatement preparedStatement = null;
    
           try{

               connect =ConnectionDataSources.getReceiver_DataSource().getConnection();

               preparedStatement = connect.prepareStatement(SQL_SELECT_COMMAND_PARAM);

               // execute select SQL stetement
               logger.info("SQL QUERY : "+preparedStatement.toString());
               ResultSet rs = preparedStatement.executeQuery();
               LIST_COMMAND_PARAM.clear();
               while (rs.next()) {
                     Command_Param cmd_param = new Command_Param();

                     cmd_param.setParam_id(rs.getInt("param_id"));
                     cmd_param.setParam_length(rs.getInt("param_length"));
                     cmd_param.setParam_name(rs.getString("param_name"));
                     cmd_param.setParam_pattern(rs.getString("param_pattern"));

                     LIST_COMMAND_PARAM.add(cmd_param);
                     
               }
         } catch (Exception e) {
                logger.error("SQL error :",e); 
           } finally {
               if (preparedStatement != null) 
                  try { 
                      preparedStatement.close(); 
                  } catch (SQLException e) 
                  {
                       logger.error("cannot close preparedStatement :",e); 
                  }
               if (connect != null) 
                   try { 
                       connect.close(); 
                   } catch (SQLException e) {
                      logger.error("cannot close connection1 :",e);
                   }
           }
   }
    
   /*
   get all command
   */
   private static void loadCommand_request(){
        Connection connect = null;  
        PreparedStatement preparedStatement = null;
    
           try{

               connect =ConnectionDataSources.getReceiver_DataSource().getConnection();

               preparedStatement = connect.prepareStatement(SQL_SELECT_COMMAND_REQUEST);

               logger.info("SQL QUERY : "+preparedStatement.toString());
               // execute select SQL stetement
               ResultSet rs = preparedStatement.executeQuery();
               LIST_COMMAND_REQUEST.clear();
               while (rs.next()) {
                     Command_Request cmd_req = new Command_Request();

                     cmd_req.setChannel(rs.getString("channel"));
                     cmd_req.setCommand_code(rs.getString("command_code"));
                     cmd_req.setCommand_id(rs.getInt("command_id"));
                     cmd_req.setParam_pattern_separate(rs.getString("param_pattern_separate"));

                     LIST_COMMAND_REQUEST.add(cmd_req);
                     
               }
         } catch (Exception e) {
                logger.error("SQL error :",e); 
           } finally {
               if (preparedStatement != null) 
                  try { 
                      preparedStatement.close(); 
                  } catch (SQLException e) 
                  {
                       logger.error("cannot close preparedStatement :",e); 
                  }
               if (connect != null) 
                   try { 
                       connect.close(); 
                   } catch (SQLException e) {
                      logger.error("cannot close connection1 :",e);
                   }
           }
   }
   
   /*
   get all command link with is action and  param
   */
   private static void loadCommand_Conf(){
        Connection connect = null;  
        PreparedStatement preparedStatement = null;
    
           try{

               connect =ConnectionDataSources.getReceiver_DataSource().getConnection();

               preparedStatement = connect.prepareStatement(SQL_SELECT_COMMAND_CONF);

               logger.info("SQL QUERY : "+preparedStatement.toString());
               // execute select SQL stetement
               ResultSet rs = preparedStatement.executeQuery();
               COMMAND_CONF_HASH.clear();
               while (rs.next()) {
                     Process_Request cmd_conf = new Process_Request();
                     String command = rs.getString("command_code");
                     int param_lenght = rs.getInt("param_length");
                     String param_name = rs.getString("param_name");
                     
                     cmd_conf.setAction_id(rs.getInt("action_id"));
                     cmd_conf.setAction_type(rs.getString("action_type"));
                     cmd_conf.setChannel(rs.getString("channel"));
                     cmd_conf.setCommand_code(command);
                     cmd_conf.setCommand_id(rs.getInt("command_id"));
                     cmd_conf.setAction_description(rs.getString("description"));
                     cmd_conf.setParam_id(rs.getInt("param_id"));
                     cmd_conf.setParam_length(param_lenght);
                     cmd_conf.setParam_name(param_name);
                     cmd_conf.setParam_pattern(rs.getString("param_pattern"));
                     cmd_conf.setParam_pattern_separate(rs.getString("param_pattern_separate"));
                     cmd_conf.setSyntax_id(rs.getInt("syntax_id"));
                     cmd_conf.setProduct_name(rs.getString("product_name"));
                     
                     String key_cmd=command;
                     
                     COMMAND_CONF_HASH.put(key_cmd, cmd_conf);
                     
               }
         } catch (Exception e) {
                logger.error("SQL error :",e); 
           } finally {
               if (preparedStatement != null) 
                  try { 
                      preparedStatement.close(); 
                  } catch (SQLException e) 
                  {
                       logger.error("cannot close preparedStatement :",e); 
                  }
               if (connect != null) 
                   try { 
                       connect.close(); 
                   } catch (SQLException e) {
                      logger.error("cannot close connection1 :",e);
                   }
           }

   }
  
  /*
   get all notification message
   */
   private static void loadNotification_Conf(){
        Connection connect = null;  
        PreparedStatement preparedStatement = null;
    
           try{

               connect =ConnectionDataSources.getReceiver_DataSource().getConnection();

               preparedStatement = connect.prepareStatement(SQL_SELECT_NOTIFICATION);

               logger.info("SQL QUERY : "+preparedStatement.toString());
               // execute select SQL stetement
               ResultSet rs = preparedStatement.executeQuery();
               SET_NOTIFICATION.clear();
               while (rs.next()) {
                     Notification_Conf notif = new Notification_Conf();
                     String param_name = rs.getString("parma_name");
                     String language = rs.getString("language");  
                     
                     String key_notif = param_name+"_"+language;
                     
                     notif.setNotification_id(rs.getInt("notification_id"));
                     notif.setParam_name(param_name);
                     notif.setLanguage(language);
                     notif.setParam_value(rs.getString("param_value"));
                    
                     SET_NOTIFICATION.put(key_notif.toUpperCase(), notif);
                     
               }
         } catch (Exception e) {
                logger.error("SQL error :",e); 
           } finally {
               if (preparedStatement != null) 
                  try { 
                      preparedStatement.close(); 
                  } catch (SQLException e) 
                  {
                       logger.error("cannot close preparedStatement :",e); 
                  }
               if (connect != null) 
                   try { 
                       connect.close(); 
                   } catch (SQLException e) {
                      logger.error("cannot close connection1 :",e);
                   }
           }

   }
    
   
   
   
   
   
   
   
   
   
   /*
    insert in mo_his
   */
   public static boolean insertMo_his(String msisdn,String content,String channel,String command, String param,Timestamp receive_time,String action_type,String err_code,int fee,String Processing_Bolt){
        Connection connect = null;  
        PreparedStatement preparedStatement = null;
        boolean result = false;
           try{

               connect =ConnectionDataSources.getReceiver_DataSource().getConnection();

               preparedStatement = connect.prepareStatement(SQL_INSERT_MO_HIS);
               preparedStatement.setString(1, msisdn);
               preparedStatement.setString(2, content);
               preparedStatement.setString(3, channel);
               preparedStatement.setString(4, command);
               preparedStatement.setString(5, param);
               preparedStatement.setTimestamp(6, receive_time);
               preparedStatement.setString(7, action_type);
               preparedStatement.setString(8, err_code);
               preparedStatement.setInt(9, fee);
               preparedStatement.setString(10, Processing_Bolt);
               
               logger.info("SQL QUERY : "+preparedStatement.toString());
               preparedStatement.executeQuery();
               
               result = true;
         } catch (Exception e) {
                logger.error("SQL error :",e); 
           } finally {
               if (preparedStatement != null) 
                  try { 
                      preparedStatement.close(); 
                  } catch (SQLException e) 
                  {
                       logger.error("cannot close preparedStatement :",e); 
                  }
               if (connect != null) 
                   try { 
                       connect.close(); 
                   } catch (SQLException e) {
                      logger.error("cannot close connection1 :",e);
                   }
           }
      return result;
   }
   
   
   
   /*
    get all product
   */
   
   private static void loadCommand_Product(){
        Connection connect = null;  
        PreparedStatement preparedStatement = null;
    
           try{

               connect =ConnectionDataSources.getReceiver_DataSource().getConnection();

               preparedStatement = connect.prepareStatement(SQL_SELECT_PRODUCT);

               logger.info("SQL QUERY : "+preparedStatement.toString());
               // execute select SQL stetement
               ResultSet rs = preparedStatement.executeQuery();
               SET_PRODUCT.clear();
               while (rs.next()) {
                     Product product = new Product();
                     String command = rs.getString("product_name");
                     
                     product.setEnd_time(rs.getTimestamp("end_time"));
                     product.setExtend_fee(rs.getInt("extend_fee"));
                     product.setList_allow(rs.getString("list_allow"));
                     product.setPending_time(rs.getString("pending_time"));
                     product.setProduct_id(rs.getInt("product_id"));
                     product.setProduct_name(command);
                     product.setReg_fee(rs.getInt("reg_fee"));
                     product.setRegister_day(rs.getString("register_day"));
                     product.setRegister_time(rs.getString("register_time"));
                     product.setRestrict_product(rs.getString("restrict_product"));
                     product.setStart_time(rs.getTimestamp("start_time"));
                     product.setValidity(rs.getString("validity"));

                    
                     SET_PRODUCT.put(command, product);
                     
               }
         } catch (Exception e) {
                logger.error("SQL error :",e); 
           } finally {
               if (preparedStatement != null) 
                  try { 
                      preparedStatement.close(); 
                  } catch (SQLException e) 
                  {
                       logger.error("cannot close preparedStatement :",e); 
                  }
               if (connect != null) 
                   try { 
                       connect.close(); 
                   } catch (SQLException e) {
                      logger.error("cannot close connection1 :",e);
                   }
           }

   }
   
   /*
     check if msisdn is in table table_name
   */
   
     public static boolean checkAllowToRegister(String table_name, String msisdn){
        Connection connect = null;  
        PreparedStatement preparedStatement = null;
        boolean result = false;
    
           try{

               connect =ConnectionDataSources.getReceiver_DataSource().getConnection();

               preparedStatement = connect.prepareStatement(SQL_SELECT_CHECK_ALLOWLIST);
               preparedStatement.setString(1, table_name);
               preparedStatement.setString(2, msisdn);

               logger.info("SQL QUERY : "+preparedStatement.toString());
               // execute select SQL stetement
               ResultSet rs = preparedStatement.executeQuery();
               if (rs.next()) {
                     String phone = rs.getString("msisdn");
                     if (phone!= null) result = true;
               }
         } catch (Exception e) {
                logger.error("SQL error :",e); 
           } finally {
               if (preparedStatement != null) 
                  try { 
                      preparedStatement.close(); 
                  } catch (SQLException e) 
                  {
                       logger.error("cannot close preparedStatement :",e); 
                  }
               if (connect != null) 
                   try { 
                       connect.close(); 
                   } catch (SQLException e) {
                      logger.error("cannot close connection1 :",e);
                   }
           }
     return result;
   }
     
 
     /*
     get active package of msisdn
     */
  public static  List<Sub_Register> getActiveSub_Reg(String msisdn){
        Connection connect = null;  
        PreparedStatement preparedStatement = null;
        List<Sub_Register> result = null;
           try{

               connect =ConnectionDataSources.getReceiver_DataSource().getConnection();

               preparedStatement = connect.prepareStatement(SQL_SELECT_REGISTER);
               preparedStatement.setString(1, msisdn);
               
               logger.info("SQL QUERY : "+preparedStatement.toString());
               // execute select SQL stetement
               ResultSet rs = preparedStatement.executeQuery();
               result = new ArrayList<Sub_Register>();
               while (rs.next()) {
                     Sub_Register sub_reg = new Sub_Register();
                     sub_reg.setAuto_extend(rs.getInt("auto_extend"));
                     sub_reg.setExpire_time(rs.getTimestamp("expire_time"));
                     sub_reg.setEnd_time(rs.getTimestamp("end_time"));
                     sub_reg.setFee(rs.getInt("fee"));
                     sub_reg.setId(rs.getInt("id"));
                     sub_reg.setMsisdn(rs.getString("msisdn"));
                     sub_reg.setPaid_time(rs.getTimestamp("paid_time"));
                     sub_reg.setProduct_name(rs.getString("product_name"));
                     sub_reg.setRestrict_product(rs.getString("restrict_product"));
                     sub_reg.setStart_time(rs.getTimestamp("start_time"));
                     sub_reg.setStatus(rs.getInt("status"));
                     sub_reg.setValidity(rs.getString("validity"));
                     
                     result.add(sub_reg);
                     
               }
         } catch (Exception e) {
                logger.error("SQL error :",e); 
           } finally {
               if (preparedStatement != null) 
                  try { 
                      preparedStatement.close(); 
                  } catch (SQLException e) 
                  {
                       logger.error("cannot close preparedStatement :",e); 
                  }
               if (connect != null) 
                   try { 
                       connect.close(); 
                   } catch (SQLException e) {
                      logger.error("cannot close connection1 :",e);
                   }
           }
     return result;
   }   
  
  
   public static boolean insertRegister(String msisdn,String product_name,int fee,Timestamp start_time,Timestamp expire_time, Timestamp end_time,Timestamp paid_time, String validity,int status,int auto_extend,String restrict_product){
        Connection connect = null;  
        PreparedStatement preparedStatement = null;
        boolean result = false;
           try{

               connect =ConnectionDataSources.getReceiver_DataSource().getConnection();

               preparedStatement = connect.prepareStatement(SQL_INSERT_REGISTER);
               preparedStatement.setString(1, msisdn);
               preparedStatement.setString(2, product_name);
               preparedStatement.setInt(3, fee);
               preparedStatement.setTimestamp(4, start_time);
               preparedStatement.setTimestamp(5, expire_time);
               preparedStatement.setTimestamp(6, paid_time);
               preparedStatement.setTimestamp(7, end_time);
               preparedStatement.setString(8, validity);
               preparedStatement.setInt(9, status);
               preparedStatement.setInt(10, auto_extend);
               preparedStatement.setString(11, restrict_product);
               
               
               logger.info("SQL QUERY : "+preparedStatement.toString());
               preparedStatement.executeQuery();
               
               result = true;
         } catch (Exception e) {
                logger.error("SQL error :",e); 
           } finally {
               if (preparedStatement != null) 
                  try { 
                      preparedStatement.close(); 
                  } catch (SQLException e) 
                  {
                       logger.error("cannot close preparedStatement :",e); 
                  }
               if (connect != null) 
                   try { 
                       connect.close(); 
                   } catch (SQLException e) {
                      logger.error("cannot close connection1 :",e);
                   }
           }
      return result;
   }
     
   
  public static boolean updateRegister(String msisdn,String product_name){
        Connection connect = null;  
        PreparedStatement preparedStatement = null;
        boolean result = false;
           try{

               connect =ConnectionDataSources.getReceiver_DataSource().getConnection();

               preparedStatement = connect.prepareStatement(SQL_UPDATE_REGISTER);
               preparedStatement.setString(1, msisdn);
               preparedStatement.setString(2, product_name);

               logger.info("SQL QUERY : "+preparedStatement.toString());
               preparedStatement.executeQuery();
               
               result = true;
         } catch (Exception e) {
                logger.error("SQL error :",e); 
           } finally {
               if (preparedStatement != null) 
                  try { 
                      preparedStatement.close(); 
                  } catch (SQLException e) 
                  {
                       logger.error("cannot close preparedStatement :",e); 
                  }
               if (connect != null) 
                   try { 
                       connect.close(); 
                   } catch (SQLException e) {
                      logger.error("cannot close connection1 :",e);
                   }
           }
      return result;
   }
  
  
  
   /*
    insert in mt_his
   */
   public static boolean insertMt_his(String msisdn,String content,String channel,Timestamp sent_time){
        Connection connect = null;  
        PreparedStatement preparedStatement = null;
        boolean result = false;
           try{

               connect =ConnectionDataSources.getReceiver_DataSource().getConnection();

               preparedStatement = connect.prepareStatement(SQL_INSERT_MT_HIS);
               preparedStatement.setString(1, msisdn);
               preparedStatement.setString(2, content);
               preparedStatement.setString(3, channel);
               preparedStatement.setTimestamp(4, sent_time);
               
               logger.info("SQL QUERY : "+preparedStatement.toString());
               preparedStatement.executeQuery();
               
               result = true;
         } catch (Exception e) {
                logger.error("SQL error :",e); 
           } finally {
               if (preparedStatement != null) 
                  try { 
                      preparedStatement.close(); 
                  } catch (SQLException e) 
                  {
                       logger.error("cannot close preparedStatement :",e); 
                  }
               if (connect != null) 
                   try { 
                       connect.close(); 
                   } catch (SQLException e) {
                      logger.error("cannot close connection1 :",e);
                   }
           }
      return result;
   }
}
