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
    
    System.out.println("发送连接请求");
    recv();
    System.out.println("收到消息");
    packer.setPort(Utils.toInt(packer.getData()));
  }
  
  public void upload(String name) {
    send(ServerThread.CMD_UPLOAD);
    send(name);
    send((int) new Reader(name).getFileLength());
    
    Sender sender = new Sender(socket, packer, 0, name);
    sender.send();
  }

  public void download(String name) {
    send(ServerThread.CMD_DOWNLOAD);
    send(name);
    
    Receiver receiver = new Receiver(socket, packer, 0, name, 0);
    receiver.recv();
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
