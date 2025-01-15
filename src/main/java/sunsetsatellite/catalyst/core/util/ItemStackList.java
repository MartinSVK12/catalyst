package sunsetsatellite.catalyst.core.util;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.UnlimitedItemStack;

import java.util.*;

@SuppressWarnings({"UnreachableCode", "RedundantSuppression"})
public class ItemStackList implements IItemStackList, Iterable<ItemStack> {
    protected final ArrayList<ItemStack> contents;
    @SuppressWarnings("FieldCanBeLocal")
    private final int maxItemSize = Integer.MAX_VALUE;
    @SuppressWarnings("FieldCanBeLocal")
    private final int maxStackSize = Integer.MAX_VALUE;

    public ItemStackList() {
        contents = new ArrayList<>();
    }

    public ItemStackList(List<ItemStack> contents) {
        this.contents = new ArrayList<>(contents);
    }

    @Override
    public ItemStack add(ItemStack stack) {
        if (stack == null) {
            return stack;
        }
        int index = find(stack.itemID, stack.getMetadata(), stack.getData());
        if (index != -1) {
            ItemStack invStack = contents.get(index);
            if (!invStack.getData().equals(stack.getData())) {
                index = -1;
            }
        }
        if (index != -1) {
            if (getAmount() + stack.stackSize <= getItemCapacity()) {
                ItemStack invStack = contents.get(index);
                invStack.stackSize += stack.stackSize;
                inventoryChanged();
                return null;
            } else {
                long remainder = (getAmount() + stack.stackSize) - getItemCapacity();
                ItemStack split = stack.splitStack((int) remainder);
                ItemStack invStack = contents.get(index);
                invStack.stackSize += stack.stackSize;
                inventoryChanged();
                return split;
            }
        } else {
            if (getAmount() + stack.stackSize <= getItemCapacity() && getStackAmount() + 1 <= getStackCapacity()) {
                ((UnlimitedItemStack) (Object) stack).setUnlimited(true);
                contents.add(stack);
                inventoryChanged();
                return null;
            } else if (getAmount() + stack.stackSize > getItemCapacity()) {
                long remainder = (getAmount() + stack.stackSize) - getItemCapacity();
                ((UnlimitedItemStack) (Object) stack).setUnlimited(true);
                ItemStack split = stack.splitStack((int) remainder);
                contents.add(stack);
                inventoryChanged();
                return split;
            }
        }
        return stack;
    }

    @Override
    public ItemStack add(int index, ItemStack stack) {
        if(index >= contents.size()) {
            return stack;
        }
        ItemStack invStack = contents.get(index);
        if (invStack == null){
            contents.add(index, stack);
            inventoryChanged();
            return null;
        } else if(invStack.isItemEqual(stack) && invStack.getData().equals(stack.getData())) {
            if (getAmount() + stack.stackSize > getItemCapacity()) {
                long remainder = (getAmount() + stack.stackSize) - getItemCapacity();
                ((UnlimitedItemStack) (Object) stack).setUnlimited(true);
                ItemStack split = stack.splitStack((int) remainder);
                invStack.stackSize += stack.stackSize;
                inventoryChanged();
                return split.stackSize <= 0 ? null : split;
            }
        }
        return stack;
    }

    @Override
    public @UnmodifiableView List<ItemStack> addAll(ItemStackList stacks) {
        return addAll(stacks.getStacks());
    }

    @Override
    public @UnmodifiableView List<ItemStack> addAll(List<ItemStack> stacks) {
        ArrayList<ItemStack> newStacks = new ArrayList<>();

        for (ItemStack stack : stacks) {
            newStacks.add(add(stack));
        }

        return Collections.unmodifiableList(Catalyst.condenseItemList(newStacks));
    }

