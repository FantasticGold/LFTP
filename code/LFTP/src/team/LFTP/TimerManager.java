package team.LFTP;

import java.util.Timer;
import java.util.TimerTask;

import javax.accessibility.AccessibleKeyBinding;
import javax.swing.text.html.HTML.Tag;

public class TimerManager {
  public static int OVERTIME = 100;
  private static TimerManager manager = null;
  private Timer timer = null;
  private int ack;
  
  private TimerManager() {
    timer = new Timer();
  }
  
  public static TimerManager getInstance() {
    if (manager == null) {
      manager = new TimerManager();
    }
    manager.ack = -1;
    return manager;
  }
  
  public int getAck() {
    return ack;
  }
  
  public void setAck(int ack) {
    this.ack = ack;
  }
  
  public void setTask(TimerTask task, int delay) {
    timer.cancel();
    timer = new Timer();
    timer.schedule(task, delay, delay);
  }
  
  public void clear() {
    timer.cancel();
    timer = new Timer();
  }
}
