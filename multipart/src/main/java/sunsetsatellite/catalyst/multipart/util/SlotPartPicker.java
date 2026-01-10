package sunsetsatellite.catalyst.multipart.util;

import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemToolAxe;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.Nullable;
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
	public void onTake(ItemStack itemstack) {
		super.onTake(itemstack);
		if (tile.contents[0] != null && tile.contents[1] != null && tile.contents[1].getItem() instanceof ItemToolAxe) {
			tile.removeItem(0, 1);
			tile.contents[1].damageItem(1, null);
		}
	}

	@Override
	public boolean isAt(Container container, int i) {
		return container == tile && i == variableIndex;
	}

	@Override
	public @Nullable ItemStack getItemStack() {
		if (variableIndex < tile.parts.size()) {
			return tile.parts.get(variableIndex);
		}
		return null;
	}

	@Override
	public boolean mayPlace(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean hasItem() {
		return getItemStack() != null;
	}

	@Override
	public @Nullable ItemStack remove(int i) {
		if (variableIndex < tile.parts.size()) {
			return tile.parts.get(variableIndex);
		}
		return null;
	}

	@Override
	public void set(@Nullable ItemStack itemstack) {

	}

	public int getSlotIndex() {
		return index;
	}

}
