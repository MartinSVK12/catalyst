package sunsetsatellite.catalyst.core.util.mp;

import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class MpGuiEntry {

	public Class<?> inventoryClass;
	public Class<? extends MenuAbstract> containerClass;

	public MpGuiEntry(Class<?> inventoryClass, Class<? extends MenuAbstract> containerClass) {
		this.inventoryClass = inventoryClass;
		this.containerClass = containerClass;
	}
}
