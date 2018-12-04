package team.LFTP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Receiver {
  DatagramSocket socket;
  Packer packer;
  Writer writer;
  
  // receive window
  static final int RECV_WND_MAX_SIZE = 128;
  DatagramPacket[] wndData;
  boolean[] wndAck;
  int seqNum;
  int ackNum;
  
  public Receiver(DatagramSocket socket, Packer packer, Writer writer, int num) {
    this.socket = socket;
    this.packer = packer;
    this.writer = writer;
    
    wndData = new DatagramPacket[RECV_WND_MAX_SIZE];
    wndAck = new boolean[RECV_WND_MAX_SIZE];
    for (int i = 0; i < wndAck.length; ++i) {
      wndAck[i] = false;
    }
    seqNum = num;
    ackNum = num;
  }
  
  private static int getPos(int num) {
    return num % RECV_WND_MAX_SIZE;
  }
  
  public void recv() {
    boolean tag = true;
    
    while (true) {
      if (tag) {
        tag = false;
        
      } else {
        try {
          socket.setSoTimeout(1000);
        } catch (SocketException e) {
          e.printStackTrace();
        }
      }

      // receive datagram packet
      byte[] buf = new byte[Packer.MAX_LENGTH];
      DatagramPacket recvPacket = new DatagramPacket(buf, buf.length);
      try {
        socket.receive(recvPacket);
      } catch (IOException e) {
        if (writer.isEnd()) break;
        else continue;
      }
      packer.toData(recvPacket);

      // save datagram packet
      int num = packer.getSeqNum();
      seqNum = Math.max(seqNum, num);
      System.out.println("Receive: " + num);
      int pos = getPos(packer.getSeqNum());
      if (num >= ackNum) {
        wndAck[pos] = true;
        wndData[pos] = recvPacket;
//        System.out.println("seqNum: " + seqNum);
//        System.out.println("ackNum: " + ackNum);
      }

      // ack
      while (ackNum <= seqNum && wndAck[getPos(ackNum)]) {
        packer.toData(wndData[getPos(ackNum)]);
        writer.write(packer.getData());
//        System.out.println("写：" + packer.getSeqNum());
        wndAck[getPos(ackNum)] = false;
        ackNum = ackNum + 1;
      }

      packer.setAckNum(ackNum);
      packer.setWindowSize(RECV_WND_MAX_SIZE - (seqNum - ackNum));
      try {
        socket.send(packer.toPacket(null));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    try {
      socket.setSoTimeout(0);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    
    System.out.println("Receive Over!");
  }
}
