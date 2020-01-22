package com.viettel.bccsgw.utils;

import org.apache.log4j.Logger;

public class ConfigParser
  extends ConfigLoader
{
  public ConfigParser(String name)
  {
    super(name);
  }
  
  protected void onTag(String name)
  {
    this.logger.debug("onTag[" + name + "]");
  }
  
  protected void onValue(String name, String value)
  {
    this.logger.debug("onValue[" + name + "=" + value + "]");
  }
  
  protected void onValue(String name, String[] value)
  {
    this.logger.debug("onValues[" + name + "={" + join(value, ",") + "}]");
  }
  
  protected void onEndFile()
  {
    this.logger.debug("on EOF");
  }
  
  /* Error */
  public void parser(String configFile)
    throws java.io.FileNotFoundException, java.io.IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: new 18	java/io/FileReader
    //   7: dup
    //   8: aload_1
    //   9: invokespecial 19	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   12: astore_2
    //   13: new 20	java/io/BufferedReader
    //   16: dup
    //   17: aload_2
    //   18: invokespecial 21	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   21: astore_3
    //   22: aload_3
    //   23: invokevirtual 22	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   26: astore 4
    //   28: aload 4
    //   30: ifnonnull +88 -> 118
    //   33: aload_0
    //   34: invokevirtual 23	utils/ConfigParser:onEndFile	()V
    //   37: aload_3
    //   38: ifnull +39 -> 77
    //   41: aload_3
    //   42: invokevirtual 24	java/io/BufferedReader:close	()V
    //   45: goto +32 -> 77
    //   48: astore 5
    //   50: aload_0
    //   51: getfield 2	utils/ConfigParser:logger	Lorg/apache/log4j/Logger;
    //   54: new 3	java/lang/StringBuilder
    //   57: dup
    //   58: invokespecial 4	java/lang/StringBuilder:<init>	()V
    //   61: ldc 26
    //   63: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   66: aload 5
    //   68: invokevirtual 27	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   71: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   74: invokevirtual 28	org/apache/log4j/Logger:error	(Ljava/lang/Object;)V
    //   77: aload_2
    //   78: ifnull +39 -> 117
    //   81: aload_2
    //   82: invokevirtual 29	java/io/FileReader:close	()V
    //   85: goto +32 -> 117
    //   88: astore 5
    //   90: aload_0
    //   91: getfield 2	utils/ConfigParser:logger	Lorg/apache/log4j/Logger;
    //   94: new 3	java/lang/StringBuilder
    //   97: dup
    //   98: invokespecial 4	java/lang/StringBuilder:<init>	()V
    //   101: ldc 30
    //   103: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   106: aload 5
    //   108: invokevirtual 27	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   111: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   114: invokevirtual 28	org/apache/log4j/Logger:error	(Ljava/lang/Object;)V
    //   117: return
    //   118: aload 4
    //   120: ldc 31
    //   122: invokevirtual 32	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   125: ifne -103 -> 22
    //   128: aload 4
    //   130: ldc 33
    //   132: invokevirtual 32	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   135: ifeq +6 -> 141
    //   138: goto -116 -> 22
    //   141: aload 4
    //   143: ldc 34
    //   145: invokevirtual 32	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   148: ifeq +43 -> 191
    //   151: aload 4
    //   153: ldc 7
    //   155: invokevirtual 35	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   158: ifeq +33 -> 191
    //   161: aload 4
    //   163: invokevirtual 36	java/lang/String:length	()I
    //   166: iconst_1
    //   167: isub
    //   168: istore 5
    //   170: iload 5
    //   172: iconst_1
    //   173: if_icmple +15 -> 188
    //   176: aload_0
    //   177: aload 4
    //   179: iconst_1
    //   180: iload 5
    //   182: invokevirtual 37	java/lang/String:substring	(II)Ljava/lang/String;
    //   185: invokevirtual 38	utils/ConfigParser:onTag	(Ljava/lang/String;)V
    //   188: goto -166 -> 22
    //   191: aload 4
    //   193: ldc 11
    //   195: iconst_0
    //   196: invokevirtual 39	java/lang/String:indexOf	(Ljava/lang/String;I)I
    //   199: ifle -177 -> 22
    //   202: aload 4
    //   204: ldc 11
    //   206: iconst_0
    //   207: invokevirtual 39	java/lang/String:indexOf	(Ljava/lang/String;I)I
    //   210: aload 4
    //   212: invokevirtual 36	java/lang/String:length	()I
    //   215: iconst_1
    //   216: isub
    //   217: if_icmpge -195 -> 22
    //   220: aload 4
    //   222: bipush 61
    //   224: invokevirtual 40	java/lang/String:indexOf	(I)I
    //   227: istore 5
    //   229: aload 4
    //   231: iconst_0
    //   232: iload 5
    //   234: invokevirtual 37	java/lang/String:substring	(II)Ljava/lang/String;
    //   237: invokevirtual 41	java/lang/String:trim	()Ljava/lang/String;
    //   240: astore 6
    //   242: aload 4
    //   244: iload 5
    //   246: iconst_1
    //   247: iadd
    //   248: invokevirtual 42	java/lang/String:substring	(I)Ljava/lang/String;
    //   251: invokevirtual 41	java/lang/String:trim	()Ljava/lang/String;
    //   254: astore 7
    //   256: aload 7
    //   258: ldc 43
    //   260: invokevirtual 32	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   263: ifeq +92 -> 355
    //   266: aload 7
    //   268: ldc 44
    //   270: invokevirtual 35	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   273: ifeq +82 -> 355
    //   276: aload 7
    //   278: iconst_1
    //   279: aload 7
    //   281: invokevirtual 36	java/lang/String:length	()I
    //   284: iconst_1
    //   285: isub
    //   286: invokevirtual 37	java/lang/String:substring	(II)Ljava/lang/String;
    //   289: invokestatic 45	utils/ConfigParser:format	(Ljava/lang/String;)Ljava/lang/String;
    //   292: astore 7
    //   294: new 46	java/util/StringTokenizer
    //   297: dup
    //   298: aload 7
    //   300: ldc 14
    //   302: invokespecial 47	java/util/StringTokenizer:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   305: astore 8
    //   307: aload 8
    //   309: invokevirtual 48	java/util/StringTokenizer:countTokens	()I
    //   312: anewarray 49	java/lang/String
    //   315: astore 9
    //   317: iconst_0
    //   318: istore 5
    //   320: aload 8
    //   322: invokevirtual 50	java/util/StringTokenizer:hasMoreTokens	()Z
    //   325: ifeq +19 -> 344
    //   328: aload 9
    //   330: iload 5
    //   332: iinc 5 1
    //   335: aload 8
    //   337: invokevirtual 51	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   340: aastore
    //   341: goto -21 -> 320
    //   344: aload_0
    //   345: aload 6
    //   347: aload 9
    //   349: invokevirtual 52	utils/ConfigParser:onValue	(Ljava/lang/String;[Ljava/lang/String;)V
    //   352: goto +14 -> 366
    //   355: aload_0
    //   356: aload 6
    //   358: aload 7
    //   360: invokestatic 45	utils/ConfigParser:format	(Ljava/lang/String;)Ljava/lang/String;
    //   363: invokevirtual 53	utils/ConfigParser:onValue	(Ljava/lang/String;Ljava/lang/String;)V
    //   366: goto -344 -> 22
    //   369: astore 10
    //   371: aload_3
    //   372: ifnull +39 -> 411
    //   375: aload_3
    //   376: invokevirtual 24	java/io/BufferedReader:close	()V
    //   379: goto +32 -> 411
    //   382: astore 11
    //   384: aload_0
    //   385: getfield 2	utils/ConfigParser:logger	Lorg/apache/log4j/Logger;
    //   388: new 3	java/lang/StringBuilder
    //   391: dup
    //   392: invokespecial 4	java/lang/StringBuilder:<init>	()V
    //   395: ldc 26
    //   397: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   400: aload 11
    //   402: invokevirtual 27	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   405: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   408: invokevirtual 28	org/apache/log4j/Logger:error	(Ljava/lang/Object;)V
    //   411: aload_2
    //   412: ifnull +39 -> 451
    //   415: aload_2
    //   416: invokevirtual 29	java/io/FileReader:close	()V
    //   419: goto +32 -> 451
    //   422: astore 11
    //   424: aload_0
    //   425: getfield 2	utils/ConfigParser:logger	Lorg/apache/log4j/Logger;
    //   428: new 3	java/lang/StringBuilder
    //   431: dup
    //   432: invokespecial 4	java/lang/StringBuilder:<init>	()V
    //   435: ldc 30
    //   437: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   440: aload 11
    //   442: invokevirtual 27	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   445: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   448: invokevirtual 28	org/apache/log4j/Logger:error	(Ljava/lang/Object;)V
    //   451: aload 10
    //   453: athrow
    // Line number table:
    //   Java source line #40	-> byte code offset #0
    //   Java source line #41	-> byte code offset #2
    //   Java source line #43	-> byte code offset #4
    //   Java source line #44	-> byte code offset #13
    //   Java source line #47	-> byte code offset #22
    //   Java source line #48	-> byte code offset #28
    //   Java source line #49	-> byte code offset #33
    //   Java source line #78	-> byte code offset #37
    //   Java source line #80	-> byte code offset #41
    //   Java source line #83	-> byte code offset #45
    //   Java source line #81	-> byte code offset #48
    //   Java source line #82	-> byte code offset #50
    //   Java source line #85	-> byte code offset #77
    //   Java source line #87	-> byte code offset #81
    //   Java source line #90	-> byte code offset #85
    //   Java source line #88	-> byte code offset #88
    //   Java source line #89	-> byte code offset #90
    //   Java source line #90	-> byte code offset #117
    //   Java source line #51	-> byte code offset #118
    //   Java source line #52	-> byte code offset #138
    //   Java source line #53	-> byte code offset #141
    //   Java source line #54	-> byte code offset #161
    //   Java source line #55	-> byte code offset #170
    //   Java source line #56	-> byte code offset #176
    //   Java source line #58	-> byte code offset #188
    //   Java source line #59	-> byte code offset #220
    //   Java source line #60	-> byte code offset #229
    //   Java source line #61	-> byte code offset #242
    //   Java source line #62	-> byte code offset #256
    //   Java source line #63	-> byte code offset #276
    //   Java source line #64	-> byte code offset #294
    //   Java source line #65	-> byte code offset #307
    //   Java source line #66	-> byte code offset #317
    //   Java source line #67	-> byte code offset #320
    //   Java source line #68	-> byte code offset #328
    //   Java source line #70	-> byte code offset #344
    //   Java source line #71	-> byte code offset #352
    //   Java source line #72	-> byte code offset #355
    //   Java source line #74	-> byte code offset #366
    //   Java source line #78	-> byte code offset #369
    //   Java source line #80	-> byte code offset #375
    //   Java source line #83	-> byte code offset #379
    //   Java source line #81	-> byte code offset #382
    //   Java source line #82	-> byte code offset #384
    //   Java source line #85	-> byte code offset #411
    //   Java source line #87	-> byte code offset #415
    //   Java source line #90	-> byte code offset #419
    //   Java source line #88	-> byte code offset #422
    //   Java source line #89	-> byte code offset #424
    //   Java source line #90	-> byte code offset #451
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	454	0	this	ConfigParser
    //   0	454	1	configFile	String
    //   1	415	2	f	java.io.FileReader
    //   3	373	3	r	java.io.BufferedReader
    //   26	217	4	str	String
    //   48	19	5	ex	java.io.IOException
    //   88	19	5	ex	java.io.IOException
    //   168	13	5	length	int
    //   227	104	5	i	int
    //   240	117	6	name	String
    //   254	105	7	value	String
    //   305	31	8	token	java.util.StringTokenizer
    //   315	33	9	values	String[]
    //   369	83	10	localObject	java.lang.Object
    //   382	19	11	ex	java.io.IOException
    //   422	19	11	ex	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   41	45	48	java/io/IOException
    //   81	85	88	java/io/IOException
    //   4	37	369	finally
    //   118	371	369	finally
    //   375	379	382	java/io/IOException
    //   415	419	422	java/io/IOException
  }
}

