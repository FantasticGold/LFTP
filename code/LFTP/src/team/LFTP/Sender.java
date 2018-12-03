package team.LFTP;

import java.io.IOException;
import java.lang.invoke.ConstantCallSite;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.TimerTask;

import javax.print.attribute.standard.PrinterMessageFromOperator;
import javax.sound.sampled.Port;
import javax.swing.text.html.HTMLDocument.HTMLReader.ParagraphAction;
import javax.xml.crypto.Data;

public class Sender {
  DatagramSocket socket;
  Packer packer;
  Reader reader;
  TimerManager manager;

  // send window
  static final int SEND_WND_MAX_SIZE = 1024;
  DatagramPacket[] wndData;
  int seqNum;
  int ackNum;
  int wndSize;
  
  public Sender(DatagramSocket socket, Packer packer, Reader reader, int num) {
    this.socket = socket;
    this.packer = packer;
    this.reader = reader;
    manager = TimerManager.getInstance();
    
    wndData = new DatagramPacket[SEND_WND_MAX_SIZE];
    seqNum = num;
    ackNum = num;
    wndSize = SEND_WND_MAX_SIZE;
  }
  
  private static int getPos(int num) {
    return num % SEND_WND_MAX_SIZE;
  }
  
  private boolean isFull() {
    return seqNum - ackNum >= wndSize;
  }
  
  private boolean isEmpty() {
    return seqNum == ackNum;
  }
  
  public void send() {
    boolean tag = false;
    new Thread(new AckReceiver()).start(); // receive ack
    
    while (reader.isOpen()) {
      while (isFull());  // wait for space
      
      packer.setSeqNum(seqNum);
      DatagramPacket packet = packer.toPacket(reader.read(Packer.MAX_DATA_LENGTH));
      wndData[getPos(seqNum)] = packet;
      seqNum = seqNum + 1;
      try {
        socket.send(packet);
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (!tag) {
        tag = true;
        timing(ackNum);
      }
    }
  }
  
  public void timing(int ackNum) {
    if (ackNum != manager.getAckNum()) {
      manager.setAckNum(ackNum);
      manager.setTask(new TimerTask() {
        @Override
        public void run() {
          System.out.println("超时重发，序列号：" + ackNum);
          try {
            socket.send(wndData[getPos(ackNum)]);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }, TimerManager.OVERTIME);
    }
  }
  
  class AckReceiver implements Runnable {
    Packer ackPacker;
    private byte[] buf;
    private DatagramPacket recvPacket;
    
    public AckReceiver() {
      ackPacker = new Packer(packer.address, packer.port);
      buf = new byte[Packer.MAX_LENGTH];
      recvPacket = new DatagramPacket(buf, buf.length);
    }
    
    @Override
    public void run() {
      while (reader.isOpen() || !isEmpty()) {
        try {
          socket.receive(recvPacket);
        } catch (IOException e) {
          e.printStackTrace();
        }
        ackPacker.toData(recvPacket);
        ackNum = ackPacker.getAckNum();
        System.out.println("接收，确认号：" + ackNum);
        timing(ackNum);
      }
      manager.clear();
    }
  }
}
