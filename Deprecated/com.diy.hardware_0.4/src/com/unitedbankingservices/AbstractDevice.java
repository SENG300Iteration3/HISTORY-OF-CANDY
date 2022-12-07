package com.unitedbankingservices;

import java.util.ArrayList;

import ca.powerutility.NoPowerException;
import ca.powerutility.PowerGrid;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * The abstract base class for all devices from United Banking Services.
 * <p>
 * This class utilizes the Observer design pattern. Subclasses inherit the
 * attach method, but each must define its own notifyXXX methods.
 * </p>
 * <p>
 * Each device must be coupled to an appropriate listener interface, which
 * extends AbstractDeviceObserver; the type parameter T represents this
 * listener.
 * </p>
 * <p>
 * Any individual device can be disabled, which means it will not permit
 * physical movements to be caused by the software. Any method that could cause
 * a physical movement will declare that it throws DisabledException.
 * </p>
 * 
 * @param <T>
 *            The type of listeners used for this device. For a device whose
 *            class is X, its corresponding listener interface would typically
 *            be XObserver.
 */
public abstract class AbstractDevice<T extends IDeviceObserver> implements IDevice<T> {
	private enum PowerState {
		NOT_CONNECTED, CONNECTED, ACTIVATED
	}

	private PowerState powerState = PowerState.NOT_CONNECTED;

	/**
	 * Checks whether this device is connected to the power grid.
	 * 
	 * @return true if this device is connected; otherwise, false.
	 */
	@Override
	public synchronized boolean isConnected() {
		return powerState != PowerState.NOT_CONNECTED;
	}

	/**
	 * Checks whether this device is connected to the power grid and activated.
	 * 
	 * @return true if the device is connected, activated, and receiving power;
	 *             otherwise, false.
	 */
	@Override
	public synchronized boolean isActivated() {
		try {
			PowerGrid.instance().hasPower();
		}
		catch(NoPowerException e) {
			return false;
		}
		return powerState == PowerState.ACTIVATED;
	}

	/**
	 * Connects this device to the electrical power grid, leaving the power off. If
	 * the device is already connected or if it is already activated, this method
	 * does nothing.
	 */
	@Override
	public synchronized void connect() {
		if(powerState == PowerState.NOT_CONNECTED)
			powerState = PowerState.CONNECTED;

		// else ignore because it is already connected
	}

	/**
	 * Disconnects this device from the electrical power grid. If the device is
	 * already disconnected, this method does nothing.
	 */
	@Override
	public synchronized void disconnect() {
		powerState = PowerState.NOT_CONNECTED;
	}

	/**
	 * Attempts to turn on the power to this device. If the device is already active
	 * or if it is disconnected from the power grid, this method does nothing.
	 */
	@Override
	public synchronized void activate() {
		switch(powerState) {
		case CONNECTED:
			powerState = PowerState.ACTIVATED;
			break;

		default:
			// ignore
		}
	}

	/**
	 * Attempts to turn off the power to this device. If the device is already
	 * disactivated or if it is disconnected from the power grid, this method does
	 * nothing.
	 */
	@Override
	public synchronized void disactivate() {
		switch(powerState) {
		case ACTIVATED:
			powerState = PowerState.CONNECTED;
			break;

		default:
			// ignore
		}
	}

	/**
	 * A list of the registered observers on this device.
	 */
	protected ArrayList<T> observers = new ArrayList<>();

	/**
	 * Locates the indicated observer and removes it such that it will no longer be
	 * informed of events from this device. If the observer is not currently
	 * registered with this device, calls to this method will return false, but
	 * otherwise have no effect.
	 * 
	 * @param observer
	 *            The observer to remove.
	 * @return true if the observer was found and removed, false otherwise.
	 */
	@Override
	public final synchronized boolean detach(T observer) {
		return observers.remove(observer);
	}

	/**
	 * All observers registered with this device are removed. If there are none,
	 * calls to this method have no effect.
	 */
	@Override
	public final synchronized void detachAll() {
		observers.clear();
	}

	/**
	 * Registers the indicated observer to receive event notifications from this
	 * device.
	 * 
	 * @param observer
	 *            The observer to be added.
	 * @throws NullPointerSimulationException
	 *             If the argument is null.
	 */
	@Override
	public final synchronized void attach(T observer) {
		if(observer == null)
			throw new NullPointerSimulationException("observer");

		observers.add(observer);
	}

	private boolean disabled = false;

	/**
	 * Disables this device from receiving input and producing output. Announces
	 * "disabled" event. Requires power.
	 */
	@Override
	public final synchronized void disable() {
		if(!isActivated())
			throw new NoPowerException();

		disabled = true;
		notifyDisabled();
	}

	private void notifyDisabled() {
		for(T observer : observers)
			observer.disabled(this);
	}

	/**
	 * Enables this device for receiving input and producing output. Announces
	 * "enabled" event. Requires power.
	 */
	@Override
	public final synchronized void enable() {
		if(!isActivated())
			throw new NoPowerException();

		disabled = false;
		notifyEnabled();
	}

	private void notifyEnabled() {
		for(T listener : observers)
			listener.enabled(this);
	}

	/**
	 * Returns whether this device is currently disabled from receiving input and
	 * producing output. Requires power.
	 * 
	 * @return true if the device is disabled; false if the device is enabled.
	 */
	@Override
	public final synchronized boolean isDisabled() {
		if(!isActivated())
			throw new NoPowerException();

		return disabled;
	}
}
