/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.config;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.Serializable;

public abstract class Formatter
  implements Serializable
{
  public abstract boolean verify(String paramString);
}
