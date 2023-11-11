package sunsetsatellite.catalyst.fluids.mixin;


import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import sunsetsatellite.catalyst.fluids.impl.ContainerFluid;
import sunsetsatellite.catalyst.fluids.impl.ContainerItemFluid;
import sunsetsatellite.catalyst.fluids.interfaces.mixins.IPlayerController;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

@Mixin(
      value = PlayerController.class,
      remap = false
)
public class PlayerControllerMixin implements IPlayerController {

    @Override
    public FluidStack fluidPickUpFromInventory(int i, int j, int k, boolean flag, boolean control, EntityPlayer entityplayer) {
        if(entityplayer.craftingInventory instanceof ContainerFluid){
            return ((ContainerFluid) entityplayer.craftingInventory).clickFluidSlot(j, k, flag, control, entityplayer);
        } else if (entityplayer.craftingInventory instanceof ContainerItemFluid) {
            return ((ContainerItemFluid) entityplayer.craftingInventory).clickFluidSlot(j, k, flag, control, entityplayer);
        }
        return null;
    }
}
