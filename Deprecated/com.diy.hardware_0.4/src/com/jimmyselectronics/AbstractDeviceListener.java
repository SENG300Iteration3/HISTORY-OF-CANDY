package com.jimmyselectronics;

/**
 * This class represents the abstract interface for all device listeners. All
 * subclasses should add their own event notification methods, the first
 * parameter of which should always be the device affected.
 * 
 * @author Jimmy's Electronics LLP
 */
public interface AbstractDeviceListener {
	/**
	 * Announces that the indicated device has been enabled.
	 * 
	 * @param device
	 *            The device that has been enabled.
	 */
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device);

	/**
	 * Announces that the indicated device has been disabled.
	 * 
	 * @param device
	 *            The device that has been disabled.
	 */
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device);

	/**
	 * Announces that the indicated device has been turned on.
	 * 
	 * @param device
	 *            The device that has been turned on.
	 */
	public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device);

	/**
	 * Announces that the indicated device has been turned off.
	 * 
	 * @param device
	 *            The device that has been turned off.
	 */
	public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device);
}
