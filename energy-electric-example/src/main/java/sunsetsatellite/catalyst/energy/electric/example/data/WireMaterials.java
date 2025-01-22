package sunsetsatellite.catalyst.energy.electric.example.data;

import sunsetsatellite.catalyst.CatalystEnergy;
import sunsetsatellite.catalyst.core.util.DataInitializer;
import sunsetsatellite.catalyst.energy.electric.api.VoltageTier;
import sunsetsatellite.catalyst.energy.electric.api.WireMaterial;

public class WireMaterials extends DataInitializer {

	public static WireMaterial ulvTest;
	public static WireMaterial lvTest;
	public static WireMaterial mvTest;
	public static WireMaterial hvTest;
	public static WireMaterial evTest;
	public static WireMaterial uvTest;
	public static WireMaterial ovTest;

	public void init() {
		if (initialized) return;
		CatalystEnergy.LOGGER.info("Initializing wire materials...");

		ulvTest = new WireMaterial("test","material.catalyst-energy-electric-example.wire.test",0x555555, 1, VoltageTier.LV,0,9001);
		lvTest = new WireMaterial("test","material.catalyst-energy-electric-example.wire.test",0xFF5555, 1, VoltageTier.LV,0,9001);
		mvTest = new WireMaterial("test", "material.catalyst-energy-electric-example.wire.test", 0xFFAA00, 1, VoltageTier.LV, 0, 9001);
        hvTest = new WireMaterial("test", "material.catalyst-energy-electric-example.wire.test", 0xFFFF55, 1, VoltageTier.LV, 0, 9001);
        evTest = new WireMaterial("test", "material.catalyst-energy-electric-example.wire.test", 0x55FF55 , 1, VoltageTier.LV, 0, 9001);
        uvTest = new WireMaterial("test", "material.catalyst-energy-electric-example.wire.test", 0x5555FF, 1, VoltageTier.LV, 0, 9001);
        ovTest = new WireMaterial("test", "material.catalyst-energy-electric-example.wire.test", 0xFF55FF, 1, VoltageTier.LV, 0, 9001);

		setInitialized(true);
	}
}
