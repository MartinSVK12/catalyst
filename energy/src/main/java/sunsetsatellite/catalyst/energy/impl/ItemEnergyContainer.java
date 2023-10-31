package sunsetsatellite.catalyst.energy.impl;


import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.energy.api.IEnergyItem;

public class ItemEnergyContainer extends Item implements IEnergyItem {
    public ItemEnergyContainer(int i) {
        super(i);
    }

    public int baseCapacity = 0;
    public int baseProvide = 0;
    public int baseReceive = 0;

    @Override
    public int provide(ItemStack stack, int amount, boolean test) {
        int provided = Math.min(getEnergy(stack), Math.min(getMaxProvide(stack), amount));
        if(!test){
            modifyEnergy(stack,-provided);
        }
        return provided;
    }

    @Override
    public int receive(ItemStack stack, int amount, boolean test) {
        int received = Math.min(getCapacity(stack) - getEnergy(stack), Math.min(getMaxReceive(stack), amount));
        if(!test){
            modifyEnergy(stack,received);
        }
        return received;
    }

    @Override
    public int getEnergy(ItemStack stack) {
        return stack.getData().getInteger("energy");
    }

    @Override
    public int getCapacity(ItemStack stack) {
        if(!stack.getData().containsKey("capacity")){
            stack.getData().putInt("capacity",baseCapacity);
            return baseCapacity;
        }
        return stack.getData().getInteger("capacity");
    }

    @Override
    public int getMaxReceive(ItemStack stack) {
        if(!stack.getData().containsKey("maxReceive")){
            stack.getData().putInt("maxReceive",baseReceive);
            return baseReceive;

        }
        return stack.getData().getInteger("maxReceive");
    }

    @Override
    public int getMaxProvide(ItemStack stack) {
        if(!stack.getData().containsKey("maxProvide")){
            stack.getData().putInt("maxProvide",baseProvide);
            return baseProvide;
        }
        return stack.getData().getInteger("maxProvide");
    }

    @Override
    public void modifyEnergy(ItemStack stack, int amount) {
        if(stack.getData().getInteger("energy")+amount > getCapacity(stack)){
            stack.getData().putInt("energy",getCapacity(stack));
        } else if(stack.getData().getInteger("energy")+amount < 0){
            stack.getData().putInt("energy",0);
        } else {
			stack.getData().putInt("energy",getEnergy(stack)+amount);
		}
    }
}
