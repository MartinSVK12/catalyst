package sunsetsatellite.catalyst.fluids.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.client.player.controller.PlayerControllerMP;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import sunsetsatellite.catalyst.fluids.impl.MenuFluid;
import sunsetsatellite.catalyst.fluids.interfaces.mixin.FluidPickupController;
import sunsetsatellite.catalyst.fluids.mp.PacketFluidWindowClick;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import turniplabs.halplibe.helper.network.NetworkHandler;

@Mixin(
	value = PlayerControllerMP.class,
	remap = false
)
public class PlayerControllerMPMixin extends PlayerController implements FluidPickupController {

	private PlayerControllerMPMixin(Minecraft minecraft) {
		super(minecraft);
	}

	@Override
	public FluidStack catalyst$fluidPickUpFromInventory(int i, int slotID, int button, boolean shift, boolean control, Player player) {
		int stateId = player.containerMenu.incrementStateId();
		FluidStack fluidStack = null;
		if (player.containerMenu instanceof MenuFluid) {
			fluidStack = ((MenuFluid) player.containerMenu).clickFluidSlot(slotID, button, shift, control, player);
		} /*else if (player.containerMenu instanceof ContainerItemFluid) {
			fluidStack = ((ContainerItemFluid)player.containerMenu).clickFluidSlot(slotID, button, shift, control, player);
		}*/
		NetworkHandler.sendToServer(new PacketFluidWindowClick(i, slotID, button, shift, control, fluidStack, stateId));
		return fluidStack;
	}
}
