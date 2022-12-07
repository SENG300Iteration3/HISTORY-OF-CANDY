package com.jimmyselectronics.nightingale;

import com.jimmyselectronics.AbstractDeviceListener;

/**
 * Listens for events emanating from a keyboard. Individual keys cause their own
 * events; the keyboard listens for these and translates them into its own
 * events.
 * 
 * @author Jimmy's Electronics LLP
 */
public interface KeyboardListener extends AbstractDeviceListener {
	/**
	 * Announces that a key has been pressed (and potentially held).
	 * 
	 * @param keyboard
	 *            The keyboard on which the event occurred.
	 * @param label
	 *            The label of the key generating the event.
	 */
	public void keyPressed(Keyboard keyboard, String label);

	/**
	 * Announces that a key has been released.
	 * 
	 * @param keyboard
	 *            The keyboard on which the event occurred.
	 * @param label
	 *            The label of the key generating the event.
	 */
	public void keyReleased(Keyboard keyboard, String label);
}
