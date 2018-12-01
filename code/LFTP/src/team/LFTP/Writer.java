package team.LFTP;

import java.io.File;
import java.io.FileOutputStream;

public class Writer {
	File file;
	FileOutputStream fileOutputStream;
	long requiredLength = 0;
	
  public Writer(String name, long len) {
  	try {
			file = new File(name);
			fileOutputStream = new FileOutputStream(file);
			requiredLength = len;
		} catch (Exception e) {
			e.printStackTrace();
		}
  }
  
  public void write(byte[] data) {
  	try {
  		if ((long) data.length >= requiredLength) {
  			fileOutputStream.write(data, 0, (int) requiredLength);
  			requiredLength = 0;
  			fileOutputStream.close();
  		}
  		else {
  			fileOutputStream.write(data);
  			requiredLength -= (long) data.length;
  		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
  }

  // test
  public static void main(String[] args) {
    Writer writer = new Writer("C:\\Users\\czj\\Desktop\\hello2.txt", 10);
    Reader reader = new Reader("C:\\Users\\czj\\Desktop\\hello.txt");
    while (reader.isOpen()) {
      byte[] data = reader.read(20);
      writer.write(data);
    }
  }
}