package team.server;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Server {
  static final int LISTEN_PORT = 1064;
  DatagramSocket datagramSocket;
  
  public Server() {
    // TODO Auto-generated constructor stub
    try {
      datagramSocket = new DatagramSocket(LISTEN_PORT);
    } catch (SocketException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
  public static void main(String[] args) {
    Server server = new Server();
  }
}
