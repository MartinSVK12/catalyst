package sunsetsatellite.catalyst.fluids.mp.packets;


import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.Packet;
import sunsetsatellite.catalyst.fluids.interfaces.mixins.INetServerHandler;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketFluidWindowClick extends Packet {
    public int window_Id;
    public int inventorySlot;
    public int mouseClick;
    public short action;
    public FluidStack fluidStack;
    public boolean shift;
    public boolean control;

    public PacketFluidWindowClick() {
    }

    public PacketFluidWindowClick(int i, int j, int k, boolean shift, boolean control, FluidStack fluidStack, short word0) {
        this.window_Id = i;
        this.inventorySlot = j;
        this.mouseClick = k;
        this.fluidStack = fluidStack;
        this.action = word0;
        this.shift = shift;
        this.control = control;
    }

    public void processPacket(NetHandler nethandler) {
        ((INetServerHandler)nethandler).handleFluidWindowClick(this);
    }

    public void readPacketData(DataInputStream datainputstream) throws IOException {
        this.window_Id = datainputstream.readByte();
        this.inventorySlot = datainputstream.readShort();
        this.mouseClick = datainputstream.readByte();
        this.action = datainputstream.readShort();
        this.shift = datainputstream.readBoolean();
        this.control = datainputstream.readBoolean();
        short liquid = datainputstream.readShort();
        if (liquid >= 0) {
            int amount = datainputstream.readInt();
            this.fluidStack = new FluidStack((BlockFluid) Block.blocksList[liquid],amount);
        } else {
            this.fluidStack = null;
        }

    }

    public void writePacketData(DataOutputStream dataoutputstream) throws IOException {
        dataoutputstream.writeByte(this.window_Id);
        dataoutputstream.writeShort(this.inventorySlot);
        dataoutputstream.writeByte(this.mouseClick);
        dataoutputstream.writeShort(this.action);
        dataoutputstream.writeBoolean(this.shift);
        dataoutputstream.writeBoolean(this.control);
        if (this.fluidStack == null) {
            dataoutputstream.writeShort(-1);
        } else {
            dataoutputstream.writeShort(this.fluidStack.liquid.id);
            dataoutputstream.writeInt(this.fluidStack.amount);
        }

    }

    public int getPacketSize() {
        return 12;
    }
}
