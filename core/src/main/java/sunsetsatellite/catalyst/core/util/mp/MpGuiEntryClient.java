package sunsetsatellite.catalyst.core.util.mp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

@Environment(EnvType.CLIENT)
public class MpGuiEntryClient extends MpGuiEntry {

	public Class<?> guiClass;

	public MpGuiEntryClient(Class<?> inventoryClass, Class<? extends Screen> guiClass, Class<? extends MenuAbstract> containerClass) {
		super(inventoryClass, containerClass);
		this.guiClass = guiClass;
	}
}
