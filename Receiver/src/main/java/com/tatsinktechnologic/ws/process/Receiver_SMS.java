 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ws.process;


import com.tatsinktechnologic.beans.DeliveryMessage;
import com.tatsinktechnologic.beans.ReceiverResponse;
import com.tatsinktechnologic.beans_entity.Command;
import com.tatsinktechnologic.beans_entity.Configuration;
import com.tatsinktechnologic.beans_entity.Delivery;
import com.tatsinktechnologic.beans_entity.Mo_Message;
import com.tatsinktechnologic.beans_entity.Mo_Message_His;
import com.tatsinktechnologic.beans_entity.Mt_Message;
import com.tatsinktechnologic.beans_entity.Param;
import com.tatsinktechnologic.beans_entity.Process_Unit;
import com.tatsinktechnologic.configuration.XML_ConfigLoader;
import com.tatsinktechnologic.dao.CommandJpaController;
import com.tatsinktechnologic.dao.DeliveryJpaController;
import com.tatsinktechnologic.dao.Mo_MessageJpaController;
import com.tatsinktechnologic.dao.Mo_Message_HisJpaController;
import com.tatsinktechnologic.dao.Mt_MessageJpaController;
import com.tatsinktechnologic.daoUtil.Receiver_DaoUtil;
import com.tatsinktechnologic.ws.handler.ServerPasswordCallback;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import javax.jws.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.tatsinktechnologic.ws.interfaces.Receiver_SMSInterface;
import com.tatsinktechnologic.xml.receiver_listener.Receiver_Listener;
import com.tatsinktechnologic.xml.smsgw_listener.SmsgwClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;
import javax.persistence.EntityManagerFactory;
import net.sf.ehcache.search.parser.ParseException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;



/**
 *
 * @author olivier.tatsinkou
 */

@WebService(serviceName="Receiver_SMS",endpointInterface = "com.tatsinktechnologic.ws.interfaces.Receiver_SMSInterface")
public class Receiver_SMS implements Receiver_SMSInterface{
   
    private static  Properties prop = new Properties();
    private static Logger logger = LogManager.getLogger(Receiver_SMS.class);
    private SmsgwClient smsgw_client= null;
    private Receiver_Listener receiver_listener;
    private XML_ConfigLoader xmlConfig ;
    private String callerIpAddress;
    private EntityManagerFactory emf;
     
    private static int numberSession = 0;
    private Process_Unit process_unit;
    private CommandJpaController command_Controller;
    private Mo_MessageJpaController mo_message_controller;
    private Mt_MessageJpaController mt_message_controller;
    private Mo_Message_HisJpaController mo_message_his_controller;
    private DeliveryJpaController deliveryController;
    private List<Command> listComd;
    private List<Command> listComd_ckeck;
    private HashMap<String,Configuration> configuration_set;
    private String smpp_dateFormat;
    
    private static String PARAM_SYNTAX_ERROR = "SYNTAX_ERROR_{short_code}";

    @Resource
    WebServiceContext webServiceContext; 

    public Receiver_SMS(Process_Unit process_unit) {
        this.xmlConfig = XML_ConfigLoader.getConfigurationLoader();
        this.receiver_listener=xmlConfig.getReceiver_listener(); 
        this.smpp_dateFormat = receiver_listener.getSmspp_dataFormat();
        this.emf = xmlConfig.getEmf();
        this.process_unit=process_unit;
        
        this.configuration_set=Receiver_DaoUtil.getConfiguration_ProcessUnit(emf,process_unit);
        this.command_Controller = new CommandJpaController(emf);
        this.mo_message_controller = new Mo_MessageJpaController(emf);
        this.mo_message_his_controller = new Mo_Message_HisJpaController(emf);
        this.mo_message_his_controller=new Mo_Message_HisJpaController(emf);
        this.mt_message_controller=new Mt_MessageJpaController(emf);
        this.deliveryController = new DeliveryJpaController(emf);
        
        

    }

