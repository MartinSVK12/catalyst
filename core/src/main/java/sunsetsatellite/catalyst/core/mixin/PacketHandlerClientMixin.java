package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.client.world.WorldClientMP;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.handler.PacketHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.mp.INetGuiHandler;
import sunsetsatellite.catalyst.core.util.mp.MpGuiEntryClient;
import sunsetsatellite.catalyst.core.util.mp.PacketOpenGui;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Mixin(value = PacketHandlerClient.class,remap = false)
public class PacketHandlerClientMixin implements INetGuiHandler {
	@Shadow
	private WorldClientMP worldClientMP;

	@Shadow
	@Final
	private Minecraft mc;

	@Override
	public void catalyst$handleOpenGui(PacketOpenGui packet) {
		if(Objects.equals(packet.type, "tile")){
			TileEntity tile = worldClientMP.getTileEntity(packet.blockX,packet.blockY,packet.blockZ);
			if(tile != null){
				try {
					this.mc.displayScreen((Screen) ((MpGuiEntryClient) Catalyst.GUIS.getItem(packet.windowTitle)).guiClass.getDeclaredConstructors()[0].newInstance(this.mc.thePlayer.inventory,tile));
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
			this.mc.thePlayer.craftingInventory.containerId = packet.windowId;
		} else if (Objects.equals(packet.type, "item")) {
			try {
				this.mc.displayScreen((Screen) ((MpGuiEntryClient) Catalyst.GUIS.getItem(packet.windowTitle)).guiClass.getDeclaredConstructors()[0].newInstance(this.mc.thePlayer.inventory,packet.stack));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
