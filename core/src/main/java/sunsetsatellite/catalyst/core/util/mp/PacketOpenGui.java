package sunsetsatellite.catalyst.core.util.mp;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.Packet;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.Catalyst;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class PacketOpenGui implements NetworkMessage {

	public int windowId;
	public String windowTitle;
	public String type;
	public int blockX;
	public int blockY;
	public int blockZ;
	public int stackIndex;
	public boolean isArmor;

	public PacketOpenGui(){}

	public PacketOpenGui(int windowId, String windowTitle, int x, int y, int z) {
		this.windowId = windowId;
		this.windowTitle = windowTitle;
		this.type = "tile";
		this.blockX = x;
		this.blockY = y;
		this.blockZ = z;
	}

	public PacketOpenGui(int windowId, String windowTitle, int stackIndex, boolean isArmor) {
		this.windowId = windowId;
		this.windowTitle = windowTitle;
		this.type = "item";
		this.stackIndex = stackIndex;
		this.isArmor = isArmor;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeByte(this.windowId);
		packet.writeString(this.windowTitle);
		packet.writeString(this.type);
		packet.writeInt(this.blockX);
		packet.writeInt(this.blockY);
		packet.writeInt(this.blockZ);
		packet.writeInt(this.stackIndex);
		packet.writeBoolean(this.isArmor);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.windowId = packet.readByte();
		this.windowTitle = packet.readString();
		this.type = packet.readString();
		this.blockX = packet.readInt();
		this.blockY = packet.readInt();
		this.blockZ = packet.readInt();
		this.stackIndex = packet.readInt();
		this.isArmor = packet.readBoolean();
	}

	@Override
	public void handle(NetworkContext context) {
		if(Objects.equals(type, "tile")){
			TileEntity tile = null;
			if (context.player.world != null) {
				tile = context.player.world.getTileEntity(blockX,blockY,blockZ);
			}
			if(tile != null){
				try {
					Minecraft.getMinecraft().displayScreen((Screen) ((MpGuiEntryClient) Catalyst.GUIS.getItem(windowTitle)).guiClass.getDeclaredConstructors()[0].newInstance(context.player.inventory,tile));
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
			context.player.craftingInventory.containerId = windowId;
		} else if (Objects.equals(type, "item")) {
			try {
				Minecraft.getMinecraft().displayScreen((Screen) ((MpGuiEntryClient) Catalyst.GUIS.getItem(windowTitle)).guiClass.getDeclaredConstructors()[0].newInstance(context.player.inventory,stackIndex,isArmor));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			context.player.craftingInventory.containerId = windowId;
		}
		//((INetGuiHandler)packetHandler).catalyst$handleOpenGui(this);
	}
}
