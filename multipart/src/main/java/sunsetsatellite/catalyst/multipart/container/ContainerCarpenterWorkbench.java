package sunsetsatellite.catalyst.multipart.container;

import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.inventory.Container;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.slot.Slot;
import sunsetsatellite.catalyst.multipart.block.entity.TileEntityCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.util.SlotPartPicker;

import java.util.ArrayList;
import java.util.List;

public class ContainerCarpenterWorkbench extends Container {

	public final TileEntityCarpenterWorkbench tile;

	public ContainerCarpenterWorkbench(IInventory inventory, TileEntityCarpenterWorkbench tile) {
		this.tile = tile;

		this.addSlot(new Slot(tile, 0, 34, 35));
		this.addSlot(new Slot(tile, 1, 65, 53));

		int j1;
		int l1;
		for(j1 = 0; j1 < 3; ++j1) {
			for(l1 = 0; l1 < 3; ++l1) {
				this.addSlot(new SlotPartPicker(tile, l1 + j1 * 3, 92 + l1 * 18, 17 + j1 * 18));
			}
		}

		for(j1 = 0; j1 < 3; ++j1) {
			for(l1 = 0; l1 < 9; ++l1) {
				this.addSlot(new Slot(inventory, l1 + j1 * 9 + 9, 8 + l1 * 18, 84 + j1 * 18));
			}
		}

		for(j1 = 0; j1 < 9; ++j1) {
			this.addSlot(new Slot(inventory, j1, 8 + j1 * 18, 142));
		}

	}

	@Override
	public List<Integer> getMoveSlots(InventoryAction inventoryAction, Slot slot, int i, EntityPlayer entityPlayer) {
		return new ArrayList<>();
	}

	@Override
	public List<Integer> getTargetSlots(InventoryAction inventoryAction, Slot slot, int i, EntityPlayer entityPlayer) {
		return new ArrayList<>();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		return tile.canInteractWith(entityPlayer);
	}
}
