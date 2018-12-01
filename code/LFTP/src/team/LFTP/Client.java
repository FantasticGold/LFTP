package team.LFTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

public class Client {
  static final int MAX_LENGTH = 1024;
  static final int PORT_MIN = 2000;
  static final int PORT_RANGE = 2000;
  private byte[] buf = new byte[MAX_LENGTH];
  private DatagramSocket socket;
  private DatagramPacket rcvPacket;
  private String rcvStr;
  private InetAddress address;
  private int port;
  
  private Client() {
    this.address = address;
    this.port = port;
    System.out.println("port = " + port);
    try {
      socket = new DatagramSocket(port);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    rcvPacket = new DatagramPacket(buf, buf.length);
  }
  
  public void connect(InetAddress address, int port) {
    port = Server.PORT_LISTEN;
    send(Server.REQUEST);
    rcvStr = recv();
    System.out.println(rcvStr);
    port = Integer.parseInt(rcvStr.substring(0, 4));
  }
  
//  private void start() {
//    while (true) {
//      System.out.print("Info: ");
//      Scanner scanner = new Scanner(System.in);
//      String info = scanner.nextLine();
//      
//      send(info);
//      rcvStr = recv();
//      System.out.println(rcvStr);
//    }
//  }
  
  private void send(DatagramPacket packet) {
    buf = str.getBytes();
    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
    try {
      socket.send(packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private DatagramPacket recv() {
    try {
      socket.receive(rcvPacket);
      return rcvPacket;
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    String str = null;
    try {
      str = new String(rcvPacket.getData(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return str;
  }
} 
