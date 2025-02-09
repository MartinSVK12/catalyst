package sunsetsatellite.catalyst.fluids.mixin;


import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.fluids.impl.MenuFluid;
import sunsetsatellite.catalyst.fluids.interfaces.mixin.FluidPacketHandler;
import sunsetsatellite.catalyst.fluids.interfaces.mixin.FluidPacketHandlerServer;
import sunsetsatellite.catalyst.fluids.mp.PacketFluidWindowClick;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

@Mixin(
        value = PacketHandlerServer.class,
        remap = false
)
public class PacketHandlerServerMixinServer implements FluidPacketHandlerServer {

    @Shadow
    private PlayerServer playerEntity;

    @Override
    public void catalyst$handleFluidWindowClick(PacketFluidWindowClick p) {
        if (this.playerEntity.craftingInventory.containerId == p.windowId && this.playerEntity.craftingInventory instanceof MenuFluid) {
            FluidStack fluidStack = ((MenuFluid)this.playerEntity.craftingInventory).clickFluidSlot(p.inventorySlot, p.mouseClick, p.shift, p.control, this.playerEntity);
        }
    }
}
