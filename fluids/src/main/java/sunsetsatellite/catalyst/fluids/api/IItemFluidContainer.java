package sunsetsatellite.catalyst.fluids.api;


import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.util.List;


public interface IItemFluidContainer {
    int getCapacity(ItemStack stack);
    int getRemainingCapacity(ItemStack stack);
    boolean canFill(ItemStack stack);
    boolean canDrain(ItemStack stack);
	FluidStack getCurrentFluid(ItemStack stack);
	void setCurrentFluid(FluidStack fluidStack, ItemStack stack);
    ItemStack fill(FluidStack fluidStack, ItemStack stack);
	ItemStack fill(FluidStack fluidStack, ItemStack stack, IFluidInventory tile);
	ItemStack fill(FluidStack fluidStack, ItemStack stack, IFluidInventory tile, int maxAmount);
	ItemStack fill(FluidStack fluidStack, ItemStack stack, IItemFluidContainer inv);
	void drain(ItemStack stack, int slot, IFluidInventory tile);
	void drain(ItemStack stack, int slot, IItemFluidContainer inv);
	FluidStack drain(ItemStack stack, int amount);

	List<Fluid> getAllowedFluids(ItemStack stack);

	ItemStack getFilled(ItemStack stack, FluidStack fluidStack);
}
