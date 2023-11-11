package sunsetsatellite.catalyst.fluids.mixin;


import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.fluids.impl.ContainerFluid;
import sunsetsatellite.catalyst.fluids.interfaces.mixins.INetServerHandler;
import sunsetsatellite.catalyst.fluids.mp.packets.PacketFluidWindowClick;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

@Mixin(
        value = NetServerHandler.class,
        remap = false
)
public class NetServerHandlerMixin implements INetServerHandler {

    @Shadow
    private EntityPlayerMP playerEntity;

    @Override
    public void handleFluidWindowClick(PacketFluidWindowClick p) {
        if (this.playerEntity.craftingInventory.windowId == p.window_Id && this.playerEntity.craftingInventory instanceof ContainerFluid) {
            FluidStack fluidStack = ((ContainerFluid)this.playerEntity.craftingInventory).clickFluidSlot(p.inventorySlot, p.mouseClick, p.shift, p.control, this.playerEntity);
        }
    }
}
