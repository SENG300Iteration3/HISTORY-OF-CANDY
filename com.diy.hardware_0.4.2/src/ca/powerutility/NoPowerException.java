package ca.powerutility;

/**
 * Signals that there was an attempt to use a device that was not turned on.
 */
public class NoPowerException extends RuntimeException {
	private static final long serialVersionUID = -2370085623779555833L;

	/**
	 * Default constructor.
	 */
	public NoPowerException() {}
}
