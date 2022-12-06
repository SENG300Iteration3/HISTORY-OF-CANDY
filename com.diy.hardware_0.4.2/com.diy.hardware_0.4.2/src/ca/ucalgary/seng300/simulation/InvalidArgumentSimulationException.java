package ca.ucalgary.seng300.simulation;

/**
 * An exception that can be raised when an invalid argument is passed inside the
 * simulation.
 */
@SuppressWarnings("serial")
public class InvalidArgumentSimulationException extends SimulationException {
	/**
	 * Basic constructor.
	 * 
	 * @param message
	 *            The message describing the problem.
	 */
	public InvalidArgumentSimulationException(String message) {
		super(message);
	}
}
