package sunsetsatellite.catalyst.fluids.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.net.handler.NetClientHandler;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.client.player.controller.PlayerControllerMP;
import net.minecraft.core.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.fluids.impl.ContainerFluid;
import sunsetsatellite.catalyst.fluids.impl.ContainerItemFluid;
import sunsetsatellite.catalyst.fluids.interfaces.mixins.IPlayerControllerMP;
import sunsetsatellite.catalyst.fluids.mp.packets.PacketFluidWindowClick;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

@Mixin(
        value = PlayerControllerMP.class,
        remap = false
)
public class PlayerControllerMPMixin extends PlayerController implements IPlayerControllerMP {
    @Shadow
    protected NetClientHandler netHandler;

    public PlayerControllerMPMixin(Minecraft minecraft) {
        super(minecraft);
    }

    @Override
    public FluidStack fluidPickUpFromInventory(int i, int slotID, int button, boolean shift, boolean control, EntityPlayer entityplayer) {
        short word0 = entityplayer.craftingInventory.getActionId(entityplayer.inventory);
        FluidStack fluidStack = null;
        if(entityplayer.craftingInventory instanceof ContainerFluid){
            fluidStack = ((ContainerFluid)entityplayer.craftingInventory).clickFluidSlot(slotID, button, shift, control, entityplayer);
        } else if (entityplayer.craftingInventory instanceof ContainerItemFluid) {
            fluidStack = ((ContainerItemFluid)entityplayer.craftingInventory).clickFluidSlot(slotID, button, shift, control, entityplayer);
        }
        this.netHandler.addToSendQueue(new PacketFluidWindowClick(i, slotID, button, shift, control, fluidStack, word0));
        return fluidStack;
    }
}
