package team.LFTP;

import java.util.Timer;
import java.util.TimerTask;

import javax.accessibility.AccessibleKeyBinding;
import javax.swing.text.html.HTML.Tag;

public class TimerManager {
  public static int OVERTIME = 50;
  private static TimerManager manager = null;
  private Timer timer = null;
  private int ackNum;
  
  private TimerManager() {
    timer = new Timer();
  }
  
  public static TimerManager getInstance() {
    if (manager == null) {
      manager = new TimerManager();
    }
    manager.ackNum = -1;
    return manager;
  }
  
  public int getAckNum() {
    return ackNum;
  }
  
  public void setAckNum(int ackNum) {
    this.ackNum = ackNum;
  }
  
  public void setTask(TimerTask task, int delay) {
    timer.cancel();
    timer = new Timer();
    timer.schedule(task, delay, delay);
  }
  
  public void clear() {
    timer.cancel();
    timer = new Timer();
    ackNum = -1;
  }
}
