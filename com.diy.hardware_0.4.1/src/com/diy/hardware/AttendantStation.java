package com.diy.hardware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jimmyselectronics.disenchantment.TouchScreen;
import com.jimmyselectronics.nightingale.Keyboard;

/**
 * Represents the station used by the attendant.
 * <p>
 * An attendant station possesses:
 * <ul>
 * <li>one touch screen; and,</li>
 * <li>one QWERTY keyboard.</li>
 * </ul>
 * <p>
 * All other functionality of the attendant station must be performed in
 * software. A given DIY self-checkout station can be attended by at most one
 * attendant station.
 */
public class AttendantStation {
	/**
	 * Represents a touch screen display on which there is a graphical user
	 * interface.
	 */
	public final TouchScreen screen;
	/**
	 * Represents a physical keyboard.
	 */
	public final Keyboard keyboard;

	private final ArrayList<DoItYourselfStation> attendedStations;

	/**
	 * Creates an attendant station.
	 */
	public AttendantStation() {
		screen = new TouchScreen();
		attendedStations = new ArrayList<DoItYourselfStation>();
		keyboard = new Keyboard(Keyboard.WINDOWS_QWERTY);
	}

	/**
	 * Accesses the list of attended self-checkout stations.
	 * 
	 * @return An immutable list of the self-checkout stations attended by this
	 *             attendant station.
	 */
	public synchronized List<DoItYourselfStation> attendedStations() {
		return Collections.unmodifiableList(attendedStations);
	}

	/**
	 * Obtains the number of self-checkout stations attended by this attendant
	 * station.
	 * 
	 * @return The count, which will always be non-negative.
	 */
	public synchronized int attendedStationCount() {
		return attendedStations.size();
	}

	/**
	 * Adds a self-checkout station to the ones attended by this attendant station.
	 * 
	 * @param station
	 *            The self-checkout station to be added to the attendance of this
	 *            attendant station.
	 * @throws IllegalArgumentException
	 *             If station is null.
	 * @throws IllegalStateException
	 *             If station is already attended.
	 */
	public synchronized void add(DoItYourselfStation station) {
		if(station == null)
			throw new IllegalArgumentException("station cannot be null");
		if(station.isAttended())
			throw new IllegalStateException("station is already attended but cannot be");

		station.setAttendantStation(this);
		attendedStations.add(station);
	}

	/**
	 * Removes the indicated station from the ones attended by this attendant
	 * station.
	 * 
	 * @param station
	 *            The station to be removed from attendance.
	 * @return true, if the indicated station was successfully removed from
	 *             attendance; otherwise, false.
	 */
	public synchronized boolean remove(DoItYourselfStation station) {
		boolean result = attendedStations.remove(station);

		if(result) {
			station.setAttendantStation(null);
		}

		return result;
	}

	/**
	 * Plugs in all the devices in the station.
	 */
	public void plugIn() {
		keyboard.plugIn();
		screen.plugIn();
	}

	/**
	 * Unplugs all the devices in the station.
	 */
	public void unplug() {
		keyboard.unplug();
		screen.unplug();
	}

	/**
	 * Turns on all the devices in the station.
	 */
	public void turnOn() {
		keyboard.turnOn();
		screen.turnOn();
	}

	/**
	 * Turns off all the devices in the station.
	 */
	public void turnOff() {
		keyboard.turnOff();
		screen.turnOff();
	}
}
