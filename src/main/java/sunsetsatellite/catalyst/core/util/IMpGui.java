package sunsetsatellite.catalyst.core.util;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;

public interface IMpGui {

	void displayCustomGUI(IInventory inventory, ItemStack stack);

	void displayCustomGUI(TileEntity tileEntity, String id);
}
