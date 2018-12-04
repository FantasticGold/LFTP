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
  Reader reader;
  Writer writer;
  
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
  
  public void connect(InetAddress address, int port) {
    packer = new Packer(address, port);
    send(packer.toPacket(Utils.toBytes(Server.REQUEST)));
    
    recv();
    packer.setPort(Utils.toInt(packer.getData()));
  }
  
  public void upload(String name) {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    send(ServerThread.CMD_UPLOAD);
    send(name);
    reader = new Reader(name);
    send(reader.getFileLength());
    
    Sender sender = new Sender(socket, packer, reader, 0);
    sender.send();
  }

  public void download(String name) {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    send(ServerThread.CMD_DOWNLOAD);
    send(name);
    recv();
    writer = new Writer(name, Utils.toLong(packer.getData()));
    
    Receiver receiver = new Receiver(socket, packer, writer, 0);
    receiver.recv();
  }

  public void send(long num) {
    send(packer.toPacket(Utils.toBytes(num)));
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
