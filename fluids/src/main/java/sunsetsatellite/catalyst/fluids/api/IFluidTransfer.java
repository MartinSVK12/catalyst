package sunsetsatellite.catalyst.fluids.api;


import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

public interface IFluidTransfer {
    void take(FluidStack fluidStack, Direction dir);

	void take(@NotNull FluidStack fluidStack, Direction dir, int slot);

	void give(Direction dir);

	void give(Direction dir, int slot, int otherSlot);
}
