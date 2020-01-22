/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ws.client;


import com.tatsinktechnologic.beans.DeliveryMessage;
import com.tatsinktechnologic.beans.ReceiverResponse;
import com.tatsinktechnologic.configuration.XML_ConfigLoader;
import com.tatsinktechnologic.ws.handler.ClientPasswordCallback;
import com.tatsinktechnologic.ws.interfaces.Receiver_SMSInterface;
import com.tatsinktechnologic.ws.interfaces.Sender_SMSInterface;
import com.tatsinktechnologic.xml.user_listener.Failover;
import com.tatsinktechnologic.xml.user_listener.Rcv_Address;
import com.tatsinktechnologic.xml.user_listener.Rcv_User_Connection;
import com.tatsinktechnologic.xml.user_listener.Receiver_User_Listener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cxf.clustering.CircuitSwitcherClusteringFeature;
import org.apache.cxf.clustering.SequentialStrategy;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
/**
 *
 * @author olivier.tatsinkou
 */
public class SOAP_Listener {

    /**
     * @param args the command line arguments
     */
    private static Logger logger = LogManager.getLogger(SOAP_Listener.class);
    private XML_ConfigLoader xmlConfig;
    private Receiver_User_Listener rcv_user_listener;
    private static Failover failover;
    private static JaxWsProxyFactoryBean factory;
    private Receiver_SMSInterface clientRcvSMS;

    public SOAP_Listener(Rcv_User_Connection userConn) {
        xmlConfig=XML_ConfigLoader.getConfigurationLoader();
        rcv_user_listener = xmlConfig.getRcv_user_listener();
        failover= rcv_user_listener.getFailover();
        
        factory = new JaxWsProxyFactoryBean();
        List<Rcv_Address> list_userAddres = userConn.getListaddress();
        
        String user_token = userConn.getService_id();
        
        List<String> addresses =new ArrayList<String>();
        List<Rcv_Address> listaddress = userConn.getListaddress();  
        
        for (Rcv_Address address : listaddress){
                addresses.add("http://"+address.getIP()+":"+address.getPort()+"/receiver");
         }

        Map outProps = new HashMap();
        outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        // Specify our username
        outProps.put(WSHandlerConstants.USER, user_token);
        if (userConn.getSecurity_mode()==0){
             // Password type : plain text
            outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        }else{
              // for hashed password use:
            outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
        }
   
      // Callback used to retrieve password for given user.
        outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS,ClientPasswordCallback.class.getName());

        factory.getOutInterceptors().add(new LoggingOutInterceptor());

        factory.getOutInterceptors().add( new WSS4JOutInterceptor( outProps ) );

        factory.getInInterceptors().add(new LoggingInInterceptor());

        factory.setServiceClass(Sender_SMSInterface.class);

        // load balancing and failover
        CircuitSwitcherClusteringFeature cbcFeature = new CircuitSwitcherClusteringFeature();

        SequentialStrategy seqtrategy = new SequentialStrategy();
        seqtrategy.setAlternateAddresses(addresses);
        seqtrategy.setDelayBetweenRetries(failover.getDelayBetweenRetries());
        
        cbcFeature.setStrategy(seqtrategy);
        cbcFeature.setAddressList(addresses);
        cbcFeature.setFailureThreshold(failover.getFailureThreshold());
        cbcFeature.setResetTimeout(failover.getResetTimeout());
        cbcFeature.setReceiveTimeout((long)failover.getReceiveTimeout());
        cbcFeature.setTargetSelector(new org.apache.cxf.clustering.LoadDistributorTargetSelector());
        
        List<Feature> listFeature = new ArrayList<Feature>();
        listFeature.add(cbcFeature);
        factory.setFeatures(listFeature);
        clientRcvSMS = factory.create(Receiver_SMSInterface.class);
        
    }
    
    public ReceiverResponse Send_to_receiver(String sender , String receiver ,String content){
        
        return clientRcvSMS.receive_sms(sender, receiver, content);
    }
    
    
    public ReceiverResponse Send_to_report(DeliveryMessage delivery_msg){
        return clientRcvSMS.receive_delivery(delivery_msg);
    }
    
    
   
}
