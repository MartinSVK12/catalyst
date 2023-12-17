package sunsetsatellite.catalyst.energy.impl;


import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.crafting.ICrafting;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.Container;
import net.minecraft.core.player.inventory.slot.Slot;

import java.util.List;

public class ContainerEnergy extends Container {

    public int energy;
    public int capacity;
    public int maxProvide;
    public int maxReceive;

    public TileEntity tile;
    public void updateClientEnergy(int energy, int capacity, int maxReceive, int maxProvide){
        ((TileEntityEnergyConductor)tile).energy = energy;
        ((TileEntityEnergyConductor)tile).capacity = capacity;
        ((TileEntityEnergyConductor)tile).maxProvide = maxProvide;
        ((TileEntityEnergyConductor)tile).maxReceive = maxReceive;
    }

    @Override
    public void updateInventory() {
        super.updateInventory();
        for(int i = 0; i < this.inventorySlots.size(); ++i) {
            ItemStack itemstack = this.inventorySlots.get(i).getStack();
            ItemStack itemstack1 = this.inventoryItemStacks.get(i);
            this.inventoryItemStacks.set(i, itemstack1);
            for (ICrafting crafter : this.crafters) {
                crafter.updateInventorySlot(this, i, itemstack);
            }
        }

        /*for (ICrafting crafter : this.crafters) {
            ((IEntityPlayer) crafter).updateEnergy(this, energy, capacity, maxReceive, maxProvide);
        }*/

        this.energy = ((TileEntityEnergyConductor)tile).energy;
        this.capacity = ((TileEntityEnergyConductor)tile).capacity;
        this.maxProvide = ((TileEntityEnergyConductor)tile).maxProvide;
        this.maxReceive = ((TileEntityEnergyConductor)tile).maxReceive;
    }

    @Override
    public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, EntityPlayer player) {
        return null;
    }

    @Override
    public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, EntityPlayer player) {
        return null;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
        return true;
    }
}
