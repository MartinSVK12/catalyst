package sunsetsatellite.catalyst.core.mixin;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.player.inventory.container.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.mp.IMpGui;
import sunsetsatellite.catalyst.core.util.mp.entry.ItemGuiEntry;
import sunsetsatellite.catalyst.core.util.mp.entry.TileDataGuiEntry;
import sunsetsatellite.catalyst.core.util.mp.entry.TileGuiEntry;

@Mixin(value = PlayerLocal.class, remap = false)
public class PlayerLocalMixin implements IMpGui {

	@Shadow
	protected Minecraft mc;

	@Override
	public void catalyst$displayCustomGUI(Container inventory, int slotIndex, boolean isArmor, String id) {
		ItemGuiEntry<?,?> entry = (ItemGuiEntry<?,?>) Catalyst.GUIS.getItem(id);
		if(entry == null){
			throw new NullPointerException("No entry defined for '"+id+"'!");
		}
		mc.displayScreen(entry.guiFactory.create(this.mc.thePlayer.inventory, slotIndex, isArmor));
	}

	@Override
	public void catalyst$displayCustomGUI(TileEntity tileEntity, String id) {
		TileGuiEntry<? super TileEntity, ?> entry = (TileGuiEntry<? super TileEntity, ?>) Catalyst.GUIS.getItem(id);
		if(entry == null){
			throw new NullPointerException("No entry defined for '"+id+"'!");
		}
		mc.displayScreen(entry.guiFactory.create(this.mc.thePlayer.inventory, tileEntity));
	}

	@Override
	public void catalyst$displayCustomGUI(TileEntity tileEntity, String id, CompoundTag data) {
		TileDataGuiEntry<? super TileEntity, ?> entry = (TileDataGuiEntry<? super TileEntity, ?>) Catalyst.GUIS.getItem(id);
		if(entry == null){
			throw new NullPointerException("No entry defined for '"+id+"'!");
		}
		mc.displayScreen(entry.guiFactory.create(this.mc.thePlayer.inventory, tileEntity, data));
	}
}
