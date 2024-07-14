package sunsetsatellite.catalyst.fluids.registry;


import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.item.Item;

import java.util.List;

public class FluidContainerRegistryEntry {

    public String modId;
    public Item container;
    public Item containerEmpty;
    public List<BlockFluid> fluid;

    public FluidContainerRegistryEntry(String modId, Item container, Item containerEmpty, List<BlockFluid> fluid) {
        this.modId = modId;
        this.container = container;
        this.containerEmpty = containerEmpty;
        this.fluid = fluid;
    }
}
