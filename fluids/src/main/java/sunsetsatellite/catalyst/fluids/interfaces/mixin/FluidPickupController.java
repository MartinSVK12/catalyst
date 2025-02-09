package sunsetsatellite.catalyst.fluids.interfaces.mixin;

import net.minecraft.core.entity.player.Player;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

public interface FluidPickupController {

    FluidStack catalyst$fluidPickUpFromInventory(int i, int slotID, int button, boolean shift, boolean control, Player player);
}