    /*@Override
    public boolean addAll(ItemStackList stacks) {
        boolean allSuccessful = true;
        ArrayList<ItemStack> toRemove = new ArrayList<>();
        for (ItemStack stack : stacks) {
            boolean success = add(stack);
            if (!success) {
                allSuccessful = false;
                continue;
            }
            toRemove.add(stack);
        }
        for (ItemStack stack : toRemove) {
            ItemStack removed = stacks.remove(stack.itemID, stack.getMetadata(), false, true);
            if (removed == null) {
                allSuccessful = false;
            }
        }
        return allSuccessful;
    }

    @Override
    public boolean addAll(List<ItemStack> stacks) {
        boolean allSuccessful = true;
        ArrayList<ItemStack> toRemove = new ArrayList<>();
        for (ItemStack stack : stacks) {
            boolean success = add(stack);
            if (!success) {
                allSuccessful = false;
            }
            toRemove.add(stack);
        }
        for (ItemStack stack : toRemove) {
            stacks.remove(stack);
        }
        return allSuccessful;
    }

    @Override
    public boolean canAdd(ItemStack stack) {
        int index = find(stack.itemID, stack.getMetadata(), stack.getData());
        if (index != -1) {
            ItemStack invStack = contents.get(index);
            if (!invStack.getData().equals(stack.getData())) {
                index = -1;
            }
        }
        if (index != -1) {
            return getAmount() + stack.stackSize <= getItemCapacity();
        } else {
            return getAmount() + stack.stackSize <= getItemCapacity() && getStackAmount() + 1 <= getStackCapacity();
        }
    }*/

    @Override
    public long getItemCapacity() {
        return maxItemSize;
    }

    @Override
    public long getStackCapacity() {
        return maxStackSize;
    }

    @Override
    public long getStackAmount() {
        return contents.stream().filter(Objects::nonNull).count();
    }

    @Override
    public long getAmount() {
        return contents.stream().mapToInt((C) -> C.stackSize).sum();
    }

    //if strict is true, method returns null if amount is more than actually present
    @Override
    public ItemStack remove(int slot, long amount, boolean strict, boolean unlimited) {
        if (slot >= contents.size()) {
            return null;
        }
        ItemStack stack = contents.get(slot);
        if (stack == null) return null;
        if (strict && amount > stack.stackSize) {
            return null;
        } else if (!strict) {
            amount = Math.min(amount, stack.stackSize);
            if (!unlimited) amount = Math.min(amount, stack.getItem().getItemStackLimit());
            ItemStack splitStack = stack.splitStack((int) amount);
            if (stack.stackSize <= 0) {
                contents.remove(slot);
            }
            inventoryChanged();
            return splitStack;
        }
        return null;
    }

    @Override
    public ItemStack remove(int slot, boolean strict, boolean unlimited) {
        if (slot >= contents.size()) {
            return null;
        }
        ItemStack stack = contents.get(slot);
        if (stack == null) return null;
        return remove(slot, stack.getItem().getItemStackLimit(), strict, unlimited);
    }

    @Override
    public @UnmodifiableView List<ItemStack> move(ItemStackList what, ItemStackList where, boolean strict) {
        return move(what.getStacks(),where,strict);
    }

    @Override
    public @UnmodifiableView List<ItemStack> move(List<ItemStack> what, ItemStackList where, boolean strict) {
        ArrayList<ItemStack> leftovers = new ArrayList<>();

        for (ItemStack stack : what) {
            ItemStack removed = remove(stack.itemID, stack.getMetadata(), stack.stackSize, stack.getData(), strict, true);
            if (removed == null) {
                leftovers.add(stack);
                continue;
            }
            ItemStack addLeftover = where.add(removed);
            leftovers.add(addLeftover);
        }
        return Collections.unmodifiableList(Catalyst.condenseItemList(leftovers));
    }


    @Override
    public ItemStack remove(int id, int meta, long amount, CompoundTag data, boolean strict, boolean unlimited) {
        int index = find(id, meta, data);
        if (index != -1) {
            return remove(index, amount, strict, unlimited);
        }
        return null;
    }

