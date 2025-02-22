package sunsetsatellite.catalyst.fluids.mp;


import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import sunsetsatellite.catalyst.fluids.interfaces.mixin.FluidPacketHandler;
import sunsetsatellite.catalyst.fluids.util.Fluid;
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

	public PacketSetFluidSlot(int windowId, int fluidSlot, FluidStack fluidStack) {
		this.windowId = windowId;
		this.fluidSlot = fluidSlot;
		this.fluidStack = fluidStack;
	}

	@Override
	public void read(DataInputStream dataInputStream) throws IOException {
		this.windowId = dataInputStream.readByte();
		this.fluidSlot = dataInputStream.readInt();
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
		dataOutputStream.writeInt(this.fluidSlot);
		if (this.fluidStack == null) {
			dataOutputStream.writeUTF("null");
		} else {
			dataOutputStream.writeUTF(this.fluidStack.fluid.id.toString());
			dataOutputStream.writeInt(this.fluidStack.amount);
		}
	}

	@Override
	public void handlePacket(PacketHandler packetHandler) {
		((FluidPacketHandler) packetHandler).catalyst$handleSetFluidSlot(this);
	}

	@Override
	public int getEstimatedSize() {
		return 1+4+4+(fluidStack == null ? 0 : fluidStack.fluid.id.length());
	}
}
