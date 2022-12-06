package ca.powerutility;

import java.util.Random;

/**
 * Represents the electrical power grid as a Singleton. The grid can experience
 * outages and surges; the former should cause plugged-in devices to stop
 * functioning and the latter risks to damage them. The grid can be connected to
 * a regular, imperfect source (the mains); a faulty source that constantly
 * surges; or be disconnected completely.
 */
public class PowerGrid {
	private final Random pseudorandomNumberGenerator = new Random();
	private static int probabilityOfPowerFailure = 1;
	private static int probabilityOfPowerSurge = 5;

	/**
	 * Disconnects from the main power grid, so all power fails.
	 */
	public static void disconnect() {
		probabilityOfPowerFailure = 10000;
		probabilityOfPowerSurge = 0;
	}

	/**
	 * Connects to a power source that always causes surges.
	 */
	public static void engageFaultyPowerSource() {
		probabilityOfPowerFailure = 0;
		probabilityOfPowerSurge = 10000;
	}

	/**
	 * Disconnects from the main power grid to use a well-conditioned, battery
	 * backup.
	 */
	public static void engageUninterruptiblePowerSource() {
		probabilityOfPowerFailure = 0;
		probabilityOfPowerSurge = 0;
	}

	/**
	 * Reconnects to the main power grid.
	 */
	public static void reconnectToMains() {
		probabilityOfPowerFailure = 1;
		probabilityOfPowerSurge = 5;
	}

	private final long stateCheckInterval = 1000;

	enum PowerState {
		NORMAL, SURGE, OUTAGE
	}

	private PowerState state = PowerState.NORMAL;

	private static PowerGrid instance = new PowerGrid();

	PowerGrid() {}

	/**
	 * For testability, forces a power surge to occur.
	 */
	public void forcePowerSurge() {
		state = PowerState.SURGE;
	}

	/**
	 * For testability, forces a power outage to occur.
	 */
	public void forcePowerOutage() {
		state = PowerState.OUTAGE;
	}

	/**
	 * For testability, forces the power to be restored.
	 */
	public void forcePowerRestore() {
		state = PowerState.NORMAL;
	}

	/**
	 * Obtains the unique instance of PowerGrid.
	 * 
	 * @return The unique instance.
	 */
	public static PowerGrid instance() {
		return instance;
	}

	/**
	 * Determines whether the power grid currently has power.
	 * 
	 * @return true if the grid has power; otherwise, false.
	 */
	public final boolean hasPower() {
		int probability = pseudorandomNumberGenerator.nextInt(10000);

		if(state == PowerState.SURGE)
			throw new PowerSurge();
		else if(state == PowerState.OUTAGE)
			throw new NoPowerException();

		if(probability < probabilityOfPowerFailure) {
			state = PowerState.OUTAGE;
			throw new NoPowerException();
		}
		else if(probability < probabilityOfPowerSurge) {
			try {
				state = PowerState.SURGE;
				throw new PowerSurge();
			}
			finally {
				state = PowerState.NORMAL;
			}
		}

		return true;
	}
}