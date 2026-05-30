package sunsetsatellite.catalyst.core.util.mp;

import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class GuiEntry<INV, MENU extends MenuAbstract> {

	public Class<INV> inventoryClass;
	public Class<MENU> containerClass;

	public GuiEntry(Class<INV> inventoryClass, Class<MENU> containerClass) {
		this.inventoryClass = inventoryClass;
		this.containerClass = containerClass;
	}
}
