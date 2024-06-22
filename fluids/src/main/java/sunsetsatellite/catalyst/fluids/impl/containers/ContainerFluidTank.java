package sunsetsatellite.catalyst.fluids.impl.containers;


import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.slot.Slot;
import sunsetsatellite.catalyst.fluids.impl.ContainerFluid;
import sunsetsatellite.catalyst.fluids.impl.tiles.TileEntityFluidItemContainer;
import sunsetsatellite.catalyst.fluids.util.SlotFluid;

public class ContainerFluidTank extends ContainerFluid {

    public ContainerFluidTank(IInventory iInventory, TileEntityFluidItemContainer tileEntityFluidTank){
        super(iInventory, tileEntityFluidTank);
        tile = tileEntityFluidTank;

        SlotFluid slot = new SlotFluid(tileEntityFluidTank, 0, 62 + 18,17 + 18);
        addFluidSlot(slot);

        for(int j = 0; j < 3; j++)
        {
            for(int i1 = 0; i1 < 9; i1++)
            {
                addSlot(new Slot(iInventory, i1 + j * 9 + 9, 8 + i1 * 18, 84 + j * 18));
            }

        }

        for(int k = 0; k < 9; k++)
        {
            addSlot(new Slot(iInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityPlayer1) {
        return true;
    }
}
