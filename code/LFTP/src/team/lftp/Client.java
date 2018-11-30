package team.lftp;

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
  
  private Client(InetAddress address, int port) {
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
  
  private void connect(InetAddress address) {
    port = Server.PORT_LISTEN;
    snd(Server.REQUEST);
    rcvStr = rcv();
    System.out.println(rcvStr);
    port = Integer.parseInt(rcvStr.substring(0, 4));
  }
  
  private void start() {
    while (true) {
      System.out.print("Info: ");
      Scanner scanner = new Scanner(System.in);
      String info = scanner.nextLine();
      
      snd(info);
      rcvStr = rcv();
      System.out.println(rcvStr);
    }
  }
  
  private void snd(String str) {
    buf = str.getBytes();
    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
    try {
      socket.send(packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private String rcv() {
    try {
      socket.receive(rcvPacket);
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
  
  public static void main(String[] args) {
    // address
    System.out.print("Address: ");
    InetAddress address = null;
    Scanner scanner = new Scanner(System.in);
    try {
      address = InetAddress.getByName(scanner.nextLine());
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    
    // port
    Random random = new Random();
    int port = random.nextInt(PORT_RANGE) + PORT_MIN;
    
    // connect
    Client client = new Client(address, port);
    System.out.println("Connecting...");
    client.connect(address);
    
    // start
    client.start();
  }
} 
