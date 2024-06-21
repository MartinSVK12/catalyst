package sunsetsatellite.catalyst.fluids.impl.tiles;


import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.IntTag;
import com.mojang.nbt.ListTag;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.Packet140TileEntityData;
import net.minecraft.server.entity.player.EntityPlayerMP;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.fluids.api.IFluidInventory;
import sunsetsatellite.catalyst.fluids.api.IFluidTransfer;
import sunsetsatellite.catalyst.fluids.api.IMassFluidInventory;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.util.*;

public class TileEntityFluidContainer extends TileEntity
    implements IFluidInventory, IFluidTransfer {

    public FluidStack[] fluidContents = new FluidStack[1];
    public int[] fluidCapacity = new int[1];
    public ArrayList<ArrayList<BlockFluid>> acceptedFluids = new ArrayList<>(fluidContents.length);

    public FluidStack shownFluid = fluidContents[0];
    public int shownMaxAmount = 0;

    public int transferSpeed = 20;

    public HashMap<Direction, Connection> fluidConnections = new HashMap<>();
    public HashMap<Direction, Integer> activeFluidSlots = new HashMap<>();

    public TileEntityFluidContainer(){
        for (Direction dir : Direction.values()) {
            fluidConnections.put(dir, Connection.NONE);
            activeFluidSlots.put(dir,0);
        }
        for (FluidStack ignored : fluidContents) {
            acceptedFluids.add(new ArrayList<>());
        }
    }

    public void take(@NotNull FluidStack fluidStack, Direction dir){
        if(fluidConnections.get(dir) == Connection.INPUT || fluidConnections.get(dir) == Connection.BOTH){
            TileEntity tile = dir.getTileEntity(worldObj,this);
            if(tile instanceof IFluidInventory && tile instanceof IFluidTransfer){
                IFluidInventory fluidInv = ((IFluidInventory) tile);
                IFluidTransfer fluidTransfer = ((IFluidTransfer) tile);
                if(fluidTransfer.getConnection(dir.getOpposite()) == Connection.OUTPUT || fluidTransfer.getConnection(dir.getOpposite()) == Connection.BOTH){
                    int maxFlow = Math.min(transferSpeed,fluidInv.getTransferSpeed());
					if(activeFluidSlots.get(dir) == -1) return;
                    if(acceptedFluids.get(activeFluidSlots.get(dir)).contains(fluidStack.getLiquid())) {
						int maxAmount = Math.min(fluidStack.amount, maxFlow);
                        if (canInsertFluid(activeFluidSlots.get(dir), new FluidStack(fluidStack.liquid, maxAmount))) {
                            FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
                            if (fluidContents[activeFluidSlots.get(dir)] == null) {
                                fluidContents[activeFluidSlots.get(dir)] = transferablePortion;
                            } else {
                                fluidContents[activeFluidSlots.get(dir)].amount += transferablePortion.amount;
                            }
                        }
                    }
                }
            }
        }
    }

	public void take(@NotNull FluidStack fluidStack, Direction dir, int slot){
		if(fluidConnections.get(dir) == Connection.INPUT || fluidConnections.get(dir) == Connection.BOTH){
			TileEntity tile = dir.getTileEntity(worldObj,this);
			if(tile instanceof IFluidInventory && tile instanceof IFluidTransfer){
				IFluidInventory fluidInv = ((IFluidInventory) tile);
				IFluidTransfer fluidTransfer = ((IFluidTransfer) tile);
				if(fluidTransfer.getConnection(dir.getOpposite()) == Connection.OUTPUT || fluidTransfer.getConnection(dir.getOpposite()) == Connection.BOTH){
					int maxFlow = Math.min(transferSpeed,fluidInv.getTransferSpeed());
					if(slot == -1) return;
					if(acceptedFluids.get(slot).contains(fluidStack.getLiquid())) {
						int maxAmount = Math.min(fluidStack.amount, maxFlow);
						if (canInsertFluid(slot, new FluidStack(fluidStack.liquid, maxAmount))) {
							FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
							if (fluidContents[slot] == null) {
								fluidContents[slot] = transferablePortion;
							} else {
								fluidContents[slot].amount += transferablePortion.amount;
							}
						}
					}
				}
			}
		}
	}

    public void give(Direction dir){
        int slot = activeFluidSlots.get(dir);
		if(slot == -1) return;
        FluidStack fluidStack = fluidContents[slot];
        if(fluidConnections.get(dir) == Connection.OUTPUT || fluidConnections.get(dir) == Connection.BOTH){
            TileEntity tile = dir.getTileEntity(worldObj,this);
            if(tile instanceof IFluidInventory && tile instanceof IFluidTransfer) {
                IFluidInventory fluidInv = ((IFluidInventory) tile);
                IFluidTransfer fluidTransfer = ((IFluidTransfer) tile);
                if(fluidTransfer.getConnection(dir.getOpposite()) == Connection.INPUT || fluidTransfer.getConnection(dir.getOpposite()) == Connection.BOTH) {
                    int maxFlow = Math.min(transferSpeed, fluidInv.getTransferSpeed());
                    if(tile instanceof IMassFluidInventory){
                        IMassFluidInventory massFluidInv = (IMassFluidInventory) tile;
                        if(fluidStack.isFluidEqual(massFluidInv.getFilter(dir.getOpposite())) || massFluidInv.getFilter(dir.getOpposite()) == null){
							int maxAmount = Math.min(fluidStack.amount, maxFlow);
                            if(massFluidInv.canInsertFluid(new FluidStack(fluidStack.liquid,maxAmount))){
                                FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
                                massFluidInv.insertFluid(transferablePortion);
                            }
                        }
                    } else {
                        int otherSlot = fluidInv.getActiveFluidSlot(dir.getOpposite());
						if(otherSlot == -1) return;
                        if(fluidInv.getAllowedFluidsForSlot(otherSlot).contains(fluidStack.getLiquid())){
							int maxAmount = Math.min(fluidStack.amount, maxFlow);
                            if(fluidInv.canInsertFluid(otherSlot,new FluidStack(fluidStack.liquid,maxAmount))){
                                FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
                                fluidInv.insertFluid(otherSlot,transferablePortion);
                            }
                        }
                    }
                }
            }
        }
    }

	public void give(Direction dir, int slot, int otherSlot){
		if(slot == -1) return;
		FluidStack fluidStack = fluidContents[slot];
		if(fluidConnections.get(dir) == Connection.OUTPUT || fluidConnections.get(dir) == Connection.BOTH){
			TileEntity tile = dir.getTileEntity(worldObj,this);
			if(tile instanceof IFluidInventory && tile instanceof IFluidTransfer) {
				IFluidInventory fluidInv = ((IFluidInventory) tile);
				IFluidTransfer fluidTransfer = ((IFluidTransfer) tile);
				if(fluidTransfer.getConnection(dir.getOpposite()) == Connection.INPUT || fluidTransfer.getConnection(dir.getOpposite()) == Connection.BOTH) {
					int maxFlow = Math.min(transferSpeed, fluidInv.getTransferSpeed());
					if(tile instanceof IMassFluidInventory){
						IMassFluidInventory massFluidInv = (IMassFluidInventory) tile;
						if(fluidStack.isFluidEqual(massFluidInv.getFilter(dir.getOpposite())) || massFluidInv.getFilter(dir.getOpposite()) == null){
							int maxAmount = Math.min(fluidStack.amount, maxFlow);
							if(massFluidInv.canInsertFluid(new FluidStack(fluidStack.liquid,maxAmount))){
								FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
								massFluidInv.insertFluid(transferablePortion);
							}
						}
					} else {
						if(otherSlot == -1) return;
						if(fluidInv.getAllowedFluidsForSlot(otherSlot).contains(fluidStack.getLiquid())){
							int maxAmount = Math.min(fluidStack.amount, maxFlow);
							if(fluidInv.canInsertFluid(otherSlot,new FluidStack(fluidStack.liquid,maxAmount))){
								FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
								fluidInv.insertFluid(otherSlot,transferablePortion);
							}
						}
					}
				}
			}
		}
	}

    @Override
    public FluidStack insertFluid(int slot, FluidStack fluidStack) {
        FluidStack stack = fluidContents[slot];
        FluidStack split = fluidStack.splitStack(Math.min(fluidStack.amount,getRemainingCapacity(slot)));
        if(stack != null && split.amount > 0){
            fluidContents[slot].amount += split.amount;
        } else {
            fluidContents[slot] = split;
        }
        return fluidStack;
    }

    @Override
    public int getRemainingCapacity(int slot) {
        if(fluidContents[slot] == null){
            return fluidCapacity[slot];
        }
        return fluidCapacity[slot]-fluidContents[slot].amount;
    }

    @Override
    public boolean canInsertFluid(int slot,FluidStack fluidStack){
        if(getFluidInSlot(slot) != null) if(!getFluidInSlot(slot).isFluidEqual(fluidStack)) return false;
        return Math.min(fluidStack.amount,getRemainingCapacity(slot)) > 0;
    }

    @Override
    public Connection getConnection(Direction dir) {
        return fluidConnections.get(dir);
    }

    public String getInvName() {
        return "Generic Fluid Container";
    }

    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);

        ListTag nbtTagList = tag.getList("Fluids");
        this.fluidContents = new FluidStack[this.getFluidInventorySize()];

        for(int i3 = 0; i3 < nbtTagList.tagCount(); ++i3) {
            CompoundTag CompoundTag4 = (CompoundTag)nbtTagList.tagAt(i3);
            int i5 = CompoundTag4.getByte("Slot") & 255;
            if(i5 >= 0 && i5 < this.fluidContents.length) {
                this.fluidContents[i5] = new FluidStack(CompoundTag4);
            }
        }

        CompoundTag connectionsTag = tag.getCompound("fluidConnections");
        for (Object con : connectionsTag.getValues()) {
            fluidConnections.replace(Direction.values()[Integer.parseInt(((IntTag)con).getTagName())],Connection.values()[((IntTag)con).getValue()]);
        }

        CompoundTag activeFluidSlotsTag = tag.getCompound("fluidActiveSlots");
        for (Object con : activeFluidSlotsTag.getValues()) {
            activeFluidSlots.replace(Direction.values()[Integer.parseInt(((IntTag)con).getTagName())],((IntTag) con).getValue());
        }

    }

    @Override
    public void tick() {
        super.tick();
		fluidContents = Arrays.stream(fluidContents).map((F)-> (F != null && F.amount <= 0) ? null : F).toArray(FluidStack[]::new);
        if(!worldObj.isClientSide){
            for (EntityPlayer player : worldObj.players) {
                if(player instanceof EntityPlayerMP){
					//((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new PacketUpdateClientFluidRender(x,y,z,fluidContents[0],fluidCapacity[0]));
                }
            }
        }
    }

    public void writeToNBT(CompoundTag CompoundTag1) {
        super.writeToNBT(CompoundTag1);
        ListTag nBTTagList2 = new ListTag();
        ListTag nbtTagList = new ListTag();
        CompoundTag connectionsTag = new CompoundTag();
        CompoundTag activeFluidSlotsTag = new CompoundTag();
        for(int i3 = 0; i3 < this.fluidContents.length; ++i3) {
            if(this.fluidContents[i3] != null && this.fluidContents[i3].getLiquid() != null) {
                CompoundTag CompoundTag4 = new CompoundTag();
                CompoundTag4.putByte("Slot", (byte)i3);
                this.fluidContents[i3].writeToNBT(CompoundTag4);
                nbtTagList.addTag(CompoundTag4);
            }
        }
        for (Map.Entry<Direction, Integer> entry : activeFluidSlots.entrySet()) {
            Direction dir = entry.getKey();
            activeFluidSlotsTag.putInt(String.valueOf(dir.ordinal()),entry.getValue());
        }
        for (Map.Entry<Direction, Connection> entry : fluidConnections.entrySet()) {
            Direction dir = entry.getKey();
            Connection con = entry.getValue();
            connectionsTag.putInt(String.valueOf(dir.ordinal()),con.ordinal());
        }
        CompoundTag1.putCompound("fluidConnections",connectionsTag);
        CompoundTag1.putCompound("fluidActiveSlots",activeFluidSlotsTag);
        CompoundTag1.put("Fluids", nbtTagList);
        CompoundTag1.put("Items", nBTTagList2);
    }

    public boolean canInteractWith(EntityPlayer entityPlayer1) {
        return this.worldObj.getBlockTileEntity(this.x, this.y, this.z) == this && entityPlayer1.distanceToSqr((double) this.x + 0.5D, (double) this.y + 0.5D, (double) this.z + 0.5D) <= 64.0D;
    }

    @Override
    public FluidStack getFluidInSlot(int slot) {
        if(this.fluidContents.length == 0) return null;
        if(this.fluidContents[slot] == null || this.fluidContents[slot].getLiquid() == null || this.fluidContents[slot].amount == 0){
            this.fluidContents[slot] = null;
        }
        return fluidContents[slot];
    }

    @Override
    public int getFluidCapacityForSlot(int slot) {
        return fluidCapacity[slot];
    }

    @Override
    public ArrayList<BlockFluid> getAllowedFluidsForSlot(int slot) {
        return acceptedFluids.get(slot);
    }

    public int getFluidAmountForSlot(int slot){
        if(fluidContents[0] == null){
            return 0;
        } else {
            return fluidContents[0].amount;
        }
    }

    @Override
    public void setFluidInSlot(int slot, FluidStack fluid) {
        if(fluid == null || fluid.amount == 0 || fluid.liquid == null){
            this.fluidContents[slot] = null;
            this.onFluidInventoryChanged();
            return;
        }
        if(acceptedFluids.get(slot).contains(fluid.liquid) || acceptedFluids.get(slot).isEmpty()){
            this.fluidContents[slot] = fluid;
            this.onFluidInventoryChanged();
        }

    }

    /*public void setOrModifyFluidInSlot(int slot, FluidStack fluid, boolean add){
        if(getFluidInSlot(0) == null){
            setFluidInSlot(0, fluid);
        } else if(getFluidInSlot(0).isFluidEqual(fluid)){
            if(add){
                incrFluidAmount(0,fluid.amount);
            } else {
                decrFluidAmount(0,fluid.amount);
            }
        }
        this.onFluidInventoryChanged();
    }*/

    /*@Override
    public FluidStack decrFluidAmount(int slot, int amount) {
        if(this.fluidContents[slot] != null) {
            if(this.fluidContents[slot].getLiquid() == null || this.fluidContents[slot].amount == 0){
                this.fluidContents[slot] = null;
                return null;
            }
            FluidStack fluidStack;
            if(this.fluidContents[slot].amount <= amount) {
                fluidStack = this.fluidContents[slot];
                this.fluidContents[slot] = null;
                this.onFluidInventoryChanged();
                return fluidStack;
            } else {
                fluidStack = this.fluidContents[slot].splitStack(amount);
                if(this.fluidContents[slot].amount == 0) {
                    this.fluidContents[slot] = null;
                }

                this.onFluidInventoryChanged();
                return fluidStack;
            }
        } else {
            return null;
        }
    }

    @Override
    public FluidStack incrFluidAmount(int slot, int amount) {
        if(this.fluidContents[slot] != null) {
            if(this.fluidContents[slot].getLiquid() == null || this.fluidContents[slot].amount == 0){
                this.fluidContents[slot] = null;
                return null;
            }
            FluidStack fluidStack;
            if(this.fluidContents[slot].amount + amount > this.fluidCapacity[slot]) {
                fluidStack = this.fluidContents[slot];
                this.onFluidInventoryChanged();
                return fluidStack;
            } else {
                fluidStack = this.fluidContents[slot];
                fluidStack.amount += amount;
                this.onFluidInventoryChanged();
                return fluidStack;
            }
        } else {
            return null;
        }
    }*/

    @Override
    public int getFluidInventorySize() {
        return fluidContents.length;
    }

    @Override
    public void onFluidInventoryChanged() {
        if (this.worldObj != null) {
            this.worldObj.updateTileEntityChunkAndSendToPlayer(this.x, this.y, this.z, this);
        }
    }

	@Override
	public Packet getDescriptionPacket() {
		return new Packet140TileEntityData(this);
	}

    @Override
    public int getTransferSpeed() {
        return transferSpeed;
    }

    @Override
    public int getActiveFluidSlot(Direction dir) {
        return activeFluidSlots.get(dir);
    }


    public void moveFluids(Direction dir, TileEntityFluidPipe tile) {
        int activeSlot = activeFluidSlots.get(dir);
		if(activeSlot == -1) return;
        if(fluidConnections.get(dir) == Connection.BOTH || fluidConnections.get(dir) == Connection.OUTPUT){
            if(getFluidInSlot(activeSlot) != null){
                give(dir);
            }
        } else if(fluidConnections.get(dir) == Connection.BOTH || fluidConnections.get(dir) == Connection.INPUT){
            if(tile.getFluidInSlot(0) != null){
                take(tile.getFluidInSlot(0),dir);
            }
        }
    }
}
