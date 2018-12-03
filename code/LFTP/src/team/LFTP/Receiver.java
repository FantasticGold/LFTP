package team.LFTP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver {
  DatagramSocket socket;
  Packer packer;
  Writer writer;
  private byte[] buf = new byte[Packer.MAX_LENGTH];
  private DatagramPacket recvPacket;
  
  // receive window
  static final int RECV_WND_MAX_SIZE = 1024;
  DatagramPacket[] wndData;
  boolean[] wndAck;
  int seqNum;
  int ackNum;
  
  public Receiver(DatagramSocket socket, Packer packer, Writer writer, int num) {
    this.socket = socket;
    this.packer = packer;
    this.writer = writer;
    recvPacket = new DatagramPacket(buf, buf.length);
    
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
    while (!writer.isEnd()) {
      // receive datagram packet
      try {
        socket.receive(recvPacket);
      } catch (IOException e) {
        e.printStackTrace();
      }
      packer.toData(recvPacket);
      
      // save datagram packet
      seqNum = Math.max(seqNum, packer.getSeqNum());
      System.out.println("接收，序列号：" + packer.getSeqNum());
      int pos = getPos(packer.getSeqNum());
      System.out.println("存放，位置：" + pos);
      if (!wndAck[pos]) {
        wndAck[pos] = true;
        wndData[pos] = recvPacket;
      }
      
      // ack
      while (ackNum <= seqNum && wndAck[getPos(ackNum)]) {
//        System.out.println("写位置：" + getPos(ackNum));
        packer.toData(wndData[getPos(ackNum)]);
//        System.out.println("写序列号：" + packer.getSeqNum());
        writer.write(packer.getData());
        ackNum = ackNum + 1;
      }
      
      System.out.println("确认，确认号：" + ackNum);
      packer.setAckNum(ackNum);
      try {
        socket.send(packer.toPacket(null));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < ackNum; ++i) {
      System.out.println("类：" + wndData[getPos(i)]);
      packer.toData(wndData[getPos(i)]);
//      System.out.println("序列号：" + packer.getSeqNum());
    }
  }
}
