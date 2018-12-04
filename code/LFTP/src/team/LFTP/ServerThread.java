package team.LFTP;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.print.attribute.standard.Finishings;
import javax.xml.crypto.Data;

public class ServerThread implements Runnable {
  static final int CMD_UPLOAD = 9987;
  static final int CMD_DOWNLOAD = 9983;
  static final int TAG_FINISH = 0;
  private byte[] buf = new byte[Packer.MAX_LENGTH];
  private DatagramSocket socket;
  private DatagramPacket recvPacket;
  Packer packer;
  Reader reader;
  Writer writer;
  int cmd;
  String name;
  long len;
  
  public ServerThread(InetAddress address, int port, int myPort, int cmd, String name, long len) {
    try {
      socket = new DatagramSocket(myPort);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    recvPacket = new DatagramPacket(buf, buf.length);
    packer = new Packer(address, port);
    this.cmd = cmd;
    this.name = name;
    this.len = len;
  }
  
  @Override
  public void run() {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    if (cmd == CMD_UPLOAD) {
      Writer writer = new Writer(name, len);
      Receiver receiver = new Receiver(socket, packer, writer, 0);
      receiver.recv();
    } else if (cmd == CMD_DOWNLOAD) {
      Reader reader = new Reader(name);
      Sender sender = new Sender(socket, packer, reader, 0);
      sender.send();
    }
  }
  
  public void send(long num) {
    send(packer.toPacket(Utils.toBytes(num)));
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
