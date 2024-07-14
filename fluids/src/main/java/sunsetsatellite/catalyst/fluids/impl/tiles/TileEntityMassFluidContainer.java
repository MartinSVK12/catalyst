package sunsetsatellite.catalyst.fluids.impl.tiles;


import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.IntTag;
import com.mojang.nbt.ListTag;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.IFluidIO;
import sunsetsatellite.catalyst.fluids.api.IFluidInventory;
import sunsetsatellite.catalyst.fluids.api.IFluidTransfer;
import sunsetsatellite.catalyst.fluids.api.IMassFluidInventory;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TileEntityMassFluidContainer extends TileEntity implements IMassFluidInventory, IFluidTransfer, IFluidIO {

    public int transferSpeed = 20;
    public HashMap<Direction, Connection> fluidConnections = new HashMap<>();
    public HashMap<Direction, BlockFluid> fluidFilters = new HashMap<>();
    public int fluidCapacity = 16000;
    public ArrayList<BlockFluid> acceptedFluids = new ArrayList<>();
    public ArrayList<FluidStack> fluidContents = new ArrayList<>();

    public TileEntityMassFluidContainer(){
        for (Direction dir : Direction.values()) {
            fluidConnections.put(dir, Connection.NONE);
            fluidFilters.put(dir,null);
        }

    }

    public String getInvName() {
        return "Generic Mass Fluid Container";
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean canInteractWith(EntityPlayer entityPlayer1) {
        return this.worldObj.getBlockTileEntity(this.x, this.y, this.z) == this && entityPlayer1.distanceToSqr((double) this.x + 0.5D, (double) this.y + 0.5D, (double) this.z + 0.5D) <= 64.0D;
    }

    @Override
    public boolean canInsertFluid(int slot, FluidStack fluidStack) {
        return canInsertFluid(fluidStack);
    }

    @Override
    public FluidStack getFluidInSlot(int slot) {
        if(this.fluidContents.get(slot) == null || this.fluidContents.get(slot).getLiquid() == null || this.fluidContents.get(slot).amount == 0){
            this.fluidContents.set(slot, null);
        }
        return fluidContents.get(slot);
    }

    @Override
    public int getFluidCapacityForSlot(int slot) {
        return fluidCapacity;
    }

    @Override
    public ArrayList<BlockFluid> getAllowedFluidsForSlot(int slot) {
        return acceptedFluids;
    }

    @Override
    public void setFluidInSlot(int slot, FluidStack fluid) {
        if(fluid == null || fluid.amount == 0 || fluid.liquid == null){
            this.fluidContents.set(slot, null);
            this.onFluidInventoryChanged();
            return;
        }
        if(acceptedFluids.contains(fluid.liquid) || acceptedFluids.isEmpty()){
            this.fluidContents.set(slot, fluid);
            this.onFluidInventoryChanged();
        }
    }

    @Override
    public FluidStack insertFluid(int slot, FluidStack fluidStack) {
        return insertFluid(fluidStack);
    }

    @Override
    public int getRemainingCapacity(int slot) {
        return getRemainingCapacity();
    }


    @Override
    public int getFluidInventorySize() {
        return fluidContents.size();
    }

    @Override
    public void onFluidInventoryChanged() {

    }

    @Override
    public int getTransferSpeed() {
        return transferSpeed;
    }

    @Override
    public int getAllFluidAmount() {
        int i = 0;
        for (FluidStack fluidContent : fluidContents) {
            i += fluidContent.amount;
        }
        return i;
    }

    @Override
    public int getRemainingCapacity() {
        return fluidCapacity-getAllFluidAmount();
    }

    @Override
    public FluidStack findStack(BlockFluid fluid) {
        return fluidContents.stream().filter(fluidStack -> fluidStack.isFluidEqual(fluid)).findFirst().orElse(null);
    }

    @Override
    public FluidStack insertFluid(FluidStack fluidStack) {
        FluidStack stack = findStack(fluidStack.liquid);
        FluidStack split = fluidStack.splitStack(Math.min(fluidStack.amount,getRemainingCapacity()));
        if(stack != null){
            stack.amount += split.amount;
        } else {
            fluidContents.add(split);
        }
        return fluidStack;
    }

    public boolean canInsertFluid(FluidStack fluidStack){
        return Math.min(fluidStack.amount,getRemainingCapacity()) > 0;
    }

    @Override
    public BlockFluid getFilter(Direction dir) {
        return fluidFilters.get(dir);
    }

    public void moveFluids(Direction dir, TileEntityFluidPipe tile) {
        if(fluidConnections.get(dir) == Connection.OUTPUT || fluidConnections.get(dir) == Connection.BOTH) {
            if( tile.getFluidInSlot(0) == null || tile.acceptedFluids.get(0).contains(fluidContents.get(0).getLiquid())){
                give(dir);
            }
            //tile.TakeFromExternal(this, tile.getFluidInSlot(0),fluidContents.get(0),amount,dir);
        } else if(fluidConnections.get(dir) == Connection.INPUT || fluidConnections.get(dir) == Connection.BOTH) {
            if( tile.getFluidInSlot(0) != null && acceptedFluids.contains(tile.getFluidInSlot(0).getLiquid())){
                take(tile.getFluidInSlot(0),dir);
            }
        }
    }

    @Override
    public void take(@NotNull FluidStack fluidStack, Direction dir) {
        if(fluidConnections.get(dir) == Connection.INPUT || fluidConnections.get(dir) == Connection.BOTH){
            TileEntity tile = dir.getTileEntity(worldObj,this);
            if(tile instanceof IFluidInventory && tile instanceof IFluidIO){
                IFluidInventory fluidInv = ((IFluidInventory) tile);
				IFluidIO fluidIO = (IFluidIO) tile;
                if(fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.OUTPUT || fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.BOTH){
                    int maxFlow = Math.min(transferSpeed,fluidInv.getTransferSpeed());
                    if(acceptedFluids.contains(fluidStack.getLiquid())){
                        if(fluidStack.isFluidEqual(fluidFilters.get(dir)) || fluidFilters.get(dir) == null) {
							int maxAmount = Math.min(fluidStack.amount, maxFlow);
                            if (canInsertFluid(new FluidStack(fluidStack.liquid, maxAmount))) {
                                FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
                                insertFluid(transferablePortion);
                            }
                        }
                    }
                }
            }
        }
    }

	@Override
	public void take(@NotNull FluidStack fluidStack, Direction dir, int slot) {
		take(fluidStack,dir);
	}

	@Override
    public void give(Direction dir) {
        if (fluidConnections.get(dir) == Connection.OUTPUT || fluidConnections.get(dir) == Connection.BOTH) {
            TileEntity tile = dir.getTileEntity(worldObj, this);
            if (tile instanceof IFluidInventory && tile instanceof IFluidIO) {
                IFluidInventory fluidInv = ((IFluidInventory) tile);
				IFluidIO fluidIO = (IFluidIO) tile;
                if (fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.INPUT || fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.BOTH) {
                    int maxFlow = Math.min(transferSpeed, fluidInv.getTransferSpeed());
                    if (tile instanceof IMassFluidInventory) {
                        IMassFluidInventory massFluidInv = (IMassFluidInventory) tile;
                        BlockFluid filter = massFluidInv.getFilter(dir.getOpposite());
                        if (filter == getFilter(dir)) {
                            FluidStack fluidStack = filter == null ? fluidContents.get(0) : findStack(filter);
                            if (fluidStack != null) {
								int maxAmount = Math.min(fluidStack.amount, maxFlow);
                                FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
                                massFluidInv.insertFluid(transferablePortion);
                            }
                        }
                    } else {
                        int otherSlot = fluidIO.getActiveFluidSlotForSide(dir.getOpposite());
						if(otherSlot == -1) return;
                        BlockFluid filter = getFilter(dir);
                        if(fluidInv.getAllowedFluidsForSlot(otherSlot).contains(filter) || filter == null){
                            if(!fluidContents.isEmpty() && fluidContents.get(0) != null){
                                FluidStack fluidStack = filter == null ? (fluidContents.get(0)) : findStack(filter);
								if(fluidStack != null){
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
        }
    }

	@Override
	public void give(Direction dir, int slot, int otherSlot) {
		if (fluidConnections.get(dir) == Connection.OUTPUT || fluidConnections.get(dir) == Connection.BOTH) {
			TileEntity tile = dir.getTileEntity(worldObj, this);
			if (tile instanceof IFluidInventory && tile instanceof IFluidIO) {
				IFluidInventory fluidInv = ((IFluidInventory) tile);
				IFluidIO fluidIO = (IFluidIO) tile;
				if (fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.INPUT || fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.BOTH) {
					int maxFlow = Math.min(transferSpeed, fluidInv.getTransferSpeed());
					if (tile instanceof IMassFluidInventory) {
						IMassFluidInventory massFluidInv = (IMassFluidInventory) tile;
						BlockFluid filter = massFluidInv.getFilter(dir.getOpposite());
						if (filter == getFilter(dir)) {
							FluidStack fluidStack = filter == null ? fluidContents.get(slot) : findStack(filter);
							if (fluidStack != null) {
								int maxAmount = Math.min(fluidStack.amount, maxFlow);
								FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
								massFluidInv.insertFluid(transferablePortion);
							}
						}
					} else {
						if(otherSlot == -1) return;
						BlockFluid filter = getFilter(dir);
						if(fluidInv.getAllowedFluidsForSlot(otherSlot).contains(filter) || filter == null){
							if(!fluidContents.isEmpty() && fluidContents.get(slot) != null){
								FluidStack fluidStack = filter == null ? (fluidContents.get(slot)) : findStack(filter);
								if(fluidStack != null){
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
		}
	}

	public void writeToNBT(CompoundTag CompoundTag1) {
        super.writeToNBT(CompoundTag1);
        ListTag nBTTagList2 = new ListTag();
        ListTag nbtTagList = new ListTag();
        CompoundTag connectionsTag = new CompoundTag();
        for(int i3 = 0; i3 < this.fluidContents.size(); ++i3) {
            if(this.fluidContents.get(i3) != null && this.fluidContents.get(i3).getLiquid() != null) {
                CompoundTag CompoundTag4 = new CompoundTag();
                CompoundTag4.putInt("Slot", i3);
                this.fluidContents.get(i3).writeToNBT(CompoundTag4);
                nbtTagList.addTag(CompoundTag4);
            }
        }
        for (Map.Entry<Direction, Connection> entry : fluidConnections.entrySet()) {
            Direction dir = entry.getKey();
            Connection con = entry.getValue();
            connectionsTag.putInt(String.valueOf(dir.ordinal()),con.ordinal());
        }
        CompoundTag1.putCompound("fluidConnections",connectionsTag);
        CompoundTag1.put("Fluids", nbtTagList);
        CompoundTag1.put("Items", nBTTagList2);
    }

    public void readFromNBT(CompoundTag CompoundTag1) {
        super.readFromNBT(CompoundTag1);

        ListTag nbtTagList = CompoundTag1.getList("Fluids");
        this.fluidContents = new ArrayList<>();

        for(int i3 = 0; i3 < nbtTagList.tagCount(); ++i3) {
            CompoundTag CompoundTag4 = (CompoundTag)nbtTagList.tagAt(i3);
            int i5 = CompoundTag4.getInteger("Slot");
            this.fluidContents.add(new FluidStack(CompoundTag4));
            //}
        }

        CompoundTag connectionsTag = CompoundTag1.getCompound("fluidConnections");
        for (Object con : connectionsTag.getValues()) {
            fluidConnections.replace(Direction.values()[Integer.parseInt(((IntTag)con).getTagName())],Connection.values()[((IntTag)con).getValue()]);
        }
    }

    @Override
    public void tick() {
        fluidContents.removeIf((F)-> F.amount <= 0);
    }

	@Override
	public int getActiveFluidSlotForSide(Direction dir) {
		return 0;
	}

	@Override
	public Connection getFluidIOForSide(Direction dir) {
		return fluidConnections.get(dir);
	}

	@Override
	public void setFluidIOForSide(Direction dir, Connection con) {
		fluidConnections.put(dir,con);
	}
}
