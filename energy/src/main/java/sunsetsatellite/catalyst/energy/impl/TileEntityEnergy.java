package sunsetsatellite.catalyst.energy.impl;


import com.mojang.nbt.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import sunsetsatellite.catalyst.core.util.*;
import sunsetsatellite.catalyst.energy.api.IEnergy;

import java.util.HashMap;

public class TileEntityEnergy extends TileEntity implements IEnergy {
    public int energy = 0;
    public int capacity = 0;
    public TileEntityEnergy lastProvided;
    public TileEntityEnergy lastReceived;
    public TickTimer lastTransferMemory;
    public HashMap<Direction, Connection> connections = new HashMap<>();

    public TileEntityEnergy(){
        this.lastTransferMemory = new TickTimer(this,"clearLastTransfers",10,true);
        for (Direction dir : Direction.values()) {
            connections.put(dir, Connection.NONE);
        }
    }

    @Override
    public void updateEntity() {
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
    public void writeToNBT(CompoundTag CompoundTag) {
        CompoundTag.putInt("energy",energy);
        CompoundTag.putInt("capacity",capacity);
        super.writeToNBT(CompoundTag);
    }

    @Override
    public void readFromNBT(CompoundTag CompoundTag) {
        energy = CompoundTag.getInteger("energy");
        capacity = CompoundTag.getInteger("capacity");
        super.readFromNBT(CompoundTag);
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
        return new BlockInstance(Block.blocksList[worldObj.getBlockId(xCoord,yCoord,zCoord)],new Vec3i(xCoord,yCoord,zCoord),this);
    }
}
