package sunsetsatellite.catalyst.fluids.mixin;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.fluids.impl.MenuFluid;
import sunsetsatellite.catalyst.fluids.interfaces.mixin.FluidSlotUpdater;
import sunsetsatellite.catalyst.fluids.mp.PacketSetFluidSlot;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import turniplabs.halplibe.helper.network.NetworkHandler;

@Mixin(value = PlayerServer.class, remap = false)

public abstract class PlayerServerMixin extends Player implements FluidSlotUpdater {
	@Shadow
	public PacketHandlerServer playerNetServerHandler;

	private PlayerServerMixin(World world) {
		super(world);
	}

	@Override
	public void catalyst$updateFluidSlot(MenuFluid container, int i, FluidStack fluidStack) {
		if (this.playerNetServerHandler != null) {
			NetworkHandler.sendToPlayer(this, new PacketSetFluidSlot(container.containerId, i, fluidStack));
		}
	}
}
