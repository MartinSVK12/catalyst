package sunsetsatellite.catalyst.fluids.api;


import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

public interface IFluidTransfer {
    void take(FluidStack fluidStack, Direction dir);

    void give(Direction dir);

    Connection getConnection(Direction dir);
}
