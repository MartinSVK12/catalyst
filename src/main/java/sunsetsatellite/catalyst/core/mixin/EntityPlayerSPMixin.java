package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.IMpGui;
import sunsetsatellite.catalyst.core.util.MpGuiEntry;

import java.lang.reflect.InvocationTargetException;


@Mixin(value = EntityPlayerSP.class,remap = false)
public class EntityPlayerSPMixin implements IMpGui {
	@Shadow
	protected Minecraft mc;

	@Override
	public void displayCustomGUI(IInventory inventory, ItemStack stack) {
		MpGuiEntry entry = Catalyst.GUIS.getItem(inventory.getInvName());
		try {
			mc.displayGuiScreen((GuiScreen) entry.guiClass.getDeclaredConstructors()[0].newInstance(this.mc.thePlayer.inventory,inventory));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
    }

	@Override
	public void displayCustomGUI(TileEntity tileEntity, String id) {
		MpGuiEntry entry = Catalyst.GUIS.getItem(id);
		try {
			mc.displayGuiScreen((GuiScreen) entry.guiClass.getDeclaredConstructors()[0].newInstance(this.mc.thePlayer.inventory,tileEntity));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
