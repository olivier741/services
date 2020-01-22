/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.gateway.server;

import com.tatsinktechnologic.client.restfull.RemoteAPI_Rest;
import com.tatsinktechnologic.client.restfull.Request;
import com.tatsinktechnologic.client.soap.RemoteAPI_Soap;
import com.tatsinktechnologic.gateway.security.Secured_tokenAuth;
import com.tatsinktechnologic.dao_commun_repository.CommunDAO_Repository;
import com.tatsinktechnologic.dao_controller.WS_Transaction_LogJpaController;
import com.tatsinktechnologic.dao_controller.WS_Billing_HistJpaController;
import com.tatsinktechnologic.entities.api_gateway.Rest_Method;
import com.tatsinktechnologic.entities.api_gateway.Type_API;
import com.tatsinktechnologic.entities.api_gateway.WS_AccessManagement;
import com.tatsinktechnologic.entities.api_gateway.WS_Billing_Hist;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_API;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_WebserviceRel;
import com.tatsinktechnologic.entities.api_gateway.WS_Header_Param;
import com.tatsinktechnologic.entities.api_gateway.WS_Transaction_Log;
import com.tatsinktechnologic.entities.api_gateway.WS_Webservice;
import com.tatsinktechnologic.gateway.bean.Param;
import com.tatsinktechnologic.gateway.bean.Request_log;
import com.tatsinktechnologic.gateway.bean.Soap_Resp;
import com.tatsinktechnologic.gateway.bean.WS_Block_Response;
import com.tatsinktechnologic.gateway.bean.WS_Request;
import com.tatsinktechnologic.gateway.bean.WS_Response;
import com.tatsinktechnologic.gateway.bean.Ws_Block_Request;
import com.tatsinktechnologic.gateway.util.ConverterXML_JSON;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.ws.rs.core.Response;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;

/**
 *
 * @author olivier.tatsinkou
 */
@Path("/api_service")
@Produces(MediaType.APPLICATION_XML)
public class API_GatewayService_userTokenAuth {

    private static Logger logger = Logger.getLogger(API_GatewayService_userTokenAuth.class);
    private static CommunDAO_Repository communConf = CommunDAO_Repository.getConfigurationLoader();
    private static WS_Transaction_LogJpaController trans_log_controller = new WS_Transaction_LogJpaController(communConf.getEmf());
    private static WS_Billing_HistJpaController billing_controller = new WS_Billing_HistJpaController(communConf.getEmf());

    @Context
    SecurityContext securityContext;

