package sunsetsatellite.catalyst.energy.impl;


import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.IntTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import sunsetsatellite.catalyst.core.util.*;
import sunsetsatellite.catalyst.energy.api.IEnergy;

import java.util.HashMap;
import java.util.Map;

public class TileEntityEnergy extends TileEntity implements IEnergy {
    public int energy = 0;
    public int capacity = 0;
    public IEnergy lastProvided;
    public IEnergy lastReceived;
    public TickTimer lastTransferMemory;
    public HashMap<Direction, Connection> connections = new HashMap<>();

    public TileEntityEnergy(){
        this.lastTransferMemory = new TickTimer(this,this::clearLastTransfers,10,true);
        for (Direction dir : Direction.values()) {
            connections.put(dir, Connection.NONE);
        }
    }

    @Override
    public void tick() {
        lastTransferMemory.tick();
    }

    public void clearLastTransfers(){
        lastProvided = null;
        lastReceived = null;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public int getEnergy(Direction dir) {
        if(dir.getTileEntity(worldObj,this) instanceof IEnergy){
            return ((IEnergy)dir.getTileEntity(worldObj,this)).getEnergy();
        }
        return 0;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putInt("energy",energy);
        tag.putInt("capacity",capacity);
		CompoundTag connectionsTag = new CompoundTag();
		for (Map.Entry<Direction, Connection> entry : connections.entrySet()) {
			Direction dir = entry.getKey();
			Connection con = entry.getValue();
			connectionsTag.putInt(String.valueOf(dir.ordinal()),con.ordinal());
		}
		tag.putCompound("connections",connectionsTag);
        super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        energy = tag.getInteger("energy");
        capacity = tag.getInteger("capacity");
		CompoundTag connectionsTag = tag.getCompound("connections");
		for (Object con : connectionsTag.getValues()) {
			connections.replace(Direction.values()[Integer.parseInt(((IntTag)con).getTagName())],Connection.values()[((IntTag)con).getValue()]);
		}
        super.readFromNBT(tag);
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public int getCapacity(Direction dir) {
        if(dir.getTileEntity(worldObj,this) instanceof IEnergy){
            return ((IEnergy)dir.getTileEntity(worldObj,this)).getCapacity();
        }
        return 0;
    }

    @Override
    public void setEnergy(int amount) {
        energy = amount;
        if (this.energy > this.capacity) {
            this.energy = this.capacity;
        } else if (this.energy < 0) {
            this.energy = 0;
        }
    }

    @Override
    public void modifyEnergy(int amount) {
        if (this.energy+amount > this.capacity) {
            this.energy = this.capacity;
        } else if (this.energy+amount < 0) {
            this.energy = 0;
        } else {
			energy += amount;
		}
    }

    @Override
    public void setCapacity(int amount) {
        capacity = amount;
    }

    @Override
    public void notifyOfReceive(IEnergy notifier) {
        lastReceived = notifier;
    }

    @Override
    public void notifyOfProvide(IEnergy notifier) {
        lastProvided = notifier;
    }

    @Override
    public void setConnection(Direction dir, Connection connection) {
        connections.replace(dir,connection);
    }

    @Override
    public boolean canConnect(Direction dir, Connection connection) {
        if(connections.get(dir).equals(Connection.BOTH) && !connection.equals(Connection.NONE)){
            return true;
        }
        return connections.get(dir).equals(connection);
    }

    public BlockInstance toInstance(){
        return new BlockInstance(Block.blocksList[worldObj.getBlockId(x,y,z)],new Vec3i(x,y,z),this);
    }
}
