package team.LFTP;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.ShutdownChannelGroupException;
import java.sql.SQLNonTransientConnectionException;
import java.util.Random;
import java.util.Scanner;

public class LFTP_Client {
  private static final String CMD_HELP = "help";
  private static final String CMD_UPLOAD = "lsend";
  private static final String CMD_DOWNLOAD = "lget";
  private static final String CMD_QUIT = "quit";
  
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    Client client = new Client();
    InetAddress address = null;
    String name;
    
    show("Welcome to LFTP!");
    show("");
    show("By Gold & Blasticag");
    show("");
    
    while (true) {
      showUI();
      String cmd = scanner.nextLine().trim();
      String[] strings = cmd.split(" ");
      
      if (strings.length == 1) {
        if (CMD_HELP.equals(strings[0])) {
          show("UPLOAD: lsend server file");
          show("DOWNLOAD: lget server file");
          show("QUIT: quit");
        } else if (CMD_QUIT.equals(strings[0])) {
          break;
        }
        
      } else if (strings.length == 3) {
        if (CMD_UPLOAD.equals(strings[0])) {
          try {
            address = InetAddress.getByName(strings[1]);
          } catch (UnknownHostException e) {
            e.printStackTrace();
          }
          name = strings[2];
          show("Connecting...");
          client.upload(address, Server.PORT_LISTEN, name);
          
        } else if (CMD_DOWNLOAD.equals(strings[0])) {
          try {
            address = InetAddress.getByName(strings[1]);
          } catch (UnknownHostException e) {
            e.printStackTrace();
          }
          name = strings[2];
          show("Connecting...");
          client.download(address, Server.PORT_LISTEN, name);
        }
      }
    }
    
    show("Bye!");
  }
  
  static void show(String string) {
    System.out.println(string);
  }
  
  static void showUI() {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.print("LFTP> ");
  }
}
