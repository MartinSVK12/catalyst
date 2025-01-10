package sunsetsatellite.catalyst.energy.electric.api;

public interface IElectricWire {
	/**
	 * @return The maximum voltage rating of this wire
	 */
	long getVoltageRating();

	/**
	 * @return The maximum amperage rating of this wire
	 */
	long getAmpRating();

	/**
	 * Triggered when the current flowing through this wire is higher than it can handle.
	 */
	void onOvercurrent();

	/**
	 * Triggered when the wire is exposed to higher voltage than it can handle.
	 */
	void onOvervoltage(long voltage);

	/**
	 * @return The wire properties of this wire
	 */
	WireProperties getProperties();
}
