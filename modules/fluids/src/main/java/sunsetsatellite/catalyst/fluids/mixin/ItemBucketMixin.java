package sunsetsatellite.catalyst.fluids.mixin;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.fluids.api.IFluidInventory;
import sunsetsatellite.catalyst.fluids.api.IItemFluidContainer;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import sunsetsatellite.catalyst.fluids.util.Fluids;

import java.util.List;

@Mixin(value = ItemBucket.class, remap = false)
public class ItemBucketMixin extends Item implements IItemFluidContainer {
	@Shadow
	@Final
	public int maxCharges;

	@Shadow
	public static int getCharges(@NotNull ItemStack stack) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public static NamespaceID getState(@NotNull ItemStack stack) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	@Final
	public static NamespaceID STATE_EMPTY;

	@Shadow
	public static ItemBucket.BucketState getBucketState(NamespaceID id) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public static void setState(@NotNull ItemStack stack, NamespaceID state) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public static void setCharges(@NotNull ItemStack stack, int charges) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	private ItemBucketMixin(@NotNull NamespaceID namespaceId, @NotNull String translationKey, int id) {
		super(namespaceId, translationKey, id);
	}

	@Override
	public int getCapacity(ItemStack stack) {
		return maxCharges * 1000;
	}

	@Override
	public int getRemainingCapacity(ItemStack stack) {
		return getCapacity(stack) - getFluidAmount(stack);
	}

	@Override
	public int getFluidAmount(ItemStack stack) {
		return getCharges(stack) * 1000;
	}

	@Override
	public boolean canFill(ItemStack stack) {
		return getState(stack) == STATE_EMPTY;
	}

	@Override
	public boolean canDrain(ItemStack stack) {
		return getState(stack) != STATE_EMPTY;
	}

	@Override
	public FluidStack getCurrentFluid(ItemStack stack) {
		NamespaceID state = getState(stack);
		if(state == STATE_EMPTY) return null;
		return new FluidStack(Fluids.getFluid(state), getFluidAmount(stack));
	}

	@Override
	public void setCurrentFluid(FluidStack fluidStack, ItemStack stack) {
		if(fluidStack.fluid.stateId == STATE_EMPTY) return;
		if(fluidStack.amount < 1000) return;
		int charges = Math.min(fluidStack.amount / 1000, maxCharges);
		setState(stack, fluidStack.fluid.stateId);
		setCharges(stack, charges);
		fluidStack.amount -= charges * 1000;
	}

	@Override
	public ItemStack fill(FluidStack fluidStack, ItemStack stack) {
		if (fluidStack == null) return stack;
		if(fluidStack.amount < 1000) return stack;
		if (getAllowedFluids(stack).contains(fluidStack.fluid)) {
			if(stack.stackSize == 1){
				setCurrentFluid(fluidStack, stack);
				return stack;
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
		if (maxAmount < 1000) return stack;
		return fill(fluidStack, stack);
	}

	@Override
	public ItemStack fill(FluidStack fluidStack, ItemStack stack, IItemFluidContainer inv) {
		return fill(fluidStack, stack);
	}

	@Override
	public void drain(ItemStack stack, int slot, IFluidInventory tile) {
		if(getState(stack) == STATE_EMPTY) return;
		if (stack.stackSize != 1) return;
		if (tile.getRemainingCapacity(slot) >= 1000) {
			if (getCurrentFluid(stack).isFluidEqual(tile.getFluidInSlot(slot))) {
				FluidStack drained = drain(stack, getFluidAmount(stack));
				tile.getFluidInSlot(slot).amount += drained.amount;
			} else if (tile.getFluidInSlot(slot) == null) {
				FluidStack drained = drain(stack, getFluidAmount(stack));
				tile.setFluidInSlot(slot, drained);
			}
		}
	}

	@Override
	public void drain(ItemStack stack, ItemStack other, int slot, IItemFluidContainer inv) {
		if(getState(stack) == STATE_EMPTY) return;
		if (stack.stackSize != 1) return;
		if (inv.getRemainingCapacity(stack) >= 1000) {
			if (getCurrentFluid(stack).isFluidEqual(inv.getCurrentFluid(stack))) {
				FluidStack drained = drain(stack, getFluidAmount(stack));
				inv.getCurrentFluid(stack).amount += drained.amount;
			} else if (inv.getCurrentFluid(other) == null) {
				FluidStack drained = drain(stack, getFluidAmount(stack));
				inv.setCurrentFluid(drained, other);
			}
		}
	}

	@Override
	public FluidStack drain(ItemStack stack, int amount) {
		if(getState(stack) == STATE_EMPTY) return null;
		if (stack.stackSize != 1) return null;
		if (amount < 1000) return null;
		int charges = amount / 1000;
		charges = Math.min(getCharges(stack), charges);
		FluidStack fluid = getCurrentFluid(stack);
		fluid.amount = charges * 1000;
		setCharges(stack, getCharges(stack) - charges);
		return fluid;
	}

	@Override
	public List<Fluid> getAllowedFluids(ItemStack stack) {
		return List.of(Fluids.LAVA, Fluids.WATER, Fluids.ACID/*, Fluids.MILK, Fluids.ICE_CREAM*/);
	}

	@Override
	public ItemStack getFilled(ItemStack stack, FluidStack fluidStack) {
		ItemStack filled = stack.copy();
		if(fluidStack.fluid.stateId == STATE_EMPTY) return null;
		if(fluidStack.amount < 1000) return null;
		int charges = Math.min(fluidStack.amount / 1000, maxCharges);
		setState(filled, fluidStack.fluid.stateId);
		setCharges(filled, charges);
		return filled;
	}
}
