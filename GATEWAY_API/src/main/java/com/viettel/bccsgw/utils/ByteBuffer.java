package com.viettel.bccsgw.utils;
 
 import java.io.UnsupportedEncodingException;
 
 public class ByteBuffer
 {
   public static final String CRLF = "\n";
   public static final String ENC_ASCII = "ASCII";
   public static final byte SZ_BYTE = 1;
   public static final byte SZ_SHORT = 2;
   public static final byte SZ_INT = 4;
   public static final byte SZ_LONG = 8;
   private static byte[] zero = new byte[1];
   private byte[] buffer;
   
   static
   {
     zero[0] = 0;
   }
   
   public ByteBuffer()
   {
     this.buffer = null;
   }
   
   public ByteBuffer(byte[] buffer)
   {
     this.buffer = buffer;
   }
   
   public byte[] getBuffer()
   {
     return this.buffer;
   }
   
   public void setBuffer(byte[] buffer)
   {
     this.buffer = buffer;
   }
   
   public int length()
   {
     if (this.buffer == null) {
       return 0;
     }
     return this.buffer.length;
   }
   
   public void appendByte(byte data)
   {
     byte[] byteBuf = new byte[1];
     byteBuf[0] = data;
     appendBytes0(byteBuf, 1);
   }
   
   public void appendShort(short data)
   {
     byte[] shortBuf = new byte[2];
     shortBuf[1] = ((byte)(data & 0xFF));
     shortBuf[0] = ((byte)(data >>> 8 & 0xFF));
     appendBytes0(shortBuf, 2);
   }
   
   public void appendInt(int data)
   {
     byte[] intBuf = new byte[4];
     intBuf[3] = ((byte)(data & 0xFF));
     intBuf[2] = ((byte)(data >>> 8 & 0xFF));
     intBuf[1] = ((byte)(data >>> 16 & 0xFF));
     intBuf[0] = ((byte)(data >>> 24 & 0xFF));
     appendBytes0(intBuf, 4);
   }
   
   public void appendCString(String string)
   {
     try
     {
       appendString0(string, true, "ASCII");
     }
     catch (UnsupportedEncodingException e) {}
   }
   
   public void appendString(String string)
   {
     try
     {
       appendString(string, "ASCII");
     }
     catch (UnsupportedEncodingException e) {}
   }
   
   public void appendString(String string, String encoding)
     throws UnsupportedEncodingException
   {
     appendString0(string, false, encoding);
   }
   
   public void appendString(String string, int count)
   {
     try
     {
       appendString(string, count, "ASCII");
     }
     catch (UnsupportedEncodingException e) {}
   }
   
   public void appendString(String string, int count, String encoding)
     throws UnsupportedEncodingException
   {
     String subStr = string.substring(0, count);
     appendString0(subStr, false, encoding);
   }
   
   private void appendString0(String string, boolean isCString, String encoding)
     throws UnsupportedEncodingException
   {
     UnsupportedEncodingException encodingException = null;
     if ((string != null) && (string.length() > 0))
     {
       byte[] stringBuf = null;
       try
       {
         if (encoding != null) {
           stringBuf = string.getBytes(encoding);
         } else {
           stringBuf = string.getBytes();
         }
       }
       catch (UnsupportedEncodingException e)
       {
         encodingException = e;
       }
       if ((stringBuf != null) && (stringBuf.length > 0)) {
         appendBytes0(stringBuf, stringBuf.length);
       }
     }
     if (encodingException != null) {
       throw encodingException;
     }
     if (isCString) {
       appendBytes0(zero, 1);
     }
   }
   
   public void appendBuffer(ByteBuffer buf)
   {
     if (buf != null) {
       try
       {
         appendBytes(buf, buf.length());
       }
       catch (NotEnoughDataInByteBufferException e) {}
     }
   }
   
   public void appendBytes(ByteBuffer bytes, int count)
     throws NotEnoughDataInByteBufferException
   {
     if (count > 0)
     {
       if (bytes == null) {
         throw new NotEnoughDataInByteBufferException(0, count);
       }
       if (bytes.length() < count) {
         throw new NotEnoughDataInByteBufferException(bytes.length(), count);
       }
       byte[] bytesData = bytes.getBuffer();
       appendBytes0(bytesData, count);
     }
   }
   
   public void appendBytes(byte[] bytes, int count)
   {
     if (bytes != null)
     {
       if (count > bytes.length) {
         count = bytes.length;
       }
       appendBytes0(bytes, count);
     }
   }
   
   public void appendBytes(byte[] bytes)
   {
     if (bytes != null) {
       appendBytes0(bytes, bytes.length);
     }
   }
   
   public byte removeByte()
     throws NotEnoughDataInByteBufferException
   {
     byte[] resBuff = removeBytes(1).getBuffer();
     byte result = resBuff[0];
     return result;
   }
   
   public short removeShort()
     throws NotEnoughDataInByteBufferException
   {
     short result = 0;
     byte[] resBuff = removeBytes(2).getBuffer();
     result = (short)(result | resBuff[0] & 0xFF);
     result = (short)(result << 8);
     result = (short)(result | resBuff[1] & 0xFF);
     return result;
   }
   
   public int removeInt()
     throws NotEnoughDataInByteBufferException
   {
     int result = readInt();
     removeBytes0(4);
     return result;
   }
   
   public int readInt()
     throws NotEnoughDataInByteBufferException
   {
     int result = 0;
     int len = length();
     if (len >= 4)
     {
       result |= this.buffer[0] & 0xFF;
       result <<= 8;
       result |= this.buffer[1] & 0xFF;
       result <<= 8;
       result |= this.buffer[2] & 0xFF;
       result <<= 8;
       result |= this.buffer[3] & 0xFF;
       return result;
     }
     throw new NotEnoughDataInByteBufferException(len, 4);
   }
   
   public String removeCString()
     throws NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException
   {
     int len = length();
     int zeroPos = 0;
     if (len == 0) {
       throw new NotEnoughDataInByteBufferException(0, 1);
     }
     while ((zeroPos < len) && (this.buffer[zeroPos] != 0)) {
       zeroPos++;
     }
     if (zeroPos < len)
     {
       String result = null;
       if (zeroPos > 0) {
         try
         {
           result = new String(this.buffer, 0, zeroPos, "ASCII");
         }
         catch (UnsupportedEncodingException e) {}
       } else {
         result = "";
       }
       removeBytes0(zeroPos + 1);
       return result;
     }
     throw new TerminatingZeroNotFoundException();
   }
   
   public String removeString(int size, String encoding)
     throws NotEnoughDataInByteBufferException, UnsupportedEncodingException
   {
     int len = length();
     if (len < size) {
       throw new NotEnoughDataInByteBufferException(len, size);
     }
     UnsupportedEncodingException encodingException = null;
     String result = null;
     if (len > 0)
     {
       try
       {
         if (encoding != null) {
           result = new String(this.buffer, 0, size, encoding);
         } else {
           result = new String(this.buffer, 0, size);
         }
       }
       catch (UnsupportedEncodingException e)
       {
         encodingException = e;
       }
       removeBytes0(size);
     }
     else
     {
       result = "";
     }
     if (encodingException != null) {
       throw encodingException;
     }
     return result;
   }
   
   public ByteBuffer removeBuffer(int count)
     throws NotEnoughDataInByteBufferException
   {
     return removeBytes(count);
   }
   
   public ByteBuffer removeBytes(int count)
     throws NotEnoughDataInByteBufferException
   {
     ByteBuffer result = readBytes(count);
     removeBytes0(count);
     return result;
   }
   
   public void removeBytes0(int count)
     throws NotEnoughDataInByteBufferException
   {
     int len = length();
     int lefts = len - count;
     if (lefts > 0)
     {
       byte[] newBuf = new byte[lefts];
       System.arraycopy(this.buffer, count, newBuf, 0, lefts);
       setBuffer(newBuf);
     }
     else
     {
       setBuffer(null);
     }
   }
   
   public ByteBuffer readBytes(int count)
     throws NotEnoughDataInByteBufferException
   {
     int len = length();
     ByteBuffer result = null;
     if (count > 0)
     {
       if (len >= count)
       {
         byte[] resBuf = new byte[count];
         System.arraycopy(this.buffer, 0, resBuf, 0, count);
         result = new ByteBuffer(resBuf);
         return result;
       }
       throw new NotEnoughDataInByteBufferException(len, count);
     }
     return result;
   }
   
   private void appendBytes0(byte[] bytes, int count)
   {
     int len = length();
     byte[] newBuf = new byte[len + count];
     if (len > 0) {
       System.arraycopy(this.buffer, 0, newBuf, 0, len);
     }
     System.arraycopy(bytes, 0, newBuf, len, count);
     setBuffer(newBuf);
   }
   
   public String getHexDump()
   {
     StringBuffer dump = new StringBuffer();
     try
     {
       int dataLen = length();
       for (int i = 0; i < dataLen; i++)
       {
         dump.append(Character.forDigit(this.buffer[i] >> 4 & 0xF, 16));
         dump.append(Character.forDigit(this.buffer[i] & 0xF, 16));
       }
     }
     catch (Throwable t)
     {
       return "Throwable caught when dumping = " + t;
     }
     return dump.toString();
   }
   
   public String dumpHex(int NumsPerRow)
   {
     StringBuffer result = new StringBuffer();
     if (length() == 0) {
       return result.toString();
     }
     int i = 0;
     int Showed = 0;
     result.append("\n");
     while (i < this.buffer.length)
     {
       String hexString;
       if (this.buffer[i] > 0)
       {
         hexString = Integer.toHexString(this.buffer[i]);
       }
       else
       {
         short temp = (short)(this.buffer[i] & 0xFF);
         hexString = Integer.toHexString(temp);
       }
       if (hexString.length() < 2) {
         hexString = "0" + hexString;
       }
       result.append(" " + hexString);
       Showed++;
       if (Showed >= NumsPerRow)
       {
         Showed = 0;
         result.append("\n");
       }
       i++;
     }
     return result.toString();
   }
 }


