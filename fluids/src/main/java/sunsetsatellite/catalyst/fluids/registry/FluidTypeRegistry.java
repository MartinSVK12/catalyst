package sunsetsatellite.catalyst.fluids.registry;

import net.minecraft.core.data.registry.Registry;
import sunsetsatellite.catalyst.fluids.util.FluidType;

public class FluidTypeRegistry extends Registry<FluidType> {
	public void register(FluidType item) {
		super.register(item.getId(), item);
	}
}
