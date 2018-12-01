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
  private byte[] buf = new byte[Packer.MAX_LENGTH];
  private DatagramSocket socket;
  private DatagramPacket recvPacket;
  Packer packer;
  
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
      }
      String string = Utils.toString(packer.getData());
      string = "ACK: " + string;
      byte[] data = Utils.toBytes(string);
      send(packer.toPacket(data));
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
