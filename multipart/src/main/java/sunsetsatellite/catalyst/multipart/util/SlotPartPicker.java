package sunsetsatellite.catalyst.multipart.util;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemToolAxe;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.slot.Slot;
import sunsetsatellite.catalyst.multipart.block.entity.TileEntityCarpenterWorkbench;

public class SlotPartPicker extends Slot {

	public int variableIndex = 0;
	public TileEntityCarpenterWorkbench tile;

	public SlotPartPicker(TileEntityCarpenterWorkbench inventory, int id, int x, int y) {
		super(inventory, id, x, y);
		this.variableIndex = id;
		this.tile = inventory;
	}

	@Override
	public boolean canPutStackInSlot(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean hasStack() {
		return getStack() != null;
	}

	@Override
	public void onPickupFromSlot(ItemStack itemstack) {
		super.onPickupFromSlot(itemstack);
		if(tile.contents[0] != null && tile.contents[1] != null && tile.contents[1].getItem() instanceof ItemToolAxe) {
			tile.decrStackSize(0,1);
			tile.contents[1].damageItem(1,null);
		}
	}

	@Override
	public boolean isHere(final IInventory iinventory, final int i) {
		return iinventory == tile && i == variableIndex;
	}

	@Override
	public boolean getIsDiscovered(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack getStack() {
		if(variableIndex < tile.parts.size()){
			return tile.parts.get(variableIndex);
		}
		return null;
	}

	@Override
	public void putStack(ItemStack itemstack) {

	}

	@Override
	public ItemStack decrStackSize(int i) {
		if(variableIndex < tile.parts.size()){
			return tile.parts.get(variableIndex);
		}
		return null;
	}

	public int getSlotIndex() {
		return slotIndex;
	}

}
