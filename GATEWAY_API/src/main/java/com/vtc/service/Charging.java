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
package com.vtc.service;

/**
 *
 * @author olivier.tatsinkou
 */
import com.sun.net.httpserver.HttpExchange;
import com.viettel.bccsgw.bo.AccessManegement;
import com.viettel.bccsgw.bo.Client;
import com.viettel.bccsgw.bo.Input;
import com.viettel.bccsgw.bo.Output;
import com.viettel.bccsgw.bo.OutputCharging;
import com.viettel.bccsgw.bo.Webservice;
import com.viettel.bccsgw.logging.LogWriter;
import com.viettel.bccsgw.security.UserToken;
import com.viettel.bccsgw.security.VSAValidate;
import com.viettel.bccsgw.utils.DateTimeUtils;
import com.viettel.bccsgw.utils.MemoryDataGateway;
import com.viettel.bccsgw.utils.PasswordGateway;
import com.viettel.bccsgw.utils.RequestInfo;
import com.viettel.bccsgw.utils.ResourceBundleUtils;
import com.viettel.bccsgw.utils.StringUtils;
import com.viettel.bccsgw.utils.XmlConfig;
import com.viettel.bccsgw.utils.XmlUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.jdom.output.XMLOutputter;


@WebService(serviceName="BCCSGateway", portName="BCCSGatewaySOAP")
@SOAPBinding(style=SOAPBinding.Style.RPC, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class Charging
{
  private String wsCode;
  private Logger logger;
  @Resource
  WebServiceContext wsContext;
  private static LogWriter logWriter;
  private static final HashMap<String, Long> connectionManager = new HashMap();
  private static final HashMap<String, Long> webserviceManager = new HashMap();
  private static final HashMap<String, RequestInfo> reqManager = new HashMap();
  private static long requestCount = 0L;
  private static Long totalCurrentRequest = Long.valueOf(0L);
  private static Long totalCurrentRequestForClient = Long.valueOf(0L);
  private static LogWriter logWSWriter;
  private static final String RESULT_CODE_FAIL = "0";
  private static final String RESULT_CODE_SUCCESS = "1";
  private static Map<String, String> errorCodeMap = MemoryDataGateway.getErrorCodeMap();
  private static final int TIMEOUT_HTTP_CONNECTION = 30000;
  private String appName = "";
  private MapCode mapCode;
  private HttpClient httpTransport;
  private static MultiThreadedHttpConnectionManager conMgr;
  
  public Charging(Logger logger, String appName)
    throws Exception
  {
    this.logger = logger;
    this.appName = appName;
    
    this.wsCode = ResourceBundleUtils.getResource("charging_wscode");
    this.mapCode = new MapCode();
  }
  
  @WebMethod(operationName="processCharging")
  @RequestWrapper(localName="processChargingRequest")
  @ResponseWrapper(localName="processChargingResponse")
  @WebResult(name="return", partName="return")
  public OutputCharging processCharging(@WebParam(name="username", partName="username") String username, @WebParam(name="password", partName="password") String password, @WebParam(name="requestId", partName="requestId") String requestId, @WebParam(name="msisdn", partName="msisdn") String msisdn, @WebParam(name="amount", partName="amount") String amount, @WebParam(name="charge_type", partName="charge_type") String charge_type, @WebParam(name="contents", partName="contents") String contents)
  {
    OutputCharging output = new OutputCharging();
    try
    {
      if (logWriter == null) {
        logWriter = new LogWriter("../conf/log_writer.cfg", "/bccsgw_log", "request");
      }
      if (logWSWriter == null) {
        logWSWriter = new LogWriter("../conf/log_writer.cfg", "/action_log_ws", "actionWS");
      }
      Date startTime = new Date();
      long begin = System.currentTimeMillis();
      increase();
      
      this.logger.info("Start new process request, concurrent request: " + totalCurrentRequest);
      









      String seqRequest = getTransId();
      ArrayList<String> listOutput = new ArrayList();
      String wscode = this.wsCode;
      String ip = getIpClient();
      String responseCode = "";
      



      Input parameters = new Input();
      ArrayList<Input.Param> listInput = new ArrayList();
      Input.Param param = new Input.Param();
      param.setName("input");
      



      String value = msisdn + "|" + username + "|" + charge_type + "|" + "2000" + "|" + amount + "|0|" + contents;
      param.setValue(value);
      listInput.add(param);
      
      param = new Input.Param();
      param.setName("client");
      param.setValue(username);
      listInput.add(param);
      
      param = new Input.Param();
      param.setName("requestId");
      param.setValue(requestId);
      listInput.add(param);
      
      parameters.setParam(listInput);
      this.logger.debug(value);
      

      String inputValue = getInputFromParamList(parameters.getParam());
      String strRequest = formatRequest(username, password, wscode, parameters.getParam(), null);
      if ((wscode != null) && (wscode.equals("BCCSReloadDB")))
      {
        MemoryDataGateway.reloadDataMemory();
        errorCodeMap = MemoryDataGateway.getErrorCodeMap();
        releaseRequest(username);
        
        output.setErrorCode("0");
        output.setMessage("Reload database into memory success!");
        

        saveLogDB(seqRequest, username, wscode, strRequest, inputValue, ip, output, "RELOAD_OK", startTime, new Date(), null, System.currentTimeMillis() - begin, null, "1", false, "?");
        


        return output;
      }
      Map<String, Client> clientMap = MemoryDataGateway.getCientMap();
      Map<String, Webservice> webserviceMap = MemoryDataGateway.getWebserviceMap();
      Map<String, AccessManegement> accessManagementMap = MemoryDataGateway.getAccessManagementMap();
      Client client = (Client)clientMap.get(username.toLowerCase());
      Webservice ws = (Webservice)webserviceMap.get(wscode.toLowerCase());
      Output checkData = validateData(username, wscode, client, ws, accessManagementMap);
      if ((checkData != null) && (checkData.getError() != null) && (!checkData.getError().equals("")))
      {
        output = this.mapCode.getOutput(checkData.getError(), requestId);
        
        releaseRequest(username);
        saveLogDB(seqRequest, username, wscode, strRequest, inputValue, ip, output, output.getErrorCode(), startTime, new Date(), null, System.currentTimeMillis() - begin, null, "0", true, checkData.getOriginal());
        


        return output;
      }
      AccessManegement am = (AccessManegement)accessManagementMap.get(client.getUsername().toLowerCase() + "-" + ws.getWsCode().toLowerCase());
      
      Output validate = authenticateUser(username, password, ip, client);
      if ((validate != null) && (validate.getError() != null) && (!validate.getError().equals("")))
      {
        output = this.mapCode.getOutput(validate.getError(), requestId);
        
        releaseRequest(username);
        saveLogDB(seqRequest, username, wscode, strRequest, inputValue, ip, output, output.getErrorCode(), startTime, new Date(), null, System.currentTimeMillis() - begin, null, "0", true, validate.getOriginal());
        


        return output;
      }
      String key = username + ":" + wscode;
      if (connectionManager.get(key) == null) {
        connectionManager.put(key, Long.valueOf(1L));
      }
      if (webserviceManager.get(wscode) == null) {
        webserviceManager.put(wscode, Long.valueOf(1L));
      }
      long maxConnection = am.getMaxConnection();
      
      boolean isReturn = false;
      if (((Long)connectionManager.get(key)).longValue() >= maxConnection)
      {
        long waitingTimeout = Long.parseLong(ResourceBundleUtils.getResource("waitingTimeout"));
        long lStartTime = startTime.getTime() + waitingTimeout * 1000L;
        int waitCount = 0;
        while (((Long)connectionManager.get(key)).longValue() >= maxConnection)
        {
          try
          {
            Thread.sleep(100L);
            waitCount++;
          }
          catch (InterruptedException e) {}
          if ((waitCount % 20 == 0) || (lStartTime - System.currentTimeMillis() < 0L)) {
            isReturn = true;
          }
        }
        if (isReturn)
        {
          String errorCode = "6000";
          releaseRequest(username);
          output.setErrorCode(errorCode);
          output.setMessage((String)errorCodeMap.get(errorCode));
          
          saveLogDB(seqRequest, username, wscode, strRequest, inputValue, ip, output, errorCode, startTime, new Date(), null, System.currentTimeMillis() - begin, null, "0", true, "?");
          


          return output;
        }
      }
      if (((Long)webserviceManager.get(wscode)).longValue() >= ws.getMaxConnection())
      {
        int waitCount = 0;
        
        long waitingTimeout = Long.parseLong(ResourceBundleUtils.getResource("waitingTimeout"));
        long lStartTime = startTime.getTime() + waitingTimeout * 1000L;
        while (((Long)webserviceManager.get(key)).longValue() >= ws.getMaxConnection())
        {
          try
          {
            Thread.sleep(100L);
            waitCount++;
          }
          catch (InterruptedException e) {}
          if ((waitCount % 50 == 0) || (lStartTime - System.currentTimeMillis() < 0L)) {
            isReturn = true;
          }
        }
        if (isReturn)
        {
          releaseRequest(username);
          String errorCode = "6000";
          output.setErrorCode(errorCode);
          output.setMessage((String)errorCodeMap.get(errorCode));
          

          saveLogDB(seqRequest, username, wscode, strRequest, inputValue, ip, output, errorCode, startTime, new Date(), null, System.currentTimeMillis() - begin, null, "0", true, "?");
          


          return output;
        }
      }
      synchronized (connectionManager)
      {
        connectionManager.put(key, Long.valueOf(((Long)connectionManager.get(key)).longValue() + 1L));
      }
      synchronized (webserviceManager)
      {
        webserviceManager.put(wscode, Long.valueOf(((Long)webserviceManager.get(wscode)).longValue() + 1L));
      }
      if ((contents == null) || (contents.trim().length() == 0) || (contents.equals("?"))) {
        return this.mapCode.getOutput("25", requestId);
      }
      try
      {
        double amountD = Double.parseDouble(amount);
        if (amountD < 0.0D) {
          return this.mapCode.getOutput("36", requestId);
        }
      }
      catch (Exception ex)
      {
        return this.mapCode.getOutput("36", requestId);
      }
      long timeCallWS = System.currentTimeMillis();
      String resultRA = httpCallRaw(username, ws, parameters.getParam(), parameters.getRawData(), seqRequest, wscode, inputValue, timeCallWS);
      
      timeCallWS = System.currentTimeMillis() - timeCallWS;
      synchronized (webserviceManager)
      {
        webserviceManager.put(wscode, Long.valueOf(((Long)webserviceManager.get(wscode)).longValue() - 1L));
      }
      synchronized (connectionManager)
      {
        connectionManager.put(key, Long.valueOf(((Long)connectionManager.get(key)).longValue() - 1L));
      }
      if (XmlUtil.xmlCheck(resultRA))
      {
        XmlConfig soapMsg = new XmlConfig();
        try
        {
          String resultCode = "1";
          soapMsg.load(resultRA);
          Element root = soapMsg.getRoot();
          Object filter = new ElementFilter("return");
          Object itResult = root.getDescendants((Filter)filter);
          while (((Iterator)itResult).hasNext()) {
            try
            {
              Element ele = (Element)((Iterator)itResult).next();
              listOutput.add(ele.getText());
            }
            catch (ClassCastException cce)
            {
              cce.printStackTrace();
            }
          }
          if (listOutput.isEmpty())
          {
            responseCode = "9000";
            output = this.mapCode.getOutput(responseCode, requestId);
            resultCode = "0";
          }
          responseCode = "0";
          String returnOutput = (String)listOutput.get(0);
          


          output = this.mapCode.getOutput(returnOutput.split("\\|")[0], requestId);
          output.setOriginal(resultRA);
          


          saveLogDB(seqRequest, username, wscode, strRequest, inputValue, ip, output, responseCode, startTime, new Date(), Long.valueOf(timeCallWS), System.currentTimeMillis() - begin, null, resultCode, true, resultRA);
        }
        catch (Exception ex)
        {
          responseCode = "6005";
          output.setErrorCode(responseCode);
          output.setMessage((String)errorCodeMap.get(responseCode));
          
          saveLogDB(seqRequest, username, wscode, strRequest, inputValue, ip, output, responseCode, startTime, new Date(), Long.valueOf(timeCallWS), System.currentTimeMillis() - begin, ex, "0", true, resultRA);
        }
      }
      else
      {
        listOutput.add(resultRA);
        if ((resultRA != null) && (resultRA.contains("timed out")))
        {
          responseCode = "2007";
          output.setErrorCode(responseCode);
          output.setMessage((String)errorCodeMap.get(responseCode));
        }
        else
        {
          responseCode = "2006";
          output = this.mapCode.getOutput(responseCode, requestId);
        }
        saveLogDB(seqRequest, username, wscode, strRequest, inputValue, ip, output, responseCode, startTime, new Date(), Long.valueOf(timeCallWS), System.currentTimeMillis() - begin, null, "1", true, resultRA);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      this.logger.error(e.getMessage(), e);
    }
    releaseRequest(username);
    

    return output;
  }
  
  private void warningResponseSlow(Long totalTimeProcess, String username, Webservice ws, Logger logger)
  {
    String errorCode = "";
    String strMessage = "";
    if ((ws.getWarningTimeLevel3() != null) && (ws.getWarningTimeLevel3().longValue() > 0L) && (totalTimeProcess.longValue() >= ws.getWarningTimeLevel3().longValue()))
    {
      errorCode = "warn_level3";
      strMessage = (String)errorCodeMap.get(errorCode);
      strMessage = strMessage.replaceAll("[$]username[$]", username).replaceAll("[$]wsCode[$]", ws.getWsCode()).replaceAll("[$]totalTime[$]", totalTimeProcess.toString());
      

      logger.warn("[" + errorCode + "]" + strMessage);
    }
    else if ((ws.getWarningTimeLevel2() != null) && (ws.getWarningTimeLevel2().longValue() > 0L) && (totalTimeProcess.longValue() >= ws.getWarningTimeLevel2().longValue()))
    {
      errorCode = "warn_level2";
      strMessage = (String)errorCodeMap.get(errorCode);
      strMessage = strMessage.replaceAll("[$]username[$]", username).replaceAll("[$]wsCode[$]", ws.getWsCode()).replaceAll("[$]totalTime[$]", totalTimeProcess.toString());
      

      logger.warn("[" + errorCode + "]" + strMessage);
    }
    else if ((ws.getWarningTimeLevel1() != null) && (ws.getWarningTimeLevel1().longValue() > 0L) && (totalTimeProcess.longValue() >= ws.getWarningTimeLevel1().longValue()))
    {
      errorCode = "warn_level1";
      strMessage = (String)errorCodeMap.get(errorCode);
      strMessage = strMessage.replaceAll("[$]username[$]", username).replaceAll("[$]wsCode[$]", ws.getWsCode()).replaceAll("[$]totalTime[$]", totalTimeProcess.toString());
      

      logger.warn("[" + errorCode + "]" + strMessage);
    }
  }
  
  private Output validateData(String userName, String wsCode, Client client, Webservice ws, Map<String, AccessManegement> accessManagementMap)
  {
    String responseCode = "";
    Output output = new Output();
    if ((userName == null) || (userName.equals("")))
    {
      responseCode = "1000";
      output.setError(responseCode);
      output.setDescription((String)errorCodeMap.get(responseCode));
      return output;
    }
    if ((wsCode == null) || (wsCode.equals("")))
    {
      responseCode = "2000";
      output.setError(responseCode);
      output.setDescription((String)errorCodeMap.get(responseCode));
      return output;
    }
    if ((client == null) || (client.getStatus() == 0))
    {
      this.logger.error("The client " + userName + " does not exist");
      responseCode = "3000";
      output.setError(responseCode);
      output.setDescription((String)errorCodeMap.get(responseCode));
      return output;
    }
    if ((ws == null) || (ws.getStatus() == 0))
    {
      this.logger.error(wsCode + " webservice does not exist");
      responseCode = "2001";
      output.setError(responseCode);
      output.setDescription((String)errorCodeMap.get(responseCode));
      return output;
    }
    AccessManegement am = (AccessManegement)accessManagementMap.get(client.getUsername().toLowerCase() + "-" + ws.getWsCode().toLowerCase());
    if ((am == null) || (am.getStatus().intValue() == 0))
    {
      this.logger.error("The client " + userName + " does not have access right to call the service or status is inactive. ws_code = " + wsCode);
      responseCode = "6001";
      output.setError(responseCode);
      output.setDescription((String)errorCodeMap.get(responseCode));
      return output;
    }
    return null;
  }
  
  private synchronized void releaseRequest(String userName)
  {
    Long localLong1;
    Long localLong2;
    if (totalCurrentRequest.longValue() > 0L)
    {
      localLong1 = totalCurrentRequest;localLong2 = Charging.totalCurrentRequest = Long.valueOf(totalCurrentRequest.longValue() - 1L);
    }
    if (totalCurrentRequestForClient.longValue() > 0L)
    {
      localLong1 = totalCurrentRequestForClient;localLong2 = Charging.totalCurrentRequestForClient = Long.valueOf(totalCurrentRequestForClient.longValue() - 1L);
    }
    RequestInfo reqInfo = (RequestInfo)reqManager.get(userName);
    if (reqInfo != null) {
      reqInfo.setCurrentNumRequest(Integer.valueOf(reqInfo.getCurrentNumRequest().intValue() - 1));
    }
  }
  
  private String getTransId()
  {
    try
    {
      if (requestCount > 9999L) {
        requestCount = 0L;
      }
      requestCount += 1L;
      return DateTimeUtils.convertDateToString(new Date(), "yyyyMMddHHmmss") + requestCount;
    }
    catch (Exception ex) {}
    return "1";
  }
  
  private String prepareSoapInputForForm(Webservice ws, Input.RawData rawData)
  {
    XmlConfig soapMsg = new XmlConfig();
    try
    {
      String msgTemplate = ws.getMsgTemplate();
      StringBuilder soapContent = new StringBuilder();
      if (rawData == null) {
        return null;
      }
      String strRawData = rawData.getValue();
      if ((strRawData == null) || (strRawData.trim().equals("")) || (strRawData.equals("?"))) {
        return null;
      }
      int start = strRawData.indexOf("<");
      int end = strRawData.indexOf(">");
      String formNameTag = strRawData.substring(start, end + 1);
      String formNameTagClose = "</" + formNameTag.substring(1);
      

      int startBody = msgTemplate.indexOf(formNameTag);
      if (startBody == -1) {
        return null;
      }
      int endBody = msgTemplate.indexOf(formNameTagClose);
      soapContent.append(msgTemplate.substring(0, startBody));
      strRawData = strRawData.replaceAll("#gt#", ">");
      strRawData = strRawData.replaceAll("#lt#", "<");
      strRawData = strRawData.replaceAll("&lt;", "<");
      strRawData = strRawData.replaceAll("&gt;", "<");
      soapContent.append(strRawData);
      soapContent.append(msgTemplate.substring(endBody + formNameTagClose.length()));
      
      System.out.println("soapContent : " + soapContent);
      
      soapMsg.load(soapContent.toString());
      Element root = soapMsg.getDocument().getRootElement();
      String strFormTag = formNameTag.replace("<", "").replaceAll(">", "");
      String prefix = strFormTag.indexOf(":") > 0 ? strFormTag.substring(0, strFormTag.indexOf(":")) : "";
      if (!prefix.equals("")) {
        strFormTag = strFormTag.replaceAll(prefix + ":", "");
      }
      Element element = XmlUtil.findElement(root, strFormTag);
      Element parent = element.getParentElement();
      if (parent != null)
      {
        if ((ws.getWsUsernameTag() != null) && (!ws.getWsUsernameTag().equals("")))
        {
          String userName = "";
          if ((ws.getWsUsername() != null) && (!ws.getWsUsername().equals(""))) {
            userName = ws.getWsUsername();
          }
          Element ele = XmlUtil.findElement(parent, ws.getWsUsernameTag());
          if (ele != null) {
            ele.setText(userName);
          } else {
            parent.addContent(new Element(ws.getWsUsernameTag()).setText(userName));
          }
        }
        if ((ws.getWsPasswordTag() != null) && (!ws.getWsPasswordTag().equals("")))
        {
          String password = "";
          if ((ws.getWsPassword() != null) && (!ws.getWsPassword().equals(""))) {
            password = ws.getWsPassword();
          }
          Element ele = XmlUtil.findElement(parent, ws.getWsPasswordTag());
          if (ele != null) {
            ele.setText(password);
          } else {
            parent.addContent(new Element(ws.getWsPasswordTag()).setText(password));
          }
        }
      }
      XMLOutputter outputter = new XMLOutputter();
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      outputter.output(soapMsg.getDocument(), os);
      return new String(os.toByteArray());
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return null;
  }
  
  protected String prepareSoapInputV2(String username, Webservice ws, ArrayList<Input.Param> param)
  {
    XmlConfig soapMsg = new XmlConfig();
    try
    {
      String msgTemplate = ws.getMsgTemplate();
      soapMsg.load(msgTemplate);
      Element root = soapMsg.getDocument().getRootElement();
      Element parent = null;
      
      StringBuilder soapContent = new StringBuilder();
      

      boolean isStrictOrder = false;
      for (Input.Param input : param) {
        if ((input.getName().toUpperCase().equals("GWORDER")) && 
          (input.getValue().equals("1"))) {
          isStrictOrder = true;
        }
      }
      if (isStrictOrder)
      {
        String aParam = "";
        for (Input.Param input : param)
        {
          soapContent.append(username);
          if (!input.getName().toUpperCase().equals("GWORDER"))
          {
            aParam = input.getName();
            soapContent.append("|");
            soapContent.append(input.getValue());
          }
        }
        Element ele = XmlUtil.findElement(root, "input");
        if (ele == null)
        {
          ele = XmlUtil.findElement(root, aParam);
          if (ele == null) {
            return msgTemplate;
          }
        }
        ele.setText(soapContent.toString());
      }
      else
      {
        for (Input.Param input : param) {
          if (!input.getName().toUpperCase().equals("GWORDER"))
          {
            Element ele = XmlUtil.findElement(root, input.getName());
            if (ele == null)
            {
              System.out.println("[BCCSGW] [" + ws.getWsCode() + "] Cannot find " + input.getName() + " tag");
              if ((ws.getWsUsernameTag() != null) && (!ws.getWsUsernameTag().equals("")))
              {
                String userName = "";
                if ((ws.getWsUsername() != null) && (!ws.getWsUsername().equals(""))) {
                  userName = ws.getWsUsername();
                }
                Element element = XmlUtil.findElement(root, ws.getWsUsernameTag());
                if (element != null) {
                  element.setText(userName);
                }
              }
              if ((ws.getWsPasswordTag() != null) && (!ws.getWsPasswordTag().equals("")))
              {
                String password = "";
                if ((ws.getWsPassword() != null) && (!ws.getWsPassword().equals(""))) {
                  password = ws.getWsPassword();
                }
                Element element = XmlUtil.findElement(root, ws.getWsPasswordTag());
                if (element != null) {
                  element.setText(password);
                }
              }
              XMLOutputter outputter = new XMLOutputter();
              ByteArrayOutputStream os = new ByteArrayOutputStream();
              outputter.output(soapMsg.getDocument(), os);
              return new String(os.toByteArray());
            }
            ele.setText(input.getValue());
            parent = ele.getParentElement();
          }
        }
      }
      if (parent != null)
      {
        if ((ws.getWsUsernameTag() != null) && (!ws.getWsUsernameTag().equals("")))
        {
          String userName = "";
          if ((ws.getWsUsername() != null) && (!ws.getWsUsername().equals(""))) {
            userName = ws.getWsUsername();
          }
          Element element = XmlUtil.findElement(parent, ws.getWsUsernameTag());
          if (element != null) {
            element.setText(userName);
          } else {
            parent.addContent(new Element(ws.getWsUsernameTag()).setText(userName));
          }
        }
        if ((ws.getWsPasswordTag() != null) && (!ws.getWsPasswordTag().equals("")))
        {
          String password = "";
          if ((ws.getWsPassword() != null) && (!ws.getWsPassword().equals(""))) {
            password = ws.getWsPassword();
          }
          Element element = XmlUtil.findElement(parent, ws.getWsPasswordTag());
          if (element != null) {
            element.setText(password);
          } else {
            parent.addContent(new Element(ws.getWsPasswordTag()).setText(password));
          }
        }
      }
      XMLOutputter outputter = new XMLOutputter();
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      outputter.output(soapMsg.getDocument(), os);
      return new String(os.toByteArray());
    }
    catch (Exception ex)
    {
      System.out.println(ws.getMsgTemplate());
      ex.printStackTrace();
    }
    return "-1";
  }
  
  private synchronized void increase()
  {
    Long localLong1 = totalCurrentRequest;Long localLong2 = Charging.totalCurrentRequest = Long.valueOf(totalCurrentRequest.longValue() + 1L);
  }
  
  private synchronized void increaseRequestForClient(RequestInfo reqInfo)
  {
    if (reqInfo != null)
    {
      Long localLong1 = totalCurrentRequestForClient;Long localLong2 = Charging.totalCurrentRequestForClient = Long.valueOf(totalCurrentRequestForClient.longValue() + 1L);
      
      reqInfo.setCurrentNumRequest(Integer.valueOf(reqInfo.getCurrentNumRequest().intValue() + 1));
    }
  }
  
  protected static HashMap<String, RequestInfo> getReqManager()
  {
    return reqManager;
  }
  
  private RequestInfo getUserInfo(String username)
  {
    RequestInfo reqInfo = (RequestInfo)reqManager.get(username);
    if (reqInfo == null) {
      synchronized (RequestInfo.class)
      {
        reqInfo = new RequestInfo();
        reqInfo.setUsername(username);
        reqInfo.setCurrentNumRequest(Integer.valueOf(1));
        reqManager.put(username, reqInfo);
      }
    }
    return reqInfo;
  }
  
  private synchronized UserToken setUserToken(String username, String password, String ip, RequestInfo reqInfo)
    throws Exception
  {
    UserToken ut = new UserToken();
    VSAValidate vsaValidate = new VSAValidate();
    String domainCode = ResourceBundleUtils.getResource("domainCode");
    String urlValidate = ResourceBundleUtils.getResource("passportServiceUrl");
    vsaValidate.setUser(username);
    vsaValidate.setPassword(password);
    vsaValidate.setDomainCode(domainCode);
    
    vsaValidate.setCasValidateUrl(urlValidate);
    try
    {
      vsaValidate.validate();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      throw ex;
    }
    if (!vsaValidate.isAuthenticationSuccesful())
    {
      this.logger.error("Login failed, check user, password and IP");
      return null;
    }
    ut = vsaValidate.getUserToken();
    reqInfo.setUt(vsaValidate.getUserToken());
    reqInfo.setNearestActionTime(new Date());
    reqInfo.setIp(ip);
    reqInfo.setPassword(password);
    
    return ut;
  }
  
  private Output authenticateUser(String username, String password, String ip, Client client)
  {
    RequestInfo reqInfo = getUserInfo(username);
    
    increaseRequestForClient(reqInfo);
    String responseCode = "";
    Output output = new Output();
    if (reqInfo.getCurrentNumRequest().intValue() > client.getMaxRequest())
    {
      this.logger.warn("User: " + username + " Too many requests. Concurrent request: " + reqInfo.getCurrentNumRequest() + ". Max request: " + client.getMaxRequest() + ". Please wait until your other request finish");
      

      responseCode = "5000";
      output.setError(responseCode);
      output.setDescription((String)errorCodeMap.get(responseCode));
      return output;
    }
    if (client.getClientType() == 1) {
      if ((username != null) && (username.equalsIgnoreCase(client.getUsername())))
      {
        String useVsa = ResourceBundleUtils.getUseVsa();
        if ((useVsa != null) && (useVsa.equals("yes")))
        {
          UserToken ut = reqInfo.getUt();
          if (ut == null)
          {
            try
            {
              ut = setUserToken(username, password, ip, reqInfo);
              if (ut == null)
              {
                responseCode = "6003";
                output.setError(responseCode);
                output.setDescription((String)errorCodeMap.get(responseCode));
                return output;
              }
            }
            catch (Exception ex)
            {
              responseCode = "8004";
              output.setError(responseCode);
              output.setDescription((String)errorCodeMap.get(responseCode));
              return output;
            }
          }
          else
          {
            Calendar cal = Calendar.getInstance();
            Date lastActive = reqInfo.getNearestActionTime();
            cal.setTime(lastActive);
            int timeout = ResourceBundleUtils.getLoginTimeout();
            cal.add(13, timeout);
            if (cal.before(new Date()))
            {
              try
              {
                ut = setUserToken(username, password, ip, reqInfo);
                if (ut == null)
                {
                  responseCode = "6003";
                  output.setError(responseCode);
                  output.setDescription((String)errorCodeMap.get(responseCode));
                  return output;
                }
              }
              catch (Exception ex)
              {
                responseCode = "8004";
                output.setError(responseCode);
                output.setDescription((String)errorCodeMap.get(responseCode));
                return output;
              }
            }
            else if (!reqInfo.getIp().equals(ip))
            {
              try
              {
                ut = setUserToken(username, password, ip, reqInfo);
                if (ut == null)
                {
                  responseCode = "6003";
                  output.setError(responseCode);
                  output.setDescription((String)errorCodeMap.get(responseCode));
                  return output;
                }
              }
              catch (Exception ex)
              {
                responseCode = "8004";
                output.setError(responseCode);
                output.setDescription((String)errorCodeMap.get(responseCode));
                return output;
              }
            }
            else if ((reqInfo.getPassword() != null) && (!reqInfo.getPassword().equals(password)))
            {
              responseCode = "8001";
              output.setError(responseCode);
              output.setDescription((String)errorCodeMap.get(responseCode));
              return output;
            }
          }
        }
        else
        {
          String strPassEncryt = "";
          try
          {
            strPassEncryt = PasswordGateway.encrypt(password);
            String passDB = client.getPassword();
            if ((strPassEncryt != null) && (strPassEncryt.equals(passDB)))
            {
              if (StringUtils.validateIP(ip, client.getIpAddress())) {
                return null;
              }
              responseCode = "8000";
              output.setError(responseCode);
              output.setDescription((String)errorCodeMap.get(responseCode));
              return output;
            }
            responseCode = "8001";
            output.setError(responseCode);
            output.setDescription((String)errorCodeMap.get(responseCode));
            return output;
          }
          catch (Exception ex)
          {
            responseCode = "9001";
            output.setError(responseCode);
            output.setDescription((String)errorCodeMap.get(responseCode) + ". " + ex.getMessage());
            return output;
          }
        }
      }
      else
      {
        responseCode = "8002";
        output.setError(responseCode);
        String str = (String)errorCodeMap.get(responseCode);
        str = str.replaceAll("[$]username[$]", username);
        output.setDescription(str);
        return output;
      }
    }
    return null;
  }
  
  protected void saveLogDB(String seqRequest, String userName, String wsCode, String request, String isdn, String ip, OutputCharging response, String responseCode, Date startDate, Date endDate, Long vasDuration, long duration, Exception exp, String resultCode, boolean hasAlert, String original)
  {
    try
    {
      if (request != null) {
        request = request.replaceAll("\t", "#@t@#").replaceAll("\n", "#@n@#");
      }
      String strMessage = "";
      String strAlert = "";
      if (response != null)
      {
        if ((response.getErrorCode() != null) && (!response.getErrorCode().equals(""))) {
          strMessage = strMessage + "<error>" + response.getErrorCode() + "</error>" + "#@n@#";
        }
        if ((response.getMessage() != null) && (!response.getMessage().equals(""))) {
          strMessage = strMessage + "<description>" + response.getMessage() + "</description>" + "#@n@#";
        }
      }
      if ((strAlert != null) && (!strAlert.equals(""))) {
        strAlert = strAlert.replaceAll("\t", "#@t@#").replaceAll("\n", "#@n@#");
      }
      String strResponse = strMessage + "<return>" + "#@n@#" + strAlert + "#@n@#" + "</return>" + "#@n@#";
      
      strResponse = strResponse + "<original>" + original + "</original>";
      String exception = exp != null ? StringUtils.getTraceException(exp) : "";
      if ((exception != null) && (!exception.equals(""))) {
        exception = exception.replaceAll("\t", "#@t@#").replaceAll("\n", "#@n@#");
      }
      String str = this.appName + "@@@" + seqRequest + "@@@" + userName + "@@@" + wsCode + "@@@" + request + "@@@" + ip + "@@@" + isdn + "@@@" + responseCode + "@@@" + strResponse + "@@@" + (startDate != null ? DateTimeUtils.convertDateToString(startDate, "dd/MM/yyyy HH:mm:ss.SSS") : null) + "@@@" + (endDate != null ? DateTimeUtils.convertDateToString(endDate, "dd/MM/yyyy HH:mm:ss.SSS") : null) + "@@@" + (vasDuration != null ? vasDuration.toString() : "") + "@@@" + duration + "@@@" + resultCode + "@@@" + (exception != null ? exception : "");
      








      str = str.replaceAll("\r", "#@t@#");
      logWriter.writeLn(str);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  protected void saveActionWSDB(String seqRequest, String wsCode, String request, String response, Date startDate, Date endDate, long duration, Exception exp)
  {
    try
    {
      if (request != null) {
        request = request.replaceAll("\t", "#@t@#").replaceAll("\n", "#@n@#");
      }
      if (response != null) {
        response = response.replaceAll("\t", "#@t@#").replaceAll("\n", "#@n@#");
      }
      String exception = exp != null ? StringUtils.getTraceException(exp) : "";
      if ((exception != null) && (!exception.equals(""))) {
        exception = exception.replaceAll("\t", "#@t@#").replaceAll("\n", "#@n@#");
      }
      String ip = "";
      try
      {
        InetAddress thisIp = InetAddress.getLocalHost();
        ip = thisIp.getHostAddress();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      String str = this.appName + "@@@" + seqRequest + "@@@" + wsCode + "@@@" + request + "@@@" + ip + "@@@" + response + "@@@" + (startDate != null ? DateTimeUtils.convertDateToString(startDate, "dd/MM/yyyy HH:mm:ss.SSS") : null) + "@@@" + (endDate != null ? DateTimeUtils.convertDateToString(endDate, "dd/MM/yyyy HH:mm:ss.SSS") : null) + "@@@" + duration + "@@@" + (exception != null ? exception : "");
      



      str = str.replaceAll("\r", "#@t@#");
      logWSWriter.writeLn(str);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  protected String getInputFromParamList(ArrayList<Input.Param> param)
  {
    StringBuffer isdn = new StringBuffer();
    int i;
    if ((param != null) && (param.size() > 0))
    {
      i = 0;
      for (Input.Param input : param)
      {
        i++;
        if (!input.getName().toUpperCase().equals("GWORDER"))
        {
          isdn.append(input.getValue());
          if (i != param.size()) {
            isdn.append("#@n@#");
          }
        }
      }
    }
    return isdn.toString();
  }
  
  protected String formatRequest(String userName, String password, String wsCode, ArrayList<Input.Param> param, Input.RawData rawData)
  {
    String strReturn = "<vas:Input>";
    strReturn = strReturn + "\n\t<username>" + userName + "</username>";
    strReturn = strReturn + "\n\t<password>" + password + "</password>";
    strReturn = strReturn + "\n\t<wscode>" + wsCode + "</wscode>";
    if ((param != null) && (param.size() > 0)) {
      for (int i = 0; i < param.size(); i++)
      {
        Input.Param paramDetail = (Input.Param)param.get(i);
        strReturn = strReturn + "\n\t<param name=\"" + paramDetail.getName() + "\" value=\"" + paramDetail.getValue() + "\"/>";
      }
    }
    if (rawData != null) {
      strReturn = strReturn + "\n\t<rawData>" + rawData.getValue() + "</rawData>";
    }
    strReturn = strReturn + "\n</vas:Input>";
    return strReturn;
  }
  
  protected String httpCallRaw(String username, Webservice ws, ArrayList<Input.Param> input, Input.RawData rawData, String seqRequest, String wsCode, String value, long begin)
  {
    Date startDate = new Date();
    Exception exception = null;
    int timeout = 30000;
    if ((ws.getTimeout() != null) && (ws.getTimeout().intValue() > 0)) {
      timeout = ws.getTimeout().intValue();
    }
    initConnection(timeout);
    String httpPost = ws.getWsdl();
    PostMethod post = new PostMethod(httpPost);
    String soapResponse = "";
    String request = "";
    try
    {
      request = prepareSoapInputForForm(ws, rawData);
      if (request == null) {
        request = prepareSoapInputV2(username, ws, input);
      }
      if ((request == null) || (request.equals("-1"))) {
        return "Error when prepare soap input";
      }
      Object entity = new StringRequestEntity(request, "text/xml", "UTF-8");
      post.setRequestEntity((RequestEntity)entity);
      try
      {
        this.httpTransport.executeMethod(post);
      }
      catch (IOException ex)
      {
        this.logger.error("user: " + username + ", webservice: " + wsCode + ". Error: " + ex.getMessage());
        if (this.httpTransport == null) {
          throw new Exception("Can not reconnect.");
        }
        throw ex;
      }
      soapResponse = post.getResponseBodyAsString(6096000);
      return soapResponse;
    }
    catch (Exception ex)
    {
      this.logger.error("Send http request to " + httpPost + " error:" + ex.getMessage());
      
      exception = ex;
      






      return ex.getMessage();
    }
    finally
    {
      post.releaseConnection();
      saveActionWSDB(seqRequest, wsCode, request, soapResponse, startDate, new Date(), System.currentTimeMillis() - begin, exception);
      

      warningResponseSlow(Long.valueOf(System.currentTimeMillis() - startDate.getTime()), username, ws, this.logger);
    }
  }
  
  private void initConnection(int timeout)
  {
    if (conMgr == null)
    {
      conMgr = new MultiThreadedHttpConnectionManager();
      conMgr.setMaxConnectionsPerHost(20000);
      conMgr.setMaxTotalConnections(20000);
    }
    if (this.httpTransport == null) {
      this.httpTransport = new HttpClient(conMgr);
    }
    HttpConnectionManager conMgr1 = this.httpTransport.getHttpConnectionManager();
    
    HttpConnectionManagerParams conPars = conMgr1.getParams();
    
    conPars.setMaxTotalConnections(2000);
    conPars.setConnectionTimeout(30000);
    conPars.setSoTimeout(timeout);
  }
  
  private String getIpClient()
  {
    MessageContext msgCtxt = this.wsContext.getMessageContext();
    HttpExchange httpEx = (HttpExchange)msgCtxt.get("com.sun.xml.ws.http.exchange");
    if (httpEx == null) {
      httpEx = (HttpExchange)msgCtxt.get("com.sun.xml.internal.ws.http.exchange");
    }
    if (httpEx == null) {
      return "127.0.0.1";
    }
    return httpEx.getRemoteAddress().getAddress().getHostAddress();
  }
}

