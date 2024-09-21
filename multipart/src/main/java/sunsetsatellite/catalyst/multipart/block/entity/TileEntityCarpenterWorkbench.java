package sunsetsatellite.catalyst.multipart.block.entity;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemToolAxe;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.util.helper.Side;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.api.MultipartType;

import java.util.ArrayList;
import java.util.List;

public class TileEntityCarpenterWorkbench extends TileEntity implements IInventory {

	public ItemStack[] contents = new ItemStack[2];
	public List<ItemStack> parts = new ArrayList<>();
	public int page = 1;
	public int maxPages = 1;
	public Side selectedSide = Side.NONE;

	public TileEntityCarpenterWorkbench() {
	}

	@Override
	public void tick() {
		super.tick();
		parts.clear();
		if(contents[1] != null && contents[1].getItem() instanceof ItemToolAxe && contents[1].stackSize <= 0){
			contents[1] = null;
		}
		if(contents[0] != null && contents[0].itemID < 16384 && Block.getBlock(contents[0].itemID).hasTag(CatalystMultipart.CAN_BE_MULTIPART)){
			if(contents[1] != null && contents[1].getItem() instanceof ItemToolAxe){
				MultipartType.types.forEach((K, V)->{
					if (!Block.getBlock(contents[0].itemID).hasTag(CatalystMultipart.TYPE_TAGS.get(K))) return;
					ItemStack stack = new ItemStack(CatalystMultipart.multipartItem,16 / V.thickness, 0);
					CompoundTag tag = new CompoundTag();
					CompoundTag multipartTag = new CompoundTag();
					multipartTag.putString("Type",K);
					multipartTag.putInt("Block", contents[0].itemID);
					multipartTag.putInt("Meta", contents[0].getMetadata());
					if(selectedSide != Side.NONE){
						multipartTag.putInt("Side", selectedSide.getId());
					}
					tag.putCompound("Multipart",multipartTag);
					stack.setData(tag);
					parts.add(stack);
				});
				maxPages = (int) Math.ceil(parts.size() / 9f);
			} else {
				page = 1;
				maxPages = 1;
			}
		} else {
			page = 1;
			maxPages = 1;
		}
	}

	public int getSizeInventory() {
		return 2;
	}

	public ItemStack getStackInSlot(int i) {
		return this.contents[i];
	}

	public ItemStack decrStackSize(int i, int j) {
		if (this.contents[i] != null) {
			ItemStack itemstack1;
			if (this.contents[i].stackSize <= j) {
				itemstack1 = this.contents[i];
				this.contents[i] = null;
				this.onInventoryChanged();
				return itemstack1;
			} else {
				itemstack1 = this.contents[i].splitStack(j);
				if (this.contents[i].stackSize <= 0) {
					this.contents[i] = null;
				}

				this.onInventoryChanged();
				return itemstack1;
			}
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.contents[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}

		this.onInventoryChanged();
	}

	public String getInvName() {
		return "Carpenter Workbench";
	}

	public void readFromNBT(CompoundTag nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		ListTag nbttaglist = nbttagcompound.getList("Items");
		this.contents = new ItemStack[this.getSizeInventory()];

		for(int i = 0; i < nbttaglist.tagCount(); ++i) {
			CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;
			if (j >= 0 && j < this.contents.length) {
				this.contents[j] = ItemStack.readItemStackFromNbt(nbttagcompound1);
			}
		}

	}

	public void writeToNBT(CompoundTag nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		ListTag nbttaglist = new ListTag();

		for(int i = 0; i < this.contents.length; ++i) {
			if (this.contents[i] != null) {
				CompoundTag nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte)i);
				this.contents[i].writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			}
		}

		nbttagcompound.put("Items", nbttaglist);
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		if (this.worldObj.getBlockTileEntity(this.x, this.y, this.z) != this) {
			return false;
		} else {
			return entityplayer.distanceToSqr((double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5) <= 64.0;
		}
	}

	@Override
	public void sortInventory() {

	}
}
