package sunsetsatellite.catalyst.core.util.network;

import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

/**
 * Marks a block entity as being able to be an active component of a network.
 * <p>
 * Block entities whose blocks implement <code>NetworkComponent</code> should also implement this.
 */
public interface NetworkComponentTile extends NetworkComponent {

	Vec3i getPosition();

	boolean isConnected(Direction direction);

	void networkChanged(Network network);

	void removedFromNetwork(Network network);
}
