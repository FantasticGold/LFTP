package team.LFTP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import team.LFTP.Sender.RecvACK;

public class Receiver {
  DatagramSocket socket;
  Packer packer;
  Writer writer;
  private byte[] buf = new byte[Packer.MAX_LENGTH];
  private DatagramPacket recvPacket;
  
  static final int RECV_WND_MAX_SIZE = 1024;
  DatagramPacket[] wndData;
  boolean[] wndAck;
  int seqNum;
  int ackNum;
  
  public Receiver(DatagramSocket socket, Packer packer, int seq, String name, int len, int num) {
    this.socket = socket;
    this.packer = packer;
    writer = new Writer(name, len);
    recvPacket = new DatagramPacket(buf, buf.length);
    
    wndData = new DatagramPacket[RECV_WND_MAX_SIZE];
    wndAck = new boolean[RECV_WND_MAX_SIZE];
    for (boolean b: wndAck) {
      b = false;
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
      if (!wndAck[pos]) {
        wndAck[pos] = true;
        wndData[pos] = recvPacket;
      }
      
      // ack
      while (ackNum <= seqNum && wndAck[getPos(ackNum)]) {
        packer.toData(wndData[getPos(ackNum)]);
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
  }
}
