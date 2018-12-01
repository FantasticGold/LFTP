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
  static final int FINISH = 1249;
  static final int PORT_MIN = 2000;
  static final int PORT_RANGE = 2000;
  private byte[] buf = new byte[Packer.MAX_LENGTH];
  private DatagramSocket socket;
  private DatagramPacket recvPacket;
  Packer packer;
  
  Client() {
    Random random = new Random();
    int port = random.nextInt(PORT_RANGE) + PORT_MIN;
    try {
      socket = new DatagramSocket(port);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    recvPacket = new DatagramPacket(buf, buf.length);
  }
  
  @Override
  protected void finalize() throws Throwable {
    send(FINISH);
    super.finalize();
  }
  
  public void connect(InetAddress address, int port) {
    packer = new Packer(address, port);
    send(packer.toPacket(Utils.toBytes(Server.REQUEST)));
    
    recv();
    packer.setPort(Utils.toInt(packer.getData()));
  }
  
  public void upload(String name) {
    send(ServerThread.CMD_UPLOAD);
    send(name);
    
    Reader reader = new Reader(name);
    while (reader.isOpen()) {
      byte[] data = reader.read(Packer.MAX_DATA_LENGTH);
      send(packer.toPacket(data));
    }
    send(packer.toPacket(Utils.toBytes(ServerThread.TAG_FINISH)));
  }

  public void download(String name) {
    send(ServerThread.CMD_DOWNLOAD);
    send(name);
    
    Writer writer = new Writer(?"F:\\SYSU_3.1\\download.txt");
    while (true) {
      recv();
      byte[] data = packer.getData();
      if (Utils.toInt(data) == ServerThread.TAG_FINISH) {
        break;
      } else {
        writer.write(data);
      }
    }
  }

  public void send(int num) {
    send(packer.toPacket(Utils.toBytes(num)));
  }
  
  public void send(String string) {
    send(packer.toPacket(Utils.toBytes(string)));
  }
  
  private void send(DatagramPacket packet) {
    try {
      socket.send(packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void recv() {
    try {
      socket.receive(recvPacket);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    packer.toData(recvPacket);
  }
  
} 
