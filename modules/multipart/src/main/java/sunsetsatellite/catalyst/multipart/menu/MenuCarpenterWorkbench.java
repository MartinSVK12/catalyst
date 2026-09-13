package sunsetsatellite.catalyst.multipart.menu;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jspecify.annotations.NonNull;
import sunsetsatellite.catalyst.multipart.tile.TileEntityCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.util.SlotPartPicker;

public class MenuCarpenterWorkbench extends MenuAbstract {

	public final TileEntityCarpenterWorkbench tile;

	public MenuCarpenterWorkbench(Container inventory, TileEntityCarpenterWorkbench tile) {
		this.tile = tile;

		this.addSlot(new Slot(tile, 0, 34, 35));
		this.addSlot(new Slot(tile, 1, 65, 53));

		int j1;
		int l1;
		for (j1 = 0; j1 < 3; ++j1) {
			for (l1 = 0; l1 < 3; ++l1) {
				this.addSlot(new SlotPartPicker(tile, l1 + j1 * 3, 92 + l1 * 18, 17 + j1 * 18));
			}
		}

		for (j1 = 0; j1 < 3; ++j1) {
			for (l1 = 0; l1 < 9; ++l1) {
				this.addSlot(new Slot(inventory, l1 + j1 * 9 + 9, 8 + l1 * 18, 84 + j1 * 18));
			}
		}

		for (j1 = 0; j1 < 9; ++j1) {
			this.addSlot(new Slot(inventory, j1, 8 + j1 * 18, 142));
		}

	}

	@Override
	public IntList getMoveSlots(@NonNull InventoryAction inventoryAction, @NonNull Slot slot, int i, Player entityPlayer) {
		return new IntArrayList();
	}

	@Override
	public IntList getTargetSlots(@NonNull InventoryAction inventoryAction, @NonNull Slot slot, int i, Player entityPlayer) {
		return new IntArrayList();
	}

	@Override
	public boolean stillValid(@NonNull Player entityPlayer) {
		return tile.stillValid(entityPlayer);
	}
}
