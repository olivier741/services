/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.authenticator;

/**
 *
 * @author olivier.tatsinkou
 */
import java.util.List;

public abstract interface MmAuthenticatorMBean
{
  public abstract List<String> getAccessibleMethods(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract String vsaURL();
}
