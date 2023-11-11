package sunsetsatellite.catalyst.fluids.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.net.handler.NetClientHandler;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.handler.NetHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.fluids.impl.ContainerFluid;
import sunsetsatellite.catalyst.fluids.impl.tiles.TileEntityFluidContainer;
import sunsetsatellite.catalyst.fluids.interfaces.mixins.INetClientHandler;
import sunsetsatellite.catalyst.fluids.mp.packets.PacketSetFluidSlot;
import sunsetsatellite.catalyst.fluids.mp.packets.PacketUpdateClientFluidRender;

@Mixin(
        value= NetClientHandler.class,
        remap = false
)
public class NetClientHandlerMixin extends NetHandler implements INetClientHandler {

    @Shadow
    private Minecraft mc;

    @Override
    public boolean isServerHandler() {
        return false;
    }

    @Override
    public void handleSetFluidSlot(PacketSetFluidSlot packetSetFluidSlot) {
        if (packetSetFluidSlot.windowId == this.mc.thePlayer.craftingInventory.windowId && this.mc.thePlayer.craftingInventory instanceof ContainerFluid) {
            ((ContainerFluid) this.mc.thePlayer.craftingInventory).putFluidInSlot(packetSetFluidSlot.fluidSlot, packetSetFluidSlot.fluidStack);
        }
    }

    @Override
    public void handleUpdateClientFluidRender(PacketUpdateClientFluidRender packetUpdateClientFluidRender) {
        TileEntity tile = this.mc.theWorld.getBlockTileEntity(packetUpdateClientFluidRender.x, packetUpdateClientFluidRender.y, packetUpdateClientFluidRender.z);
        if(tile instanceof TileEntityFluidContainer){
            ((TileEntityFluidContainer) tile).shownFluid = packetUpdateClientFluidRender.fluidStack;
            ((TileEntityFluidContainer) tile).shownMaxAmount = packetUpdateClientFluidRender.fluidMaxAmount;
        }
    }
}
