package sunsetsatellite.catalyst.energy.electric.api;

public class WireMaterial {
	private final String name;
	private final String identifier;
	private final int color;
	private final VoltageTier maxVoltage;
	private final int defaultAmps;
	private final int lossPerBlock;
	private final int meltingTemperature;


	public WireMaterial(String name, String identifier, int color, int defaultAmps, VoltageTier maxVoltage, int lossPerBlock, int meltingTemperature) {
		this.name = name;
		this.identifier = identifier;
		this.color = color;
		this.maxVoltage = maxVoltage;
		this.defaultAmps = defaultAmps;
		this.lossPerBlock = lossPerBlock;
		this.meltingTemperature = meltingTemperature;
	}

	public String getName() {
		return name;
	}

	public int getMeltingTemperature() {
		return meltingTemperature;
	}

	public VoltageTier getMaxVoltage() {
		return maxVoltage;
	}

	public int getLossPerBlock() {
		return lossPerBlock;
	}

	public String getIdentifier() {
		return identifier;
	}

	public int getDefaultAmps() {
		return defaultAmps;
	}

	public int getColor() {
		return color;
	}
}
