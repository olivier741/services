/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page.chat.websocket_client;

import com.tatsinktechnologic.bean.page.chat.MessageWs;
import java.io.IOException;
import java.io.Serializable;
import javax.websocket.EncodeException;

/**
 *
 * @author olivier.tatsinkou
 */
public class ChatClient implements Serializable{
    
      public static void sendMsg(MessageWs msgWS,ChatClientEndpoint clientEndPoint) throws IOException, EncodeException {
        clientEndPoint.addMessageHandler(new ChatClientEndpoint.MessageHandler() {
            public void handleMessage(MessageWs msgWS) {
                clientEndPoint.sendMessage(msgWS);
            }
        });
    }
}
