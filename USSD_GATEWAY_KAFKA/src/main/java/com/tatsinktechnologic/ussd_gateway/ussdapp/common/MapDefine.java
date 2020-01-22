/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.common;

/**
 *
 * @author olivier.tatsinkou
 */
public class MapDefine
{
  public static final int MAP_OPEN_IND = 2;
  public static final int MAP_OPEN_REQ = 1;
  public static final int MAP_OPEN_RSP = 129;
  public static final int MAP_OPEN_CNF = 130;
  public static final int MAP_U_ABORT_IND = 8;
  public static final int MAP_U_ABORT_REQ = 7;
  public static final int MAP_P_ABORT_IND = 9;
  public static final int MAP_NOTICE_IND = 10;
  public static final int MAP_DELIMITER_IND = 6;
  public static final int MAP_DELIMITER_REQ = 5;
  public static final int MAP_CLOSE_REQ = 3;
  public static final int MAP_CLOSE_IND = 4;
  public static final int MAP_PROC_UNSTR_SS_REQ_IND = 28;
  public static final int MAP_PROC_UNSTR_SS_REQ_REQ = 27;
  public static final int MAP_PROC_UNSTR_SS_REQ_RSP = 149;
  public static final int MAP_PROC_UNSTR_SS_REQ_CNF = 150;
  public static final int MAP_UNSTR_SS_REQ_CNF = 146;
  public static final int MAP_UNSTR_SS_REQ_RSP = 145;
  public static final int MAP_UNSTR_SS_REQ_REQ = 23;
  public static final int MAP_UNSTR_SS_REQ_IND = 24;
  public static final int MAP_UNSTR_SS_NOTIFY_CNF = 148;
  public static final int MAP_UNSTR_SS_NOTIFY_RSP = 147;
  public static final int MAP_UNSTR_SS_NOTIFY_REQ = 25;
  public static final int MAP_UNSTR_SS_NOTIFY_IND = 26;
  public static final int MAP_NORMAL_RELEASE = 0;
  public static final int MAP_PREARRANGED_END = 1;
  public static final int MAP_DLG_ACCEPT = 0;
  public static final int MAP_DLG_REFUSE = 1;
  public static final int MAPRR_NO_REASON = 0;
  public static final int MAPRR_INV_DEST_REF = 1;
  public static final int MAPRR_INV_ORIG_REF = 2;
  public static final int MAPRR_APPL_CONTEXT = 3;
  public static final int MAPRR_VER_INCOMP = 4;
  public static final int MAPRR_NODE_NOTREACH = 5;
  public static final int MAPUR_USER_SPECIFIC = 0;
  public static final int MAPUR_RESOURCE_LIMITAION = 1;
  public static final int MAPUR_RESOURCE_UNAVAIL = 2;
  public static final int MAPUR_APP_PROC_CANCELLED = 3;
  public static final int MAPUR_PROCEDURE_ERROR = 4;
  public static final int MAPUR_UNSPEC_REASON = 5;
  public static final int MAPUR_VER_NOT_SUPPORTED = 6;
  public static final int MAPPR_PROV_MALFCT = 0;
  public static final int MAPPR_DLG_RLSD = 1;
  public static final int MAPPR_RSRC_LIMIT = 2;
  public static final int MAPPR_MNT_ACT = 3;
  public static final int MAPPR_VER_INCOMP = 4;
  public static final int MAPPR_AB_DLG = 5;
  public static final int MAPPR_INVALID_PDU = 6;
  public static final int MAPPD_ABEVT_DETECT_BY_PEER = 0;
  public static final int MAPPD_RSP_REJEC_BY_PEER = 1;
  public static final int MAPPD_ABEVT_RX_FROM_PEER = 2;
  public static final int MAPPD_MSG_NOT_DELIVER = 3;
  public static final int MAPUE_UNKNOWN_SUBSCRIBER = 1;
  public static final int MAPUE_UNIDENTIFIED_SUBSCRIBER = 5;
  public static final int MAPUE_ABSENTSUBSCRIBER_SM = 6;
  public static final int MAPUE_UNKNOWN_EQUIPMENT = 7;
  public static final int MAPUE_ROAMING_NOT_ALLOWED = 8;
  public static final int MAPUE_ILLEGAL_SUBSCRIBER = 9;
  public static final int MAPUE_BEARER_SERVICE_NOT_PROVISIONED = 10;
  public static final int MAPUE_TELESERVICE_NOT_PROVISIONED = 11;
  public static final int MAPUE_ILLEGAL_EQUIPMENT = 12;
  public static final int MAPUE_CALL_BARRED = 13;
  public static final int MAPUE_FORWARDING_VIOLATION = 14;
  public static final int MAPUE_CUG_REJECT = 15;
  public static final int MAPUE_ILLEGAL_SS_OPERATION = 16;
  public static final int MAPUE_SS_ERROR_STATUS = 17;
  public static final int MAPUE_SS_NOT_AVAILABLE = 18;
  public static final int MAPUE_SS_SUBSCRIPTION_VIOLATION = 19;
  public static final int MAPUE_SS_INCOMPATIBILITY = 20;
  public static final int MAPUE_FACILITY_NOT_SUPPORTED = 21;
  public static final int MAPUE_PW_REGISTRATION_FAILURE = 23;
  public static final int MAPUE_NEGATIVE_PW_CHECK = 24;
  public static final int MAPUE_ABSENT_SUBSCRIBER = 27;
  public static final int MAPUE_SUBSCRIBER_BUSY_FOR_MT_SMS = 31;
  public static final int MAPUE_SM_DELIVERY_FAILURE = 32;
  public static final int MAPUE_MESSAGE_WAITING_LIST_FULL = 33;
  public static final int MAPUE_SYSTEM_FAILURE = 34;
  public static final int MAPUE_DATA_MISSING = 35;
  public static final int MAPUE_UNEXPECTED_DATA_VALUE = 36;
  public static final int MAPUE_RESOURCE_LIMITATION = 37;
  public static final int MAPUE_INITIATING_RELEASE = 38;
  public static final int MAPUE_NO_ROAMING_NUMBER_AVAILABLE = 39;
  public static final int MAPUE_TRACING_BUFFER_FULL = 40;
  public static final int MAPUE_NUMBER_OF_PW_ATTEMPTS_VIOLATION = 43;
  public static final int MAPUE_NUMBER_CHANGED = 44;
  public static final int MAPUE_BUSY_SUBSCRIBER = 45;
  public static final int MAPUE_NO_SUBSCRIBER_REPLY = 46;
  public static final int MAPUE_OR_NOT_ALLOWED = 48;
  public static final int MAPUE_ATI_NOT_ALLOWED = 49;
  public static final int MAPUE_UNAUTHORISED_REQUESTING_NETWORK = 52;
  public static final int MAPUE_UNAUTHORISED_LCS_CLIENT = 53;
  public static final int MAPUE_POSITION_METHOD_FAILURE = 54;
  public static final int MAPUE_UNKNOWN_OR_UNREACHABLE_LCS_CLIENT = 58;
  public static final int MAPUE_MM_EVENT_NOT_SUPPORTED = 59;
  public static final int MAPUE_ATSI_NOT_ALLOWED = 60;
  public static final int MAPUE_ATM_NOT_ALLOWED = 61;
  public static final int MAPUE_INFORMATION_NOT_AVAILABLE = 62;
  public static final int MAPUE_UNKNOWN_ALPHABET = 71;
  public static final int MAPUE_USSD_BUSY = 72;
  public static final int MAPPE_DUPLICATED_INVOKE_ID = 1;
  public static final int MAPPE_NOT_SUPPORTED_SERVICE = 2;
  public static final int MAPPE_MISTYPED_PARAMETER = 3;
  public static final int MAPPE_RESOURCE_LIMITATION = 4;
  public static final int MAPPE_INITIATING_RELEASE = 5;
  public static final int MAPPE_UNEXPECTED_RESPONSE_FROM_PEER = 6;
  public static final int MAPPE_SERVICE_COMPLETION_FAILURE = 7;
  public static final int MAPPE_NO_RESPONSE_FROM_PEER = 8;
  public static final int MAPPE_INVALID_RESPONSE_RECEIVED = 9;
  
