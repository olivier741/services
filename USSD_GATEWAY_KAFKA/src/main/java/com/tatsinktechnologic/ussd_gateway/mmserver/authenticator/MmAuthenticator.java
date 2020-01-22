///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.tatsinktechnologic.ussd_gateway.mmserver.authenticator;
//
///**
// *
// * @author olivier.tatsinkou
// */
//
//import com.tatsinktechnologic.ussd_gateway.mmserver.agent.MMbeanServer;
//import com.tatsinktechnologic.ussd_gateway.mmserver.base.Log;
//import java.util.LinkedList;
//import javax.management.ObjectName;
//import viettel.passport.client.VSAValidate;
//
//public class MmAuthenticator
//  implements MmAuthenticatorMBean
//{
//  private VSAValidate vsa;
//  private static MmAuthenticator mmAuthenticator;
//  private final int timeOut = 30000;
//  
//  public static MmAuthenticator getInstance()
//  {
//    if (mmAuthenticator == null) {
//      mmAuthenticator = new MmAuthenticator();
//    }
//    return mmAuthenticator;
//  }
//  
//  private MmAuthenticator()
//  {
//    try
//    {
//      ObjectName name = new ObjectName("Tools:name=MmAuthenticator");
//      MMbeanServer.getInstance().registerMBean(this, name);
//      Log.info("Registered MmAuthenticator");
//    }
//    catch (Exception e)
//    {
//      Log.error("Can not register Mbean MmAuthenticator");
//      Log.error(e);
//    }
//    Log.info("Creating VSAValidate...");
//    this.vsa = new VSAValidate();
//    this.vsa.setTimeOutVal(30000);
//    
//    Log.info("VSAValidate created!");
//  }
//  
////  public MmUser validate(String username, String password, String appID)
////  {
////    MmUser mmUser = null;
////    this.vsa.setDomainCode(appID);
////    this.vsa.setUser(username);
////    this.vsa.setPassword(password);
////    try
////    {
////      Log.info("Validating VSA with timeout = " + this.vsa.getTimeOutVal());
////      this.vsa.validate();
////      Log.info("Validated");
////      if (this.vsa.isAuthenticationSuccesful())
////      {
////        Hashtable<String, List<String>> roleMap = new Hashtable();
////        ArrayList<ObjectToken> objList = this.vsa.getUserToken().getParentMenu();
////        for (Iterator parentObject = objList.iterator(); parentObject.hasNext();)
////        {
////          Object ob = parentObject.next();
////          if ((ob instanceof ObjectToken))
////          {
////            LinkedList<String> temp = new LinkedList();
////            ObjectToken element = (ObjectToken)ob;
////            String objectName = element.getObjectName();
////            ArrayList<ObjectToken> childs = element.getChildObjects();
////            if (childs != null) {
////              for (int i = 0; i < childs.size(); i++)
////              {
////                ObjectToken child = (ObjectToken)childs.get(i);
////                if ("C".equalsIgnoreCase(child.getObjectType())) {
////                  temp.add(child.getObjectName());
////                }
////              }
////            }
////            roleMap.put(objectName, temp);
////          }
////        }
////        mmUser = new MmUser(username, password, appID, roleMap);
////      }
////    }
////    catch (Exception e)
////    {
////      Log.error("Error in validate with Passport");
////      Log.error(e);
////    }
////    return mmUser;
////  }
////  
//  private String formatCanonicalName(String name)
//  {
//    String formatedName = name;
//    if (name == null) {
//      formatedName = "";
//    }
//    int index = name.indexOf(":");
//    if ((index != -1) && (index < name.length()))
//    {
//      formatedName = name.substring(0, index + 1);
//      name = name.substring(index + 1);
//      String[] names = name.split(",");
//      if (names.length > 0)
//      {
//        for (int i = 0; i < names.length - 1; i++) {
//          for (int j = i + 1; j < names.length; j++) {
//            if (names[i].compareTo(names[j]) > 0)
//            {
//              String temp = names[i];
//              names[i] = names[j];
//              names[j] = temp;
//            }
//          }
//        }
//        for (int i = 0; i < names.length; i++) {
//          formatedName = formatedName + names[i] + ",";
//        }
//        formatedName = formatedName.substring(0, formatedName.length() - 1);
//      }
//    }
//    return formatedName;
//  }
//  
//  public LinkedList<String> getAccessibleMethods(String appId, String userName, String password, String mBeanObjectName)
//  {
//    MmUser mmUser = validate(userName, password, appId);
//    if (mmUser == null) {
//      return null;
//    }
//    LinkedList<String> method = mmUser.getMethods(mBeanObjectName);
//    if (method == null) {
//      return new LinkedList();
//    }
//    return method;
//  }
//  
//  public String vsaURL()
//  {
//    String s = "VSA's URL: ";
//    if (this.vsa.getCasValidateUrl() != null) {
//      s = s + this.vsa.getCasValidateUrl();
//    } else {
//      s = s + "Unknown";
//    }
//    return s;
//  }
//}
