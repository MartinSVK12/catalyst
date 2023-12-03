package sunsetsatellite.catalyst.energy.impl;


import com.mojang.nbt.CompoundTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.energy.api.IEnergy;
import sunsetsatellite.catalyst.energy.api.IEnergySink;
import sunsetsatellite.catalyst.energy.api.IEnergySource;

public class TileEntityEnergyConductor extends TileEntityEnergy implements IEnergySink, IEnergySource {
    public int maxReceive = 0;
    public int maxProvide = 0;

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
    public int getMaxReceive() {
        return maxReceive;
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
        if(stack.getItem() instanceof ItemEnergyContainer){
            int provided = Math.min(this.energy, Math.min(this.maxProvide, amount));
            int received = ((ItemEnergyContainer) stack.getItem()).receive(stack,amount,true);
            int actual = Math.min(provided,received);
            if(!test){
                energy -= actual;
                ((ItemEnergyContainer) stack.getItem()).receive(stack,actual,false);
            }
            return actual;
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
    public void writeToNBT(CompoundTag CompoundTag) {
        CompoundTag.putInt("maxReceive",maxReceive);
        CompoundTag.putInt("maxProvide",maxProvide);
        super.writeToNBT(CompoundTag);
    }

    @Override
    public void readFromNBT(CompoundTag CompoundTag) {
        maxReceive = CompoundTag.getInteger("maxReceive");
        maxProvide = CompoundTag.getInteger("maxProvide");
        super.readFromNBT(CompoundTag);
    }

    public void setTransfer(int amount){
        maxProvide = amount;
        maxReceive = amount;
    }

    @Override
    public void tick() {
        for (Direction dir : Direction.values()) {
            TileEntity facingTile = dir.getTileEntity(worldObj,this);
            if(facingTile instanceof IEnergySink && !facingTile.equals(lastReceived)){
                int provided = provide(dir,getMaxProvide(),true);
                if(provided <= 0){
                    continue;
                }
                int received = ((IEnergySink) facingTile).receive(dir.getOpposite(),provided,true);
                if(received > 0){
                    ((IEnergySink) facingTile).receive(dir.getOpposite(),provided,false);
                    provide(dir,received,false);
                    notifyOfProvide((IEnergy) facingTile);
                    ((IEnergy) facingTile).notifyOfReceive(this);
                }
            }
        }
    }
}
