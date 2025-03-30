package sunsetsatellite.catalyst.core.util.mp;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;

public interface IMpGui {

	void catalyst$displayCustomGUI(Container inventory, int slotIndex, boolean isArmor, String id);

	void catalyst$displayCustomGUI(TileEntity tileEntity, String id);

	void catalyst$displayCustomGUI(TileEntity tileEntity, String id, CompoundTag data);
}
