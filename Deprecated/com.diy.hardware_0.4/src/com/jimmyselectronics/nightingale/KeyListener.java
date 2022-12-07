package com.jimmyselectronics.nightingale;

import com.jimmyselectronics.AbstractDeviceListener;

/**
 * Listens for events emanating from a key on a keyboard.
 * 
 * @author Jimmy's Electronics LLP
 */
public interface KeyListener extends AbstractDeviceListener {
	/**
	 * Announces that the indicated key has been pressed.
	 * 
	 * @param k
	 *            The key where the event occurred.
	 */
	public void pressed(Key k);

	/**
	 * Announces that the indicated key has been released.
	 * 
	 * @param k
	 *            The key where the event occurred.
	 */
	public void released(Key k);
}
