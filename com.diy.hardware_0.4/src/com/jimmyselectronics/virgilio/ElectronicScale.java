package com.jimmyselectronics.virgilio;

import java.util.ArrayList;
import java.util.Random;

import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.Item;
import com.jimmyselectronics.OverloadException;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents electronic scales on which can be placed one or more items. Scales
 * have a sensitivity: changes to the weight smaller than this will go
 * unnoticed. Scales have a weight limit as well; when the items on the scale
 * are heavier than this limit, the scale cannot operate.
 * 
 * @author Jimmy's Electronics LLP
 */
public class ElectronicScale extends AbstractDevice<ElectronicScaleListener> {
	private ArrayList<Item> items = new ArrayList<>();

	private double weightLimitInGrams;
	private double currentWeightInGrams = 0;
	private double weightAtLastEvent = 0;
	private double sensitivity;

	/**
	 * Constructs an electronic scale with the indicated maximum weight that it can
	 * handle before going into overload. The constructed scale will initially be in
	 * the configuration phase.
	 * 
	 * @param weightLimitInGrams
	 *            The weight threshold beyond which the scale will overload.
	 * @param sensitivity
	 *            The number of grams that can be added or removed since the last
	 *            change event, without causing a new change event.
	 * @throws SimulationException
	 *             If either argument is &le;0.
	 */
	public ElectronicScale(double weightLimitInGrams, double sensitivity) {
		if(weightLimitInGrams <= 0.0)
			throw new InvalidArgumentSimulationException("The maximum weight cannot be zero or less.");

		if(sensitivity <= 0.0)
			throw new InvalidArgumentSimulationException("The sensitivity cannot be zero or less.");

		this.weightLimitInGrams = weightLimitInGrams;
		this.sensitivity = sensitivity;
	}

	/**
	 * Gets the weight limit for the scale. Weights greater than this will not be
	 * weighable by the scale, but will cause overload. Does not require power.
	 * 
	 * @return The weight limit.
	 */
	public double getWeightLimit() {
		return weightLimitInGrams;
	}

	/**
	 * Gets the current weight on the scale. Requires power.
	 * 
	 * @return The current weight.
	 * @throws OverloadException
	 *             If the weight has overloaded the scale.
	 */
	public synchronized double getCurrentWeight() throws OverloadException {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(currentWeightInGrams <= weightLimitInGrams)
			return currentWeightInGrams + new Random().nextDouble() / 10.0;

		throw new OverloadException();
	}

	/**
	 * Gets the sensitivity of the scale. Changes smaller than this limit are not
	 * noticed or announced. Does not require power.
	 * 
	 * @return The sensitivity.
	 */
	public double getSensitivity() {
		return sensitivity;
	}

	/**
	 * Adds an item to the scale. If the addition is successful, a weight changed
	 * event is announced. If the weight is greater than the weight limit, announces
	 * "overload" event. Requires power.
	 * 
	 * @param item
	 *            The item to add.
	 * @throws SimulationException
	 *             If the same item is added more than once or is null.
	 */
	public synchronized void add(Item item) {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(item == null)
			throw new NullPointerSimulationException("item");

		if(items.contains(item))
			throw new InvalidArgumentSimulationException("The same item cannot be added more than once to the scale.");

		currentWeightInGrams += item.getWeight();

		items.add(item);

		if(currentWeightInGrams > weightLimitInGrams)
			notifyOverload();

		if(Math.abs(currentWeightInGrams - weightAtLastEvent) > sensitivity)
			notifyWeightChanged();
	}

	/**
	 * Removes an item from the scale. If the operation is successful, announces
	 * "weightChanged" event. If the scale was overloaded and this removal causes it
	 * to no longer be overloaded, announces "outOfOverload" event. Does not require
	 * power.
	 * 
	 * @param item
	 *            The item to remove.
	 * @throws SimulationException
	 *             If the item is not on the scale (including if it is null).
	 */
	public synchronized void remove(Item item) {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(!items.remove(item))
			throw new InvalidArgumentSimulationException("The item was not found amongst those on the scale.");

		// To avoid drift in the sum due to round-off error, recalculate the weight.
		double newWeightInGrams = 0.0;
		for(Item itemOnScale : items)
			newWeightInGrams += itemOnScale.getWeight();

		currentWeightInGrams = newWeightInGrams;

		if(weightAtLastEvent > weightLimitInGrams && newWeightInGrams <= weightLimitInGrams)
			notifyOutOfOverload();

		if(currentWeightInGrams <= weightLimitInGrams
			&& Math.abs(weightAtLastEvent - currentWeightInGrams) > sensitivity)
			notifyWeightChanged();
	}

	private void notifyOverload() {
		for(ElectronicScaleListener l : listeners())
			l.overload(this);
	}

	private void notifyOutOfOverload() {
		weightAtLastEvent = currentWeightInGrams;

		for(ElectronicScaleListener l : listeners())
			l.outOfOverload(this);
	}

	private void notifyWeightChanged() {
		weightAtLastEvent = currentWeightInGrams;

		for(ElectronicScaleListener l : listeners())
			l.weightChanged(this, currentWeightInGrams);
	}
}
