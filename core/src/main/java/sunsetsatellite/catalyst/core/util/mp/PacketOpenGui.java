package sunsetsatellite.catalyst.core.util.mp;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketOpenGui extends Packet {

	public int windowId;
	public String windowTitle;
	public String type;
	public int blockX;
	public int blockY;
	public int blockZ;
	public int stackIndex;

	public PacketOpenGui(){}

	public PacketOpenGui(int windowId, String windowTitle, int x, int y, int z) {
		this.windowId = windowId;
		this.windowTitle = windowTitle;
		this.type = "tile";
		this.blockX = x;
		this.blockY = y;
		this.blockZ = z;
	}

	public PacketOpenGui(int windowId, String windowTitle, int stackIndex) {
		this.windowId = windowId;
		this.windowTitle = windowTitle;
		this.type = "item";
		this.stackIndex = stackIndex;
	}

	@Override
	public void read(DataInputStream datainputstream) throws IOException {
		this.windowId = datainputstream.readByte();
		this.windowTitle = datainputstream.readUTF();
		this.type = datainputstream.readUTF();
		this.blockX = datainputstream.readInt();
		this.blockY = datainputstream.readInt();
		this.blockZ = datainputstream.readInt();
		this.stackIndex = datainputstream.readInt();
	}

	@Override
	public void write(DataOutputStream dataoutputstream) throws IOException {
		dataoutputstream.writeByte(this.windowId);
		dataoutputstream.writeUTF(this.windowTitle);
		dataoutputstream.writeUTF(this.type);
		dataoutputstream.writeInt(this.blockX);
		dataoutputstream.writeInt(this.blockY);
		dataoutputstream.writeInt(this.blockZ);
		dataoutputstream.writeInt(this.stackIndex);
	}

	@Override
	public void handlePacket(PacketHandler packetHandler) {
		((INetGuiHandler)packetHandler).catalyst$handleOpenGui(this);
	}

	@Override
	public int getEstimatedSize() {
		return 2 + (3*4) + this.windowTitle.length() + this.type.length() + 5;
	}
}
