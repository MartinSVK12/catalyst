package sunsetsatellite.catalyst.fluids.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.core.player.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.fluids.interfaces.mixins.IEntityPlayer;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

@Mixin(value = EntityPlayerSP.class, remap = false)
public class EntityPlayerSPMixin implements IEntityPlayer {
    @Shadow
    protected Minecraft mc;

    @Override
    public void updateFluidSlot(Container container, int i, FluidStack fluidStack) {

    }

}
