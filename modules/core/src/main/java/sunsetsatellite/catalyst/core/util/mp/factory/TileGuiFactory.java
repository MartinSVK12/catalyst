package sunsetsatellite.catalyst.core.util.mp.factory;

import net.minecraft.client.gui.Screen;
import net.minecraft.core.player.inventory.container.ContainerInventory;

@FunctionalInterface
	public interface TileGuiFactory<INV> extends GuiFactory {
		Screen create(ContainerInventory playerInventory, INV tile);
	}
