package sunsetsatellite.catalyst.fluids.util;

import net.minecraft.core.block.BlockFluid;
import sunsetsatellite.catalyst.fluids.api.IFluidInventory;

import java.util.List;

public class SlotFluid {
    public final IFluidInventory fluidInventory;
    public int slotIndex;
    public int slotNumber;
    public int xPos;
    public int yPos;

    public SlotFluid(IFluidInventory iFluidInventory, int idx, int x, int y) {
        fluidInventory = iFluidInventory;
        slotIndex = idx;
        xPos = x;
        yPos = y;
    }

    public void onSlotChanged(){
        this.fluidInventory.onFluidInventoryChanged();
    }

    public void onPickupFromSlot(FluidStack stack) {
        this.onSlotChanged();
    }

    public boolean isFluidValid(BlockFluid stack) {
        return true;
    }

	public boolean isAnyFluidValid(List<BlockFluid> stack) {
		return true;
	}

	public boolean areAllFluidValid(List<BlockFluid> stack) {
		return true;
	}

    public FluidStack getFluidStack() {
        return fluidInventory.getFluidInSlot(this.slotIndex);
    }

    public boolean getHasStack() {
        return this.getFluidStack() != null;
    }

    public void putStack(FluidStack stack) {
        if(stack == null){
            this.fluidInventory.setFluidInSlot(this.slotIndex,null);
            this.onSlotChanged();
        }
        else if(fluidInventory.getAllowedFluidsForSlot(slotIndex).isEmpty() || fluidInventory.getAllowedFluidsForSlot(slotIndex).contains(stack.liquid)){
            this.fluidInventory.setFluidInSlot(this.slotIndex, stack);
            this.onSlotChanged();
        }
    }

    public int getBackgroundIconIndex() {
        return -1;
    }
}
