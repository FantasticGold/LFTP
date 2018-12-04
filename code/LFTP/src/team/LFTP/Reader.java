package team.LFTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Reader {
	private long remainLength = 0;
	File file = null;
	FileInputStream fileInputStream = null;
	private long fileLength = 0;
  public Reader(String name) {
    try {
    	file = new File(name);
    	if (file.exists() && file.isFile()) {
    		fileInputStream = new FileInputStream(file);
    		fileLength = remainLength = file.length();
//    		System.out.println("文件长度：" +file.length());
    	}
		} catch (IOException e) {
			e.printStackTrace();
		}
  }
  
  // null to EOF, return actual length
  byte[] read(int len) {
  	try {
			if (len <= 0 || !isOpen()) return null;
			
			//Set the length of data to the minimun of remainLength and len
			long dataLength = remainLength < len ? remainLength : len;
			if (dataLength == 0) return null;
			byte[] data = new byte[(int) dataLength];
			fileInputStream.read(data);
			remainLength -= dataLength;
			
			//If reach the end, close fileInputStream
			if (remainLength == 0) {
				fileInputStream.close();
				file = null;
			}
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
  	return null;
  }
  
  @Override
  protected void finalize() throws Throwable {
    fileInputStream.close();
    super.finalize();
  }
  
  public boolean isOpen() {
		if (file == null) return false;
		return true;
	}
  
  public long getFileLength() {
		return fileLength;
	}
}
