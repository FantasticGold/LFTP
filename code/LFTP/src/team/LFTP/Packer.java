package team.LFTP;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class Packer {
  static final int MAX_LENGTH = 1024;
  static final int MAX_DATA_LENGTH = 800;
  byte[] data = new byte[MAX_DATA_LENGTH];
  DatagramPacket packet;
  InetAddress address;
  int port;
  
  public Packer(InetAddress address, int port) {
    this.address = address;
    this.port = port;
  }
  
  DatagramPacket toPacket(byte[] bytes) {
    packet = new DatagramPacket(bytes, bytes.length, address, port);
    return packet;
  }
  
  void toData(DatagramPacket packet) {
    this.packet = packet;
    data = packet.getData();
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
  
  byte[] getData() {
    return packet.getData();
  }
  public void setData(byte[] data) {
    this.data = data;
  }
}
