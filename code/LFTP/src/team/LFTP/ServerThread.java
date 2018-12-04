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
  
  public ServerThread(InetAddress address, int port, int myPort) {
    try {
      socket = new DatagramSocket(myPort);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    recvPacket = new DatagramPacket(buf, buf.length);
    packer = new Packer(address, port);
  }
  
  @Override
  public void run() {
    while (true) {
      recv();
      int num = Utils.toInt(packer.getData());
      if (num == Client.FINISH) {
        break;
      } else if (num == CMD_UPLOAD) {
        uploadService();
      } else if (num == CMD_DOWNLOAD) {
        downloadService();
      }
    }
  }
  
  private void uploadService() {
    recv();
    String name = Utils.toString(packer.getData());
    recv();
    long len = Utils.toLong(packer.getData());
    writer = new Writer(name, len);
    
    Receiver receiver = new Receiver(socket, packer, writer, 0);
    receiver.recv();
  }
  
  private void downloadService() {
    recv();
    String name = Utils.toString(packer.getData());
    Reader reader = new Reader(name);
    send(reader.getFileLength());
    
    Sender sender = new Sender(socket, packer, reader, 0);
    sender.send();
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
