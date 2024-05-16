package sunsetsatellite.catalyst.fluids.registry;

import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.block.ItemBlock;
import sunsetsatellite.catalyst.fluids.render.ItemModelFluid;

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

	@Override
	public void register(String key, FluidRegistryEntry item) {
		super.register(key, item);
		if(key.split(":").length < 1){
			throw new IllegalArgumentException("Invalid or malformed key: " + key);
		}
		for (BlockFluid blockFluid : item.fluid) {
			ItemBlock itemBlock = (ItemBlock) Item.itemsList[blockFluid.id];
			ItemModelDispatcher.getInstance().addDispatch(new ItemModelFluid(itemBlock,key.split(":")[0]));
		}
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

	public List<BlockFluid> findFluidsWithAnyContainer(Item container){
		List<BlockFluid> fluids = new ArrayList<>();
		for (FluidRegistryEntry fluidRegistryEntry : this) {
			if(fluidRegistryEntry.containerEmpty == container){
				List<BlockFluid> fluid = fluidRegistryEntry.fluid;
				fluids.addAll(fluid);
			} else if(fluidRegistryEntry.container == container){
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
