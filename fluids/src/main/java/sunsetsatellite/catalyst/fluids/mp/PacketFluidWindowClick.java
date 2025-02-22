package sunsetsatellite.catalyst.fluids.mp;


import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.server.net.handler.PacketHandlerServer;
import sunsetsatellite.catalyst.fluids.interfaces.mixin.FluidPacketHandlerServer;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketFluidWindowClick extends Packet {
    public int windowId;
    public int inventorySlot;
    public int mouseClick;
    public short action;
    public FluidStack fluidStack;
    public boolean shift;
    public boolean control;

	public PacketFluidWindowClick() {
	}

	public PacketFluidWindowClick(int i, int j, int k, boolean shift, boolean control, FluidStack fluidStack, short word0) {
		this.windowId = i;
		this.inventorySlot = j;
		this.mouseClick = k;
		this.fluidStack = fluidStack;
		this.action = word0;
		this.shift = shift;
		this.control = control;
	}

	@Override
	public void read(DataInputStream dataInputStream) throws IOException {
		this.windowId = dataInputStream.readByte();
		this.inventorySlot = dataInputStream.readShort();
		this.mouseClick = dataInputStream.readByte();
		this.action = dataInputStream.readShort();
		this.shift = dataInputStream.readBoolean();
		this.control = dataInputStream.readBoolean();
		String fluidId = dataInputStream.readUTF();
		int amount = dataInputStream.readInt();
		if(!fluidId.equals("null")) {
			try {
				fluidStack = new FluidStack(Fluid.fluidMap.get(NamespaceID.getTemp(fluidId)),amount);
			} catch (HardIllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeByte(this.windowId);
		dataOutputStream.writeShort(this.inventorySlot);
		dataOutputStream.writeByte(this.mouseClick);
		dataOutputStream.writeShort(this.action);
		dataOutputStream.writeBoolean(this.shift);
		dataOutputStream.writeBoolean(this.control);
		if (this.fluidStack == null) {
			dataOutputStream.writeUTF("null");
			dataOutputStream.writeInt(-1);
		} else {
			dataOutputStream.writeUTF(this.fluidStack.fluid.id.toString());
			dataOutputStream.writeInt(this.fluidStack.amount);
		}
	}

	@Override
	public void handlePacket(PacketHandler packetHandler) {
		((FluidPacketHandlerServer) packetHandler).catalyst$handleFluidWindowClick(this);
	}

	@Override
	public int getEstimatedSize() {
		return 1+2+1+1+1+1+(fluidStack == null ? 0 : fluidStack.fluid.id.length())+4;
	}
}
