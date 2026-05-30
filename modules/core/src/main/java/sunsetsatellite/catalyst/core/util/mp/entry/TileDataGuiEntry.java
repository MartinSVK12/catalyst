package sunsetsatellite.catalyst.core.util.mp.entry;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import sunsetsatellite.catalyst.core.util.mp.factory.TileDataGuiFactory;

public class TileDataGuiEntry<INV extends TileEntity, MENU extends MenuAbstract> extends GuiEntryClient<INV, MENU> {
	public final TileDataGuiFactory<INV> guiFactory;

	public TileDataGuiEntry(Class<INV> inventoryClass, Class<MENU> containerClass, TileDataGuiFactory<INV> guiFactory) {
		super(inventoryClass, containerClass);
		this.guiFactory = guiFactory;
	}
}
