package sunsetsatellite.catalyst.fluids.util;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.collection.Pair;
import sunsetsatellite.catalyst.Catalyst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Fluids {

	public static Fluid WATER;
	public static Fluid LAVA;
	public static Fluid ACID;
	//public static Fluid MILK;
	//public static Fluid ICE_CREAM;

	public static void init() {
		WATER = new Fluid(
			NamespaceID.fromPool("minecraft", "fluid/water"),
			"fluid.water",
			Catalyst.listOf(Blocks.FLUID_WATER_FLOWING, Blocks.FLUID_WATER_STILL),
			ItemBucket.STATE_WATER);
		LAVA = new Fluid(
			NamespaceID.fromPool("minecraft", "fluid/lava"),
			"fluid.lava",
			Catalyst.listOf(Blocks.FLUID_LAVA_FLOWING, Blocks.FLUID_LAVA_STILL),
			ItemBucket.STATE_LAVA);
		ACID = new Fluid(
			NamespaceID.fromPool("minecraft", "fluid/acid"),
			"fluid.acid", Catalyst.listOf(Blocks.FLUID_ACID_FLOWING, Blocks.FLUID_ACID_STILL),
			ItemBucket.STATE_ACID);
		/*MILK = new Fluid(
			NamespaceID.fromPool("minecraft", "fluid/milk"),
			"fluid.milk", List.of(),
			ItemBucket.STATE_MILK);
		ICE_CREAM = new Fluid(
			NamespaceID.fromPool("minecraft", "fluid/ice_cream"),
			"fluid.ice_cream", List.of(),
			ItemBucket.STATE_ICECREAM);*/
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

	public static Fluid getFluid(NamespaceID stateId) {
		for (Map.Entry<NamespaceID, Fluid> entry : Fluid.fluidMap.entrySet()) {
			NamespaceID k = entry.getKey();
			Fluid v = entry.getValue();
			if (stateId.equals(v.stateId)) {
				return v;
			}
		}
		return null;
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
