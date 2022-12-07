package ca.ucalgary.seng300.simulation;

/**
 * An exception that can be raised when a null pointer is passed inside the
 * simulation.
 */
@SuppressWarnings("serial")
public class NullPointerSimulationException extends SimulationException {
	/**
	 * Default constructor.
	 */
	public NullPointerSimulationException() {
		this("Null is not a valid argument.");
	}

	/**
	 * Basic constructor.
	 * 
	 * @param name
	 *            The name of the parameter that was bound to null.
	 */
	public NullPointerSimulationException(String name) {
		super("Null is not a valid " + name + ".");
	}
}
