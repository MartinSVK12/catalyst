package sunsetsatellite.catalyst.fluids.impl.tiles;


import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.IntTag;
import com.mojang.nbt.ListTag;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class TileEntityFluidItemContainer extends TileEntityFluidContainer
    implements IInventory {

    public ItemStack[] itemContents = new ItemStack[2];

    public HashMap<Direction, Connection> itemConnections = new HashMap<>();
    public HashMap<Direction, Integer> activeItemSlots = new HashMap<>();

    public TileEntityFluidItemContainer(){
        for (Direction dir : Direction.values()) {
            itemConnections.put(dir, Connection.NONE);
            activeItemSlots.put(dir,0);
        }
    }

    public int getSizeInventory() {
        return itemContents.length;
    }

    public ItemStack getStackInSlot(int i1) {
        return this.itemContents[i1];
    }

    public ItemStack decrStackSize(int i1, int i2) {
        if(this.itemContents[i1] != null) {
            ItemStack itemStack3;
            if(this.itemContents[i1].stackSize <= i2) {
                itemStack3 = this.itemContents[i1];
                this.itemContents[i1] = null;
                this.onInventoryChanged();
                return itemStack3;
            } else {
                itemStack3 = this.itemContents[i1].splitStack(i2);
                if(this.itemContents[i1].stackSize == 0) {
                    this.itemContents[i1] = null;
                }

                this.onInventoryChanged();
                return itemStack3;
            }
        } else {
            return null;
        }
    }

    public void setInventorySlotContents(int i1, ItemStack itemStack2) {
        this.itemContents[i1] = itemStack2;
        if(itemStack2 != null && itemStack2.stackSize > this.getInventoryStackLimit()) {
            itemStack2.stackSize = this.getInventoryStackLimit();
        }

        this.onInventoryChanged();
    }

    public String getInvName() {
        return "Generic Fluid & Item Container";
    }

    public void readFromNBT(CompoundTag CompoundTag1) {
        super.readFromNBT(CompoundTag1);
        ListTag nBTTagList2 = CompoundTag1.getList("Items");
        this.itemContents = new ItemStack[this.getSizeInventory()];

        for(int i3 = 0; i3 < nBTTagList2.tagCount(); ++i3) {
            CompoundTag CompoundTag4 = (CompoundTag)nBTTagList2.tagAt(i3);
            int i5 = CompoundTag4.getByte("Slot") & 255;
            if(i5 >= 0 && i5 < this.itemContents.length) {
                this.itemContents[i5] = ItemStack.readItemStackFromNbt(CompoundTag4);
            }
        }

        ListTag nbtTagList = CompoundTag1.getList("Fluids");
        this.fluidContents = new FluidStack[this.getFluidInventorySize()];

        for(int i3 = 0; i3 < nbtTagList.tagCount(); ++i3) {
            CompoundTag CompoundTag4 = (CompoundTag)nbtTagList.tagAt(i3);
            int i5 = CompoundTag4.getByte("Slot") & 255;
            if(i5 >= 0 && i5 < this.fluidContents.length) {
                this.fluidContents[i5] = new FluidStack(CompoundTag4);
            }
        }

        CompoundTag connectionsTag = CompoundTag1.getCompound("itemConnections");
        for (Object con : connectionsTag.getValues()) {
            itemConnections.replace(Direction.values()[Integer.parseInt(((IntTag)con).getTagName())],Connection.values()[((IntTag)con).getValue()]);
        }

        CompoundTag activeItemSlotsTag = CompoundTag1.getCompound("itemActiveSlots");
        for (Object con : activeItemSlotsTag.getValues()) {
            activeItemSlots.replace(Direction.values()[Integer.parseInt(((IntTag)con).getTagName())],((IntTag) con).getValue());
        }

    }

    public void writeToNBT(CompoundTag CompoundTag1) {
        super.writeToNBT(CompoundTag1);
        ListTag nBTTagList2 = new ListTag();
        ListTag nbtTagList = new ListTag();
        CompoundTag connectionsTag = new CompoundTag();
        CompoundTag activeItemSlotsTag = new CompoundTag();

        for(int i3 = 0; i3 < this.itemContents.length; ++i3) {
            if(this.itemContents[i3] != null) {
                CompoundTag CompoundTag4 = new CompoundTag();
                CompoundTag4.putByte("Slot", (byte)i3);
                this.itemContents[i3].writeToNBT(CompoundTag4);
                nbtTagList.addTag(CompoundTag4);
            }
        }

        for(int i3 = 0; i3 < this.fluidContents.length; ++i3) {
            if(this.fluidContents[i3] != null && this.fluidContents[i3].amount != 0 && this.fluidContents[i3].liquid != null) {
                CompoundTag CompoundTag4 = new CompoundTag();
                CompoundTag4.putByte("Slot", (byte)i3);
                this.fluidContents[i3].writeToNBT(CompoundTag4);
                nBTTagList2.addTag(CompoundTag4);
            }
        }
        for (Map.Entry<Direction, Integer> entry : activeItemSlots.entrySet()) {
            Direction dir = entry.getKey();
            activeItemSlotsTag.putInt(String.valueOf(dir.ordinal()),entry.getValue());
        }
        for (Map.Entry<Direction, Connection> entry : itemConnections.entrySet()) {
            Direction dir = entry.getKey();
            Connection con = entry.getValue();
            connectionsTag.putInt(String.valueOf(dir.ordinal()),con.ordinal());
        }
        CompoundTag1.putCompound("itemConnections",connectionsTag);
        CompoundTag1.putCompound("itemActiveSlots",activeItemSlotsTag);
        CompoundTag1.put("Items", nbtTagList);
        CompoundTag1.put("Fluids", nBTTagList2);
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean canInteractWith(EntityPlayer entityPlayer1) {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && entityPlayer1.distanceToSqr((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
    }
}
