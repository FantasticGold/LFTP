package team.LFTP;

import java.io.UnsupportedEncodingException;

import javax.swing.text.MaskFormatter;

public class Utils {
  public static byte[] toBytes(String string) {
    return string.getBytes();
  }
  
  public static String toString(byte[] bytes) {
    String string = null;
    
    try {
      string = new String(bytes, "UTF-8").trim();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    
    return string;
  }

  public static byte[] toBytes(int num) {
    byte[] bytes = new byte[4];
    int mask = 0XFF;
    
    bytes[0] = (byte) ((num >> 24) & mask);
    bytes[1] = (byte) ((num >> 16) & mask);
    bytes[2] = (byte) ((num >> 8) & mask);
    bytes[3] = (byte) ((num >> 0) & mask);
    
    return bytes;
  }

  public static byte[] toBytes(long num) {
    byte[] bytes = new byte[8];
    int mask = 0XFF;
    
    bytes[0] = (byte) ((num >> 56) & mask);
    bytes[1] = (byte) ((num >> 48) & mask);
    bytes[2] = (byte) ((num >> 40) & mask);
    bytes[3] = (byte) ((num >> 32) & mask);
    bytes[4] = (byte) ((num >> 24) & mask);
    bytes[5] = (byte) ((num >> 16) & mask);
    bytes[6] = (byte) ((num >> 8) & mask);
    bytes[7] = (byte) ((num >> 0) & mask);
    
    return bytes;
  }
  
  public static int toInt(byte[] bytes) {
    int num = 0;
    int mask = 0XFF;

    num |= (bytes[0] << 24) & (mask << 24); 
    num |= (bytes[1] << 16) & (mask << 16); 
    num |= (bytes[2] << 8) & (mask << 8);  
    num |= (bytes[3] << 0) & (mask << 0);  
    
    return num;
  }

  public static int toLong(byte[] bytes) {
    int num = 0;
    int mask = 0XFF;

    num |= (bytes[0] << 56) & (mask << 56); 
    num |= (bytes[1] << 48) & (mask << 48); 
    num |= (bytes[2] << 40) & (mask << 40);  
    num |= (bytes[3] << 32) & (mask << 32);
    num |= (bytes[4] << 24) & (mask << 24); 
    num |= (bytes[5] << 16) & (mask << 16); 
    num |= (bytes[6] << 8) & (mask << 8);  
    num |= (bytes[7] << 0) & (mask << 0);    
    
    return num;
  }
  
}
