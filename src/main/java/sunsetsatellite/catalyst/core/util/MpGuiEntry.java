package sunsetsatellite.catalyst.core.util;

import net.minecraft.client.gui.Gui;
import net.minecraft.core.player.inventory.Container;
import net.minecraft.core.player.inventory.IInventory;

public class MpGuiEntry {

	public Class<?> inventoryClass;
	public Class<? extends Gui> guiClass;
	public Class<? extends Container> containerClass;

	public MpGuiEntry(Class<?> inventoryClass, Class<? extends Gui> guiClass, Class<? extends Container> containerClass) {
		this.inventoryClass = inventoryClass;
		this.guiClass = guiClass;
		this.containerClass = containerClass;
	}
}
