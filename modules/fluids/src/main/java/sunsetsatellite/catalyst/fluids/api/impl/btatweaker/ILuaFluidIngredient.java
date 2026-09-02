package sunsetsatellite.catalyst.fluids.api.impl.btatweaker;

import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.util.List;

public interface ILuaFluidIngredient {
	int getAmount();

	List<FluidStack> resolve();
}
