package sunsetsatellite.catalyst.energy.electric.api;

public class WireProperties {

	private final int size;
	private final boolean insulated;
	private final boolean superconductor;
	private final WireMaterial material;

	public WireProperties(int size, boolean insulated, boolean superconductor, WireMaterial material) {
		this.size = size;
		this.insulated = insulated;
		this.superconductor = superconductor;
		this.material = material;
	}

	public int getSize() {
		return size;
	}

	public boolean isSuperconductor() {
		return superconductor;
	}

	public WireMaterial getMaterial() {
		return material;
	}

	public boolean isInsulated() {
		return insulated;
	}
}
