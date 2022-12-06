package com.jimmyselectronics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.powerutility.NoPowerException;
import ca.powerutility.PowerGrid;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * The abstract base class for all devices from Jimmy's Electronics.
 * <p>
 * This class utilizes the Observer design pattern. Subclasses inherit the
 * register method, but each must define its own specialized notifyXXX methods
 * according to the details of the event notification protocol (over and above
 * enable/disable/turnOn/turnOff) in place.
 * </p>
 * <p>
 * Each device will normally be coupled to an appropriate listener interface,
 * which extends AbstractDeviceListener; the type parameter T represents this
 * listener. The associated, specialized subinterface must be designed in
 * connection with the device class itself for the sake of a unified event
 * notification protocol.
 * </p>
 * <p>
 * Any individual device can be disabled, which means it will not permit use of
 * its state-changing methods. All such methods will declare to throw
 * DisabledException.
 * </p>
 * 
 * @param <T>
 *            The type of listeners used for this device. For a device whose
 *            class is X, its corresponding listener interface would typically
 *            be XListener.
 * @author Jimmy's Electronics LLP
 */
public abstract class AbstractDevice<T extends AbstractDeviceListener> {
	/**
	 * Represents possible states of the device in terms of its ability to receive
	 * electrical power.
	 */
	private enum PowerState {
		/**
		 * The device is completed disconnected from the power grid.
		 */
		DISCONNECTED,
		/**
		 * The device is connected to the power grid, but turned off.
		 */
		PLUGGED_IN,
		/**
		 * The device is connected to the power grid, and it is turned on.
		 */
		TURNED_ON
	}

	/**
	 * The current state of connectivity and activation of this device. It is
	 * protected to permit certain direct manipulations. It is preferable to use the
	 * public methods provided.
	 * 
	 * @see #plugIn()
	 * @see #unplug()
	 * @see #turnOn()
	 * @see #turnOff()
	 */
	private PowerState powerState = PowerState.DISCONNECTED;

	/**
	 * Checks whether this device is plugged-in.
	 * 
	 * @return true if this device is plugged-in; otherwise, false.
	 */
	public synchronized boolean isPluggedIn() {
		return powerState != PowerState.DISCONNECTED;
	}

	/**
	 * Checks whether this device is turned on and powered up.
	 * 
	 * @return true if the device is plugged in, turned on, and receiving power;
	 *             otherwise, false.
	 */
	public synchronized boolean isPoweredUp() {
		PowerGrid.instance().hasPower();
		return powerState == PowerState.TURNED_ON;
	}

	/**
	 * Connects this device to the electrical power grid, leaving the power turned
	 * off. If the device is already plugged in or if its power is already turned
	 * on, this method does nothing.
	 */
	public synchronized void plugIn() {
		if(powerState == PowerState.DISCONNECTED)
			powerState = PowerState.PLUGGED_IN;

		// else ignore because it is already plugged in
	}

	/**
	 * Disconnects this device from the electrical power grid. If the device is
	 * already disconnected, this method does nothing.
	 */
	public synchronized void unplug() {
		powerState = PowerState.DISCONNECTED;
	}

	/**
	 * Attempts to turn on the power to this device. If the device is already turned
	 * on or if it is disconnected from the power grid, this method does nothing.
	 */
	public synchronized void turnOn() {
		switch(powerState) {
		case PLUGGED_IN:
			powerState = PowerState.TURNED_ON;
			notifyTurnedOn();
			break;

		default:
			// ignore
		}
	}

	private void notifyTurnedOn() {
		for(T listener : listeners)
			listener.turnedOn(this);
	}

	/**
	 * Attempts to turn off the power to this device. If the device is already
	 * turned off or if it is disconnected from the power grid, this method does
	 * nothing.
	 */
	public synchronized void turnOff() {
		switch(powerState) {
		case TURNED_ON:
			powerState = PowerState.PLUGGED_IN;
			notifyTurnedOff();
			break;

		default:
			// ignore
		}
	}

	private void notifyTurnedOff() {
		for(T listener : listeners)
			listener.turnedOff(this);
	}

	private ArrayList<T> listeners = new ArrayList<>();

	/**
	 * Obtains the registered listeners on this device. Does not require power.
	 * 
	 * @return An unmodifiable list of the listeners registered on this device.
	 */
	public List<T> listeners() {
		@SuppressWarnings("unchecked")
		List<T> clone = (List<T>)listeners.clone();

		return Collections.unmodifiableList(clone);
	}

	/**
	 * Locates the indicated listener and removes it such that it will no longer be
	 * informed of events from this device. If the listener is not currently
	 * registered with this device, calls to this method will return false, but
	 * otherwise have no effect. Does not require power.
	 * 
	 * @param listener
	 *            The listener to remove.
	 * @return true if the listener was found and removed, false otherwise.
	 */
	public synchronized boolean deregister(T listener) {
		return listeners.remove(listener);
	}

	/**
	 * All listeners registered with this device are removed. If there are none,
	 * calls to this method have no effect.
	 */
	public synchronized void deregisterAll() {
		listeners.clear();
	}

	/**
	 * Registers the indicated listener to receive event notifications from this
	 * device. Does not require power.
	 * 
	 * @param listener
	 *            The listener to be added.
	 * @throws NullPointerSimulationException
	 *             If the argument is null.
	 */
	public final synchronized void register(T listener) {
		if(listener == null)
			throw new NullPointerSimulationException("listener");

		listeners.add(listener);
	}

	private boolean disabled = false;

	/**
	 * Disables this device from receiving input and producing output. Announces
	 * "disabled" event. Requires power.
	 */
	public synchronized void disable() {
		if(!isPoweredUp())
			throw new NoPowerException();

		disabled = true;
		notifyDisabled();
	}

	private void notifyDisabled() {
		for(T listener : listeners())
			listener.disabled(this);
	}

	/**
	 * Enables this device for receiving input and producing output. Announces
	 * "enabled" event. Requires power.
	 */
	public synchronized void enable() {
		if(!isPoweredUp())
			throw new NoPowerException();

		disabled = false;
		notifyEnabled();
	}

	private void notifyEnabled() {
		for(T listener : listeners())
			listener.enabled(this);
	}

	/**
	 * Returns whether this device is currently disabled from receiving input and
	 * producing output. Requires power.
	 * 
	 * @return true if the device is disabled; false if the device is enabled.
	 */
	public final synchronized boolean isDisabled() {
		if(!isPoweredUp())
			throw new NoPowerException();

		return disabled;
	}
}
