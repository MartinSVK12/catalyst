package sunsetsatellite.catalyst.fluids.util;


import com.mojang.nbt.CompoundTag;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import sunsetsatellite.catalyst.fluids.api.IFluidInventory;

public class NBTHelper {

	private static boolean preventSaving = false;

    public static void saveInvToNBT(ItemStack source_item, IInventory inv){
		if(preventSaving) return;
        CompoundTag itemData = source_item.getData().getCompound("inventory");
        for(int i = 0; i < inv.getSizeInventory();i++){
            ItemStack item = inv.getStackInSlot(i);
            CompoundTag itemNBT = new CompoundTag();
            if(item != null){
                item.writeToNBT(itemNBT);
                itemData.putCompound(String.valueOf(i),itemNBT);
            } else {
                itemData.getValue().remove(String.valueOf(i));
            }
        }
        source_item.getData().putCompound("inventory",itemData);
        if(inv instanceof IFluidInventory){
            IFluidInventory fluidInv = (IFluidInventory) inv;
            CompoundTag fluidData = source_item.getData().getCompound("fluidInventory");
            for(int i = 0; i < fluidInv.getFluidInventorySize();i++){
                FluidStack fluid = ((IFluidInventory) inv).getFluidInSlot(i);
                CompoundTag fluidNBT = new CompoundTag();
                if(fluid != null){
                    fluid.writeToNBT(fluidNBT);
                    fluidData.putCompound(String.valueOf(i),fluidNBT);
                } else {
                    fluidData.getValue().remove(String.valueOf(i));
                }
            }
            source_item.getData().putCompound("fluidInventory",fluidData);
        }
    }

    public static void loadInvFromNBT(ItemStack source_item, IInventory inv, int amount, int fluidAmount){
		preventSaving = true;
        CompoundTag itemNBT = source_item.getData().getCompound("inventory");
        CompoundTag fluidNBT = source_item.getData().getCompound("fluidInventory");
        for(int i = 0; i < amount;i++){
            if(itemNBT.containsKey(String.valueOf(i))){
                ItemStack item = ItemStack.readItemStackFromNbt(itemNBT.getCompound(String.valueOf(i)));
                inv.setInventorySlotContents(i,item);
            }
        }
        for (int i = 0; i < fluidAmount; i++) {
            if(inv instanceof IFluidInventory){
                IFluidInventory fluidInv = (IFluidInventory) inv;
                FluidStack fluid = new FluidStack(fluidNBT.getCompound(String.valueOf(i)));
                fluidInv.setFluidInSlot(i,fluid);
            }
        }
		preventSaving = false;
    }
}
