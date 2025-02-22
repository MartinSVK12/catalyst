package sunsetsatellite.catalyst.fluids.util;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.collection.NamespaceID;
import sunsetsatellite.catalyst.Catalyst;

public class Fluids {

	public static Fluid WATER;
	public static Fluid LAVA;
	public static Fluid MILK;
	public static Fluid ICE_CREAM;

	public static void init() {
		WATER = new Fluid(NamespaceID.getPermanent("minecraft", "fluid/water"), "fluid.water.name", Catalyst.listOf(Blocks.FLUID_WATER_FLOWING, Blocks.FLUID_WATER_STILL));
		LAVA = new Fluid(NamespaceID.getPermanent("minecraft", "fluid/lava"), "fluid.lava.name", Catalyst.listOf(Blocks.FLUID_LAVA_FLOWING, Blocks.FLUID_LAVA_STILL));
		MILK = new Fluid(NamespaceID.getPermanent("minecraft", "fluid/milk"), "fluid.milk.name");
		ICE_CREAM = new Fluid(NamespaceID.getPermanent("minecraft", "fluid/ice_cream"),"fluid.ice_cream.name");
	}



}
