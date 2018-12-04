package team.LFTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import javax.swing.RepaintManager;

import org.omg.CORBA.SystemException;

public class Server {
  static final int PORT_LISTEN = 1061;
  static final int PORT_MIN = 2000;
  static final int PORT_RANGE = 2000;
  static final int REQUEST = 1327;
  private byte[] buf = new byte[Packer.MAX_LENGTH];
  private DatagramSocket socket;
  private DatagramPacket recvPacket;
  private Packer packer;
  
  public Server() {
    try {
      socket = new DatagramSocket(PORT_LISTEN);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    recvPacket = new DatagramPacket(buf, buf.length);
    packer = new Packer(null, 0);
  }
  
  public void listen() {
    Random random = new Random();
    System.out.println("Listening... ");
    
    while (true) {
      recv();
//      System.out.println("get");
      
      byte[] data = packer.getData();
      byte[] bcmd = new byte[4];
      System.arraycopy(data, 0, bcmd, 0, 4);
      
      int cmd = Utils.toInt(bcmd);
      packer = new Packer(recvPacket.getAddress(), recvPacket.getPort());

      if (cmd == ServerThread.CMD_UPLOAD) {
//        System.out.println("上传");
        byte[] blen = new byte[8];
        byte[] bname = new byte[32];
        System.arraycopy(data, 4, blen, 0, 8);
        System.arraycopy(data, 12, bname, 0, bname.length);
        long len = Utils.toLong(blen);
        String name = Utils.toString(bname);
        
        int myPort = random.nextInt(PORT_RANGE) + PORT_MIN;
        byte[] bmyPort = Utils.toBytes(myPort);
        byte[] info = new byte[64];
        System.arraycopy(bmyPort, 0, info, 0, 4);
        DatagramPacket packet = packer.toPacket(info);

        try {
          socket.setSoTimeout(1000);
        } catch (SocketException e) {
          e.printStackTrace();
        }
        while (true) {
          try {
            socket.send(packet);
//            System.out.println("send upload");
          } catch (IOException e1) {
            e1.printStackTrace();
          }
          try {
            socket.receive(recvPacket);
          } catch (IOException e) {
            break;
          }
        }
        try {
          socket.setSoTimeout(0);
        } catch (SocketException e) {
          e.printStackTrace();
        }
        new Thread(new ServerThread(recvPacket.getAddress(), recvPacket.getPort(), 
            myPort, ServerThread.CMD_UPLOAD, name, len)).start();
        
      } else if (cmd == ServerThread.CMD_DOWNLOAD) {
//        System.out.println("下载");
        byte[] bname = new byte[32];
        System.arraycopy(data, 4, bname, 0, 32);
        String name = Utils.toString(bname);
//        System.out.println("名字：" + name + "结束");
        Reader reader = new Reader(name);
        long len = reader.getFileLength();
//        System.out.println("长度为：" + len);
        
        int myPort = random.nextInt(PORT_RANGE) + PORT_MIN;
        byte[] bmyPort = Utils.toBytes(myPort);
        byte[] blen = Utils.toBytes(len);
        byte[] info = new byte[64];
        System.arraycopy(bmyPort, 0, info, 0, 4);
        System.arraycopy(blen, 0, info, 4, 8);
        DatagramPacket packet = packer.toPacket(info);

        try {
          socket.setSoTimeout(2000);
        } catch (SocketException e) {
          e.printStackTrace();
        }
        while (true) {
          try {
            socket.send(packet);
//            System.out.println("send download");
          } catch (IOException e1) {
            e1.printStackTrace();
          }
          try {
            socket.receive(recvPacket);
          } catch (IOException e) {
            break;
          }
        }
        try {
          socket.setSoTimeout(0);
        } catch (SocketException e) {
          e.printStackTrace();
        }

        new Thread(new ServerThread(recvPacket.getAddress(), recvPacket.getPort(), 
            myPort, ServerThread.CMD_DOWNLOAD, name, 0)).start();
        
      }
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
