package team.LFTP;

import java.io.File;
import java.io.FileInputStream;

public class Reader {
	long remainLength = 0;
	File file = null;
	FileInputStream fileInputStream = null;
	private boolean Opened = false;
  public Reader(String name) {
    try {
    	file = new File(name);
    	if (file.exists() && file.isFile()) {
    		fileInputStream = new FileInputStream(file);
    		Opened = true;
    		remainLength = file.length();
    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
  }
  
  // null to EOF, return actual length
  byte[] read(int len) {
  	try {
			if (len <= 0 || !isOpen()) return null;
			
			//Set the length of data to the minimun of remainLength and len
			long dataLength = remainLength < len ? remainLength:len;
			if (dataLength == 0) return null;
			byte[] data = new byte[(int) dataLength];
			fileInputStream.read(data);
			remainLength -= dataLength;
			
			//If reach the end, close fileInputStream
			if (remainLength == 0) {
				fileInputStream.close();
				Opened = false;
			}
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
  	return null;
  }
  
  public boolean isOpen() {
		return Opened;
	}
}
