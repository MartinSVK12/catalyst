package sunsetsatellite.catalyst.fluids.mixin;


import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import sunsetsatellite.catalyst.fluids.impl.MenuFluid;
import sunsetsatellite.catalyst.fluids.interfaces.mixin.FluidPickupController;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

@Mixin(
      value = PlayerController.class,
      remap = false
)
public class PlayerControllerMixin implements FluidPickupController {

    @Override
    public FluidStack catalyst$fluidPickUpFromInventory(int i, int j, int k, boolean flag, boolean control, Player player) {
        if(player.craftingInventory instanceof MenuFluid){
            return ((MenuFluid) player.craftingInventory).clickFluidSlot(j, k, flag, control, player);
        } /*else if (player.craftingInventory instanceof ContainerItemFluid) {
            return ((ContainerItemFluid) entityplayer.craftingInventory).clickFluidSlot(j, k, flag, control, entityplayer);
        }*/
        return null;
    }
}
