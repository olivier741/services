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
package com.tatsinktechnologic.util;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tatsinktechnologic.beans.DeliveryMessage;
import com.tatsinktechnologic.beans.Message_Exchg;
import com.tatsinktechnologic.beans.Process_Request;

/**
 *
 * @author olivier.tatsinkou
 */
public class ConverterJSON {
    
    
    public static String convertMsgExchToJson( Message_Exchg msg_exch) {
        ObjectMapper mapper = new ObjectMapper();

        String result = null;

        try {
                // Convert object to JSON string and pretty print
                result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg_exch);

        } catch (JsonGenerationException e) {
                e.printStackTrace();
        } catch (JsonMappingException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return result;
   }

    
  public static Message_Exchg convertJsonToMsgExch(String msg_exch) {
        ObjectMapper mapper = new ObjectMapper();

        Message_Exchg result = null;

        try {
                // Convert JSON string to Object
                result = mapper.readValue(msg_exch, Message_Exchg.class);
                
        } catch (JsonGenerationException e) {
                e.printStackTrace();
        } catch (JsonMappingException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        
        return result;
   }

     public static String convertDeliveryToJson( DeliveryMessage msg_exch) {
        ObjectMapper mapper = new ObjectMapper();
        String result = null;
        try {
                // Convert object to JSON string and pretty print
                result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg_exch);

        } catch (JsonGenerationException e) {
                e.printStackTrace();
        } catch (JsonMappingException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return result;
   }
     
 
   public static DeliveryMessage convertJsonToDelivery(String msg_exch) {
        ObjectMapper mapper = new ObjectMapper();
        DeliveryMessage result = null;
        try {
                // Convert JSON string to Object
                result = mapper.readValue(msg_exch, DeliveryMessage.class);
                
        } catch (JsonGenerationException e) {
                e.printStackTrace();
        } catch (JsonMappingException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        
        return result;
   }
   
   
   
    public static String convertProcess_rqToJson( Process_Request msg_exch) {
        ObjectMapper mapper = new ObjectMapper();

        String result = null;

        try {
                // Convert object to JSON string and pretty print
                result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg_exch);

        } catch (JsonGenerationException e) {
                e.printStackTrace();
        } catch (JsonMappingException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return result;
   }

    
  public static Process_Request convertJsonToProcess_rq(String msg_exch) {
        ObjectMapper mapper = new ObjectMapper();

        Process_Request result = null;

        try {
                // Convert JSON string to Object
                result = mapper.readValue(msg_exch, Process_Request.class);
                
        } catch (JsonGenerationException e) {
                e.printStackTrace();
        } catch (JsonMappingException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        
        return result;
   }
}
