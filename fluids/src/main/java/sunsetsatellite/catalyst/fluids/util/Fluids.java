package sunsetsatellite.catalyst.fluids.util;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.collection.Pair;
import sunsetsatellite.catalyst.Catalyst;

import java.util.ArrayList;
import java.util.List;

public class Fluids {

	public static Fluid WATER;
	public static Fluid LAVA;
	//public static Fluid MILK;
	//public static Fluid ICE_CREAM;

	public static void init() {
		WATER = new Fluid(NamespaceID.getPermanent("minecraft", "fluid/water"), "fluid.water", Catalyst.listOf(Blocks.FLUID_WATER_FLOWING, Blocks.FLUID_WATER_STILL));
		LAVA = new Fluid(NamespaceID.getPermanent("minecraft", "fluid/lava"), "fluid.lava", Catalyst.listOf(Blocks.FLUID_LAVA_FLOWING, Blocks.FLUID_LAVA_STILL));
		//MILK = new Fluid(NamespaceID.getPermanent("minecraft", "fluid/milk"), "fluid.milk.name");
		//ICE_CREAM = new Fluid(NamespaceID.getPermanent("minecraft", "fluid/ice_cream"),"fluid.ice_cream.name");
	}

	public static Fluid getFluid(int id) {
		List<Fluid> list = new ArrayList<>();
		for (Fluid F : Fluid.fluidMap.values()) {
			if (F.blocks.contains(Blocks.getBlock(id))) {
				list.add(F);
			}
		}
		return list.isEmpty() ? null : list.get(0);
	}

	public static List<FluidStack> getFluidStacks(List<ItemStack> items) {
		List<Pair<Fluid, Integer>> list = new ArrayList<>();
		for (ItemStack I : items) {
			Fluid fluid = getFluid(I.itemID);
			if (fluid != null) {
				list.add(Pair.of(fluid, I.stackSize));
			}
		}
		List<FluidStack> result = new ArrayList<>();
		for (Pair<Fluid, Integer> P : list) {
			FluidStack fluidStack = new FluidStack(P.getLeft(), P.getRight());
			result.add(fluidStack);
		}
		return result;
	}


}
