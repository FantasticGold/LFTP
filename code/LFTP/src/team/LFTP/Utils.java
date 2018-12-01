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
  
  public static int toInt(byte[] bytes) {
    int num = 0;
    int mask = 0XFF;

    num |= (bytes[0] << 24) & (mask << 24); 
    num |= (bytes[1] << 16) & (mask << 16); 
    num |= (bytes[2] << 8) & (mask << 8);  
    num |= (bytes[3] << 0) & (mask << 0);  
    
    return num;
  }
  
}
