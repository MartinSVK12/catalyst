package sunsetsatellite.catalyst.multipart.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemToolAxe;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.core.util.IScreenActionListener;
import sunsetsatellite.catalyst.multipart.api.MultipartType;
import sunsetsatellite.catalyst.multipart.util.SlotPartPicker;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TileEntityCarpenterWorkbench extends TileEntity implements Container, IScreenActionListener {

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
		if(contents[0] != null && contents[0].itemID < 16384 && Blocks.getBlock(contents[0].itemID).hasTag(CatalystMultipart.CAN_BE_MULTIPART)){
			if(contents[1] != null && contents[1].getItem() instanceof ItemToolAxe){
				MultipartType.types.forEach((K, V)->{
					if (!Blocks.getBlock(contents[0].itemID).hasTag(CatalystMultipart.TYPE_TAGS.get(K))) return;
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

	@Override
	public int getContainerSize() {
		return contents.length;
	}

	public ItemStack getItem(int i) {
		return this.contents[i];
	}

	public ItemStack removeItem(int i, int j) {
		if (this.contents[i] != null) {
			ItemStack itemstack1;
			if (this.contents[i].stackSize <= j) {
				itemstack1 = this.contents[i];
				this.contents[i] = null;
				this.setChanged();
				return itemstack1;
			} else {
				itemstack1 = this.contents[i].splitStack(j);
				if (this.contents[i].stackSize <= 0) {
					this.contents[i] = null;
				}

				this.setChanged();
				return itemstack1;
			}
		} else {
			return null;
		}
	}

	public void setItem(int i, ItemStack itemstack) {
		this.contents[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getMaxStackSize()) {
			itemstack.stackSize = this.getMaxStackSize();
		}

		this.setChanged();
	}

	@Override
	public String getNameTranslationKey() {
		return "container.catalyst-multipart.carpenterWorkbench";
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void readFromNBT(CompoundTag nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		ListTag nbttaglist = nbttagcompound.getList("Items");
		ListTag partsTag = nbttagcompound.getList("Parts");
		this.contents = new ItemStack[this.getContainerSize()];

		for(int i = 0; i < nbttaglist.tagCount(); ++i) {
			CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;
			if (j >= 0 && j < this.contents.length) {
				this.contents[j] = ItemStack.readItemStackFromNbt(nbttagcompound1);
			}
		}

	}

	@Override
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

	public boolean stillValid(Player entityplayer) {
		if (this.worldObj.getTileEntity(this.x, this.y, this.z) != this) {
			return false;
		} else {
			return entityplayer.distanceToSqr((double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5) <= 64.0;
		}
	}

	@Override
	public void sortContainer() {

	}

	@Override
	public void buttonClicked(int id, int button, int channel) {
		switch (id) {
			case 0:
				if (page < maxPages) {
					page++;
				}
				break;
			case 1:
				if (page > 1) {
					page--;
				}
				break;
			case 2:
				int i = selectedSide.ordinal();
				i++;
				if(i >= Side.values().length) {
					i = 0;
				}
				selectedSide = Side.values()[i];
				if(selectedSide == Side.NONE) {
					break;
				}
				break;
		}
	}

	@Override
	public void dropContents(World world, int x, int y, int z) {
		super.dropContents(world, x, y, z);
		for(int i = 0; i < this.getContainerSize(); i++) {
			ItemStack itemStack = this.getItem(i);
			if(itemStack == null) continue;
			world.dropItem(x, y, z, itemStack);
		}
	}
}
