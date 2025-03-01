package sunsetsatellite.catalyst.fluids.util;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;

public class FluidStack {
    public int amount;
    public Fluid fluid;

    public FluidStack(Fluid fluid, int size){
		if(fluid == null){
			throw new NullPointerException("Cannot create a fluid stack if no fluid is provided!");
		}
        this.amount = size;
		this.fluid = fluid;
    }

    public FluidStack(CompoundTag nbt){
        readFromNBT(nbt);
    }

	public FluidStack(Fluid fluid) {
		this(fluid, 1000);
	}

	public CompoundTag writeToNBT(CompoundTag nbt) {
		if(fluid != null){
			nbt.putString("fluid", fluid.id.toString());
			nbt.putInt("amount",amount);
		}
		return nbt;
	}

	public void readFromNBT(CompoundTag nbt){
		if(nbt.containsKey("fluid")){
			try {
				this.fluid = Fluid.fluidMap.get(NamespaceID.getTemp(nbt.getString("fluid")));
			} catch (HardIllegalArgumentException e) {
				throw new RuntimeException(e);
			}
			this.amount = nbt.getInteger("amount");
		} else {
			throw new NullPointerException("Cannot create a fluid stack if no fluid is provided!");
		}
	}


	public boolean isFluidEqual(FluidStack stack){
		if(stack == null) return false;
		return stack.fluid == this.fluid;
	}

	public static boolean areFluidsEqual(FluidStack fluidStack, FluidStack fluidStack1) {
		if (fluidStack == null && fluidStack1 == null) {
			return true;
		} else {
			return fluidStack != null && fluidStack1 != null && fluidStack.isFluidEqual(fluidStack1);
		}
	}

	public ItemStack toItemStack(){
		if(fluid.blocks.isEmpty()){
			return null;
		}
		return new ItemStack(fluid.blocks.get(0), amount);
	}

	public boolean isStackEqual(FluidStack stack){
		return stack.fluid == fluid && stack.amount == amount;
	}

	public FluidStack splitStack(int amount){
		this.amount -= amount;
		return new FluidStack(this.fluid, amount);
	}

	public String toString(){
		return amount+"mB "+fluid.getName();
	}


	public FluidStack copy(){
		return new FluidStack(fluid, amount);
	}

}
