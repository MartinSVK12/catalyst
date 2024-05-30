package sunsetsatellite.catalyst.energy.impl;


import com.mojang.nbt.CompoundTag;
import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.energy.api.IEnergyItem;
import sunsetsatellite.catalyst.energy.api.IEnergySource;

public class TileEntityEnergySource extends TileEntityEnergy implements IEnergySource {

    public int maxProvide = 0;
    @Override
    public int provide(Direction dir, int amount, boolean test) {
        if(canConnect(dir, Connection.OUTPUT)){
            int provided = Math.min(this.energy, Math.min(this.maxProvide, amount));
            if(!test){
                energy -= provided;
            }
            return provided;
        }
        return 0;
    }

    @Override
    public int provide(ItemStack stack, int amount, boolean test){
        if(stack.getItem() instanceof IEnergyItem){
            int provided = Math.min(this.energy, Math.min(this.maxProvide, amount));
            int received = ((IEnergyItem) stack.getItem()).receive(stack,amount,true);
            int actual = Math.min(provided,received);
            if(!test){
                energy -= actual;
                ((IEnergyItem) stack.getItem()).receive(stack,actual,false);
            }
            return actual;
        }
        return 0;
    }

    @Override
    public int getMaxProvide() {
        return maxProvide;
    }

    @Override
    public int getMaxProvide(Direction dir) {
        if(dir.getTileEntity(worldObj,this) instanceof IEnergySource){
            return ((IEnergySource)dir.getTileEntity(worldObj,this)).getMaxProvide();
        }
        return 0;
    }

    @Override
    public void setMaxProvide(int amount) {
        maxProvide = amount;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putInt("maxProvide",maxProvide);
        super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        maxProvide = tag.getInteger("maxProvide");
        super.readFromNBT(tag);
    }
}
