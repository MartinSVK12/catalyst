package sunsetsatellite.catalyst.screens.menu;

import com.mojang.nbt.tags.CompoundTag;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.screens.component.SlotComponent;
import sunsetsatellite.catalyst.screens.component.server.InventoryServerComponent;
import sunsetsatellite.catalyst.screens.component.server.SlotServerComponent;

import java.util.List;

public class MenuComposed extends MenuAbstract {

	public ContainerInventory playerInventory;
	public Container inventory;

	public boolean initialized = false;

	public MenuComposed(ContainerInventory playerInventory, Container inventory) {
		this.playerInventory = playerInventory;
		this.inventory = inventory;
	}

	public void init(CompoundTag tag) {
		initialized = true;

		List<SlotServerComponent> slotComponents = SlotServerComponent.fromNbt(tag);
		List<InventoryServerComponent> invComponents = InventoryServerComponent.fromNbt(tag);

		for (InventoryServerComponent invComponent : invComponents) {
			for (int j = 0; j < 3; j++) {
				for (int i = 0; i < 9; i++) {
					addSlot(new Slot(playerInventory, i + j * 9 + 9, invComponent.x() + 18 * i, invComponent.y() + j * 18));
				}
			}

			for (int k = 0; k < 9; k++) {
				addSlot(new Slot(playerInventory, k, invComponent.x() + 18 * k, invComponent.y() + (3 * 18) + 4));
			}
		}

		for (SlotServerComponent slot : slotComponents) {
			switch (slot.type()) {
				case PLAYER -> {
					addSlot(new Slot(playerInventory, slot.index(), slot.x(), slot.y()));
				}
				case INVENTORY -> {
					addSlot(new Slot(inventory, slot.index(), slot.x(), slot.y()));
				}
			}

		}
	}

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
		if(inventory instanceof TileEntity tile){
			return player.distanceToSqr(tile.tilePos.x + 0.5f, tile.tilePos.y + 0.5f, tile.tilePos.z + 0.5f) <= 64D;
		}
		return true;
	}
}
