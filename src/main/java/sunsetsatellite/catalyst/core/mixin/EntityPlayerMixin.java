package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;
import sunsetsatellite.catalyst.core.util.IMpGui;

@Mixin(value = EntityPlayer.class,remap = false)
public class EntityPlayerMixin implements IMpGui {
	@Override
	public void displayCustomGUI(IInventory inventory, ItemStack stack) {

	}

	@Override
	public void displayCustomGUI(TileEntity tileEntity, String id) {

	}
}
