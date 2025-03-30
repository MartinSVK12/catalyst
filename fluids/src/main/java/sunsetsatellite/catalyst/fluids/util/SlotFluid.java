package sunsetsatellite.catalyst.fluids.util;

import sunsetsatellite.catalyst.fluids.api.IFluidInventory;

import java.util.List;

public class SlotFluid {
    public final IFluidInventory fluidInventory;
    public int slotIndex;
    public int slotNumber;
    public int x;
    public int y;

    public SlotFluid(IFluidInventory iFluidInventory, int idx, int x, int y) {
        fluidInventory = iFluidInventory;
        slotIndex = idx;
        this.x = x;
        this.y = y;
    }

    public void onSlotChanged(){
        this.fluidInventory.onFluidInventoryChanged();
    }

    public void onPickupFromSlot(FluidStack stack) {
        this.onSlotChanged();
    }

    public boolean isFluidValid(Fluid stack) {
        return fluidInventory.getAllowedFluidsForSlot(slotIndex).contains(stack);
    }

	public boolean isAnyFluidValid(List<Fluid> stack) {
		return true;
	}

	public boolean areAllFluidValid(List<Fluid> stack) {
		return true;
	}

    public FluidStack getFluidStack() {
        return fluidInventory.getFluidInSlot(this.slotIndex);
    }

    public boolean hasStack() {
        return this.getFluidStack() != null;
    }

    public void putStack(FluidStack stack) {
        if(stack == null){
            this.fluidInventory.setFluidInSlot(this.slotIndex,null);
            this.onSlotChanged();
        }
        else if(fluidInventory.getAllowedFluidsForSlot(slotIndex).isEmpty() || fluidInventory.getAllowedFluidsForSlot(slotIndex).contains(stack.fluid)){
            this.fluidInventory.setFluidInSlot(this.slotIndex, stack);
            this.onSlotChanged();
        }
    }

    public int getBackgroundIconIndex() {
        return -1;
    }
}
