package sunsetsatellite.catalyst.fluids.util;

import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.item.ItemStack;
import turniplabs.halplibe.mixin.accessors.RecipeSymbolAccessor;

import java.util.*;
import java.util.stream.Collectors;

public class RecipeExtendedSymbol {
    private char symbol;
    private ItemStack stack;
    private String itemGroup;
    private FluidStack fluidStack;

    private List<ItemStack> override;

    private RecipeExtendedSymbol(char symbol, ItemStack stack, FluidStack fluidStack, String itemGroup) {
        if(stack == null && fluidStack == null && (itemGroup == null || Objects.equals(itemGroup, ""))){
            throw new NullPointerException("Null symbol!");
        }
        this.symbol = symbol;
        this.stack = stack;
        this.itemGroup = itemGroup;
        this.fluidStack = fluidStack;
    }

    public RecipeExtendedSymbol(char symbol, ItemStack stack, String itemGroup) {
        if(stack == null && (itemGroup == null || Objects.equals(itemGroup, ""))){
            throw new NullPointerException("Null symbol!");
        }
        this.symbol = symbol;
        this.stack = stack;
        this.itemGroup = itemGroup;
    }

    public RecipeExtendedSymbol(ItemStack stack, String itemGroup) {
        if(stack == null && (itemGroup == null || Objects.equals(itemGroup, ""))){
            throw new NullPointerException("Null symbol!");
        }
        this.stack = stack;
        this.itemGroup = itemGroup;
    }

    public RecipeExtendedSymbol(ItemStack stack) {
        if(stack == null){
            throw new NullPointerException("Null symbol!");
        }
        this.stack = stack;
    }

    public RecipeExtendedSymbol(String itemGroup) {
        if(itemGroup == null || Objects.equals(itemGroup, "")){
            throw new NullPointerException("Null symbol!");
        }
        this.itemGroup = itemGroup;
    }

    public RecipeExtendedSymbol(FluidStack fluidStack) {
        if(fluidStack == null){
            throw new NullPointerException("Null symbol!");
        }
        this.fluidStack = fluidStack;
    }

    public RecipeExtendedSymbol(char symbol, FluidStack fluidStack) {
        if(fluidStack == null){
            throw new NullPointerException("Null symbol!");
        }
        this.symbol = symbol;
        this.fluidStack = fluidStack;
    }

    public RecipeExtendedSymbol(List<ItemStack> override){
        if(override == null || override.isEmpty()) {
            throw new NullPointerException("Null symbol!");
        }
        this.override = override;
        this.stack = override.get(0);
    }

    public List<ItemStack> resolve(){
        if(override != null) return override;
        if(stack != null && itemGroup == null){
            return Collections.singletonList(stack);
        } else if (itemGroup != null && stack == null) {
            return Registries.ITEM_GROUPS.getItem(itemGroup);
        } else if (itemGroup != null) {
            ArrayList<ItemStack> list = new ArrayList<>(Registries.ITEM_GROUPS.getItem(itemGroup));
            list.add(stack);
            return list;
        }
        return new ArrayList<>();
    }

    public List<FluidStack> resolveFluids(){
        if(fluidStack != null && itemGroup == null){
            return Collections.singletonList(fluidStack);
        }
        return new ArrayList<>();
    }

    public boolean matches(ItemStack stack){
        if(stack == null) return false;
        List<ItemStack> stacks = resolve();
        boolean foundId = false;
        boolean foundMeta = false;
        for (ItemStack resolvedStack : stacks) {
            if(resolvedStack.itemID == stack.itemID){
                foundId = true;
            }
            if(resolvedStack.getMetadata() == -1 || resolvedStack.getMetadata() == stack.getMetadata()){
                foundMeta = true;
            }
        }
        return foundId && foundMeta;
    }

    public boolean matches(RecipeExtendedSymbol symbol){
        if(symbol == null) return false;
        if(equals(symbol)) return true;
        List<RecipeExtendedSymbol> symbols = resolve().stream().map(RecipeExtendedSymbol::new).collect(Collectors.toList());
        List<ItemStack> checkedStacks = symbol.resolve();
        return symbols.stream().anyMatch((S)->checkedStacks.stream().anyMatch(S::matches));
    }

    public boolean matchesFluid(FluidStack fluidStack){
        if(fluidStack == null) return false;
        List<FluidStack> fluidStacks = resolveFluids();
        boolean found = false;
        for (FluidStack fluid : fluidStacks) {
            if(fluid.isFluidEqual(fluidStack)){
                found = true;
                break;
            }
        }
        return found;
    }

    public char getSymbol() {
        return symbol;
    }

    public ItemStack getStack() {
        return stack;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public FluidStack getFluidStack() { return fluidStack; }

    public boolean hasFluid() { return fluidStack != null; }

    public RecipeExtendedSymbol copy(){
        return new RecipeExtendedSymbol(symbol,stack,fluidStack,itemGroup);
    }

    public RecipeSymbol asNormalSymbol(){
        if(itemGroup == null){
            RecipeSymbol r = new RecipeSymbol(stack);
            ((RecipeSymbolAccessor)r).setSymbol(symbol);
            return r;
        } else {
            return new RecipeSymbol(symbol,stack,itemGroup);
        }
    }

    public static RecipeExtendedSymbol[] arrayOf(Object... objs){
        //java varargs bruh moment
        if(objs.length == 1 && objs[0] instanceof List){
            return arrayOf((List<Object>) objs[0]);
        }

        return Arrays.stream(objs).map((O) -> {
            if (O instanceof ItemStack) {
                return new RecipeExtendedSymbol((ItemStack) O);
            } else if (O instanceof FluidStack) {
                return new RecipeExtendedSymbol((FluidStack) O);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).toArray(RecipeExtendedSymbol[]::new);
    }

    public static RecipeExtendedSymbol[] arrayOf(List<Object> list){
        return list.stream().map((O) -> {
            if (O instanceof ItemStack) {
                return new RecipeExtendedSymbol((ItemStack) O);
            } else if (O instanceof FluidStack) {
                return new RecipeExtendedSymbol((FluidStack) O);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).toArray(RecipeExtendedSymbol[]::new);
    }

    public static List<RecipeExtendedSymbol> listOf(Object... objs){
        return Arrays.stream(objs).map((O) -> {
            if (O instanceof ItemStack) {
                return new RecipeExtendedSymbol((ItemStack) O);
            } else if (O instanceof FluidStack) {
                return new RecipeExtendedSymbol((FluidStack) O);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeExtendedSymbol that = (RecipeExtendedSymbol) o;

        if (getSymbol() != that.getSymbol()) return false;
        if (getStack() != null && that.getStack() != null ? !getStack().isItemEqual(that.getStack()) : that.getStack() != null) {
            return false;
        }
        if (getItemGroup() != null ? !getItemGroup().equals(that.getItemGroup()) : that.getItemGroup() != null)
            return false;
        return getFluidStack() != null ? getFluidStack().isFluidEqual(that.getFluidStack()) : that.getFluidStack() == null;
    }



    @Override
    public String toString() {
        if(stack != null && itemGroup == null){
            return "symbol: "+stack;
        } else if (itemGroup != null && stack == null) {
            return "symbol: "+itemGroup;
        } else if (itemGroup != null) {
            return "symbol: "+stack+" && "+itemGroup;
        } else if (fluidStack != null) {
            return "symbol: "+fluidStack;
        }
        return "null symbol";
    }
}
