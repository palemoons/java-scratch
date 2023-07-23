package machinelearning.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * For the input of an int value.
 */
public class IntegerField extends TextField implements FocusListener {

	/**
	 * Serial uid. Not quite useful.
	 */
	private static final long serialVersionUID = -2462338973265150779L;

	/**
	 * Only specify the content.
	 */
	public IntegerField() {
		this("513");
	}// Of constructor

	/**
	 * Specify the content and the width.
	 * 
	 * @param paraString
	 *            The default value of the content.
	 * @param paraWidth 
	 * The width of the field.
	 */
	public IntegerField(String paraString, int paraWidth) {
		super(paraString, paraWidth);
		addFocusListener(this);
	}// Of constructor

	/**
	 * Only specify the content.
	 * 
	 * @param paraString
	 *            The given default string.
	 */
	public IntegerField(String paraString) {
		super(paraString);
		addFocusListener(this);
	}

	/**
	 * Only specify the width.
	 * 
	 * @param paraWidth
	 *            The width of the field.
	 */
	public IntegerField(int paraWidth) {
		super(paraWidth);
		setText("513");
		addFocusListener(this);
	}

	/**
	 * Implement FocusListenter.
	 * 
	 * @param paraEvent
	 *            The event is unimportant.
	 */
	public void focusGained(FocusEvent paraEvent) {
	}

	/**
	 * Implement FocusListenter.
	 * 
	 * @param paraEvent
	 *            The event is unimportant.
	 */
	public void focusLost(FocusEvent paraEvent) {
		try {
			Integer.parseInt(getText());
		} catch (Exception ee) {
			ErrorDialog.errorDialog.setMessageAndShow("\"" + getText()
					+ "\"Not an integer. Please check.");
			requestFocus();
		}
	}

	/**
	 * Get the int value. Show error message if the content is not an int.
	 * 
	 * @return the int value.
	 */
	public int getValue() {
		int tempInt = 0;
		try {
			tempInt = Integer.parseInt(getText());
		} catch (Exception ee) {
			ErrorDialog.errorDialog.setMessageAndShow("\"" + getText()
					+ "\" Not an int. Please check.");
			requestFocus();
		}
		return tempInt;
	}

}
