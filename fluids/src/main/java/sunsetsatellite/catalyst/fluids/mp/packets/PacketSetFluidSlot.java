package sunsetsatellite.catalyst.fluids.mp.packets;


import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.Packet;
import sunsetsatellite.catalyst.fluids.interfaces.mixins.INetClientHandler;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketSetFluidSlot extends Packet {
    public int windowId;
    public int fluidSlot;
    public FluidStack fluidStack;

    public PacketSetFluidSlot() {
    }

    public void processPacket(NetHandler nethandler) {
        ((INetClientHandler)nethandler).handleSetFluidSlot(this);
    }

    public PacketSetFluidSlot(int i, int j, FluidStack fluidStack) {
        this.windowId = i;
        this.fluidSlot = j;
        this.fluidStack = fluidStack != null ? fluidStack.copy() : null;
    }

    public void readPacketData(DataInputStream datainputstream) throws IOException {
        this.windowId = datainputstream.readByte();
        this.fluidSlot = datainputstream.readShort();
        short liquid = datainputstream.readShort();
        if (liquid >= 0) {
            int amount = datainputstream.readInt();
            this.fluidStack = new FluidStack((BlockFluid) Block.blocksList[liquid],amount);
        } else {
            this.fluidStack = null;
        }

    }

    public void writePacketData(DataOutputStream dataoutputstream) throws IOException {
        dataoutputstream.writeByte(this.windowId);
        dataoutputstream.writeShort(this.fluidSlot);
        if (this.fluidStack == null) {
            dataoutputstream.writeShort(-1);
        } else {
            dataoutputstream.writeShort(this.fluidStack.liquid.id);
            dataoutputstream.writeInt(this.fluidStack.amount);
        }

    }

    public int getPacketSize() {
        return 9;
    }
}
