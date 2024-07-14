package sunsetsatellite.catalyst.fluids.util;

import net.minecraft.core.block.BlockFluid;

import java.util.List;

public class FluidType {
	private final String id;
	public List<BlockFluid> fluids;

	public FluidType(String id, List<BlockFluid> fluids) {
		this.id = id;
		this.fluids = fluids;
	}

	public String getId() {
		return id;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FluidType)) return false;

		FluidType fluidType = (FluidType) o;
		return getId().equals(fluidType.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}
