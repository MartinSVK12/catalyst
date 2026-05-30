package sunsetsatellite.catalyst.core.util.mp.entry;

import net.minecraft.core.player.inventory.menu.MenuAbstract;
import sunsetsatellite.catalyst.core.util.mp.factory.ItemGuiFactory;

public class ItemGuiEntry<INV, MENU extends MenuAbstract> extends GuiEntryClient<INV, MENU> {
	public final ItemGuiFactory guiFactory;

	public ItemGuiEntry(Class<INV> inventoryClass, Class<MENU> containerClass, ItemGuiFactory guiFactory) {
		super(inventoryClass, containerClass);
		this.guiFactory = guiFactory;
	}
}
