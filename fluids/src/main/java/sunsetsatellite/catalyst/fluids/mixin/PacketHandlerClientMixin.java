package sunsetsatellite.catalyst.fluids.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.handler.PacketHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.fluids.impl.MenuFluid;
import sunsetsatellite.catalyst.fluids.interfaces.mixin.FluidPacketHandler;
import sunsetsatellite.catalyst.fluids.mp.PacketSetFluidSlot;

@Mixin(
        value= PacketHandlerClient.class,
        remap = false
)
public class PacketHandlerClientMixin extends PacketHandler implements FluidPacketHandler {

    @Final
	@Shadow
    private Minecraft mc;

    @Override
    public boolean isServerHandler() {
        return false;
    }

    @Override
    public void catalyst$handleSetFluidSlot(PacketSetFluidSlot packetSetFluidSlot) {

    }
}
