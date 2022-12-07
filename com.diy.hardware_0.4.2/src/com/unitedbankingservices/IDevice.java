package com.unitedbankingservices;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Base type for all devices from United Banking Services.
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
public interface IDevice<T extends IDeviceObserver> {
	/**
	 * Checks whether this device is connected to the power grid.
	 * 
	 * @return true if this device is connected; otherwise, false.
	 */
	boolean isConnected();

	/**
	 * Checks whether this device is connected to the power grid and activated.
	 * 
	 * @return true if the device is connected, activated, and receiving power;
	 *             otherwise, false.
	 */
	boolean isActivated();

	/**
	 * Connects this device to the electrical power grid, leaving the power off. If
	 * the device is already connected or if it is already activated, this method
	 * does nothing.
	 */
	void connect();

	/**
	 * Disconnects this device from the electrical power grid. If the device is
	 * already disconnected, this method does nothing.
	 */
	void disconnect();

	/**
	 * Attempts to turn on the power to this device. If the device is already active
	 * or if it is disconnected from the power grid, this method does nothing.
	 */
	void activate();

	/**
	 * Attempts to turn off the power to this device. If the device is already
	 * disactivated or if it is disconnected from the power grid, this method does
	 * nothing.
	 */
	void disactivate();

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
	boolean detach(T observer);

	/**
	 * All observers registered with this device are removed. If there are none,
	 * calls to this method have no effect.
	 */
	void detachAll();

	/**
	 * Registers the indicated observer to receive event notifications from this
	 * device.
	 * 
	 * @param observer
	 *            The observer to be added.
	 * @throws NullPointerSimulationException
	 *             If the argument is null.
	 */
	void attach(T observer);

	/**
	 * Disables this device from receiving input and producing output. Announces
	 * "disabled" event. Requires power.
	 */
	void disable();

	/**
	 * Enables this device for receiving input and producing output. Announces
	 * "enabled" event. Requires power.
	 */
	void enable();

	/**
	 * Returns whether this device is currently disabled from receiving input and
	 * producing output. Requires power.
	 * 
	 * @return true if the device is disabled; false if the device is enabled.
	 */
	boolean isDisabled();
}