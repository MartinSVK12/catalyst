package sunsetsatellite.catalyst.fluids.mixin;

import net.minecraft.core.item.*;
import net.minecraft.core.util.collection.NamespaceID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.fluids.api.IFluidInventory;
import sunsetsatellite.catalyst.fluids.api.IItemFluidContainer;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import sunsetsatellite.catalyst.fluids.util.Fluids;

import java.util.List;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(value = {ItemBucket.class, ItemBucketEmpty.class, ItemBucketIceCream.class},remap = false)
public class ItemBucketMixin extends Item implements IItemFluidContainer {

	@Unique
	public Item thisAs = this;

	private ItemBucketMixin(NamespaceID namespaceId, int id) {
		super(namespaceId, id);
	}

	@Override
	public int getCapacity(ItemStack stack) {
		return 1000;
	}

	@Override
	public int getRemainingCapacity(ItemStack stack) {
		return (thisAs instanceof ItemBucketEmpty) ? 1000 : 0;
	}

	@Override
	public int getFluidAmount(ItemStack stack) {
		return (thisAs instanceof ItemBucketEmpty) ? 0 : 1000;
	}

	@Override
	public boolean canFill(ItemStack stack) {
		return thisAs instanceof ItemBucketEmpty;
	}

	@Override
	public boolean canDrain(ItemStack stack) {
		return !(thisAs instanceof ItemBucketEmpty);
	}

	@Override
	public FluidStack getCurrentFluid(ItemStack stack) {
		if(thisAs instanceof ItemBucketEmpty) {
			return null;
		} else if (thisAs == Items.BUCKET_WATER) {
			return new FluidStack(Fluids.WATER,1000);
		} else if (thisAs == Items.BUCKET_LAVA) {
			return new FluidStack(Fluids.LAVA,1000);
		} /*else if (thisAs == Items.BUCKET_MILK) {
			return new FluidStack(Fluids.MILK,1000);
		} else if (thisAs == Items.BUCKET_ICECREAM) {
			return new FluidStack(Fluids.ICE_CREAM,1000);
		}*/
		return null;
	}

	@Override
	public void setCurrentFluid(FluidStack fluidStack, ItemStack stack) {
		if(fluidStack.amount < 1000) return;
		if (fluidStack.fluid == Fluids.WATER) {
			stack.itemID = Items.BUCKET_WATER.id;
			fluidStack.amount -= 1000;
		} else if (fluidStack.fluid == Fluids.LAVA) {
			stack.itemID = Items.BUCKET_LAVA.id;
			fluidStack.amount -= 1000;
		} /*else if (fluidStack.fluid == Fluids.MILK) {
			stack.itemID = Items.BUCKET_MILK.id;
			fluidStack.amount -= 1000;
		} else if (fluidStack.fluid == Fluids.ICE_CREAM) {
			stack.itemID = Items.BUCKET_ICECREAM.id;
			fluidStack.amount -= 1000;
		}*/
	}

	@Override
	public ItemStack fill(FluidStack fluidStack, ItemStack stack) {
		if(fluidStack.amount < 1000) return stack;
		if(getAllowedFluids(stack).contains(fluidStack.fluid)) {
			if (fluidStack.fluid == Fluids.WATER) {
				if(stack.stackSize == 1){
					stack.itemID = Items.BUCKET_WATER.id;
					fluidStack.amount -= 1000;
					return stack;
				}
			} else if (fluidStack.fluid == Fluids.LAVA) {
				if(stack.stackSize == 1) {
					stack.itemID = Items.BUCKET_LAVA.id;
					fluidStack.amount -= 1000;
					return stack;
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack fill(FluidStack fluidStack, ItemStack stack, IFluidInventory tile) {
		return fill(fluidStack, stack);
	}

	@Override
	public ItemStack fill(FluidStack fluidStack, ItemStack stack, IFluidInventory tile, int maxAmount) {
		if(maxAmount < 1000) return stack;
		return fill(fluidStack, stack);
	}

	@Override
	public ItemStack fill(FluidStack fluidStack, ItemStack stack, IItemFluidContainer inv) {
		if(fluidStack.amount < 1000) return stack;
		return fill(fluidStack, stack);
	}

	@Override
	public void drain(ItemStack stack, int slot, IFluidInventory tile) {
		if(thisAs instanceof ItemBucketEmpty) return;
		if(stack.stackSize != 1) return;
		if(tile.getRemainingCapacity(slot) >= 1000) {
			if(getCurrentFluid(stack).isFluidEqual(tile.getFluidInSlot(slot))) {
				tile.getFluidInSlot(slot).amount += 1000;
				stack.itemID = Items.BUCKET.id;
			} else if (tile.getFluidInSlot(slot) == null) {
				tile.setFluidInSlot(slot, getCurrentFluid(stack));
				stack.itemID = Items.BUCKET.id;
			}
		}
	}

	@Override
	public void drain(ItemStack stack, ItemStack other, int slot, IItemFluidContainer inv) {
		if(thisAs instanceof ItemBucketEmpty) return;
		if(stack.stackSize != 1) return;
		if(inv.getRemainingCapacity(stack) >= 1000) {
			if(getCurrentFluid(stack).isFluidEqual(inv.getCurrentFluid(stack))) {
				inv.getCurrentFluid(stack).amount += 1000;
				stack.itemID = Items.BUCKET.id;
			} else if (inv.getCurrentFluid(other) == null) {
				inv.setCurrentFluid(getCurrentFluid(stack), other);
				stack.itemID = Items.BUCKET.id;
			}
		}
	}

	@Override
	public FluidStack drain(ItemStack stack, int amount) {
		if(stack.stackSize != 1) return null;
		if(thisAs instanceof ItemBucketEmpty) return null;
		if(amount < 1000) return null;
		FluidStack currentFluid = getCurrentFluid(stack);
		stack.itemID = Items.BUCKET.id;
		return currentFluid;
	}

	@Override
	public List<Fluid> getAllowedFluids(ItemStack stack) {
		return Catalyst.listOf(Fluids.LAVA, Fluids.WATER/*, Fluids.MILK, Fluids.ICE_CREAM*/);
	}

	@Override
	public ItemStack getFilled(ItemStack stack, FluidStack fluidStack) {
		if(!(thisAs instanceof ItemBucketEmpty)) return null;
		if (fluidStack.fluid == Fluids.WATER) {
			return Items.BUCKET_WATER.getDefaultStack();
		} else if (fluidStack.fluid == Fluids.LAVA) {
			return Items.BUCKET_LAVA.getDefaultStack();
		} /*else if (fluidStack.fluid == Fluids.MILK) {
			return Items.BUCKET_MILK.getDefaultStack();
		} else if (fluidStack.fluid == Fluids.ICE_CREAM) {
			return Items.BUCKET_ICECREAM.getDefaultStack();
		}*/
		return null;
	}
}
