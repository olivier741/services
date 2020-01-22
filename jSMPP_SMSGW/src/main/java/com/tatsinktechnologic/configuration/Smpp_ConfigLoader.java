/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Properties;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GSMSpecificFeature;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.InterfaceVersion;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.MessageMode;
import org.jsmpp.bean.MessageType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.ReplaceIfPresentFlag;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class Smpp_ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(Smpp_ConfigLoader.class);
    private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();
    private static Properties prop;
    
    private InterfaceVersion interface_version;
    private String IP_address;
    private int port;
    private BindType bindType;
    private String systemId;
    private String password;
    private String systemType;
    private String service_type;
    private TypeOfNumber addrTon ;
    private NumberingPlanIndicator addrNpi;
    private TypeOfNumber sourceAddrTon; 
    private NumberingPlanIndicator sourceAddrNpi;
    private TypeOfNumber destAddrTon;
    private NumberingPlanIndicator destAddrNpi;
    private ESMClass esmClass;
    private String addressRange;
    private int protocolId;
    private int priorityFlag;
    private String scheduleDeliveryTime;
    private String validityPeriod;
    private ReplaceIfPresentFlag replaceIfPresentFlag;
    private MessageClass messageClass;
    private Alphabet alphabet;
    private DataCoding dataCoding;
    private String charset;
    private int smDefaultMsgId;
    private long receive_timeout;
    private int sms_max_size;
    private MessageMode messageMode;
    private MessageType messageType;
    private GSMSpecificFeature gsmpecificFeature;
        
    private Smpp_ConfigLoader() {
        
        try {
            // set up new properties object from file "smpp.cfg"
            prop = new Properties();
            prop.load(new FileInputStream("etc" + File.separator + "smpp.cfg"));
            
            String interf = prop.getProperty("smpp.interface-version","34");
            if (interf.equals("33"))interface_version = InterfaceVersion.IF_33;
            else if (interf.equals("50"))interface_version = InterfaceVersion.IF_50;
            else interface_version = InterfaceVersion.IF_33;
            
            IP_address = prop.getProperty("smpp.ip-address","localhost");
            port = Integer.parseInt(prop.getProperty("smpp.port","2775"));
            
            String bind =prop.getProperty("smpp.bind-mode","tr");
            
            if (bind.equals("t")) bindType = BindType.BIND_TX;
            else if (bind.equals("r")) bindType = BindType.BIND_RX;
            else bindType = BindType.BIND_TRX;
            
            systemId = prop.getProperty("smpp.system-id","defaut_user");
            service_type=prop.getProperty("smpp.service-type",null);
            password = prop.getProperty("smpp.password","defaut_password");
            systemType=prop.getProperty("smpp.system-type","SMSGW");
            
            int msgMode = Integer.parseInt(prop.getProperty("smpp.MessageMode","0"));
            
             switch (msgMode){
                case 1:
                    messageMode = MessageMode.DATAGRAM;
                    break;
                case 2:
                   messageMode = MessageMode.STORE_AND_FORWARD;
                    break;
                case 3:
                    messageMode = MessageMode.TRANSACTION;
                    break;
                default:
                    messageMode = MessageMode.DEFAULT;
                    
             }
             
             int msgType = Integer.parseInt(prop.getProperty("smpp.MessageType","0"));
             
             switch (msgType){
                case 1:
                    messageType = MessageType.CONV_ABORT;
                    break;
                case 2:
                   messageType = MessageType.ESME_DEL_ACK;
                    break;
                case 3:
                    messageType = MessageType.ESME_MAN_ACK;
                    break;
                case 4:
                    messageType = MessageType.INTER_DEL_NOTIF;
                    break;
                case 5:
                    messageType = MessageType.SME_DEL_ACK;
                    break;
                case 6:
                    messageType = MessageType.SME_MAN_ACK;
                    break;
                case 7:
                    messageType = MessageType.SMSC_DEL_RECEIPT;
                    break;
                default:
                     messageType = MessageType.DEFAULT;
                    
             }
             
             
             int gsmpecFeature = Integer.parseInt(prop.getProperty("smpp.GSMSpecificFeature","2"));
            
             switch (gsmpecFeature){
                case 1:
                    gsmpecificFeature = GSMSpecificFeature.REPLYPATH;
                    break;
                case 2:
                    gsmpecificFeature = GSMSpecificFeature.UDHI;
                    break;
                case 3:
                     gsmpecificFeature = GSMSpecificFeature.UDHI_REPLYPATH;
                    break;
                default:
                     gsmpecificFeature = GSMSpecificFeature.DEFAULT;
                    
             }
            
            esmClass = new ESMClass(messageMode, messageType, gsmpecificFeature);
            
            int adr_ton = Integer.parseInt(prop.getProperty("smpp.addr-ton","5"));
             switch (adr_ton){
                case 0:
                    addrTon = TypeOfNumber.UNKNOWN;
                    break;
                case 1:
                    addrTon = TypeOfNumber.INTERNATIONAL;
                    break;
                case 2:
                   addrTon = TypeOfNumber.NATIONAL;
                    break;
                case 3:
                    addrTon = TypeOfNumber.NETWORK_SPECIFIC;
                    break;
                case 4:
                    addrTon = TypeOfNumber.SUBSCRIBER_NUMBER;
                    break;
                case 6:
                    addrTon = TypeOfNumber.ABBREVIATED;
                    break;
                default:
                    addrTon = TypeOfNumber.ALPHANUMERIC;
                    
             }

             int source_ton = Integer.parseInt(prop.getProperty("smpp.source-ton","5"));
             switch (source_ton){
                case 0:
                    sourceAddrTon = TypeOfNumber.UNKNOWN;
                    break;
                case 1:
                    sourceAddrTon = TypeOfNumber.INTERNATIONAL;
                    break;
                case 2:
                   sourceAddrTon = TypeOfNumber.NATIONAL;
                    break;
                case 3:
                    sourceAddrTon = TypeOfNumber.NETWORK_SPECIFIC;
                    break;
                case 4:
                    sourceAddrTon = TypeOfNumber.SUBSCRIBER_NUMBER;
                    break;
                case 6:
                    sourceAddrTon = TypeOfNumber.ABBREVIATED;
                    break;
                default:
                    sourceAddrTon = TypeOfNumber.ALPHANUMERIC;
                    
             }

             int destination_ton = Integer.parseInt(prop.getProperty("smpp.destination-ton","5"));
             switch (destination_ton){
                case 0:
                    destAddrTon = TypeOfNumber.UNKNOWN;
                    break;
                case 1:
                    destAddrTon = TypeOfNumber.INTERNATIONAL;
                    break;
                case 2:
                   destAddrTon = TypeOfNumber.NATIONAL;
                    break;
                case 3:
                    destAddrTon = TypeOfNumber.NETWORK_SPECIFIC;
                    break;
                case 4:
                    destAddrTon = TypeOfNumber.SUBSCRIBER_NUMBER;
                    break;
                case 6:
                    destAddrTon = TypeOfNumber.ABBREVIATED;
                    break;
                default:
                    destAddrTon = TypeOfNumber.ALPHANUMERIC;
                    
             }
           
            int adr_npi = Integer.parseInt(prop.getProperty("smpp.addr-npi","0"));

            switch (adr_npi){
                case 1:
                    addrNpi = NumberingPlanIndicator.ISDN;
                    break;
                case 3:
                    addrNpi = NumberingPlanIndicator.DATA;
                    break;
                case 4:
                    addrNpi = NumberingPlanIndicator.TELEX;
                    break;
                case 6:
                    addrNpi = NumberingPlanIndicator.LAND_MOBILE;
                    break;
                case 8:
                    addrNpi = NumberingPlanIndicator.NATIONAL;
                    break;
                case 9:
                    addrNpi = NumberingPlanIndicator.PRIVATE;
                    break;
                case 10:
                    addrNpi = NumberingPlanIndicator.ERMES;
                    break;
                case 13:
                    addrNpi = NumberingPlanIndicator.INTERNET;
                    break;
                case 18:
                    addrNpi = NumberingPlanIndicator.WAP;
                    break;
                default:
                    addrNpi = NumberingPlanIndicator.UNKNOWN;
                  
            }
            
            
            int source_npi = Integer.parseInt(prop.getProperty("smpp.source-npi","0"));

            switch (source_npi){
                case 1:
                    sourceAddrNpi = NumberingPlanIndicator.ISDN;
                    break;
                case 3:
                    sourceAddrNpi = NumberingPlanIndicator.DATA;
                    break;
                case 4:
                    sourceAddrNpi = NumberingPlanIndicator.TELEX;
                    break;
                case 6:
                    sourceAddrNpi = NumberingPlanIndicator.LAND_MOBILE;
                    break;
                case 8:
                    sourceAddrNpi = NumberingPlanIndicator.NATIONAL;
                    break;
                case 9:
                    sourceAddrNpi = NumberingPlanIndicator.PRIVATE;
                    break;
                case 10:
                    sourceAddrNpi = NumberingPlanIndicator.ERMES;
                    break;
                case 13:
                    sourceAddrNpi = NumberingPlanIndicator.INTERNET;
                    break;
                case 18:
                    sourceAddrNpi = NumberingPlanIndicator.WAP;
                    break;
                default:
                    sourceAddrNpi = NumberingPlanIndicator.UNKNOWN;
                  
            }

            int destination_npi = Integer.parseInt(prop.getProperty("smpp.destination-npi","0"));

            switch (destination_npi){
                case 1:
                    destAddrNpi = NumberingPlanIndicator.ISDN;
                    break;
                case 3:
                    destAddrNpi = NumberingPlanIndicator.DATA;
                    break;
                case 4:
                    destAddrNpi = NumberingPlanIndicator.TELEX;
                    break;
                case 6:
                    destAddrNpi = NumberingPlanIndicator.LAND_MOBILE;
                    break;
                case 8:
                    destAddrNpi = NumberingPlanIndicator.NATIONAL;
                    break;
                case 9:
                    destAddrNpi = NumberingPlanIndicator.PRIVATE;
                    break;
                case 10:
                    destAddrNpi = NumberingPlanIndicator.ERMES;
                    break;
                case 13:
                    destAddrNpi = NumberingPlanIndicator.INTERNET;
                    break;
                case 18:
                    destAddrNpi = NumberingPlanIndicator.WAP;
                    break;
                default:
                    destAddrNpi = NumberingPlanIndicator.UNKNOWN;
                  
            }            
            
            addressRange=prop.getProperty("smpp.address-range",null);
            
            protocolId = Integer.parseInt(prop.getProperty("smpp.protocolId","0"));
            priorityFlag =Integer.parseInt(prop.getProperty("smpp.priorityFlag","0"));
            scheduleDeliveryTime = prop.getProperty("smpp.scheduleDeliveryTime",null); 
            validityPeriod=prop.getProperty("smpp.validityPeriod",null);
            
            int replaceif = Integer.parseInt(prop.getProperty("smpp.replaceIfPresentFlag","0"));
            switch (replaceif){
                case 0:
                    replaceIfPresentFlag=ReplaceIfPresentFlag.DONT_REPLACE;
                    break;
                case 1:
                    replaceIfPresentFlag=ReplaceIfPresentFlag.REPLACE;
                    break;
                default :
                    replaceIfPresentFlag=ReplaceIfPresentFlag.DEFAULT;        
            }
           
            
            int  coding = Integer.parseInt(prop.getProperty("smpp.dataCoding","0"));
          
                alphabet = Alphabet.ALPHA_DEFAULT;
                
		switch (coding){
                    
                        case 1:
				alphabet = Alphabet.ALPHA_IA5;
				break;                              
                        case 2:
				alphabet = Alphabet.ALPHA_UNSPECIFIED_2;
				break;
                        case 3:
				alphabet = Alphabet.ALPHA_LATIN1;
				break;
			case 4:
				alphabet = Alphabet.ALPHA_8_BIT;
				break;
                        case 5:
				alphabet = Alphabet.ALPHA_JIS;
				break;
                        case 6:
				alphabet = Alphabet.ALPHA_CYRILLIC;
				break;
                        case 7:
				alphabet = Alphabet.ALPHA_LATIN_HEBREW;
				break;
			case 8:
				alphabet = Alphabet.ALPHA_UCS2;
				break;
			case 9:
				alphabet = Alphabet.ALPHA_PICTOGRAM_ENCODING;
				break;
                        case 10:
				alphabet = Alphabet.ALPHA_ISO_2022_JP_MUSIC_CODES;
				break;
                        case 11:
				alphabet = Alphabet.ALPHA_RESERVED_11;
				break;
                        case 12:
				alphabet = Alphabet.ALPHA_RESERVED_12;
				break;
                        case 13:
				alphabet = Alphabet.ALPHA_JIS_X_0212_1990;
				break;
                        case 14:
				alphabet = Alphabet.ALPHA_KS_C_5601;
				break;
                        case 15:
				alphabet = Alphabet.ALPHA_RESERVED_15;
				break;
                        default : alphabet = Alphabet.ALPHA_DEFAULT;
		}
                
                charset = prop.getProperty("smpp.charset","UTF-8");
                
                StandardCharsets.UTF_8.name();
                
                int  msg_class = Integer.parseInt(prop.getProperty("smpp.MessageClass","1"));
               
                boolean compress= false;
                int comp = Integer.parseInt(prop.getProperty("smpp.compress","0"));
                if (comp == 1) compress = true;


               switch (msg_class)
		{
			case 0:   //MSGCLASS_FLASH
				dataCoding = new GeneralDataCoding(alphabet, MessageClass.CLASS0, compress);
                                messageClass = MessageClass.CLASS0;
				break;
			case  1:  //MSGCLASS_ME
				dataCoding = new GeneralDataCoding(alphabet, MessageClass.CLASS1, compress);
                                messageClass = MessageClass.CLASS1;
				break;
			case 2:   //MSGCLASS_SIM
				dataCoding = new GeneralDataCoding(alphabet, MessageClass.CLASS2, compress);
                                messageClass = MessageClass.CLASS2;
				break;
			case 3:  //MSGCLASS_TE
				dataCoding = new GeneralDataCoding(alphabet, MessageClass.CLASS3, compress);
                                messageClass = MessageClass.CLASS3;
				break;
			default:
				dataCoding = new GeneralDataCoding(alphabet, MessageClass.CLASS1, compress);
                                messageClass = MessageClass.CLASS1;
		}

               smDefaultMsgId = Integer.parseInt(prop.getProperty("smpp.smDefaultMsgId","0"));
               receive_timeout= Long.parseLong(prop.getProperty("smpp.receive-timeout","0"));
               if (Integer.parseInt(prop.getProperty("sms_max_size","254"))>254) {
                   sms_max_size = 254;
               }
               else sms_max_size= Integer.parseInt(prop.getProperty("sms_max_size","254"));
            
        } catch (java.io.FileNotFoundException e) {
            logger.error("Can't find smpp.cfg");
        } catch (java.io.IOException e) {
            logger.error("I/O failed.",e);
        }catch (Exception e) {
            logger.error("error",e);
        }
      
       logger.info("SMPP CONFIGURATION : ") ;
       logger.info(toString()) ;
    }

    private static class SingletonConfig{
        private static final Smpp_ConfigLoader _configLoad = new Smpp_ConfigLoader();
    }
    
    public static Smpp_ConfigLoader getConfigurationLoader(){
        return SingletonConfig._configLoad;
    }

    public static Logger getLogger() {
        return logger;
    }

    public InterfaceVersion getInterface_version() {
        return interface_version;
    }

    public String getIP_address() {
        return IP_address;
    }

    public int getPort() {
        return port;
    }

    public BindType getBindType() {
        return bindType;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getPassword() {
        return password;
    }

    public String getSystemType() {
        return systemType;
    }

    public TypeOfNumber getAddrTon() {
        return addrTon;
    }

    public NumberingPlanIndicator getAddrNpi() {
        return addrNpi;
    }

    public String getAddressRange() {
        return addressRange;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public int getPriorityFlag() {
        return priorityFlag;
    }

    public String getScheduleDeliveryTime() {
        if (prop.getProperty("smpp.scheduleDeliveryTime","").equals("sysdate")) scheduleDeliveryTime= timeFormatter.format(new Date());
        return scheduleDeliveryTime;
    }

    public String getValidityPeriod() {
        if (prop.getProperty("smpp.validityPeriod","").equals("sysdate")) validityPeriod= timeFormatter.format(new Date());
        return validityPeriod;
    }

    public ReplaceIfPresentFlag getReplaceIfPresentFlag() {
        return replaceIfPresentFlag;
    }

    public MessageClass getMessageClass() {
        return messageClass;
    }

    public Alphabet getAlphabet() {
        return alphabet;
    }

    public String getCharset() {
        return charset;
    }
    
       
    public DataCoding getDataCoding() {
        return dataCoding;
    }

    public int getSmDefaultMsgId() {
        return smDefaultMsgId;
    }

    public long getReceive_timeout() {
        return receive_timeout;
    }

    public String getService_type() {
        return service_type;
    }

    public TypeOfNumber getSourceAddrTon() {
        return sourceAddrTon;
    }

    public NumberingPlanIndicator getSourceAddrNpi() {
        return sourceAddrNpi;
    }

    public TypeOfNumber getDestAddrTon() {
        return destAddrTon;
    }

    public NumberingPlanIndicator getDestAddrNpi() {
        return destAddrNpi;
    }

    public ESMClass getEsmClass() {
        return esmClass;
    }

    public int getSms_max_size() {
        return sms_max_size;
    }

    public MessageMode getMessageMode() {
        return messageMode;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public GSMSpecificFeature getGsmpecificFeature() {
        return gsmpecificFeature;
    }
    
    

    @Override
    public String toString() {
        return "Smpp_ConfigLoader{" + "interface_version=" + interface_version + ",\n"
                + " IP_address=" + IP_address + ",\n"
                + " port=" + port + ",\n"
                + " bindType=" + bindType + ",\n"
                + " systemId=" + systemId + ",\n"
                + " password=" + password + ",\n"
                + " systemType=" + systemType + ",\n"
                + " service_type=" + service_type + ",\n"
                + " addrTon=" + addrTon + ",\n"
                + " addrNpi=" + addrNpi + ",\n"
                + " sourceAddrTon=" + sourceAddrTon + ",\n"
                + " sourceAddrNpi=" + sourceAddrNpi + ",\n"
                + " destAddrTon=" + destAddrTon + ",\n"
                + " destAddrNpi=" + destAddrNpi + ",\n"
                + " esmClass=" + esmClass.value() + ",\n"
                + " MessageMode="+ messageMode.value()+"\n"
                + " MessageType="+ messageType.value()+"\n" 
                + " GSMSpecificFeature="+gsmpecificFeature.value()+"\n"
                + " addressRange=" + addressRange + ",\n"
                + " protocolId=" + protocolId + ",\n"
                + " priorityFlag=" + priorityFlag + ",\n"
                + " scheduleDeliveryTime=" + scheduleDeliveryTime + ",\n"
                + " validityPeriod=" + validityPeriod + ",\n"
                + " replaceIfPresentFlag=" + replaceIfPresentFlag.value() + ",\n"
                + " messageClass = "+messageClass.value()+",\n"
                + " alphabet= "+ alphabet.value()+",\n"
                + " charset = "+charset+",\n"
                + " dataCoding=" + dataCoding + ",\n"
                + " smDefaultMsgId=" + smDefaultMsgId + ",\n"
                + " receive_timeout=" + receive_timeout + ",\n"
                + " sms_max_size=" + sms_max_size + '}';
    }
    
    
    
       
}