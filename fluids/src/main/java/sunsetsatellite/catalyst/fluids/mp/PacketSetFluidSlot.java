package sunsetsatellite.catalyst.fluids.mp;


import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.fluids.impl.MenuFluid;
import sunsetsatellite.catalyst.fluids.interfaces.mixin.FluidPacketHandler;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketSetFluidSlot implements NetworkMessage {
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
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeByte(this.windowId);
		packet.writeInt(this.fluidSlot);
		if (this.fluidStack == null) {
			packet.writeString("null");
			packet.writeInt(-1);
		} else {
			packet.writeString(this.fluidStack.fluid.id.toString());
			packet.writeInt(this.fluidStack.amount);
		}
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.windowId = packet.readByte();
		this.fluidSlot = packet.readInt();
		String fluidId = packet.readString();
		int amount = packet.readInt();
		if(!fluidId.equals("null")) {
			try {
				fluidStack = new FluidStack(Fluid.fluidMap.get(NamespaceID.getTemp(fluidId)),amount);
			} catch (HardIllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void handle(NetworkContext context) {
		if (windowId == context.player.craftingInventory.containerId && context.player.craftingInventory instanceof MenuFluid) {
			((MenuFluid) context.player.craftingInventory).putFluidInSlot(fluidSlot, fluidStack);
		}
	}
}
