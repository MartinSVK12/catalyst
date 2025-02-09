package sunsetsatellite.catalyst.core.util.mp;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;

public interface IMpGui {

	void displayCustomGUI(Container inventory, ItemStack stack);

	void displayCustomGUI(TileEntity tileEntity, String id);
}
