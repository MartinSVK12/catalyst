package sunsetsatellite.catalyst.fluids.api;


import net.minecraft.core.block.BlockFluid;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.fluids.util.FluidStack;


public interface IMassFluidInventory extends IFluidInventory {
    int getAllFluidAmount();

    int getRemainingCapacity();

    FluidStack findStack(BlockFluid fluid);
    FluidStack insertFluid(FluidStack fluidStack);

    BlockFluid getFilter(Direction dir);

    boolean canInsertFluid(FluidStack fluidStack);
}
