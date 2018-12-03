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
  Window window;
  TimerManager manager;
  private byte[] buf = new byte[Packer.MAX_LENGTH];
  private DatagramPacket recvPacket;
  
  public Sender(DatagramSocket socket, Packer packer, int seq, String name) {
    this.socket = socket;
    this.packer = packer;
    window = new Window(Window.WND_MAX_SIZE, seq);
    reader = new Reader(name);
    manager = TimerManager.getInstance();
    recvPacket = new DatagramPacket(buf, buf.length);
  }
  
  public void send() {
    boolean tag = false;
    new Thread(new RecvACK()).start(); // receive ack
    
    while (reader.isOpen()) {
      while (window.isFull());  // wait for space
      
      packer.setSeqNum(window.getSeq());
      DatagramPacket packet = packer.toPacket(reader.read(Packer.MAX_DATA_LENGTH));
      System.out.println("发送，序列号：" + window.getSeq());
      if (!tag) {
        tag = true;
        timing(window.getSeq());  // first timing
      }
      window.addPacket(packet);
      try {
        socket.send(packet);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void timing(int ack) {
    if (ack != manager.getAck()) {
      manager.setAck(ack);
      manager.setTask(new TimerTask() {
        @Override
        public void run() {
          System.out.println("超时重发，序列号：" + ack);
          try {
            socket.send(window.getPacket(ack));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }, TimerManager.OVERTIME);
    }
  }
  
  class RecvACK implements Runnable {
    Packer recvPacker;
    
    public RecvACK() {
      recvPacker = new Packer(packer.address, packer.port);
    }
    
    @Override
    public void run() {
      while (reader.isOpen() || !window.isEmpty()) {
        try {
          socket.receive(recvPacket);
        } catch (IOException e) {
          e.printStackTrace();
        }
        recvPacker.toData(recvPacket);
        int ack = recvPacker.getAckNum();
        System.out.println("接收，确认号：" + ack);
        window.setAck(ack);
        timing(ack);
      }
      manager.clear();
    }
  }
}
