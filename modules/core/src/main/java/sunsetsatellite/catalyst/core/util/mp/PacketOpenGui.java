package sunsetsatellite.catalyst.core.util.mp;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.entity.TileEntity;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.mp.entry.ItemGuiEntry;
import sunsetsatellite.catalyst.core.util.mp.entry.TileDataGuiEntry;
import sunsetsatellite.catalyst.core.util.mp.entry.TileGuiEntry;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

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
	public CompoundTag data = null;

	public PacketOpenGui() {
	}

	public PacketOpenGui(int windowId, String windowTitle, int x, int y, int z) {
		this.windowId = windowId;
		this.windowTitle = windowTitle;
		this.type = "tile";
		this.blockX = x;
		this.blockY = y;
		this.blockZ = z;
	}

	public PacketOpenGui(int windowId, String windowTitle, int x, int y, int z, CompoundTag data) {
		this.windowId = windowId;
		this.windowTitle = windowTitle;
		this.type = "tile";
		this.blockX = x;
		this.blockY = y;
		this.blockZ = z;
		this.data = data;
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
		packet.writeBoolean(data != null);
		if (data != null) {
			packet.writeCompoundTag(data);
		}
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
		boolean hasData = packet.readBoolean();
		if (hasData) {
			this.data = packet.readCompoundTag();
		} else {
			this.data = null;
		}
	}

	@Override
	public void handleClientEnv(NetworkContext context) {
		Minecraft mc = Minecraft.getMinecraft();
		if (Objects.equals(type, "tile")) {
			TileEntity tile;
			tile = context.player.world.getTileEntity(blockX, blockY, blockZ);
			if (tile != null) {
				if (data == null) {
					TileGuiEntry<? super TileEntity, ?> entry = (TileGuiEntry<? super TileEntity, ?>) Catalyst.GUIS.getItem(windowTitle);
					if(entry == null){
						throw new NullPointerException("No entry defined for '"+windowTitle+"'!");
					}
					mc.displayScreen(entry.guiFactory.create(mc.thePlayer.inventory, tile));
				} else {
					TileDataGuiEntry<? super TileEntity, ?> entry = (TileDataGuiEntry<? super TileEntity, ?>) Catalyst.GUIS.getItem(windowTitle);
					if(entry == null){
						throw new NullPointerException("No entry defined for '"+windowTitle+"'!");
					}
					mc.displayScreen(entry.guiFactory.create(mc.thePlayer.inventory, tile, data));
				}

			}
			context.player.containerMenu.containerId = windowId;
		} else if (Objects.equals(type, "item")) {
			ItemGuiEntry<?,?> entry = (ItemGuiEntry<?,?>) Catalyst.GUIS.getItem(windowTitle);
			if(entry == null){
				throw new NullPointerException("No entry defined for '"+windowTitle+"'!");
			}
			mc.displayScreen(entry.guiFactory.create(mc.thePlayer.inventory, stackIndex, isArmor));
			context.player.containerMenu.containerId = windowId;
		}
	}

}
