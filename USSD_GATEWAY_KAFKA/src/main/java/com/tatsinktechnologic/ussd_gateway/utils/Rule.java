 package com.tatsinktechnologic.ussd_gateway.utils;
 
 import org.apache.log4j.Logger;
 import org.nfunk.jep.JEP;
 
 public class Rule
 {
   protected String expression;
   protected Logger logger;
   protected JEP parser;
   
   public Rule()
   {
     this.logger = Logger.getLogger("Rule");
     this.parser = new JEP();
     this.parser.addStandardFunctions();
     this.parser.addStandardConstants();
   }
   
   public Rule(String logName)
   {
     this.logger = Logger.getLogger(logName);
     this.parser = new JEP();
     this.parser.addStandardFunctions();
     this.parser.addStandardConstants();
   }
   
   public void setLogger(Logger logger)
   {
     this.logger = logger;
   }
   
   public String getExpression()
   {
     return this.expression;
   }
   
   public void setExpression(String expression)
   {
     this.logger.debug("expression=" + expression);
     this.expression = expression;
   }
   
   public void setVariable(String name, Object value)
   {
     this.logger.debug("add variable (" + name + "=" + value + ")");
     this.parser.addVariable(name, value);
   }
   
   public boolean parser()
   {
     this.logger.debug("begin parsing rule");
     this.logger.debug("expression:" + this.expression);
     try
     {
       this.parser.parseExpression(this.expression);
     }
     catch (Exception ex)
     {
       this.logger.error("exception when try to parse expression.", ex);
       return false;
     }
     if (this.parser.hasError())
     {
       this.logger.debug("Error while parsing, return false");
       this.logger.debug(this.expression);
       return false;
     }
     double value = this.parser.getValue();
     this.logger.debug("parse rule result =" + value);
     return value == 1.0D;
   }
 }



