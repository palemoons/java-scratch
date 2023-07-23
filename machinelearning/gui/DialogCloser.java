package machinelearning.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * Close the dialog.
 */
public class DialogCloser extends WindowAdapter implements ActionListener {

  /**
   * The dialog under control.
   */
  private Dialog currentDialog;

  /**
   * The first constructor.
   */
  public DialogCloser() {
    super();
  }

  /**
   * The second constructor.
   * 
   * @param paraDialog
   *                   the dialog under control
   */
  public DialogCloser(Dialog paraDialog) {
    currentDialog = paraDialog;
  }

  /**
   * Close the dialog which clicking the cross at the up-right corner of the
   * window.
   * 
   * @param comeInWindowEvent
   *                          From it we can obtain which window sent the message
   *                          because X
   *                          was used.
   */
  public void windowClosing(WindowEvent paraWindowEvent) {
    paraWindowEvent.getWindow().dispose();
  }

  /**
   * Close the dialog while pushing an "OK" or "Cancel" button.
   * 
   * @param paraEvent
   *                  Not considered.
   */
  public void actionPerformed(ActionEvent paraEvent) {
    currentDialog.dispose();
  }
}