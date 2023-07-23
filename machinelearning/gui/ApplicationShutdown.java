package machinelearning.gui;

import java.awt.event.*;

/**
 * Shut down the application according to window action or button action.
 */
public class ApplicationShutdown implements WindowListener, ActionListener {
  /**
   * Only one instance.
   */
  public static ApplicationShutdown applicationShutdown = new ApplicationShutdown();

  /**
   * This constructor is private such that the only instance is generated here.
   */
  private ApplicationShutdown() {
  }

  /**
   * Shutdown the system
   */
  public void windowClosing(WindowEvent comeInWindowEvent) {
    System.exit(0);
  }

  public void windowActivated(WindowEvent comeInWindowEvent) {
  }

  public void windowClosed(WindowEvent comeInWindowEvent) {
  }

  public void windowDeactivated(WindowEvent comeInWindowEvent) {
  }

  public void windowDeiconified(WindowEvent comeInWindowEvent) {
  }

  public void windowIconified(WindowEvent comeInWindowEvent) {
  }

  public void windowOpened(WindowEvent comeInWindowEvent) {
  }

  public void actionPerformed(ActionEvent ee) {
    System.exit(0);
  }
}
