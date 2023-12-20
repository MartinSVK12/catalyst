package sunsetsatellite.catalyst.core.util;


import com.mojang.nbt.CompoundTag;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.handler.NetHandler;
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
    public ItemStack stack;

    public PacketOpenGui(int windowId, String windowTitle, int x, int y, int z) {
        this.windowId = windowId;
        this.windowTitle = windowTitle;
		this.type = "tile";
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
    }

    public PacketOpenGui(int windowId, String windowTitle, ItemStack stack) {
        this.windowId = windowId;
        this.windowTitle = windowTitle;
		this.type = "item";
        this.stack = stack;
    }

    public void processPacket(NetHandler nethandler) {
        ((INetGuiHandler)nethandler).handleOpenGui(this);
    }

    public void readPacketData(DataInputStream datainputstream) throws IOException {
        this.windowId = datainputstream.readByte();
        this.windowTitle = datainputstream.readUTF();
		this.type = datainputstream.readUTF();
        this.blockX = datainputstream.readInt();
        this.blockY = datainputstream.readInt();
        this.blockZ = datainputstream.readInt();
        short id = datainputstream.readShort();
        if (id >= 0) {
            byte amount = datainputstream.readByte();
            short metadata = datainputstream.readShort();
            CompoundTag tag = readCompressedCompoundTag(datainputstream);
            this.stack = new ItemStack(id, amount, metadata, tag);
        } else {
            this.stack = null;
        }
    }

    public void writePacketData(DataOutputStream dataoutputstream) throws IOException {
        dataoutputstream.writeByte(this.windowId);
        dataoutputstream.writeUTF(this.windowTitle);
		dataoutputstream.writeUTF(this.type);
        dataoutputstream.writeInt(this.blockX);
        dataoutputstream.writeInt(this.blockY);
        dataoutputstream.writeInt(this.blockZ);
        if (this.stack == null) {
            dataoutputstream.writeShort(-1);
        } else {
            dataoutputstream.writeShort(this.stack.itemID);
            dataoutputstream.writeByte(this.stack.stackSize);
            dataoutputstream.writeShort(this.stack.getMetadata());
            writeCompressedCompoundTag(this.stack.getData(), dataoutputstream);
        }
    }

    public int getPacketSize() {
        return 2 + (3*4) + this.windowTitle.length() + this.type.length() + 5;
    }
}
