package sunsetsatellite.catalyst.energy.electric.api;

import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Direction;

public interface IElectric {
	/**
	 * @param dir Direction to check
	 * @return <code>true</code> if container can receive energy from <code>dir</code>, <code>false</code> otherwise
	 */
	boolean canReceive(@NotNull Direction dir);
	/**
	 * @param dir Direction to check
	 * @return <code>true</code> if container can provide energy to <code>dir</code>, <code>false</code> otherwise
	 */
	default boolean canProvide(@NotNull Direction dir) { return false; }

	/**
	 * @return Amount of energy currently available in container
	 */
	long getEnergy();
	/**
	 * @return Maximum energy capacity of the container
	 */
	long getCapacity();

	/**
	 * @return Amount of unused capacity left
	 */
	default long getCapacityRemaining() {
		return getCapacity() - getEnergy();
	}

	/**
	 * @return Maximum voltage this container can handle
	 */
	long getMaxInputVoltage();

	/**
	 * @return Maximum amount of amps this machine can use
	 */
	long getMaxInputAmperage();

	/**
	 * @return Maximum voltage this container can maintain
	 */
	long getMaxOutputVoltage();

	/**
	 * @return Maximum amperage this container can deliver
	 */
	long getMaxOutputAmperage();

	/**
	 * Changes energy amount in container.
	 * @param difference Amount of energy changed, will remove energy if negative
	 * @return Amount of energy actually changed.
	 */
	long internalChangeEnergy(long difference);

	/**
	 * Adds energy to container.
	 * @param energy Amount of energy to be added
	 * @return Amount of energy actually added
	 */
	default long internalAddEnergy(long energy) {
		return internalChangeEnergy(Math.min(energy,getCapacityRemaining()));
	}

	/**
	 * Removes energy from the container.
	 * @param energy Amount of energy to be removed
	 * @return Amount of energy actually removed
	 */
	default long internalRemoveEnergy(long energy) {
		return internalChangeEnergy(-Math.min(getEnergy(),energy));
	}

	double getAverageEnergyTransfer();

	/**
	 * @return Amperage currently being used
	 */
	long getAmpsCurrentlyUsed();

	double getAverageAmpLoad();

	void addAmpsToUse(long amperage);

	/**
	 * Only this method should be to pass energy in blocks, handles both voltage and amperage.
	 * @param dir Direction of receive
	 * @param amperage Receiving amperage
	 * @return Amps used
	 */
	long receiveEnergy(@NotNull Direction dir, long amperage);

	/**
	 * Triggered when the container is exposed to more voltage than it can handle.
	 */
	void onOvervoltage(long voltage);
}
