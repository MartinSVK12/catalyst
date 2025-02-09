package sunsetsatellite.catalyst.fluids.mixin;

import net.minecraft.client.entity.player.PlayerLocal;
import org.spongepowered.asm.mixin.Mixin;
import sunsetsatellite.catalyst.fluids.impl.MenuFluid;
import sunsetsatellite.catalyst.fluids.interfaces.mixin.FluidSlotUpdater;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

@Mixin(value = PlayerLocal.class, remap = false)

public class PlayerLocalMixin implements FluidSlotUpdater {
	@Override
	public void catalyst$updateFluidSlot(MenuFluid container, int i, FluidStack fluidStack) {

	}
}