    public ReceiverResponse receive_sms(@WebParam(name = "Sender") String sender,@WebParam(name = "Receiver") String receiver,@WebParam(name = "MsgContent") String content){
        ReceiverResponse resp = null;
        smsgw_client= ServerPasswordCallback.getsmsgw_client();
        int status = -1 ;
        logger.info("#################### START RECEIVE SMS ############################");
        if (checkIP(smsgw_client.getClient_IP())){

            boolean ismo = receiver_listener.checkMo(content, receiver, sender);           
            if (ismo){
                 // insert MO table


                    java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());

                    logger.info("SMS Receive From SMSGW : "+sender+" --> "+receiver+"( msg ="+content+" | IP ="+callerIpAddress+" )");

                    listComd= command_Controller.findCommandEntities();

                    if (listComd!= null){

                        listComd_ckeck = checkCommand(content,listComd);

                        if (listComd_ckeck!=null&&listComd_ckeck.size()>0){

                         Mo_Message mo_message = new Mo_Message();
                         mo_message.setMsisdn(sender);
                         mo_message.setShortcode_channel(receiver);
                         mo_message.setReceive_date(date);
                         mo_message.setMessage(content);
                         mo_message.setCommand(listComd_ckeck.get(0));
                         mo_message_controller.create(mo_message);

                         logger.info("SUCCESSFUL PROCESS MESSAGE : "+mo_message);
                         
                         status = 0;

                        }else {
                            
                            Mo_Message_His mo_message_his= new Mo_Message_His();
                            mo_message_his.setMsisdn(sender);
                            mo_message_his.setMo_message(content);
                            mo_message_his.setProcess_date(date);
                            mo_message_his.setProcess_unit(process_unit);
                            mo_message_his.setReceive_date(date);
                            mo_message_his.setShortcode_channel(receiver);  

                            PARAM_SYNTAX_ERROR = PARAM_SYNTAX_ERROR.replace("{short_code}", receiver);
                            Configuration conf_error = (Configuration)configuration_set.get(PARAM_SYNTAX_ERROR);
                             
                            mo_message_his_controller.create(mo_message_his);
                            logger.info("SET MESSAGE IN HISTORY :"+mo_message_his);
                            
                            if (conf_error!=null){
                                Mt_Message mt_message = new Mt_Message();
                                mt_message.setMsisdn(sender);                           
                                mt_message.setProcess_date(date);
                                mt_message.setShortcode_channel(receiver);                               
                                mt_message.setMessage(conf_error.getProcess_value()); 
                                
                                mt_message_controller.create(mt_message);
                                logger.info("SENT MT MESSAGE :"+mt_message);
                                status = 1;
                            }else{
                               logger.error("not message for this command"); 
                               status = 2;
                            }
                        }
                }else{
                    logger.info("command not configure in database.");
                }

                 logger.info("##########################################");

                if (status == 0){
                    resp = new ReceiverResponse("SUCCESS_SMS_CONTENT  ",status, "SMS Content have been send successfully");
                    logger.info("SUCCESS_SMS_CONTENT");
                    logger.info("SUCCESS Submition of : "+ sender+ " --> " +receiver +" ( msg ="+content+" )");
                }else {

                    resp = new ReceiverResponse("WARN_SMS_COMMAND  ",status, "COMMAND not exist");
                    logger.info("WARN_SMS_COMMAND");
                    logger.info("WRONG COMMAND receiver from : "+ sender+ " --> " +receiver +" ( msg ="+content+" )");
                }

            }else{
                status = -1;
                resp = new ReceiverResponse("ERROR_SMS_CONTENT",status, "COMMAND is not allow");
                logger.error("ERROR_SMS_CONTENT: Service not allow  : "+ sender+ " --> " +receiver +" ( msg ="+content+" )");
            }

        }else{
            status = -2;
            resp = new ReceiverResponse("ERROR_HOST_Auth",status, "Your IP address is allow");
            logger.error("ERROR_HOST_Auth: Your IP address is allow in the service : "+ sender+ " --> " +receiver +" ( msg ="+content+" )");
        }           
        logger.info("#################### END RECEIVE SMS ############################");
        return resp;
    }
    
    
    public ReceiverResponse receive_delivery(@WebParam(name = "delivery") DeliveryMessage delivery_msg){
        ReceiverResponse resp = null;
        smsgw_client= ServerPasswordCallback.getsmsgw_client();
        int status = -1 ;
        logger.info("#################### START RECEIVE DELIVERY ############################");
        if (checkIP(smsgw_client.getClient_IP())){
            
            Delivery delivery= new Delivery();
            delivery.setDelivery_dlvrd(Integer.parseInt(delivery_msg.getDelivery_dlvrd()));
            
            java.sql.Date sqlDate = new java.sql.Date(convertToDate(delivery_msg.getDelivery_doneDate()).getTime());

            delivery.setDelivery_doneDate(sqlDate);
            
            delivery.setDelivery_err(delivery_msg.getDelivery_err());
            delivery.setDelivery_id(delivery_msg.getDelivery_id());
            delivery.setDelivery_status(delivery_msg.getDelivery_status());
            delivery.setDelivery_sub(Integer.parseInt(delivery_msg.getDelivery_sub()));
            
            sqlDate = new java.sql.Date(convertToDate(delivery_msg.getDelivery_submitDate()).getTime());
            
            delivery.setDelivery_submitDate(sqlDate);
            delivery.setDelivery_text(delivery_msg.getDelivery_text());
            delivery.setMessage_id(delivery_msg.getMessage_id());
            delivery.setReceiver(delivery_msg.getReceiver());
            delivery.setSender(delivery_msg.getSender());
            
            
            deliveryController.create(delivery);
            

            resp = new ReceiverResponse("SUCCESS_DELIVERY_CONTENT  ",0, "SMS Content have been send successfully");
            logger.info("SUCCESS_DELIVERY_CONTENT : " +delivery_msg);

        }else{
            status = 1;
            resp = new ReceiverResponse("ERROR_HOST_Auth",status, "Your IP address is not allow");
           logger.error("ERROR_HOST_Auth: Your IP address is not allow to get this report : "+ delivery_msg);
        }           
        logger.info("#################### END RECEIVE DELIVERY ############################");
        return resp;
    }

     
     private boolean checkIP(String IP_address) { 
       
        boolean result = false;
        List<String> listIP= Arrays.asList(IP_address.split(","));

        MessageContext messageContext = webServiceContext.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST); 
        callerIpAddress = request.getRemoteAddr();

        logger.info("Client IP Address = " + callerIpAddress); 

        if (listIP.contains(callerIpAddress)) result= true;
        return result;
   }
     
     
          // filter command
       private List<Command> checkCommand(String content,List<Command> listCommnad){
                
        List<Command> result =new ArrayList<Command>();
        for(Command cmd : listCommnad){
            result.add(cmd);
        }
         
        CollectionUtils.filter(result, new Predicate() {

            @Override
            public boolean evaluate(Object o) {
                
                String comd = ((Command) o).getCommand_name().toUpperCase();
                String split_sep = ((Command) o).getSplit_separator();
                Set<Param> setParam = ((Command) o).getListParam();
                
                List<String> listContent =  Arrays.asList(content.split(split_sep));
                String content_command = listContent.get(0);
                int number_content = listContent.size();
                
                Pattern pattern_comd = Pattern.compile(comd);
               
                
                if (pattern_comd.matcher(content_command.trim().toUpperCase()).find()&& setParam!=null){
                    int number_param = setParam.size();
                    if (number_param == (listContent.size() -1) ){
                        
                        Pattern pattern_param = null;
                        boolean match_param;
                        
                        int i = 1;
                        Param currentParam=null;
                        
                        for(Param param : setParam){
                           pattern_param = Pattern.compile(param.getParam_name().toUpperCase());
                           match_param = pattern_param.matcher(listContent.get(i).toUpperCase()).find();
                           currentParam = param;
                           if (!match_param) break;
                           i++;
                        }
                        
                        if (i==number_param) {
                            logger.info("SUCCESS COMMAND :"+listContent.toString().toUpperCase());
                            return true;
                        }
                        else{
                            logger.info("COMMAND = "+comd+" : customer parameter = "+listContent.get(i).toUpperCase()+" not match with real parameter = "+currentParam.getParam_name());
                            return false;
                        }
                        
                    }else{
                        logger.info("COMMAND = "+comd+" : number of customer parameter  not match with real number of  parameter");
                        return false;
                    } 
                }else if (pattern_comd.matcher(content_command.trim().toUpperCase()).find()&& setParam==null){
                    if (number_content ==1){
                         logger.info("SUCCESS COMMAND :"+listContent.toString().toUpperCase());
                         return true;
                    }
                    else{
                        logger.warn("COMMAND = "+comd+" : this command don't have parameter");
                        return false;
                    }
                } else{
                    logger.info("COMMAND = "+comd+" : wong command");
                    return false;
                }
            }

        });
        
        return result;
    }
       
     private Date convertToDate(String dateValue){
          Date date =null;
          SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmm");
          if (smpp_dateFormat!=null){
              formatter = new SimpleDateFormat(smpp_dateFormat);
              try {
                date = formatter.parse(dateValue);
            } catch (Exception e) {
               logger.error("cannot convert "+ dateValue + " to date following format : "+smpp_dateFormat);
               formatter = new SimpleDateFormat("yyMMddHHmm");
               try {
                   date = formatter.parse(dateValue);
               } catch (Exception e1) {
                   logger.error("cannot convert "+ dateValue + " to date following format yyMMddHHmm",e1);
               }
            }
          }else {
           try {
                date = formatter.parse(dateValue);
            } catch (Exception e) {
                logger.error("cannot convert "+ dateValue + " to date following format yyMMddHHmm",e);
            }
          }

      return date;
     }
     
}

