package sunsetsatellite.catalyst.screens.menu;

import com.mojang.nbt.tags.CompoundTag;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.fluids.impl.MenuFluid;
import sunsetsatellite.catalyst.fluids.util.FluidItemContainer;
import sunsetsatellite.catalyst.fluids.util.SlotFluid;
import sunsetsatellite.catalyst.screens.component.server.InventoryServerComponent;
import sunsetsatellite.catalyst.screens.component.server.SlotGridServerComponent;
import sunsetsatellite.catalyst.screens.component.server.SlotServerComponent;
import sunsetsatellite.catalyst.screens.packet.NetworkMessageSendScreenDataServer;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;

import java.util.Comparator;
import java.util.List;

public class MenuComposed extends MenuFluid {

	public ContainerInventory playerInventory;
	public Container inventory;

	public boolean initialized = false;

	public MenuComposed(ContainerInventory playerInventory, FluidItemContainer inventory) {
		super(inventory);
		this.playerInventory = playerInventory;
		this.inventory = inventory;
	}

	public void init(CompoundTag tag) {
		if(initialized) return;
		initialized = true;

		List<SlotServerComponent> slotComponents = SlotServerComponent.fromNbt(tag);
		slotComponents.sort(Comparator.comparingInt(SlotServerComponent::index));
		List<InventoryServerComponent> invComponents = InventoryServerComponent.fromNbt(tag);
		List<SlotGridServerComponent> gridComponents = SlotGridServerComponent.fromNbt(tag);

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

		for (SlotGridServerComponent gridComponent : gridComponents) {
			switch (gridComponent.type()){
				case PLAYER -> {
					for (int j = 0; j < gridComponent.rows(); j++) {
						for (int i = 0; i < gridComponent.columns(); i++) {
							addSlot(new Slot(playerInventory, i + j * gridComponent.columns(), gridComponent.x() + 18 * i, gridComponent.y() + j * 18));
						}
					}
				}
				case INVENTORY -> {
					for (int j = 0; j < gridComponent.rows(); j++) {
						for (int i = 0; i < gridComponent.columns(); i++) {
							addSlot(new Slot(inventory, i + j * gridComponent.columns(), gridComponent.x() + 18 * i, gridComponent.y() + j * 18));
						}
					}
				}
				case FLUID_INVENTORY -> {
					for (int j = 0; j < gridComponent.rows(); j++) {
						for (int i = 0; i < gridComponent.columns(); i++) {
							addFluidSlot(new SlotFluid(fluidInventory, i + j * gridComponent.columns(), gridComponent.x() + 18 * i, gridComponent.y() + j * 18));
						}
					}
				}
			}
		}

		final int size = 18;
		for (SlotServerComponent slot : slotComponents) {
			switch (slot.type()) {
				case PLAYER -> {
					addSlot(new Slot(playerInventory, slot.index(), slot.x() + ((slot.xSize()-size)/2), slot.y() + ((slot.ySize()-size)/2)));
				}
				case INVENTORY -> {
					addSlot(new Slot(inventory, slot.index(), slot.x() + ((slot.xSize()-size)/2), slot.y() + ((slot.ySize()-size)/2)));
				}
				case FLUID_INVENTORY -> {
					addFluidSlot(new SlotFluid(fluidInventory, slot.index(), slot.x() + ((slot.xSize()-size)/2), slot.y() + ((slot.ySize()-size)/2)));
				}
			}
		}

		if(EnvironmentHelper.isMultiplayerClient()){
			NetworkHandler.sendToServer(new NetworkMessageSendScreenDataServer(tag));
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
