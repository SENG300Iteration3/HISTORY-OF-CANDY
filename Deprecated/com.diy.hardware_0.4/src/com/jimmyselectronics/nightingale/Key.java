package com.jimmyselectronics.nightingale;

import java.util.List;

import com.jimmyselectronics.AbstractDevice;
import com.unitedbankingservices.DisabledException;

import ca.powerutility.NoPowerException;

/**
 * Represents a key on a physical keyboard. Keys can be pressed (down) or
 * released (up).
 * 
 * @author Jimmy's Electronics LLP
 */
public class Key extends AbstractDevice<KeyListener> {
	private boolean isPressed = false;

	/**
	 * Simulates pushing (and holding) this key. Requires power.
	 * 
	 * @see #release()
	 * @throws DisabledException
	 *             if the key is disabled.
	 */
	public synchronized void press() throws DisabledException {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(isPressed)
			return; // it is already, so ignore

		isPressed = true;
		notifyKeyPressed();
	}

	/**
	 * Simulates letting go of this key, allowing it to return to the unpressed
	 * state. Requires power.
	 * 
	 * @see #press()
	 * @throws DisabledException
	 *             if the key is disabled.
	 */
	public synchronized void release() throws DisabledException {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(!isPressed)
			return; // it is already, so ignore

		isPressed = false;
		notifyKeyReleased();
	}

	/**
	 * Checks whether this key is currently pressed.
	 * 
	 * @return true if the key is pressed; otherwise, false.
	 */
	public synchronized boolean isPressed() {
		return isPressed;
	}

	synchronized void deregisterAllSpecial() {
		super.deregisterAll();
	}

	synchronized boolean deregisterSpecial(KeyListener listener) {
		return super.deregister(listener);
	}

	@Override
	public synchronized boolean deregister(KeyListener listener) {
		// Prevent the special listeners from being removed
		if(listener instanceof Keyboard.SynchronizeKeyAndKeyboardState)
			return false;

		return super.deregister(listener);
	}

	@Override
	public synchronized void deregisterAll() {
		List<KeyListener> listeners = listeners();

		for(KeyListener listener : listeners) {
			if(listener instanceof Keyboard.SynchronizeKeyAndKeyboardState)
				continue;

			deregisterSpecial(listener);
		}
	}

	private void notifyKeyPressed() {
		for(KeyListener l : listeners())
			l.pressed(this);
	}

	private void notifyKeyReleased() {
		for(KeyListener l : listeners())
			l.released(this);
	}

	void disableWithoutEvents() {
		List<KeyListener> listeners = listeners();
		deregisterAll();

		disable();

		for(KeyListener listener : listeners)
			register(listener);
	}

	void enableWithoutEvents() {
		List<KeyListener> listeners = listeners();
		deregisterAll();

		enable();

		for(KeyListener listener : listeners)
			register(listener);
	}

	void turnOnWithoutEvents() {
		List<KeyListener> listeners = listeners();
		deregisterAllSpecial();

		turnOn();

		for(KeyListener listener : listeners)
			register(listener);
	}

	void turnOffWithoutEvents() {
		List<KeyListener> listeners = listeners();
		deregisterAllSpecial();

		turnOff();

		for(KeyListener listener : listeners)
			register(listener);
	}

	@Override
	public synchronized void plugIn() {
		// ignore, the keyboard has to be plugged in as a whole
	}

	@Override
	public synchronized void unplug() {
		// ignore, the keyboard has to be unplugged in as a whole
	}

	synchronized void plugInSpecial() {
		super.plugIn();
	}

	synchronized void unplugSpecial() {
		super.unplug();
	}
}
