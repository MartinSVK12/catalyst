package sunsetsatellite.catalyst.fluids.api;


import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import sunsetsatellite.catalyst.fluids.util.SlotFluid;


public interface IItemFluidContainer {
    int getCapacity(ItemStack stack);
    int getRemainingCapacity(ItemStack stack);
    boolean canFill(ItemStack stack);
    boolean canDrain(ItemStack stack);
    ItemStack fill(SlotFluid slot, ItemStack stack);
    void drain(ItemStack stack, FluidStack fluid);
}
