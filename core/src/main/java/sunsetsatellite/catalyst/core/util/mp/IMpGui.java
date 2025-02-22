package sunsetsatellite.catalyst.core.util.mp;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;

public interface IMpGui {

	void catalyst$displayCustomGUI(Container inventory, ItemStack stack);

	void catalyst$displayCustomGUI(TileEntity tileEntity, String id);
}
