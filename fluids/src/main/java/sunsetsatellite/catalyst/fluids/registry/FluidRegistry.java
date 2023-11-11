package sunsetsatellite.catalyst.fluids.registry;

import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.item.Item;

import java.util.ArrayList;
import java.util.List;

public class FluidRegistry extends Registry<FluidRegistryEntry> {

	public List<Item> findContainers(BlockFluid fluid){
		List<Item> items = new ArrayList<>();
		for (FluidRegistryEntry fluidRegistryEntry : this) {
			if(fluidRegistryEntry.fluid.contains(fluid)){
				Item item = fluidRegistryEntry.container;
				items.add(item);
			}
		}
		return items;
	}

	public List<Item> findFilledContainersWithContainer(BlockFluid fluid, Item container){
		List<Item> items = new ArrayList<>();
		for (FluidRegistryEntry fluidRegistryEntry : this) {
			if(fluidRegistryEntry.fluid.contains(fluid) && fluidRegistryEntry.containerEmpty == container){
				Item item = fluidRegistryEntry.container;
				items.add(item);
			}
		}
		return items;
	}

	public List<Item> findEmptyContainers(BlockFluid fluid){
		List<Item> items = new ArrayList<>();
		for (FluidRegistryEntry fluidRegistryEntry : this) {
			if(fluidRegistryEntry.fluid.contains(fluid)) {
				Item item = fluidRegistryEntry.containerEmpty;
				items.add(item);
			}
		}
		return items;
	}

	public List<Item> findEmptyContainersWithContainer(BlockFluid fluid, Item container){
		List<Item> items = new ArrayList<>();
		for (FluidRegistryEntry fluidRegistryEntry : this) {
			if(fluidRegistryEntry.fluid.contains(fluid) && fluidRegistryEntry.container == container) {
				Item item = fluidRegistryEntry.containerEmpty;
				items.add(item);
			}
		}
		return items;
	}

	public List<BlockFluid> findFluidsWithFilledContainer(Item container){
		List<BlockFluid> fluids = new ArrayList<>();
		for (FluidRegistryEntry fluidRegistryEntry : this) {
			if(fluidRegistryEntry.container == container){
				List<BlockFluid> fluid = fluidRegistryEntry.fluid;
				fluids.addAll(fluid);
			}
		}
		return fluids;
	}

	public List<BlockFluid> findFluidsWithEmptyContainer(Item container){
		List<BlockFluid> fluids = new ArrayList<>();
		for (FluidRegistryEntry fluidRegistryEntry : this) {
			if(fluidRegistryEntry.containerEmpty == container){
				List<BlockFluid> fluid = fluidRegistryEntry.fluid;
				fluids.addAll(fluid);
			}
		}
		return fluids;
	}

	public List<BlockFluid> getAllFluids(){
		List<BlockFluid> fluids = new ArrayList<>();
        for (FluidRegistryEntry fluidRegistryEntry : this) {
			List<BlockFluid> fluid = fluidRegistryEntry.fluid;
            fluids.addAll(fluid);
        }
		return fluids;
	}
}
