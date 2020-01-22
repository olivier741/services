/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans;

/**
 *
 * @author olivier.tatsinkou
 */
public interface USSDType {
     int USSDMSG_TYPE_SUB_SEND_REQ = 100;
     int USSDMSG_TYPE_SUB_SEND_RSP = 101;
     int USSDMSG_TYPE_SUB_CANCEL = 102;
     int USSDMSG_TYPE_SUB_RECV_OK = 103;
     int USSDMSG_TYPE_TRANS_ERR = 104;
     
     int USSDMSG_TYPE_APP_SEND_REQ_FIRST = 200;
     int USSDMSG_TYPE_APP_SEND_NOTIFY_FIRST = 201;
     int USSDMSG_TYPE_APP_SEND_MENU = 202;
     int USSDMSG_TYPE_APP_SEND_RSP = 203;
     int USSDMSG_TYPE_APP_SEND_NOTIFY = 204;
     int USSDMSG_TYPE_APP_CANCEL = 205;
     int USSDMSG_TYPE_APP_CLOSE_TRANS = 206;
     
     
}
