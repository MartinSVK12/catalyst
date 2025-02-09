package sunsetsatellite.catalyst.fluids.impl;

import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import sunsetsatellite.catalyst.fluids.api.IFluidInventory;
import sunsetsatellite.catalyst.fluids.api.IItemFluidContainer;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import sunsetsatellite.catalyst.fluids.util.SlotFluid;

import java.util.ArrayList;
import java.util.List;

public class MenuFluid extends MenuAbstract {

	public ArrayList<SlotFluid> fluidSlots = new ArrayList<>();
	public List<FluidStack> fluidItemStacks = new ArrayList<>();
	public IFluidInventory fluidInventory;

	public MenuFluid(IFluidInventory fluidInventory){
		this.fluidInventory = fluidInventory;
	}

	protected void addFluidSlot(SlotFluid slot){
		slot.slotNumber = this.fluidSlots.size();
		this.fluidSlots.add(slot);
		this.fluidItemStacks.add(null);
	}

	public SlotFluid getFluidSlot(int idx) { return this.fluidSlots.get(idx); }
	public void putFluidInSlot(int idx, FluidStack fluid) { this.getFluidSlot(idx).putStack(fluid);}

	public FluidStack clickFluidSlot(int slotID, int button, boolean shift, boolean control, Player player) {
		if(slotID == -999){
			return null;
		}
		SlotFluid slot = fluidSlots.get(slotID);
		ContainerInventory inventory = player.inventory;

		if(slot != null){
			ItemStack stack = inventory.getHeldItemStack();
			if(stack != null && stack.getItem() instanceof IItemFluidContainer) {
				IItemFluidContainer item = (IItemFluidContainer) stack.getItem();
				FluidStack currentFluid = item.getCurrentFluid(stack);
				if(currentFluid != null){
					if (slot.isFluidValid(currentFluid.fluid)) {
						if(item.canDrain(inventory.getHeldItemStack())){
							if (fluidInventory.getFluidInSlot(slot.slotIndex) == null){
								item.drain(inventory.getHeldItemStack(), slot.slotIndex,fluidInventory);
								slot.onSlotChanged();
							}
							else if (fluidInventory.getFluidInSlot(slot.slotIndex).amount < fluidInventory.getFluidCapacityForSlot(slot.slotIndex)) {
								item.drain(inventory.getHeldItemStack(), slot.slotIndex,fluidInventory);
								slot.onSlotChanged();
							}
							else if(fluidInventory.getFluidInSlot(slot.slotIndex).amount >= fluidInventory.getFluidCapacityForSlot(slot.slotIndex)){
								if(item.canFill(inventory.getHeldItemStack())){
									ItemStack filledStack = item.fill(slot.getFluidStack(),inventory.getHeldItemStack(),fluidInventory);
									if(filledStack != null){
										inventory.setHeldItemStack(filledStack);
										inventory.setChanged();
									}
									slot.onSlotChanged();
								}
							}
						} else if(item.canFill(inventory.getHeldItemStack())){ //fill
							ItemStack filledStack = item.fill(slot.getFluidStack(),inventory.getHeldItemStack(),fluidInventory);
							if(filledStack != null){
								inventory.setHeldItemStack(filledStack);
							}
							slot.onSlotChanged();
						}
					}
				}

			}

			slot.onSlotChanged();
			return fluidSlots.get(slotID).getFluidStack();
		}

		return null;
	}

	@Override
	public List<Integer> getMoveSlots(InventoryAction inventoryAction, Slot slot, int i, Player player) {
		return null;
	}

	@Override
	public List<Integer> getTargetSlots(InventoryAction inventoryAction, Slot slot, int i, Player player) {
		return null;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
