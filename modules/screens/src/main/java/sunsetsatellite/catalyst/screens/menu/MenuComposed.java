package sunsetsatellite.catalyst.screens.menu;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class MenuComposed extends MenuAbstract {

	@Override
	public IntList getMoveSlots(@NotNull InventoryAction inventoryAction, @NotNull Slot slot, int i, Player player) {
		return null;
	}

	@Override
	public IntList getTargetSlots(@NotNull InventoryAction inventoryAction, @NotNull Slot slot, int i, Player player) {
		return null;
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return true;
	}
}
