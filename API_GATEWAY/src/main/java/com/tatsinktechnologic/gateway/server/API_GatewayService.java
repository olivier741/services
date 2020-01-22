package com.tatsinktechnologic.gateway.server;

///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.tatsinktechnologic.resfull.server;
//
///**
// *
// * @author olivier.tatsinkou
// */
////JAX-RS Imports
//
//import com.tatsinktechnologic.dao_commun_repository.CommunDAO_Repository;
//import com.tatsinktechnologic.entities.api_gateway.WS_AccessManagement;
//import com.tatsinktechnologic.entities.api_gateway.Type_API;
//import com.tatsinktechnologic.entities.api_gateway.WS_Webservice;
//import com.tatsinktechnologic.resfull.bean.Param;
//import com.tatsinktechnologic.resfull.bean.WS_Request;
//import java.util.List;
//
//import javax.ws.rs.Consumes;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.Response;
//
//@Path("/api_gateway_service")
//@Produces("application/xml")
//public class API_GatewayService {
//
//    private static CommunDAO_Repository communConf = CommunDAO_Repository.getConfigurationLoader();
//    
//    @POST
//    @Path("/category")
//    @Consumes("application/xml")
//    public Response sendRequest(WS_Request resquest) {
//        
//          String ws_client_name = resquest.getWs_client_name();
//          String ws_webserive_name = resquest.getWs_webservice_name();
//          List<Param> listParam = resquest.getWSparam();
//          
//          WS_AccessManagement accessMng = communConf.getSETACCESSMANAGEMENT().get(ws_client_name+"_"+ws_webserive_name);
//          
//          if (accessMng!= null){
//              
//              WS_Webservice webservice = accessMng.getWs_webservice();
//              Type_API api_type = webservice.getWebservice_type();
//              
//              if (api_type!=null){
//                    if (api_type==Type_API.SOAP){
//                  
//                    }else if (api_type==Type_API.REST){
//
//                    }else{
//                        return Response.status(Response.Status.METHOD_NOT_ALLOWED)
//                               .entity("44|UNKNOW Type of API. Administrator must set SOAP/REST on "+ws_webserive_name)
//                               .build();
//                    }
//              }else{
//                  return Response.status(Response.Status.METHOD_NOT_ALLOWED)
//                               .entity("45|Type of API is not define. Administrator must set SOAP/REST on "+ws_webserive_name)
//                               .build();
//              }
//             
//              
//                return Response.status(Response.Status.OK)
//                               .entity("00|SUCCESS PROCESS ")
//                               .build();
//          }else{
//              
//              
//               return Response.status(Response.Status.BAD_GATEWAY)
//                              .entity("55|"+ws_client_name+" and "+ws_webserive_name+" don't define in API gateway" )
//                              .build();
//          }
//          
//         
//
//    }
//    
//   
//    
//
//}
