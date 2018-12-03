package team.LFTP;

import java.net.DatagramPacket;

public class Window {
  static final int WND_MAX_SIZE = 1024;  // max window size
  DatagramPacket[] wnd;  // packets window
  boolean[] tag;  // ack or not
  int wndSize;  // window size
  int seq; // sequence number
  int ack; // ack number
  
  public Window(int size, int num) {
    wnd = new DatagramPacket[WND_MAX_SIZE];
    tag = new boolean[WND_MAX_SIZE];
    for (boolean b: tag) {
      b = false;
    }
    wndSize = size;
    seq = num;
    ack = num;
  }
  
  // position of the number
  static int getPos(int n) {
    return n % WND_MAX_SIZE;
  }
  
  // the number of current packets
  public int getPacketSize() {
    return seq - ack;
  }

  boolean isFull() {
    return getPacketSize() == wndSize;
  }
  
  boolean isEmpty() {
    return getPacketSize() == 0;
  }
  
  public int getWndSize() {
    return wndSize;
  }
  
  public int getSeq() {
    return seq;
  }
  
  public int getAck() {
    return ack;
  }
  public void setAck(int ack) {
    this.ack = ack;
  }

  DatagramPacket getPacket(int num) {
    return wnd[getPos(num)];
  }
  
  boolean getTag(int num) {
    return tag[getPos(num)];
  }
  
  void ackTag(int num) {
    tag[getPos(num)] = true;
  }
  
  void addAck() {
    ack = ack + 1;
  }
  
  void addSeq() {
    seq = seq + 1;
  }
  
  // Sender
  void addPacket(DatagramPacket packet) {
    int pos = getPos(seq);
    wnd[pos] = packet;
    tag[pos] = false;
    seq = seq + 1;
  }
  
  void checkAck() {
    while (ack < seq && tag[getPos(ack)]) {
      ack = ack + 1;
    }
  }
  
  
  // Receiver
  void checkPacket(int num, DatagramPacket packet, Packer packer, Writer writer) {
    seq = Math.max(seq, num+1);
    int pos = getPos(num);
    if (!tag[pos]) {
      wnd[pos] = packet;
      tag[pos] = true;
      System.out.println("确认");
    }
    while (ack < seq && tag[getPos(ack)]) {
      packer.toData(wnd[getPos(ack)]);
      System.out.println("写，序列号：" + ack);
      writer.write(packer.getData());
      ack = ack + 1;
      System.out.println("确认号：" + ack + "，序列号：" + seq + "确认过吗？" + 
          (tag[getPos(ack)] ? "有" : "无"));
    }
  }
}
