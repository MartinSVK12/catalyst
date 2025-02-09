package sunsetsatellite.catalyst.core.util.network;


import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.HashMap;
import java.util.List;

/**
 * Maps points to valid paths in a network that could be traversed if started from those points.
 */
public class NetworkPathMap extends HashMap<Vec3i, List<NetworkPath>> {
}
