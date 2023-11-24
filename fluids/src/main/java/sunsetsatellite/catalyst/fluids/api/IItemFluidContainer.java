package sunsetsatellite.catalyst.fluids.api;


import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.fluids.impl.ItemInventoryFluid;
import sunsetsatellite.catalyst.fluids.impl.tiles.TileEntityFluidContainer;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import sunsetsatellite.catalyst.fluids.util.SlotFluid;


public interface IItemFluidContainer {
    int getCapacity(ItemStack stack);
    int getRemainingCapacity(ItemStack stack);
    boolean canFill(ItemStack stack);
    boolean canDrain(ItemStack stack);
    ItemStack fill(FluidStack fluidStack, ItemStack stack);
	ItemStack fill(FluidStack fluidStack, ItemStack stack, TileEntityFluidContainer tile);

	ItemStack fill(FluidStack fluidStack, ItemStack stack, TileEntityFluidContainer tile, int maxAmount);

	ItemStack fill(FluidStack fluidStack, ItemStack stack, ItemInventoryFluid inv);

	void drain(ItemStack stack, SlotFluid slot, TileEntityFluidContainer tile);
	void drain(ItemStack stack, SlotFluid slot, ItemInventoryFluid inv);
}
