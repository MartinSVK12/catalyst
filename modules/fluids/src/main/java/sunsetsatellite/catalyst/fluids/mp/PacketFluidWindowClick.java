package sunsetsatellite.catalyst.fluids.mp;


import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.fluids.impl.MenuFluid;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class PacketFluidWindowClick implements NetworkMessage {
	public int windowId;
	public int inventorySlot;
	public int mouseClick;
	public int stateId;
	public FluidStack fluidStack;
	public boolean shift;
	public boolean control;

	public PacketFluidWindowClick() {
	}

	public PacketFluidWindowClick(int i, int j, int k, boolean shift, boolean control, FluidStack fluidStack, int stateId) {
		this.windowId = i;
		this.inventorySlot = j;
		this.mouseClick = k;
		this.fluidStack = fluidStack;
		this.stateId = stateId;
		this.shift = shift;
		this.control = control;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeByte(this.windowId);
		packet.writeInt(this.inventorySlot);
		packet.writeByte(this.mouseClick);
		packet.writeInt(this.stateId);
		packet.writeBoolean(this.shift);
		packet.writeBoolean(this.control);
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
		this.inventorySlot = packet.readInt();
		this.mouseClick = packet.readByte();
		this.stateId = packet.readInt();
		this.shift = packet.readBoolean();
		this.control = packet.readBoolean();
		String fluidId = packet.readString();
		int amount = packet.readInt();
		if (!fluidId.equals("null")) {
			try {
				fluidStack = new FluidStack(Fluid.fluidMap.get(NamespaceID.getTemp(fluidId)), amount);
			} catch (HardIllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void handle(NetworkContext context) {
		if (context.player.containerMenu.containerId == windowId && context.player.containerMenu instanceof MenuFluid) {
			FluidStack fluidStack = ((MenuFluid) context.player.containerMenu).clickFluidSlot(inventorySlot, mouseClick, shift, control, context.player);
		}
	}
}
