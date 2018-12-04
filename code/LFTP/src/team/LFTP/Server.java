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
  static final int PORT_MIN = 2000;
  static final int PORT_RANGE = 2000;
  static final int REQUEST = 1327;
  private byte[] buf = new byte[Packer.MAX_LENGTH];
  private DatagramSocket socket;
  private DatagramPacket recvPacket;
  private Packer packer;
  
  public Server() {
    try {
      socket = new DatagramSocket(PORT_LISTEN);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    recvPacket = new DatagramPacket(buf, buf.length);
    packer = new Packer(null, 0);
  }
  
  public void listen() {
    Random random = new Random();
    System.out.println("Listening... ");
    while (true) {
      recv();
      
      if (REQUEST == Utils.toInt(packer.getData())) {
        InetAddress address = recvPacket.getAddress();
        int port = recvPacket.getPort();
        packer = new Packer(address, port);
        
        int myPort = random.nextInt(PORT_RANGE) + PORT_MIN;
        byte[] data = Utils.toBytes(myPort);
        send(packer.toPacket(data));
        
        new Thread(new ServerThread(address, port, myPort)).start();
      }
    }
  }

  private void send(DatagramPacket packet) {
    try {
      socket.send(packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void recv() {
    try {
      socket.receive(recvPacket);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    packer.toData(recvPacket);
  }
  
}
