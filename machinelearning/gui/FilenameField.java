package machinelearning.gui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

/**
 * For the input of a filename.
 */
public class FilenameField extends TextField implements ActionListener,
    FocusListener {
  /**
   * Serial uid. Not quite useful.
   */
  private static final long serialVersionUID = 4572287941606065298L;

  /**
   * No special initialization.
   */
  public FilenameField() {
    super();
    setText("");
    addFocusListener(this);
  }

  /**
   * No special initialization.
   * 
   * @param paraWidth
   *                  The width of the filename.
   */
  public FilenameField(int paraWidth) {
    super(paraWidth);
    setText("");
    addFocusListener(this);
  }

  /**
   * No special initialization.
   * 
   * @param paraWidth
   *                  The width of the .
   * @param paraText
   *                  The given initial text
   */
  public FilenameField(int paraWidth, String paraText) {
    super(paraWidth);
    setText(paraText);
    addFocusListener(this);
  }

  /**
   * No special initialization.
   * 
   * @param paraWidth
   *                  The width of the .
   * @param paraText
   *                  The given initial text
   */
  public FilenameField(String paraText, int paraWidth) {
    super(paraWidth);
    setText(paraText);
    addFocusListener(this);
  }

  /**
   * Avoid setting null or empty string.
   * 
   * @param paraText
   *                 The given text.
   */
  public void setText(String paraText) {
    if (paraText.trim().equals(""))
      super.setText("unspecified");
    else
      super.setText(paraText.replace('\\', '/'));
  }

  /**
   * Implement ActionListenter.
   * 
   * @param paraEvent
   *                  The event is unimportant.
   */
  public void actionPerformed(ActionEvent paraEvent) {
    FileDialog tempDialog = new FileDialog(GUICommon.mainFrame,
        "Select a file");
    tempDialog.setVisible(true);
    if (tempDialog.getDirectory() == null) {
      setText("");
      return;
    }

    String directoryName = tempDialog.getDirectory();

    String tempFilename = directoryName + tempDialog.getFile();

    setText(tempFilename);
  }

  /**
   * Implement FocusListenter.
   * 
   * @param paraEvent
   *                  The event is unimportant.
   */
  public void focusGained(FocusEvent paraEvent) {
  }

  /**
   * Implement FocusListenter.
   * 
   * @param paraEvent
   *                  The event is unimportant.
   */
  public void focusLost(FocusEvent paraEvent) {
    String tempString = getText();
    if ((tempString.equals("unspecified"))
        || (tempString.equals("")))
      return;
    File tempFile = new File(tempString);
    if (!tempFile.exists()) {
      ErrorDialog.errorDialog.setMessageAndShow("File \"" + tempString
          + "\" not exists. Please check.");
      requestFocus();
      setText("");
    }
  }
}
