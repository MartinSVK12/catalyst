package sunsetsatellite.catalyst.core.util.tile.feature;

import sunsetsatellite.catalyst.core.util.tile.TEFeature;

public interface IHasFeatures {
	boolean hasFeature(String id);

	TEFeature getFeature(String id);

	TEFeature createAndAddFeature(String featureId);
}
