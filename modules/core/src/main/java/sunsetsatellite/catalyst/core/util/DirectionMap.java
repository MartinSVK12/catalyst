package sunsetsatellite.catalyst.core.util;

import java.util.HashMap;

public class DirectionMap<V> extends HashMap<Direction, V> {

	public DirectionMap(V defaultValue) {
		super();
		for (Direction dir : Direction.values()) {
			put(dir, defaultValue);
		}
	}
}
