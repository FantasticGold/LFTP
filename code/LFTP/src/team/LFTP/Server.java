package team.LFTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import org.omg.CORBA.SystemException;

public class Server {
  static final int PORT_LISTEN = 1061;
  static final int MAX_LENGTH = 1024;
  static final int PORT_MIN = 2000;
  static final int PORT_RANGE = 2000;
  static final String REQUEST = "request";
  private byte[] buf = new byte[MAX_LENGTH];
  private DatagramSocket socket;
  private DatagramPacket recvPacket;
  private String rcvStr;
  private InetAddress address;
  private int port;
  
  private Server() {
    try {
      socket = new DatagramSocket(PORT_LISTEN);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    recvPacket = new DatagramPacket(buf, buf.length);
  }
  
  private void listen() {
    while (true) {
      System.out.println("Listening... ");
      rcvStr = recv();
      
//      if (rcvStr.equals(REQUEST)) {
        address = recvPacket.getAddress();
        port = recvPacket.getPort();
        Random random = new Random();
        int myPort = random.nextInt(PORT_RANGE) + PORT_MIN;
        send(String.valueOf(myPort));
        new Thread(new Connection(address, port, myPort)).start();
//      }
    }
  }

  private void send(String str) {
    buf = str.getBytes();
    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
    try {
      socket.send(packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private String recv() {
    try {
      socket.receive(recvPacket);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    String str = null;
    try {
      str = new String(recvPacket.getData(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return str;
  }
  
  public static void main(String[] args) {
    Server server = new Server();
    server.listen();
  }
  
  // no using
  private class Connection implements Runnable {
    private byte[] buf = new byte[MAX_LENGTH];
    private DatagramSocket socket;
    private DatagramPacket rcvPacket;
    private String rcvStr;
    private InetAddress address;
    private int port;
    
    public Connection(InetAddress address, int port, int myPort) {
      this.address = address;
      this.port = port;
      try {
        socket = new DatagramSocket(myPort);
      } catch (SocketException e) {
        e.printStackTrace();
      }
      rcvPacket = new DatagramPacket(buf, buf.length);
    }

    @Override
    public void run() {
      while (true) {
        rcvStr = rcv();
        snd("ACK");
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
    
  }
}
