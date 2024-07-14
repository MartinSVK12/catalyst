package sunsetsatellite.catalyst.fluids.util;


import com.mojang.nbt.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.CatalystFluids;

public class FluidStack {
    public int amount;
    public BlockFluid liquid;

    public FluidStack(BlockFluid block, int size){
        amount = size;
        liquid = block;
    }

	public FluidStack(FluidType type, int size){
		liquid = type.fluids.get(0);
		amount = size;
	};

    public FluidStack(CompoundTag nbt){
        readFromNBT(nbt);
    }

	public FluidStack(BlockFluid block) {
		this(block, 1000);
	}

	public int getAmount() {
		return amount;
	}

	public BlockFluid getLiquid(){
        return liquid;
    }

	public FluidType getType(){
		for (FluidType type : CatalystFluids.TYPES) {
			if(type.fluids.contains(liquid)){
				return type;
			}
		}
		return null;
	}

    public FluidStack splitStack(int amount){
        this.amount -= amount;
        return new FluidStack(this.liquid, amount);
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        if(liquid != null){
            nbt.putShort("liquid", (short) liquid.id);
            nbt.putInt("amount",amount);
        }
        return nbt;
    }

    public void readFromNBT(CompoundTag nbt){
        if(nbt.containsKey("liquid")){
            this.liquid = (BlockFluid) Block.blocksList[nbt.getShort("liquid")];
            this.amount = nbt.getInteger("amount");
        }
    }

    public FluidStack copy(){
        return new FluidStack(liquid, amount);
    }

	public ItemStack toItemStack(){
		return new ItemStack(liquid, amount);
	}

    public String getFluidName(){
        return liquid.getLanguageKey(0);
    }

    public String toString(){
        return amount+"mB "+liquid.getLanguageKey(0);
    }

    public boolean isFluidEqual(FluidStack stack){
		if(stack == null) return false;
        return stack.liquid == liquid || getType() == stack.getType();
    }

    public boolean isFluidEqual(BlockFluid fluid){
        return liquid == fluid;
    }

	public boolean isTypeEqual(FluidType type){ return type.fluids.contains(liquid); }

	public boolean isTypeEqual(String typeId){
		if(CatalystFluids.TYPES.getItem(typeId) == null) return false;
		return CatalystFluids.TYPES.getItem(typeId).fluids.contains(liquid);
	}


	public boolean isStackEqual(FluidStack stack){
        return (stack.liquid == liquid || getType() == stack.getType()) && stack.amount == amount;
    }

    public static boolean areFluidStacksEqual(FluidStack fluidStack, FluidStack fluidStack1) {
        if (fluidStack == null && fluidStack1 == null) {
            return true;
        } else {
            return fluidStack != null && fluidStack1 != null && fluidStack.isStackEqual(fluidStack1);
        }
    }

	public static boolean areFluidsEqual(FluidStack fluidStack, FluidStack fluidStack1) {
		if (fluidStack == null && fluidStack1 == null) {
			return true;
		} else {
			return fluidStack != null && fluidStack1 != null && fluidStack.isFluidEqual(fluidStack1);
		}
	}

}
