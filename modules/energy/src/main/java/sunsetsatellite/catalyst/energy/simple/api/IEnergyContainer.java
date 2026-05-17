package sunsetsatellite.catalyst.energy.simple.api;

import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Direction;

public interface IEnergyContainer {
	/**
	 * @param dir Direction to check
	 * @return <code>true</code> if container can receive energy from <code>dir</code>, <code>false</code> otherwise
	 */
	boolean canReceive(@NotNull Direction dir);

	/**
	 * @param dir Direction to check
	 * @return <code>true</code> if container can provide energy to <code>dir</code>, <code>false</code> otherwise
	 */
	default boolean canProvide(@NotNull Direction dir) {
		return false;
	}

	/**
	 * @return Amount of energy currently available in container
	 */
	long getEnergy();

	/**
	 * @return Maximum energy capacity of the container.
	 */
	long getCapacity();

	/**
	 * @return Amount of unused capacity left
	 */
	default long getCapacityRemaining() {
		return getCapacity() - getEnergy();
	}

	/**
	 * @return Maximum energy that can be received in one tick
	 */
	long getMaxReceive();

	/**
	 * @return Maximum energy that can be provided in one tick
	 */
	long getMaxProvide();

	/**
	 * Changes energy amount in container.
	 *
	 * @param difference Amount of energy changed, will remove energy if negative.
	 * @return Amount of energy actually changed.
	 */
	long internalChangeEnergy(long difference);

	/**
	 * Adds energy to container.
	 *
	 * @param energy Amount of energy to be added
	 * @return Amount of energy actually added
	 */
	default long internalAddEnergy(long energy) {
		return internalChangeEnergy(energy);
	}

	/**
	 * Removes energy from the container.
	 *
	 * @param energy Amount of energy to be removed
	 * @return Amount of energy actually removed
	 */
	default long internalRemoveEnergy(long energy) {
		return internalChangeEnergy(-energy);
	}

	/**
	 * Only this method should be to pass energy in blocks.
	 *
	 * @param dir    Direction of receive
	 * @param energy Amount of energy that should be received
	 * @return Amount of energy actually received
	 */
	long receiveEnergy(@NotNull Direction dir, long energy);

}
