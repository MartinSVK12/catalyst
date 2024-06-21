package sunsetsatellite.catalyst.core.util;

import net.minecraft.core.item.ItemStack;

public interface IItemIO {

    int getActiveItemSlotForSide(Direction dir);

	int getActiveItemSlotForSide(Direction dir, ItemStack stack);

    Connection getItemIOForSide(Direction dir);

	void setItemIOForSide(Direction dir, Connection con);
}
