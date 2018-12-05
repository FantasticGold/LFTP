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
//    System.out.println("端口：" + port);
    try {
      socket = new DatagramSocket(port);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    recvPacket = new DatagramPacket(buf, buf.length);
  }
  
  public void upload(InetAddress address, int port, String name) {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    packer = new Packer(address, port);
    reader = new Reader(name);

    byte[] bcmd = Utils.toBytes(ServerThread.CMD_UPLOAD);
    byte[] blen = Utils.toBytes(reader.getFileLength());
    byte[] bname = Utils.toBytes(name);
    byte[] info = new byte[4 + 8 + bname.length];
    System.arraycopy(bcmd, 0, info, 0, 4);
    System.arraycopy(blen, 0, info, 4, 8);
    System.arraycopy(bname, 0, info, 12, bname.length);
    packer.setSYN(1);
    DatagramPacket packet = packer.toPacket(info);
    
    try {
      socket.setSoTimeout(1000);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    while (true) {
      send(packet);
      try {
        socket.receive(recvPacket);
      } catch (IOException e) {
        continue;
      }
      packer.toData(recvPacket);
      break;
    }
    try {
      socket.setSoTimeout(0);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    
    byte[] data = packer.getData();
    byte[] bport = new byte[4];
    System.arraycopy(data, 0, bport, 0, 4);
    packer.setPort(Utils.toInt(bport));
//    try {
////      System.out.println("地址: " + InetAddress.getLocalHost());
//    } catch (UnknownHostException e1) {
//      e1.printStackTrace();
//    }
    
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    Sender sender = new Sender(socket, packer, reader, 0);
    sender.send();
  }

  public void download(InetAddress address, int port, String name) {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    packer = new Packer(address, port);

    byte[] bcmd = Utils.toBytes(ServerThread.CMD_DOWNLOAD);
    byte[] bname = Utils.toBytes(name);
    byte[] info = new byte[4 + 64];
    System.arraycopy(bcmd, 0, info, 0, 4);
    System.arraycopy(bname, 0, info, 4, bname.length);
//    System.out.println("名字是：" + name);
//    System.out.println("字节名字是：" + Utils.toString(bname));
    packer.setSYN(1);
    DatagramPacket packet = packer.toPacket(info);
    
    try {
      socket.setSoTimeout(1000);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    while (true) {
      send(packet);
      try {
        socket.receive(recvPacket);
      } catch (IOException e) {
        continue;
      }
      packer.toData(recvPacket);
      break;
    }
    try {
      socket.setSoTimeout(0);
    } catch (SocketException e) {
      e.printStackTrace();
    }

    byte[] data = packer.getData();
    byte[] bport = new byte[4];
    byte[] blen = new byte[8];
    System.arraycopy(data, 0, bport, 0, 4);
    System.arraycopy(data, 4, blen, 0, 8);
    packer.setPort(Utils.toInt(bport));
    long len = Utils.toLong(blen);
//    System.out.println("收到的长度为："+len);
    if (len == 0) return;
    writer = new Writer(name, len);

//    try {
//      Thread.sleep(1000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
    
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