  public static String printPNoticeReason(int reason)
  {
    String result;
    switch (reason)
    {
    case 0: 
      result = "Abnormal event detected by peer";
      break;
    case 2: 
      result = "Abnormal event receive from peer";
      break;
    case 3: 
      result = "Message not delivered";
      break;
    case 1: 
      result = "response rejected by peer";
      break;
    default: 
      result = "UNKNOW";
    }
    return result;
  }
  
  public static String printPAbortReason(int reason)
  {
    String result;
    switch (reason)
    {
    case 5: 
      result = "Abnormal MAP dialogue";
      break;
    case 1: 
      result = "Supporting dialogue/transaction released";
      break;
    case 6: 
      result = "Invalid PDU";
      break;
    case 3: 
      result = "Maintenance activity";
      break;
    case 0: 
      result = "Provider malfunction";
      break;
    case 2: 
      result = "Resource limitation";
      break;
    case 4: 
      result = "version incompatibility";
      break;
    default: 
      result = "UNKNOW";
    }
    return result;
  }
  
  public static String printUAbortReason(int reason)
  {
    String result;
    switch (reason)
    {
    case 0: 
      result = "User specific reason";
      break;
    case 3: 
      result = "Application procedure cancelled";
      break;
    case 4: 
      result = "Procedure Error";
      break;
    case 1: 
      result = "User resource limitation";
      break;
    case 2: 
      result = "Resource unavailable";
      break;
    case 5: 
      result = "Unspecified Reason";
      break;
    case 6: 
      result = "Version Not Supported";
      break;
    default: 
      result = "UNKNOW";
    }
    return result;
  }
  
