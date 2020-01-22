//package com.viettel.bccsgw.utils;
// 
// import org.apache.log4j.Logger;
// 
// public class Rule
// {
//   protected String expression;
//   protected Logger logger;
//   protected JEP parser;
//   
//   public Rule()
//   {
//     this.logger = Logger.getLogger("Rule");
//     this.parser = new JEP();
//     this.parser.addStandardFunctions();
//     this.parser.addStandardConstants();
//   }
//   
//   public Rule(String logName)
//   {
//     this.logger = Logger.getLogger(logName);
//     this.parser = new JEP();
//     this.parser.addStandardFunctions();
//     this.parser.addStandardConstants();
//   }
//   
//   public void setLogger(Logger logger)
//   {
//     this.logger = logger;
//   }
//   
//   public String getExpression()
//   {
//     return this.expression;
//   }
//   
//   public void setExpression(String expression)
//   {
//     this.logger.debug("expression=" + expression);
//     this.expression = expression;
//   }
//   
//   public void setVariable(String name, Object value)
//   {
//     this.logger.debug("add variable (" + name + "=" + value.toString() + ")");
//     this.parser.addVariable(name, value);
//   }
//   
//   public boolean parser()
//   {
//     this.logger.debug("begin parsing rule");
//     this.logger.debug("expression:" + this.expression);
//     this.parser.parseExpression(this.expression);
//     if (this.parser.hasError())
//     {
//       this.logger.debug("Error while parsing, return false");
//       this.logger.debug(this.expression);
//       this.logger.debug(this.parser.getErrorInfo());
//       return false;
//     }
//     double value = this.parser.getValue();
//     this.logger.debug("parse rule result =" + value);
//     return value == 1.0D;
//   }
// }
