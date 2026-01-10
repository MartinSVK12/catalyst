package sunsetsatellite.catalyst.fluids.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.client.player.controller.PlayerControllerMP;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

	@Shadow
	protected PacketHandlerClient netHandler;

	private PlayerControllerMPMixin(Minecraft minecraft) {
		super(minecraft);
	}

	@Override
	public FluidStack catalyst$fluidPickUpFromInventory(int i, int slotID, int button, boolean shift, boolean control, Player player) {
		short word0 = player.craftingInventory.backup(player.inventory);
		FluidStack fluidStack = null;
		if (player.craftingInventory instanceof MenuFluid) {
			fluidStack = ((MenuFluid) player.craftingInventory).clickFluidSlot(slotID, button, shift, control, player);
		} /*else if (player.craftingInventory instanceof ContainerItemFluid) {
			fluidStack = ((ContainerItemFluid)player.craftingInventory).clickFluidSlot(slotID, button, shift, control, player);
		}*/
		NetworkHandler.sendToServer(new PacketFluidWindowClick(i, slotID, button, shift, control, fluidStack, word0));
		return fluidStack;
	}
}
