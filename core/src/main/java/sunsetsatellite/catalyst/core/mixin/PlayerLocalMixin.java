package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.gui.Screen;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.mp.IMpGui;
import sunsetsatellite.catalyst.core.util.mp.MpGuiEntryClient;

import java.lang.reflect.InvocationTargetException;

@Mixin(value = PlayerLocal.class,remap = false)
public class PlayerLocalMixin implements IMpGui {

	@Shadow
	protected Minecraft mc;

	@Override
	public void catalyst$displayCustomGUI(Container inventory, ItemStack stack) {
		MpGuiEntryClient entry = (MpGuiEntryClient) Catalyst.GUIS.getItem(inventory.getNameTranslationKey());
		try {
			mc.displayScreen((Screen) entry.guiClass.getDeclaredConstructors()[0].newInstance(this.mc.thePlayer.inventory,inventory));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void catalyst$displayCustomGUI(TileEntity tileEntity, String id) {
		MpGuiEntryClient entry = (MpGuiEntryClient) Catalyst.GUIS.getItem(id);
		try {
			mc.displayScreen((Screen) entry.guiClass.getDeclaredConstructors()[0].newInstance(this.mc.thePlayer.inventory,tileEntity));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
