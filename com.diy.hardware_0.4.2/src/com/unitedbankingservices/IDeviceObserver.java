package com.unitedbankingservices;

/**
 * This class represents the abstract interface for all device observers. All
 * subclasses should add their own event notification methods, the first
 * parameter of which should always be the device affected.
 */
public interface IDeviceObserver {
	/**
	 * Announces that the indicated device has been enabled.
	 * 
	 * @param device
	 *            The device that has been enabled.
	 */
	default public void enabled(IDevice<? extends IDeviceObserver> device) {}

	/**
	 * Announces that the indicated device has been disabled.
	 * 
	 * @param device
	 *            The device that has been disabled.
	 */
	default public void disabled(IDevice<? extends IDeviceObserver> device) {}

	/**
	 * Announces that the indicated device has been turned on.
	 * 
	 * @param device
	 *            The device that has been turned on.
	 */
	default public void turnedOn(IDevice<? extends IDeviceObserver> device) {}

	/**
	 * Announces that the indicated device has been turned off.
	 * 
	 * @param device
	 *            The device that has been turned off.
	 */
	default public void turnedOff(IDevice<? extends IDeviceObserver> device) {}
}
