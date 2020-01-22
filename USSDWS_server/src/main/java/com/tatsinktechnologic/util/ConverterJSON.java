/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.util;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tatsinktechnologic.beans.Message_Exchg;

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

  
}
