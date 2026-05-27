package sunsetsatellite.catalyst.fluids.impl.tile;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.IntTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.io.IItemIO;
import sunsetsatellite.catalyst.fluids.util.FluidItemContainer;

import java.util.HashMap;
import java.util.Map;

public abstract class TileEntityFluidItemContainer extends TileEntityFluidContainer
	implements FluidItemContainer, IItemIO {

	public ItemStack[] itemContents = new ItemStack[1];

	public HashMap<Direction, Connection> itemConnections = new HashMap<>();
	public HashMap<Direction, Integer> activeItemSlots = new HashMap<>();

	public TileEntityFluidItemContainer() {
		for (Direction dir : Direction.values()) {
			itemConnections.put(dir, Connection.NONE);
			activeItemSlots.put(dir, 0);
		}
	}

	@Override
	public void readAdditionalData(@NotNull CompoundTag tag) {
		super.readAdditionalData(tag);
		ListTag nbttaglist = tag.getList("Items");
		itemContents = new ItemStack[getContainerSize()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			CompoundTag nbttagcompound1 = (CompoundTag) nbttaglist.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 0xff;
			if (j < itemContents.length) {
				itemContents[j] = ItemStack.readItemStackFromNbt(nbttagcompound1);
			}
		}

		CompoundTag connectionsTag = tag.getCompound("itemConnections");
		for (Object con : connectionsTag.getValues()) {
			itemConnections.replace(Direction.values()[Integer.parseInt(((IntTag) con).getTagName())], Connection.values()[((IntTag) con).getValue()]);
		}

		CompoundTag activeItemSlotsTag = tag.getCompound("itemActiveSlots");
		for (Object con : activeItemSlotsTag.getValues()) {
			activeItemSlots.replace(Direction.values()[Integer.parseInt(((IntTag) con).getTagName())], ((IntTag) con).getValue());
		}
	}

	@Override
	public void writeAdditionalData(@NotNull CompoundTag tag) {
		super.writeAdditionalData(tag);
		ListTag nbttaglist = new ListTag();
		CompoundTag itemConnectionsTag = new CompoundTag();
		CompoundTag activeItemSlotsTag = new CompoundTag();
		for (int i = 0; i < itemContents.length; i++) {
			if (itemContents[i] != null) {
				CompoundTag nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte) i);
				itemContents[i].writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			}
		}

		for (Map.Entry<Direction, Integer> entry : activeItemSlots.entrySet()) {
			Direction dir = entry.getKey();
			activeItemSlotsTag.putInt(String.valueOf(dir.ordinal()), entry.getValue());
		}
		for (Map.Entry<Direction, Connection> entry : itemConnections.entrySet()) {
			Direction dir = entry.getKey();
			Connection con = entry.getValue();
			itemConnectionsTag.putInt(String.valueOf(dir.ordinal()), con.ordinal());
		}
		tag.putCompound("itemConnections", itemConnectionsTag);
		tag.putCompound("itemActiveSlots", activeItemSlotsTag);

		tag.put("Items", nbttaglist);
	}

	@Override
	public int getContainerSize() {
		return itemContents.length;
	}

	@Override
	public @Nullable ItemStack getItem(int index) {
		return itemContents[index];
	}

	@Override
	public @Nullable ItemStack removeItem(int index, int takeAmount) {
		if (itemContents[index] != null) {
			if (itemContents[index].stackSize <= takeAmount) {
				ItemStack itemstack = itemContents[index];
				itemContents[index] = null;
				setChanged();
				return itemstack;
			}
			ItemStack itemstack1 = itemContents[index].splitStack(takeAmount);
			if (itemContents[index].stackSize <= 0) {
				itemContents[index] = null;
			}
			setChanged();
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public void setItem(int index, @Nullable ItemStack itemstack) {
		itemContents[index] = itemstack;
		if (itemstack != null && itemstack.stackSize > getMaxStackSize()) {
			itemstack.stackSize = getMaxStackSize();
		}
		setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public boolean stillValid(@NonNull Player entityplayer) {
		if (worldObj == null || worldObj.getTileEntity(tilePos) != this) {
			return false;
		}
		return entityplayer.distanceToSqr((double) tilePos.x + 0.5D, (double) tilePos.y + 0.5D, (double) tilePos.z + 0.5D) <= 64D;
	}

	@Override
	public void dropContents(World world, int x, int y, int z) {
		super.dropContents(world, x, y, z);
		for (int i = 0; i < this.getContainerSize(); i++) {
			ItemStack itemStack = this.getItem(i);
			if (itemStack == null) continue;
			world.dropItem(x, y, z, itemStack);
		}
	}

	@Override
	public int getActiveItemSlotForSide(Direction dir) {
		if (activeItemSlots.get(dir) == -1) {
			if (itemConnections.get(dir) == Connection.INPUT) {
				for (int i = 0; i < itemContents.length; i++) {
					ItemStack content = itemContents[i];
					if (content == null) {
						return i;
					}
				}
			} else if (itemConnections.get(dir) == Connection.OUTPUT) {
				for (int i = 0; i < itemContents.length; i++) {
					ItemStack content = itemContents[i];
					if (content != null) {
						return i;
					}
				}
			}
			return 0;
		} else {
			return activeItemSlots.get(dir);
		}
	}

	@Override
	public int getActiveItemSlotForSide(Direction dir, ItemStack stack) {
		if (activeItemSlots.get(dir) == -1) {
			if (itemConnections.get(dir) == Connection.INPUT) {
				for (int i = 0; i < itemContents.length; i++) {
					ItemStack content = itemContents[i];
					if (content == null || (content.isItemEqual(stack) && content.stackSize + stack.stackSize <= content.getMaxStackSize())) {
						return i;
					}
				}
			} else if (itemConnections.get(dir) == Connection.OUTPUT) {
				for (int i = 0; i < itemContents.length; i++) {
					ItemStack content = itemContents[i];
					if (content != null) {
						return i;
					}
				}
			}
			return 0;
		} else {
			return activeItemSlots.get(dir);
		}
	}

	@Override
	public void setActiveItemSlotForSide(Direction dir, int slot) {
		activeItemSlots.replace(dir, slot);
	}

	@Override
	public Connection getItemIOForSide(Direction dir) {
		return itemConnections.get(dir);
	}

	@Override
	public void setItemIOForSide(Direction dir, Connection con) {
		itemConnections.put(dir, con);
	}

	@Override
	public void cycleItemIOForSide(Direction dir) {
		switch (itemConnections.get(dir)) {
			case NONE:
				itemConnections.replace(dir, Connection.INPUT);
				break;
			case INPUT:
				itemConnections.replace(dir, Connection.OUTPUT);
				break;
			case OUTPUT:
				itemConnections.replace(dir, Connection.BOTH);
				break;
			case BOTH:
				itemConnections.replace(dir, Connection.NONE);
				break;
		}
	}

	@Override
	public void cycleActiveItemSlotForSide(Direction dir, boolean backwards) {
		int i = activeItemSlots.get(dir);
		if (!backwards) {
			if (i < getContainerSize() - 1) {
				activeItemSlots.replace(dir, i + 1);
			} else {
				activeItemSlots.replace(dir, 0);
			}
		} else {
			if (i > -1) {
				activeItemSlots.replace(dir, i - 1);
			} else {
				activeItemSlots.replace(dir, getContainerSize() - 1);
			}
		}
	}

}
