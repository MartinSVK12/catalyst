package sunsetsatellite.catalyst.fluids.registry;

import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.item.Item;

import java.util.ArrayList;
import java.util.List;

public class FluidContainerRegistry extends Registry<FluidContainerRegistryEntry> {

	public List<Item> findContainers(BlockFluid fluid){
		List<Item> items = new ArrayList<>();
		for (FluidContainerRegistryEntry fluidContainerRegistryEntry : this) {
			if(fluidContainerRegistryEntry.fluid.contains(fluid)){
				Item item = fluidContainerRegistryEntry.container;
				items.add(item);
			}
		}
		return items;
	}

	@Override
	public void register(String key, FluidContainerRegistryEntry item) {
		super.register(key, item);
		if(!key.contains(":") || key.split(":").length < 1){
			throw new IllegalArgumentException("Invalid or malformed key: " + key);
		}
	}

	public List<Item> findFilledContainersWithContainer(BlockFluid fluid, Item container){
		List<Item> items = new ArrayList<>();
		for (FluidContainerRegistryEntry fluidContainerRegistryEntry : this) {
			if(fluidContainerRegistryEntry.fluid.contains(fluid) && fluidContainerRegistryEntry.containerEmpty == container){
				Item item = fluidContainerRegistryEntry.container;
				items.add(item);
			}
		}
		return items;
	}

	public List<Item> findEmptyContainers(BlockFluid fluid){
		List<Item> items = new ArrayList<>();
		for (FluidContainerRegistryEntry fluidContainerRegistryEntry : this) {
			if(fluidContainerRegistryEntry.fluid.contains(fluid)) {
				Item item = fluidContainerRegistryEntry.containerEmpty;
				items.add(item);
			}
		}
		return items;
	}

	public List<Item> findEmptyContainersWithContainer(BlockFluid fluid, Item container){
		List<Item> items = new ArrayList<>();
		for (FluidContainerRegistryEntry fluidContainerRegistryEntry : this) {
			if(fluidContainerRegistryEntry.fluid.contains(fluid) && fluidContainerRegistryEntry.container == container) {
				Item item = fluidContainerRegistryEntry.containerEmpty;
				items.add(item);
			}
		}
		return items;
	}

	public List<BlockFluid> findFluidsWithFilledContainer(Item container){
		List<BlockFluid> fluids = new ArrayList<>();
		for (FluidContainerRegistryEntry fluidContainerRegistryEntry : this) {
			if(fluidContainerRegistryEntry.container == container){
				List<BlockFluid> fluid = fluidContainerRegistryEntry.fluid;
				fluids.addAll(fluid);
			}
		}
		return fluids;
	}

	public List<BlockFluid> findFluidsWithEmptyContainer(Item container){
		List<BlockFluid> fluids = new ArrayList<>();
		for (FluidContainerRegistryEntry fluidContainerRegistryEntry : this) {
			if(fluidContainerRegistryEntry.containerEmpty == container){
				List<BlockFluid> fluid = fluidContainerRegistryEntry.fluid;
				fluids.addAll(fluid);
			}
		}
		return fluids;
	}

	public List<BlockFluid> findFluidsWithAnyContainer(Item container){
		List<BlockFluid> fluids = new ArrayList<>();
		for (FluidContainerRegistryEntry fluidContainerRegistryEntry : this) {
			if(fluidContainerRegistryEntry.containerEmpty == container){
				List<BlockFluid> fluid = fluidContainerRegistryEntry.fluid;
				fluids.addAll(fluid);
			} else if(fluidContainerRegistryEntry.container == container){
				List<BlockFluid> fluid = fluidContainerRegistryEntry.fluid;
				fluids.addAll(fluid);
			}
		}
		return fluids;
	}

	public List<BlockFluid> getAllFluids(){
		List<BlockFluid> fluids = new ArrayList<>();
        for (FluidContainerRegistryEntry fluidContainerRegistryEntry : this) {
			List<BlockFluid> fluid = fluidContainerRegistryEntry.fluid;
            fluids.addAll(fluid);
        }
		return fluids;
	}
}
