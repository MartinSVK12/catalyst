
package sunsetsatellite.catalyst.fluids.impl.tile;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.IntTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicChest;
import net.minecraft.core.block.motion.CarriedBlock;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.InventorySorter;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.util.HashMap;
import java.util.Map;

public abstract class TileEntityFluidItemContainer extends TileEntityFluidContainer
    implements Container {

    public ItemStack[] itemContents = new ItemStack[1];

    public HashMap<Direction, Connection> itemConnections = new HashMap<>();
    public HashMap<Direction, Integer> activeItemSlots = new HashMap<>();

    public TileEntityFluidItemContainer(){
        for (Direction dir : Direction.values()) {
            itemConnections.put(dir, Connection.NONE);
            activeItemSlots.put(dir,0);
        }
    }

	@Override
	public void readFromNBT(CompoundTag nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
		ListTag nbttaglist = nbttagcompound.getList("Items");
		itemContents = new ItemStack[getContainerSize()];
		for(int i = 0; i < nbttaglist.tagCount(); i++)
		{
			CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 0xff;
			if(j < itemContents.length)
			{
				itemContents[j] = ItemStack.readItemStackFromNbt(nbttagcompound1);
			}
		}
	}

	@Override
	public void writeToNBT(CompoundTag nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		ListTag nbttaglist = new ListTag();
		CompoundTag itemConnectionsTag = new CompoundTag();
		CompoundTag activeItemSlotsTag = new CompoundTag();
		for(int i = 0; i < itemContents.length; i++)
		{
			if(itemContents[i] != null)
			{
				CompoundTag nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte)i);
				itemContents[i].writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			}
		}

		for (Map.Entry<Direction, Integer> entry : activeFluidSlots.entrySet()) {
			Direction dir = entry.getKey();
			activeItemSlotsTag.putInt(String.valueOf(dir.ordinal()),entry.getValue());
		}
		for (Map.Entry<Direction, Connection> entry : fluidConnections.entrySet()) {
			Direction dir = entry.getKey();
			Connection con = entry.getValue();
			itemConnectionsTag.putInt(String.valueOf(dir.ordinal()),con.ordinal());
		}
		nbttagcompound.putCompound("itemConnections",itemConnectionsTag);
		nbttagcompound.putCompound("itemActiveSlots",activeItemSlotsTag);

		nbttagcompound.put("Items", nbttaglist);
	}

	@Override
	public int getContainerSize()
	{
		return itemContents.length;
	}

	@Override
	public @Nullable ItemStack getItem(int index)
	{
		return itemContents[index];
	}

	@Override
	public @Nullable ItemStack removeItem(int index, int takeAmount)
	{
		if(itemContents[index] != null)
		{
			if(itemContents[index].stackSize <= takeAmount)
			{
				ItemStack itemstack = itemContents[index];
				itemContents[index] = null;
				setChanged();
				return itemstack;
			}
			ItemStack itemstack1 = itemContents[index].splitStack(takeAmount);
			if(itemContents[index].stackSize <= 0)
			{
				itemContents[index] = null;
			}
			setChanged();
			return itemstack1;
		} else
		{
			return null;
		}
	}

	@Override
	public void setItem(int index, @Nullable ItemStack itemstack)
	{
		itemContents[index] = itemstack;
		if(itemstack != null && itemstack.stackSize > getMaxStackSize())
		{
			itemstack.stackSize = getMaxStackSize();
		}
		setChanged();
	}

	@Override
	public int getMaxStackSize()
	{
		return 64;
	}

	@Override
	public boolean stillValid(Player entityplayer)
	{
		if(worldObj == null || worldObj.getTileEntity(x, y, z) != this)
		{
			return false;
		}
		return entityplayer.distanceToSqr((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D) <= 64D;
	}

	@Override
	public void sortContainer() {

	}

	@Override
	public void dropContents(World world, int x, int y, int z) {
		super.dropContents(world, x, y, z);
		for(int i = 0; i < this.getContainerSize(); i++) {
			ItemStack itemStack = this.getItem(i);
			if(itemStack == null) continue;
			EntityItem item = world.dropItem(x, y, z, itemStack);
			item.xd *= 0.5;
			item.yd *= 0.5;
			item.zd *= 0.5;
			item.pickupDelay = 0;
		}
	}

}
