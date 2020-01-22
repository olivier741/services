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
public class PairVasReqRsp
{
  private VasRequest req;
  private VasResponse rsp;
  
  public PairVasReqRsp(VasRequest req, VasResponse rsp)
  {
    this.req = req;
    this.rsp = rsp;
  }
  
  public VasRequest getReq()
  {
    return this.req;
  }
  
  public VasResponse getRsp()
  {
    return this.rsp;
  }
}