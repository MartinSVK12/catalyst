package sunsetsatellite.catalyst.core.util.io;

import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;

public interface IItemIO {

    int getActiveItemSlotForSide(Direction dir);

	int getActiveItemSlotForSide(Direction dir, ItemStack stack);

    Connection getItemIOForSide(Direction dir);

	void setItemIOForSide(Direction dir, Connection con);
}
