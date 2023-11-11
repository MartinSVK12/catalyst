package sunsetsatellite.catalyst.fluids.interfaces.mixins;


import net.minecraft.core.entity.player.EntityPlayer;
import sunsetsatellite.catalyst.fluids.util.FluidStack;


public interface IPlayerControllerMP {

    FluidStack fluidPickUpFromInventory(int i, int slotID, int button, boolean shift, boolean control, EntityPlayer entityplayer);
}
