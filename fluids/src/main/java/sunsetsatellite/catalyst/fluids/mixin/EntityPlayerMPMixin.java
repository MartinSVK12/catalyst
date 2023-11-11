package sunsetsatellite.catalyst.fluids.mixin;


import net.minecraft.core.crafting.ICrafting;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.inventory.Container;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.fluids.interfaces.mixins.IEntityPlayer;
import sunsetsatellite.catalyst.fluids.mp.packets.PacketSetFluidSlot;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

@Mixin(
        value = EntityPlayerMP.class,
        remap = false
)
public abstract class EntityPlayerMPMixin extends EntityPlayer implements IEntityPlayer, ICrafting {

    public EntityPlayerMPMixin(World world) {
        super(world);
    }

    @Shadow
    protected abstract void getNextWindowId();

    @Shadow public NetServerHandler playerNetServerHandler;

    @Shadow private int currentWindowId;

    @Unique
    private final EntityPlayerMP thisAs = (EntityPlayerMP)(Object)this;

    @Override
    public void updateFluidSlot(Container container, int i, FluidStack fluidStack) {
        if (this.playerNetServerHandler != null) {
            this.playerNetServerHandler.sendPacket(new PacketSetFluidSlot(container.windowId, i, fluidStack));
        }
    }
}
