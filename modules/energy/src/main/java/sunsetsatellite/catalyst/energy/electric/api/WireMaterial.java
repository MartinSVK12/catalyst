package sunsetsatellite.catalyst.energy.electric.api;

import sunsetsatellite.catalyst.core.util.material.Material;

public record WireMaterial(
	Material material,
	VoltageTier maxVoltage, int defaultAmps,
	int lossPerBlock, int meltingTemperature, boolean superconductor) {
}
