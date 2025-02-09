package sunsetsatellite.catalyst.core.util.mp;

import net.minecraft.client.gui.Gui;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class MpGuiEntry {

	public Class<?> inventoryClass;
	public Class<? extends Gui> guiClass;
	public Class<? extends MenuAbstract> containerClass;

	public MpGuiEntry(Class<?> inventoryClass, Class<? extends Gui> guiClass, Class<? extends MenuAbstract> containerClass) {
		this.inventoryClass = inventoryClass;
		this.guiClass = guiClass;
		this.containerClass = containerClass;
	}
}
