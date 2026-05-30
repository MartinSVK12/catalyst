package sunsetsatellite.catalyst.core.util.mp.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import sunsetsatellite.catalyst.core.util.mp.GuiEntry;

@Environment(EnvType.CLIENT)
public abstract class GuiEntryClient<INV, MENU extends MenuAbstract> extends GuiEntry<INV, MENU> {

	protected GuiEntryClient(Class<INV> inventoryClass, Class<MENU> containerClass) {
		super(inventoryClass, containerClass);
	}

	/*public GuiFactory guiFactory;

	public MpGuiEntryClient(Class<INV> inventoryClass, Class<MENU> containerClass, TileGuiFactory<INV> guiFactory) {
		super(inventoryClass, containerClass);
		this.guiFactory = guiFactory;
	}

	public MpGuiEntryClient(Class<INV> inventoryClass, Class<MENU> containerClass, DataGuiFactory<INV> guiFactory) {
		super(inventoryClass, containerClass);
		this.guiFactory = guiFactory;
	}

	public MpGuiEntryClient(Class<INV> inventoryClass, Class<MENU> containerClass, ItemGuiFactory guiFactory) {
		super(inventoryClass, containerClass);
		this.guiFactory = guiFactory;
	}*/
}
