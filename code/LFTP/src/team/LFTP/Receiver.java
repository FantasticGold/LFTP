package team.LFTP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.text.html.HTMLDocument.HTMLReader.ParagraphAction;
import javax.xml.crypto.Data;

import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

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
    packer.setSYN(0);
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
//    System.out.println("开始接收");
    boolean tag = true;
    
    while (true) {
      try {
        socket.setSoTimeout(1000);
      } catch (SocketException e) {
        e.printStackTrace();
      }

      // receive datagram packet
      byte[] buf = new byte[Packer.MAX_LENGTH];
      DatagramPacket recvPacket = new DatagramPacket(buf, buf.length);
      try {
        socket.receive(recvPacket);
        tag = false;
        packer.setPort(recvPacket.getPort());
      } catch (IOException e) {
//        System.out.println(writer.requiredLength);
//        System.out.println("等待超时");
        if (tag) {
//          System.out.println("标签为true");
          packer.setAckNum(ackNum);
          try {
            socket.send(packer.toPacket(null));
          } catch (IOException e1) {
            e1.printStackTrace();
          }
          continue;
        } else if (writer.isEnd()) {
//          System.out.println("写完了");
          break;
        } else {
//          System.out.println("没写完");
          packer.setAckNum(ackNum);
          packer.setWindowSize(RECV_WND_MAX_SIZE - (seqNum - ackNum));
          try {
            socket.send(packer.toPacket(null));
          } catch (IOException e1) {
            e1.printStackTrace();
          }
          continue;
        }
      }
      packer.toData(recvPacket);
      if (packer.isSYN()) {
//        System.out.println("等待包");
        continue;
      } else {
//        System.out.println("接受包");
      }
//      System.out.println("收到数据包");

      // save datagram packet
      int num = packer.getSeqNum();
      seqNum = Math.max(seqNum, num);
//      System.out.println("地址：" + packer.getAddress() + "，端口：" + packer.getPort());
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
//        System.out.println("发送：" + ackNum);
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
