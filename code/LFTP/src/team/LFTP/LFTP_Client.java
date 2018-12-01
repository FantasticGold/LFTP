package team.LFTP;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.ShutdownChannelGroupException;
import java.util.Random;
import java.util.Scanner;

public class LFTP_Client {
  
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    InetAddress address = null;
    
    System.out.print("Address: ");
    try {
      address = InetAddress.getByName(scanner.nextLine());
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    
    Client client = new Client();
    show("Connecting...");
    client.connect(address, Server.PORT_LISTEN);
    
    String cmd;
    while (true) {
      showUI();
      cmd = scanner.nextLine();
      
      client.send(cmd);
      client.recv();
    }
  }
  
  static void show(String string) {
    System.out.println(string);
  }
  
  static void showUI() {
    System.out.print("LFTP> ");
  }
}