    @Override
    public boolean removeAll(List<ItemStack> stacks, boolean strict, boolean unlimited) {
        for (ItemStack stack : stacks) {
            ItemStack removed = remove(stack.itemID, stack.getMetadata(), stack.stackSize, stack.getData(), strict, unlimited);
            if (removed == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<ItemStack> exportAll(List<ItemStack> stacks, boolean strict, boolean unlimited) {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (ItemStack stack : stacks) {
            ItemStack removed = remove(stack.itemID, stack.getMetadata(), stack.stackSize, stack.getData(), strict, unlimited);
            if (removed != null) {
                list.add(removed);
            }
        }
        return list;
    }

    @Override
    public boolean eject(World world, int x, int y, int z, int slot, long amount, boolean strict) {
        ItemStack content = remove(slot, amount, strict, false);
        if (content != null) {
            float f = world.rand.nextFloat() * 0.8F + 0.1F;
            float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
            float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
            EntityItem entityitem = new EntityItem(world, (float) x + f, (float) y + f1, (float) z + f2, content);
            float f3 = 0.05F;
            entityitem.xd = (float) world.rand.nextGaussian() * f3;
            entityitem.yd = (float) world.rand.nextGaussian() * f3 + 0.2F;
            entityitem.zd = (float) world.rand.nextGaussian() * f3;
            world.entityJoinedWorld(entityitem);
            inventoryChanged();
            return true;
        }
        return false;
    }

    @Override
    public boolean eject(World world, int x, int y, int z, int id, int meta, CompoundTag data, long amount, boolean strict) {
        ItemStack content = remove(id, meta, amount, data, strict, false);
        if (content != null) {
            float f = world.rand.nextFloat() * 0.8F + 0.1F;
            float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
            float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
            EntityItem entityitem = new EntityItem(world, (float) x + f, (float) y + f1, (float) z + f2, content);
            float f3 = 0.05F;
            entityitem.xd = (float) world.rand.nextGaussian() * f3;
            entityitem.yd = (float) world.rand.nextGaussian() * f3 + 0.2F;
            entityitem.zd = (float) world.rand.nextGaussian() * f3;
            world.entityJoinedWorld(entityitem);
            inventoryChanged();
            return true;
        }
        return false;
    }

    @Override
    public void ejectAll(World world, int x, int y, int z) {
        for (ItemStack content : getStacks()) {
            if(content == null) continue;
            eject(world,x,y,z,content.itemID,content.getMetadata(),content.getData(),content.stackSize,false);
        }
    }

    @Override
    public boolean contains(int id, int meta, CompoundTag data) {
        return contents.stream().anyMatch((S) -> S.itemID == id && S.getMetadata() == meta);
    }

    @Override
    public boolean containsAtLeast(int id, int meta, CompoundTag data, long amount) {
        return contents.stream().anyMatch((S) -> S.itemID == id && S.getMetadata() == meta && S.stackSize >= amount);
    }

    @Override
    public boolean containsAtLeast(List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            boolean contains = containsAtLeast(stack.itemID, stack.getMetadata(), stack.getData(), stack.stackSize);
            if (!contains) return false;
        }
        return true;
    }

    @Override
    public boolean containsAtLeast(ItemStackList stacks) {
        for (ItemStack stack : stacks) {
            boolean contains = containsAtLeast(stack.itemID, stack.getMetadata(), stack.getData(), stack.stackSize);
            if (!contains) return false;
        }
        return true;
    }

    @Override
    public ArrayList<ItemStack> returnMissing(ArrayList<ItemStack> stacks) {
        ArrayList<ItemStack> missing = new ArrayList<>();
        for (ItemStack stack : stacks) {
            long c = count(stack.itemID, stack.getMetadata(), stack.getData());
            if (c <= 0) {
                missing.add(stack.copy());
            } else if (c != stack.stackSize) {
                ItemStack copy = stack.copy();
                copy.stackSize -= (int) c;
                missing.add(stack.copy());
            }
        }
        return missing;
    }

    @Override
    public long count(int id, int meta, CompoundTag data) {
        return contents.stream().mapToInt((S) -> {
            if (S.itemID == id && S.getMetadata() == meta) {
                return S.stackSize;
            }
            return 0;
        }).sum();
    }

    @Override
    public long count(int id) {
        return contents.stream().mapToInt((S) -> {
            if (S.itemID == id) {
                return S.stackSize;
            }
            return 0;
        }).sum();
    }

    @Override
    public int find(int id, int meta, CompoundTag data) {
        for (int i = 0; i < contents.size(); i++) {
            ItemStack content = contents.get(i);
            if (content.getMetadata() == meta && content.itemID == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ItemStack get(int index) {
        if (index < 0 || index >= contents.size()) {
            return null;
        }
        return contents.get(index);
    }

    @Override
    public ItemStack get(int id, int meta, CompoundTag data) {
        return get(find(id, meta, data));
    }

    @Override
    public ItemStack getLast() {
        return contents.get(contents.size() - 1);
    }

    @Override
    public void inventoryChanged() {
    }

    @Override
    public void clear() {
        contents.clear();
        inventoryChanged();
    }

    @Override
    public IItemStackList copy() {
        ItemStackList inv = new ItemStackList();
        inv.contents.stream().map(ItemStack::copy).forEach(inv.contents::add);
        return inv;
    }

    @Override
    public List<ItemStack> getStacks() {
        return Collections.unmodifiableList(contents);
    }

    @Override
    public boolean isEmpty() {
        return contents.isEmpty();
    }

    @Override
    public String toString() {
        return contents.toString();
    }

    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return contents.iterator();
    }
}
