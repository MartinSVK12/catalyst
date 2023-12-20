package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.net.handler.NetClientHandler;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.Packet100OpenWindow;
import net.minecraft.core.player.inventory.IInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.IMpGui;
import sunsetsatellite.catalyst.core.util.INetGuiHandler;
import sunsetsatellite.catalyst.core.util.PacketOpenGui;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Mixin(
	value= NetClientHandler.class,
	remap = false
)
public class NetClientHandlerMixin extends NetHandler implements INetGuiHandler {

	@Final
	@Shadow
	private Minecraft mc;

	@Shadow
	private WorldClient worldClient;

	@Override
	public boolean isServerHandler() {
		return false;
	}

	@Override
	public void handleOpenGui(PacketOpenGui packet) {
		if(Objects.equals(packet.type, "tile")){
			TileEntity tile = worldClient.getBlockTileEntity(packet.blockX,packet.blockY,packet.blockZ);
			if(tile != null){
				try {
					this.mc.displayGuiScreen((GuiScreen) Catalyst.GUIS.getItem(packet.windowTitle).guiClass.getDeclaredConstructors()[0].newInstance(this.mc.thePlayer.inventory,tile));
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
			this.mc.thePlayer.craftingInventory.windowId = packet.windowId;
		} else if (Objects.equals(packet.type, "item")) {
            try {
                this.mc.displayGuiScreen((GuiScreen) Catalyst.GUIS.getItem(packet.windowTitle).guiClass.getDeclaredConstructors()[0].newInstance(this.mc.thePlayer.inventory,packet.stack));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

	}
}
