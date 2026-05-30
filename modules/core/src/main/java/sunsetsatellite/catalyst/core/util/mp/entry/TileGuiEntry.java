package sunsetsatellite.catalyst.core.util.mp.entry;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import sunsetsatellite.catalyst.core.util.mp.factory.TileGuiFactory;

public class TileGuiEntry<INV extends TileEntity, MENU extends MenuAbstract> extends GuiEntryClient<INV, MENU> {
	public final TileGuiFactory<INV> guiFactory;

	public TileGuiEntry(Class<INV> inventoryClass, Class<MENU> containerClass, TileGuiFactory<INV> guiFactory) {
		super(inventoryClass, containerClass);
		this.guiFactory = guiFactory;
	}
}