  public static String getMapTypeName(int type)
  {
    String result;
    switch (type)
    {
    case 2: 
      result = "MAP_OPEN_IND";
      break;
    case 1: 
      result = "MAP_OPEN_REQ";
      break;
    case 129: 
      result = "MAP_OPEN_RSP";
      break;
    case 130: 
      result = "MAP_OPEN_CNF";
      break;
    case 8: 
      result = "MAP_U_ABORT_IND";
      break;
    case 7: 
      result = "MAP_U_ABORT_REQ";
      break;
    case 9: 
      result = "MAP_P_ABORT_IND";
      break;
    case 10: 
      result = "MAP_NOTICE_IND";
      break;
    case 6: 
      result = "MAP_DELIMITER_IND";
      break;
    case 5: 
      result = "MAP_DELIMITER_REQ";
      break;
    case 4: 
      result = "MAP_CLOSE_IND";
      break;
    case 3: 
      result = "MAP_CLOSE_REQ";
      break;
    case 28: 
      result = "MAP_PROC_UNSTR_SS_REQ_IND";
      break;
    case 27: 
      result = "MAP_PROC_UNSTR_SS_REQ_REQ";
      break;
    case 149: 
      result = "MAP_PROC_UNSTR_SS_REQ_RSP";
      break;
    case 150: 
      result = "MAP_PROC_UNSTR_SS_REQ_CNF";
      break;
    case 146: 
      result = "MAP_UNSTR_SS_REQ_CNF";
      break;
    case 145: 
      result = "MAP_UNSTR_SS_REQ_RSP";
      break;
    case 23: 
      result = "MAP_UNSTR_SS_REQ_REQ";
      break;
    case 24: 
      result = "MAP_UNSTR_SS_REQ_IND";
      break;
    case 148: 
      result = "MAP_UNSTR_SS_NOTIFY_CNF";
      break;
    case 147: 
      result = "MAP_UNSTR_SS_NOTIFY_RSP";
      break;
    case 25: 
      result = "MAP_UNSTR_SS_NOTIFY_REQ";
      break;
    case 26: 
      result = "MAP_UNSTR_SS_NOTIFY_IND";
      break;
    default: 
      result = "UNKNOW";
    }
    return result;
  }
}