    @Secured_tokenAuth
    @POST
    @Path("/sendRequest")
    @Consumes(MediaType.APPLICATION_XML)
    public Response sendRequest(WS_Request ws_resquest, @Context SecurityContext securityContext) {

        Timestamp beforeRequest_time = new Timestamp(System.currentTimeMillis());
        Status status_err = Response.Status.OK;
        String msisdn = ws_resquest.getMsisdn();
        String trans_id = ws_resquest.getReg_transaction_id();
        long charge_amount = ws_resquest.getCharge_amount();
        String charge_reason = ws_resquest.getCharge_reason();
        String ws_client_name = ws_resquest.getWs_client_name();
        String ws_block_api_name = ws_resquest.getWs_block_api_name();
        List<Param> listParam = ws_resquest.getWSparam();

        WS_AccessManagement accessMng = communConf.getSETACCESSMANAGEMENT().get(ws_client_name + "_" + ws_block_api_name);
        WS_Block_Response ws_block_response = new WS_Block_Response();
        ws_block_response.setWs_block_name(ws_block_api_name);
        Ws_Block_Request ws_block_request = new Ws_Block_Request();
        ws_block_request.setWs_block_name(ws_block_api_name);
        
        WS_Response ws_response = new WS_Response();
        ws_response.setMsisdn(msisdn);
        ws_response.setReg_trans_id(trans_id);
        
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        int ws_error = 0;
        WS_Block_API block_api = null;
        WS_Webservice webservice = null;
        
        Request_log req_log = new Request_log();
        req_log.setMsisdn(msisdn);
        req_log.setReg_trans_id(trans_id);
        
        Type_API api_type = null;

        Request request = new Request();
        MultivaluedMap<String, String> headers = null;
        if (accessMng != null) {

            block_api = accessMng.getWs_block_api();
            List<WS_Block_WebserviceRel> listWS_Block_WebserviceRel = new ArrayList<WS_Block_WebserviceRel>(block_api.getListWS_Block_WebserviceRel());

            for (WS_Block_WebserviceRel block_websrv : listWS_Block_WebserviceRel) {

                webservice = block_websrv.getWs_webservice();
                String ws_webserive_name = webservice.getWebservice_name();
                ws_response.setWS_name(ws_webserive_name);

                api_type = webservice.getWebservice_type();
                Soap_Resp soap_resp = null;
                Response rest_resp = null;

                if (api_type != null) {
                    switch (api_type) {
                        case SOAP:
                                soap_resp = RemoteAPI_Soap.consumeSoap(webservice, listParam);
                                ws_error = soap_resp.getSoap_erro_code();
                                req_log.setHeaders1(soap_resp.getSoap_head_request1());
                                req_log.setHeaders2(soap_resp.getSoap_head_request2());
                                req_log.setRest_request(soap_resp.getSoap_request());
                                req_log.setMethod(Rest_Method.SOAP);
                                req_log.setHost(webservice.getApi_url());
                                
                            break;
                        case REST:
                            String path_api = webservice.getApi_rest_uri();
                            String rest_req = webservice.getApi_rest_request();

                            request.setHost(webservice.getApi_url());
                            request.setUser_name(webservice.getSecurity_login());
                            request.setPassword(webservice.getSecurity_password());
                            request.setConnexion_timeout(webservice.getRemote_con_timeout());
                            request.setRequest_timeout(webservice.getRemote_req_timeout());
                            request.setContent_type(webservice.getContent_type());
                            request.setMedia_type(webservice.getRest_media_type());
                            request.setMethod(webservice.getApi_rest_method());
                            request.setSec_mode(webservice.getRemote_secutity_mode());

                            int con_timeout = webservice.getRemote_con_timeout();
                            int req_timeout = webservice.getRemote_req_timeout();

                            if (con_timeout > 0) {
                                request.setConnexion_timeout(con_timeout);
                            }

                            if (req_timeout > 0) {
                                request.setRequest_timeout(req_timeout);
                            }

                            for (Param param : listParam) {
                                rest_req = rest_req.replace(param.getAlias(), param.getValue());
                            }
                            request.setRest_request(rest_req);

                            for (Param param : listParam) {
                                path_api = path_api.replace(param.getAlias(), param.getValue());
                            }
                            request.setPath(path_api);

                            String authorizationType = webservice.getAuthorizationType();
                            String token = webservice.getToken();

                            headers = new MetadataMap();

                            if (!StringUtil.isBlank(token)) {
                                if (!StringUtil.isBlank(authorizationType)) {
                                    token = authorizationType + " " + token;
                                }

                                headers.add(HttpHeaders.AUTHORIZATION, token);
                            }

                            List<WS_Header_Param> listheaders_param = new ArrayList<WS_Header_Param>(webservice.getListWS_Header_Param());

                            if (listheaders_param != null && !listheaders_param.isEmpty()) {
                                for (WS_Header_Param head_param : listheaders_param) {
                                    String param_key = head_param.getParam_key();
                                    String param_val = head_param.getParam_value();
                                    for (Param param : listParam) {
                                        param_key = param_key.replace(param.getAlias(), param.getValue());
                                        param_val = param_val.replace(param.getAlias(), param.getValue());
                                    }
                                    headers.add(param_key, param_val);
                                }
                            }
                            request.setHeaders(headers);
                            req_log.setHeaders1(ConverterXML_JSON.convertMultiToRegularMap(headers));
                            req_log.setRest_request(request.getRest_request());
                            req_log.setHost(request.getHost());
                            
                            rest_resp = RemoteAPI_Rest.sendRestfullRequest(request);
                            ws_error = rest_resp.getStatus();

                            break;

                        default:
                            ws_response.setAPI_GW_Description("UNKNOW Type of API. Administrator must set SOAP/REST on " + ws_webserive_name);
                            ws_response.setAPI_GW_Error("44");
                            ws_response.setWS_Description("");
                            ws_response.setWS_ERROR("");
                            ws_response.setWS_ResponseContent("");
                            ws_response.setWS_request_time(timeStamp);
                            ws_response.setClient_IP(getIP());
                            ws_block_response.getListws_response().add(ws_response);
                            status_err = Response.Status.METHOD_NOT_ALLOWED;
                            break;

                    }

                } else {
                    ws_response.setAPI_GW_Description("Type of API is not define. Administrator must set SOAP/REST on " + ws_webserive_name);
                    ws_response.setAPI_GW_Error("45");
                    ws_response.setWS_Description("");
                    ws_response.setWS_ERROR("");
                    ws_response.setWS_ResponseContent("");
                    ws_response.setWS_request_time(timeStamp);
                    ws_response.setClient_IP(getIP());

                    ws_block_response.getListws_response().add(ws_response);
                    status_err = Response.Status.METHOD_NOT_ALLOWED;

                }

                ws_response.setAPI_GW_Description("SUCCESS PROCESS : " + ws_webserive_name);
                ws_response.setAPI_GW_Error("00");
                ws_response.setWS_Description("");
                ws_response.setWS_ERROR(String.valueOf(ws_error));
                
                switch (api_type) {
                        case SOAP:
                            if (soap_resp!=null) ws_response.setWS_ResponseContent(soap_resp.getSoap_response());
                            break;
                        case REST:
                            if (rest_resp!=null) ws_response.setWS_ResponseContent(rest_resp.readEntity(String.class));
                            break;
                }
                
                ws_response.setWS_request_time(timeStamp);
                ws_response.setClient_IP(getIP());

                ws_block_response.getListws_response().add(ws_response);
                status_err = Response.Status.OK;

                req_log.setConnexion_timeout(request.getConnexion_timeout());
                req_log.setContent_type(request.getContent_type());
                req_log.setMedia_type(request.getMedia_type());

                

                req_log.setMethod(request.getMethod());
                req_log.setSec_mode(request.getSec_mode());
                req_log.setPassword(request.getPassword());
                req_log.setUser_name(request.getUser_name());
                req_log.setPath(request.getPath());
                req_log.setRequest_timeout(request.getRequest_timeout());

                ws_block_request.getListws_request().add(req_log);
            }

        } else {

            ws_response.setAPI_GW_Description(ws_client_name + " and " + ws_block_api_name + " don't define in API gateway");
            ws_response.setAPI_GW_Error("55");
            ws_response.setWS_Description("");
            ws_response.setWS_ERROR("");
            ws_response.setWS_ResponseContent("");
            ws_response.setWS_request_time(timeStamp);
            ws_response.setClient_IP(getIP());

            ws_block_response.getListws_response().add(ws_response);
            status_err = Response.Status.BAD_GATEWAY;

        }
       
        Timestamp receive_time = new Timestamp(System.currentTimeMillis());
        long diffInMS = (receive_time.getTime() - beforeRequest_time.getTime());
        
        if ( charge_amount != 0  || !StringUtils.isBlank(charge_reason)){
            WS_Billing_Hist bill_his = new WS_Billing_Hist();
            bill_his.setAccess_management(accessMng);
            bill_his.setCharge_amount(charge_amount);
            bill_his.setCharge_error(String.valueOf(ws_error));
            bill_his.setCharge_reason(charge_reason);
            bill_his.setClient_name(ws_client_name);
            bill_his.setDuration(diffInMS);
            bill_his.setMsisdn(msisdn);
            bill_his.setClient_IP(getIP());
            if (webservice != null) {
                bill_his.setOperator(webservice.getRemote_operator());
                bill_his.setWebservice_name(webservice.getWebservice_name());
            }
            bill_his.setReg_transaction_id(trans_id);
            
            billing_controller.create(bill_his);
        }
        
        
        
        WS_Transaction_Log trans_log = new WS_Transaction_Log();
        trans_log.setAccess_management(accessMng);
        trans_log.setClient_name(ws_client_name);
        trans_log.setDuration(diffInMS);
        trans_log.setError_code(String.valueOf(ws_error));
        trans_log.setMsisdn(msisdn);
        trans_log.setClient_IP(getIP());

        trans_log.setReg_transaction_id(trans_id);
        trans_log.setRequest(ConverterXML_JSON.convertWs_Block_RequesttoXML(ws_block_request));
        trans_log.setResponse(ConverterXML_JSON.convertWS_Block_ResponseToXML(ws_block_response));

        if (webservice != null) {
            trans_log.setOperator(webservice.getRemote_operator());
            trans_log.setWebservice_name(webservice.getWebservice_name());
        }

        trans_log_controller.create(trans_log);

        return Response.status(status_err)
                .entity(ws_block_response)
                .build();
    }

    private String getIP() {

        Message message = PhaseInterceptorChain.getCurrentMessage();
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        String callerIpAddress = request.getRemoteAddr();

        return callerIpAddress;
    }
}
