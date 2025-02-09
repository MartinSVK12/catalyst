package sunsetsatellite.catalyst.fluids.mixin;

import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.fluids.impl.MenuFluid;
import sunsetsatellite.catalyst.fluids.interfaces.mixin.FluidSlotUpdater;
import sunsetsatellite.catalyst.fluids.mp.PacketSetFluidSlot;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

@Mixin(value = PlayerServer.class, remap = false)

public class PlayerServerMixin implements FluidSlotUpdater {
	@Shadow
	public PacketHandlerServer playerNetServerHandler;

	@Override
	public void catalyst$updateFluidSlot(MenuFluid container, int i, FluidStack fluidStack) {
		if (this.playerNetServerHandler != null) {
			this.playerNetServerHandler.sendPacket(new PacketSetFluidSlot(container.containerId, i, fluidStack));
		}
	}
}
