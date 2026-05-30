package sunsetsatellite.catalyst.core.util.mp.factory;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.gui.Screen;
import net.minecraft.core.player.inventory.container.ContainerInventory;

@FunctionalInterface
	public interface TileDataGuiFactory<INV> extends GuiFactory {
		Screen create(ContainerInventory playerInventory, INV tile, CompoundTag tag);
	}
