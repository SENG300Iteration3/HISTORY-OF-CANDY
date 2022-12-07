package com.jimmyselectronics.nightingale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Represents a physical keyboard. The individual keys have labels but no
 * physical location.
 * 
 * @author Jimmy's Electronics LLP
 */
public class Keyboard extends AbstractDevice<KeyboardListener> {
	/**
	 * This is an unmodifiable map, usable as a configuration for the keyboard. It
	 * is based on the keyboard I am using to write this.
	 */
	public static final List<String> WINDOWS_QWERTY;

	static {
		String[] windowsQwertyLabels = new String[] { /* Row 1 */ "FnLock Esc", "F1", "F2", "F3", "F4", "F5", "F6",
			"F7", "F8", "F9", "F10", "F11", "F12", "Home", "End", "Insert", "Delete", /* Row 2 */ "` ~", "1 !", "2 @",
			"3 #", "4 $", "5 %", "6 ^", "7 &", "8 *", "9 (", "0 )", "- _", "= +", "Backspace", /* Row 3 */ "Tab", "Q",
			"W", "E", "R", "T", "Y", "U", "I", "O", "P", "[ {", "] }", "\\ |", /* Row 4 */ "CapsLock", "A", "S", "D",
			"F", "G", "H", "J", "K", "L", "; :", "' \"", "Enter", /* Row 5 */ "Shift (Left)", "Z", "X", "C", "V", "B",
			"N", "M", ", <", ". >", "/ ?", "Shift (Right)", /* Row 6 */ "Fn", "Ctrl (Left)", "Windows", "Alt (Left)",
			"Spacebar", "Alt (Right)", "PrtSc", "Ctrl (Right)", "PgUp", "Up Arrow", "PgDn", "Left Arrow", "Down Arrow",
			"Right Arrow" };

		WINDOWS_QWERTY = List.of(windowsQwertyLabels);
	}

	private Map<String, Key> keys;

	class SynchronizeKeyAndKeyboardState implements KeyListener {
		private String label;

		public SynchronizeKeyAndKeyboardState(String label) {
			this.label = label;
		}

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// To avoid infinite loops, we need to directly set the state
			if(Keyboard.this.isDisabled())
				((Key)device).disableWithoutEvents();
		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// To avoid infinite loops, we need to directly set the state
			if(!Keyboard.this.isDisabled())
				((Key)device).enableWithoutEvents();
		}

		@Override
		public void pressed(Key k) {
			Keyboard.this.notifyKeyPressed(label);
		}

		@Override
		public void released(Key k) {
			Keyboard.this.notifyKeyReleased(label);
		}

		@Override
		public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
			((Key)device).turnOffWithoutEvents();
		}

		@Override
		public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
			((Key)device).turnOnWithoutEvents();
		}
	}

	/**
	 * Constructs a keyboard with the indicated set of keys. The set of keys is not
	 * modifiable once constructed. The labels must be unique.
	 * 
	 * @param keys
	 *            A map from key labels to key objects.
	 */
	public Keyboard(List<String> keys) {
		if(keys == null)
			throw new NullPointerSimulationException("keys");

		this.keys = new HashMap<String, Key>();

		for(String label : keys) {
			Key key = new Key();
			key.register(new SynchronizeKeyAndKeyboardState(label));
			this.keys.put(label, key);
		}
	}

	/**
	 * Obtains the key with the indicated label on this keyboard. Does not require
	 * power.
	 * 
	 * @param label
	 *            The label of the key of interest.
	 * @return The key corresponding to the indicated label, or null if nonesuch
	 *             exists.
	 */
	public Key getKey(String label) {
		return keys.get(label);
	}

	/**
	 * Obtains all the keys on the keyboard, indexed by their labels. Does not
	 * require power.
	 * 
	 * @return A map from key labels to key objects.
	 */
	public Map<String, Key> keys() {
		return keys;
	}

	@Override
	public synchronized void turnOn() {
		super.turnOn();

		for(Key key : keys.values())
			key.turnOnWithoutEvents();
	}

	@Override
	public synchronized void turnOff() {
		super.turnOff();

		for(Key key : keys.values())
			key.turnOffWithoutEvents();
	}

	@Override
	public synchronized void plugIn() {
		super.plugIn();

		for(Key key : keys.values())
			key.plugInSpecial();
	}

	@Override
	public synchronized void unplug() {
		super.unplug();

		for(Key key : keys.values())
			key.unplugSpecial();
	}

	/**
	 * Disables the keyboard as well as its individual keys. Attempts to enable
	 * individual keys on a disabled keyboard (or vice versa) are blocked to
	 * maintain the invariant that the enabled/disabled state of the keyboard will
	 * always be the same as those of its keys. Requires power.
	 */
	@Override
	public synchronized void disable() {
		super.disable();

		for(Key key : keys.values())
			key.disable(); // disable() has to be called so that other listeners can be notified
	}

	/**
	 * Enables the keyboard as well as its individual keys. Attempts to disable
	 * individual keys on an enabled keyboard (or vice versa) are blocked to
	 * maintain the invariant that the enabled/disabled state of the keyboard will
	 * always be the same as those of its keys. Requires power.
	 */
	@Override
	public synchronized void enable() {
		super.enable();

		for(Key key : keys.values())
			key.enable(); // enable() has to be called so that other listeners can be notified
	}

	private void notifyKeyPressed(String label) {
		for(KeyboardListener listener : listeners())
			listener.keyPressed(this, label);
	}

	private void notifyKeyReleased(String label) {
		for(KeyboardListener listener : listeners())
			listener.keyReleased(this, label);
	}
}
