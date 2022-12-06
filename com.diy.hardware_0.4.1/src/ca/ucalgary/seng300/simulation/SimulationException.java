package ca.ucalgary.seng300.simulation;

/**
 * An exception that can be raised when the behaviour within the simulator makes
 * no sense, typically when it has not been configured correctly. This is
 * different from an exception being raised because the preconditions of a
 * component are violated, but that would make sense in the real world.
 */
@SuppressWarnings("serial")
public abstract class SimulationException extends RuntimeException {
	/**
	 * Basic constructor.
	 * 
	 * @param message
	 *            An explanatory message of the problem.
	 */
	public SimulationException(String message) {
		super(message);
	}
}
