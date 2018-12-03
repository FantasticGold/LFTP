package team.LFTP;

import java.net.DatagramPacket;
import java.net.InetAddress;
import team.LFTP.Utils;

//SEQ 32-bit 
//ACK 32-bit
//FLAGS 32-bit : SYN 8-bit, ACK 8-bit, FIN 8-bit, Reserved 8-bit
//WND 32-bit

//DATA: MAX_LENGTH - 16bytes
public class Packer {
  static final int MAX_LENGTH = 1024;
  static final int MAX_DATA_LENGTH = MAX_LENGTH - 16;
  byte[] data = new byte[MAX_DATA_LENGTH];
  DatagramPacket packet;
  InetAddress address;
  
  int seq; //Sequence number
  int ack; //Acknowledge number
  int wnd; //Window size
  //Flags
  byte SYN; //SYN flag
  byte ACK; //ACK flag
  byte FIN; //FIN flag
  
  int port;
  
  public Packer(InetAddress address, int port) {
    this.address = address;
    this.port = port;
  }
  
  DatagramPacket toPacket(byte[] inputdata) {
    // inputdata == null
    int len = inputdata == null ? 0 : inputdata.length;
  	byte[] bytes = new byte[len+16];
  	
  	byte[] seqdata = Utils.toBytes(seq);
  	byte[] ackdata = Utils.toBytes(ack);
  	byte[] flagdata = new byte[4];
  	flagdata[0] = SYN;
  	flagdata[1] = ACK;
  	flagdata[2] = FIN;
  	flagdata[3] = 0;
  	byte[] wnddata = Utils.toBytes(wnd);
  	
  	System.arraycopy(seqdata, 0, bytes, 0, 4);
  	System.arraycopy(ackdata, 0, bytes, 4, 4);
  	System.arraycopy(flagdata, 0, bytes, 8, 4);
  	System.arraycopy(wnddata, 0, bytes, 12, 4);
  	
    // inputdata == null
  	if (inputdata != null) {
      System.arraycopy(inputdata, 0, bytes, 16, inputdata.length);
  	}

    packet = new DatagramPacket(bytes, bytes.length, address, port);
    return packet;
  }
  
  void toData(DatagramPacket packet) {
    this.packet = packet;
    byte[] packetData = packet.getData();
    
    //Parse the header
    byte[] headerBuffer = new byte[4];
    
    System.arraycopy(packetData, 0, headerBuffer, 0, 4);
    seq = Utils.toInt(headerBuffer);
    
    System.arraycopy(packetData, 4, headerBuffer, 0, 4);
    ack = Utils.toInt(headerBuffer);
    
    System.arraycopy(packetData, 8, headerBuffer, 0, 4);
    SYN = headerBuffer[0];
    ACK = headerBuffer[1];
    FIN = headerBuffer[2];
    
    System.arraycopy(packetData, 12, headerBuffer, 0, 4);
    wnd = Utils.toInt(headerBuffer);
    
    //Parse the data
    System.arraycopy(packetData, 16, data, 0, packetData.length - 16);
  }
  
  public InetAddress getAddress() {
    return address;
  }
  public void setAddress(InetAddress address) {
    this.address = address;
  }
  
  public int getPort() {
    return port;
  }
  public void setPort(int port) {
    this.port = port;
  }
  
  public DatagramPacket getPacket() {
    return packet;
  }
  void setPacket(DatagramPacket packet) {
    this.packet = packet;
    
  }
  
  //Get & set data
  byte[] getData() {
//    return packet.getData();
    return data;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
  
  //Get & Set flags
  public boolean isSYN() {
		if (SYN == 1) return true;
		return false;
	}
  
  public void setSYN(int inputSYN) {
		SYN = (byte)inputSYN;
	}
  
  public boolean isACK() {
		if (ACK == 1) return true;
		return false;
	}
  
  public void setACK(int inputACK) {
		ACK = (byte)inputACK;
	}
  
  public boolean isFIN() {
		if (FIN == 1) return true;
		return false;
	}
  
  public void setFIN(int inputFIN) {
		FIN = (byte)inputFIN;
	}
  
  //Get & set sequence number
  public int getSeqNum() {
		return seq;
	}
  
  public void setSeqNum(int inputSeqNum) {
		seq = inputSeqNum;
	}
  
  //Get & set acknowledge number
  public int getAckNum() {
		return ack;
	}
  
  public void setAckNum(int inputAckNum) {
		ack = inputAckNum;
	}
  
  //Get & set window size
  public int getWindowSize() {
		return wnd;
	}
  
  public void setWindowSize(int inputWindowSize) {
		wnd = inputWindowSize;
	}
  
  public static void main(String[] args) {
		byte[] myData = new byte[20];
		for (int i = 0; i < 20; i++) {
			myData[i] = (byte) i;
		}
		Packer packer = new Packer(null, 1234);
		packer.setACK(1);
		packer.setSYN(1);
		packer.setFIN(1);
		packer.setData(myData);
		packer.setWindowSize(21);
		packer.setSeqNum(23);
		packer.setAckNum(24);
		
		DatagramPacket package1 = packer.toPacket(myData);
		for (int i = 0; i < package1.getData().length; i++) {
			System.out.println((int)package1.getData()[i]);
		}
		packer.toData(package1);
		
	}
}
