package team.LFTP;

import java.io.IOException;
import java.lang.invoke.ConstantCallSite;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.print.attribute.standard.PrinterMessageFromOperator;
import javax.sound.sampled.Port;
import javax.swing.text.html.HTMLDocument.HTMLReader.ParagraphAction;
import javax.xml.crypto.Data;

public class Sender {
  DatagramSocket socket;
  Packer packer;
  Reader reader;
  TimerManager manager;
  Queue<Integer> queue;

  // send window
  static final int SEND_WND_MAX_SIZE = 1024;
  DatagramPacket[] wndData;
  int seqNum;
  int ackNum;
  int recvWndSize;
  
  // congestion control
  int cWnd;
  int ssthresh;
  int state;
  static final int SLOW_START = 0;
  static final int CONGESTION_AVOIDANCE = 1;
  static final int FAST_RECOVERY = 2;
  
  public Sender(DatagramSocket socket, Packer packer, Reader reader, int num) {
    this.socket = socket;
    this.packer = packer;
    this.reader = reader;
    manager = TimerManager.getInstance();
    queue = new ConcurrentLinkedQueue<>(); 
    
    wndData = new DatagramPacket[SEND_WND_MAX_SIZE];
    for (int i = 0; i < wndData.length; ++i) {
      wndData[i] = null;
    }
    seqNum = num;
    ackNum = num;
    recvWndSize = Receiver.RECV_WND_MAX_SIZE;
    cWnd = 1;
    ssthresh = Receiver.RECV_WND_MAX_SIZE;
    state = SLOW_START;
  }
  
  private static int getPos(int num) {
    return num % SEND_WND_MAX_SIZE;
  }
  
  private boolean isFull() {
    return seqNum - ackNum >= Math.min(recvWndSize, cWnd);
  }
  
  private boolean isEmpty() {
    return seqNum == ackNum;
  }
  
  public void send() {
    boolean tag = false;
    Thread thread = new Thread(new AckReceiver()); // receive ack
    thread.start();
    
    while (reader.isOpen()) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
      
      DatagramPacket packet;
      if (!queue.isEmpty()) {
        int num = queue.poll();
        System.out.println("Overtime!\nResend: " + num);
        packet = wndData[getPos(num)];
        
      } else if (!isFull()) {
        packer.setSeqNum(seqNum);
        System.out.println("Send: " + seqNum);
        packet = packer.toPacket(reader.read(Packer.MAX_DATA_LENGTH));
        wndData[getPos(seqNum)] = packet;
        seqNum = seqNum + 1;
        
      } else {
        continue;
      }
      
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
    
    try {
      thread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("Send Over!");
  }
  
  public void timing(int ackNum) {
    if (ackNum != manager.getAckNum()) {
      manager.setAckNum(ackNum);
      manager.setTask(new TimerTask() {
        @Override
        public void run() {
          queue.offer(ackNum);
          ssthresh = cWnd / 2;
          cWnd = 1;
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
      boolean tag = true;
      int cnt = 0;
      int before = -1;
      
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

        try {
          socket.receive(recvPacket);
        } catch (IOException e) {
          if (!reader.isOpen() && isEmpty()) break;
          else continue;
        }

        ackPacker.toData(recvPacket);
        recvWndSize = ackPacker.getWindowSize();
        ackNum = ackPacker.getAckNum();
          
        if (ackNum == before) {
          cnt = cnt + 1;
        } else {
          cnt = 0;
        }
        before = ackNum;
        
        if (cnt >= 3) {
          cWnd = cWnd / 2;
          state = CONGESTION_AVOIDANCE;
        } else if (state == SLOW_START) {
          cWnd = cWnd * 2;
          if (cWnd > ssthresh) state = CONGESTION_AVOIDANCE;
        } else if (state == CONGESTION_AVOIDANCE) {
          cWnd = cWnd + 1;
        }
//        System.out.println("接收：" + ackNum);
        if (ackNum < seqNum) {
          timing(ackNum);
        }
      }
      
      try {
        socket.setSoTimeout(0);
      } catch (SocketException e) {
        e.printStackTrace();
      }
      manager.clear();
    }
  }
}
