package sunsetsatellite.catalyst.fluids.interfaces.mixins;


import net.minecraft.core.player.inventory.Container;
import sunsetsatellite.catalyst.fluids.util.FluidStack;


public interface IEntityPlayer {

    void updateFluidSlot(Container container, int i, FluidStack fluidStack);
}
