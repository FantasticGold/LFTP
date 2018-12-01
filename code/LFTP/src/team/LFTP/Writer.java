package team.LFTP;

import java.io.File;
import java.io.FileOutputStream;

public class Writer {
	File file;
	FileOutputStream fileOutputStream;
  public Writer(String name) {
  	try {
			file = new File(name);
			fileOutputStream = new FileOutputStream(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
  }
  
  public void write(byte[] data) {
  	try {
			fileOutputStream.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
  }
  
  public void closeFileStream() {
		try {
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}