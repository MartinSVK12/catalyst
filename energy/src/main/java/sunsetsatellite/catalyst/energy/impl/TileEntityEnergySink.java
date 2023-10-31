package sunsetsatellite.catalyst.energy.impl;


import com.mojang.nbt.CompoundTag;
import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.energy.api.IEnergySink;

public class TileEntityEnergySink extends TileEntityEnergy implements IEnergySink {

    public int maxReceive = 0;

    @Override
    public int receive(Direction dir, int amount, boolean test) {
        if(canConnect(dir, Connection.INPUT)){
            int received = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, amount));
            if(!test){
                energy += received;
            }
            return received;
        }
        return 0;
    }

    @Override
    public int receive(ItemStack stack, int amount, boolean test){
        if(stack.getItem() instanceof ItemEnergyContainer){
            int received = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, amount));
            int provided = ((ItemEnergyContainer) stack.getItem()).provide(stack,amount,true);
            int actual = Math.min(provided,received);
            if(!test){
                energy += actual;
                ((ItemEnergyContainer) stack.getItem()).provide(stack,actual,false);
            }
            return actual;
        }
        return 0;
    }

    @Override
    public int getMaxReceive() {
        return maxReceive;
    }

    @Override
    public void writeToNBT(CompoundTag CompoundTag) {
        CompoundTag.putInt("maxReceive",maxReceive);
        super.writeToNBT(CompoundTag);
    }

    @Override
    public void readFromNBT(CompoundTag CompoundTag) {
        maxReceive = CompoundTag.getInteger("maxReceive");
        super.readFromNBT(CompoundTag);
    }

    @Override
    public int getMaxReceive(Direction dir) {
        if(dir.getTileEntity(worldObj,this) instanceof IEnergySink){
            return ((IEnergySink)dir.getTileEntity(worldObj,this)).getMaxReceive();
        }
        return 0;
    }

    @Override
    public void setMaxReceive(int amount) {
        maxReceive = amount;
    }
}
