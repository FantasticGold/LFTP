package team.LFTP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Writer {
	File file;
	FileOutputStream fileOutputStream;
	long requiredLength = 0;
	
  public Writer(String name, long len) {
  	try {
  		if (len <= 0) return;
			file = new File(name);
			fileOutputStream = new FileOutputStream(file);
			requiredLength = len;
		} catch (IOException e) {
			e.printStackTrace();
		}
  }
  
  // check end
  public boolean isEnd() {
    return requiredLength <= 0;
  }
  
  public void write(byte[] data) {
  	try {
  		if (requiredLength <= 0) return;
  		if ((long) data.length >= requiredLength) {
  			fileOutputStream.write(data, 0, (int) requiredLength);
  			requiredLength = 0;
  			fileOutputStream.close();
  		}
  		else {
  			fileOutputStream.write(data);
  			requiredLength -= (long) data.length;
  		}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
  }

  // test
  public static void main(String[] args) {
  	Reader reader = new Reader("C:\\Users\\czj\\Desktop\\hello.txt");
    Writer writer = new Writer("C:\\Users\\czj\\Desktop\\hello2.txt", reader.getFileLength());
    
    while (reader.isOpen()) {
      byte[] data = reader.read(1);
      writer.write(data);
    }
  }
}