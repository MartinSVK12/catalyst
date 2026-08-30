package sunsetsatellite.catalyst.fluids.impl.tile;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.IntTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.io.IFluidIO;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;
import sunsetsatellite.catalyst.fluids.api.IFluidInventory;
import sunsetsatellite.catalyst.fluids.api.IFluidTransfer;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class TileEntityFluidContainer extends TileEntity
	implements IFluidInventory, IFluidTransfer, IFluidIO {

	public FluidStack[] fluidContents = new FluidStack[1];
	public int[] fluidCapacity = new int[1];
	public ArrayList<ArrayList<Fluid>> acceptedFluids = new ArrayList<>(fluidContents.length);

	public int transferSpeed = 20;

	public HashMap<Direction, Connection> fluidConnections = new HashMap<>();
	public HashMap<Direction, Integer> activeFluidSlots = new HashMap<>();

	public TileEntityFluidContainer() {
		for (Direction dir : Direction.values()) {
			fluidConnections.put(dir, Connection.NONE);
			activeFluidSlots.put(dir, 0);
		}
		for (FluidStack ignored : fluidContents) {
			acceptedFluids.add(new ArrayList<>());
		}
	}

	@Override
	public void take(@NotNull FluidStack fluidStack, Direction dir) {
		if (getFluidIOForSide(dir) == Connection.INPUT || getFluidIOForSide(dir) == Connection.BOTH) {
			TileEntity tile = dir.getTileEntity(worldObj, this);
			if (tile instanceof IFluidInventory fluidInv && tile instanceof IFluidIO fluidIO) {
				if (fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.OUTPUT || fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.BOTH) {
					int maxFlow = Math.min(transferSpeed, fluidInv.getTransferSpeed());
					int slot = getActiveFluidSlotForSide(dir);
					if (slot == -1) return;
					if (getAllowedFluidsForSlot(slot).contains(fluidStack.fluid)) {
						int maxAmount = Math.min(fluidStack.amount, maxFlow);
						if (canInsertFluid(slot, new FluidStack(fluidStack.fluid, maxAmount))) {
							FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
							if (getFluidInSlot(slot) == null) {
								setFluidInSlot(slot,transferablePortion);
							} else {
								getFluidInSlot(slot).amount += transferablePortion.amount;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void take(@NotNull FluidStack fluidStack, Direction dir, int slot) {
		if (getFluidIOForSide(dir) == Connection.INPUT || getFluidIOForSide(dir) == Connection.BOTH) {
			TileEntity tile = dir.getTileEntity(worldObj, this);
			if (tile instanceof IFluidInventory fluidInv) {
				IFluidIO fluidIO = (IFluidIO) tile;
				if (fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.OUTPUT || fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.BOTH) {
					int maxFlow = Math.min(transferSpeed, fluidInv.getTransferSpeed());
					if (slot == -1) return;
					if (getAllowedFluidsForSlot(slot).contains(fluidStack.fluid)) {
						int maxAmount = Math.min(fluidStack.amount, maxFlow);
						if (canInsertFluid(slot, new FluidStack(fluidStack.fluid, maxAmount))) {
							FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
							if (getFluidInSlot(slot) == null) {
								setFluidInSlot(slot,transferablePortion);
							} else {
								getFluidInSlot(slot).amount += transferablePortion.amount;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void give(Direction dir) {
		int slot = getActiveFluidSlotForSide(dir);
		if (slot == -1) return;
		FluidStack fluidStack = getFluidInSlot(slot);
		if (getFluidIOForSide(dir) == Connection.OUTPUT || getFluidIOForSide(dir) == Connection.BOTH) {
			TileEntity tile = dir.getTileEntity(worldObj, this);
			if (tile instanceof IFluidInventory fluidInv && tile instanceof IFluidIO fluidIO) {
				if (fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.INPUT || fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.BOTH) {
					int maxFlow = Math.min(transferSpeed, fluidInv.getTransferSpeed());
					int otherSlot = fluidIO.getActiveFluidSlotForSide(dir.getOpposite());
					if (otherSlot == -1) return;
					if (fluidInv.getAllowedFluidsForSlot(otherSlot).contains(fluidStack.fluid)) {
						int maxAmount = Math.min(fluidStack.amount, maxFlow);
						maxAmount = Math.min(maxAmount, fluidInv.getRemainingCapacity(otherSlot));
						if (fluidInv.canInsertFluid(otherSlot, new FluidStack(fluidStack.fluid, maxAmount))) {
							FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
							fluidInv.insertFluid(otherSlot, transferablePortion);
						}
					}
					//}
				}
			}
		}
	}

	@Override
	public void give(Direction dir, int slot, int otherSlot) {
		if (slot == -1) return;
		FluidStack fluidStack = getFluidInSlot(slot);
		if (getFluidIOForSide(dir) == Connection.OUTPUT || getFluidIOForSide(dir) == Connection.BOTH) {
			TileEntity tile = dir.getTileEntity(worldObj, this);
			if (tile instanceof IFluidInventory fluidInv && tile instanceof IFluidIO fluidIO) {
				if (fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.INPUT || fluidIO.getFluidIOForSide(dir.getOpposite()) == Connection.BOTH) {
					int maxFlow = Math.min(transferSpeed, fluidInv.getTransferSpeed());
					if (otherSlot == -1) return;
					if (fluidInv.getAllowedFluidsForSlot(otherSlot).contains(fluidStack.fluid)) {
						int maxAmount = Math.min(fluidStack.amount, maxFlow);
						if (fluidInv.canInsertFluid(otherSlot, new FluidStack(fluidStack.fluid, maxAmount))) {
							FluidStack transferablePortion = fluidStack.splitStack(maxAmount);
							fluidInv.insertFluid(otherSlot, transferablePortion);
						}
					}
					//}
				}
			}
		}
	}

	@Override
	public FluidStack insertFluid(int slot, FluidStack fluidStack) {
		FluidStack stack = fluidContents[slot];
		FluidStack split = fluidStack.splitStack(Math.min(fluidStack.amount, getRemainingCapacity(slot)));
		if (stack != null && split.amount > 0) {
			fluidContents[slot].amount += split.amount;
		} else {
			fluidContents[slot] = split;
		}
		return fluidStack;
	}

	@Override
	public int getRemainingCapacity(int slot) {
		if (fluidContents[slot] == null) {
			return fluidCapacity[slot];
		}
		return fluidCapacity[slot] - fluidContents[slot].amount;
	}

	@Override
	public boolean canInsertFluid(int slot, FluidStack fluidStack) {
		if (getFluidInSlot(slot) != null) if (!getFluidInSlot(slot).isFluidEqual(fluidStack)) return false;
		return Math.min(fluidStack.amount, getRemainingCapacity(slot)) > 0;
	}

	public void readAdditionalData(@NotNull CompoundTag tag) {
		ListTag nbtTagList = tag.getList("Fluids");
		this.fluidContents = new FluidStack[this.getFluidInventorySize()];

		for (int i3 = 0; i3 < nbtTagList.tagCount(); ++i3) {
			CompoundTag CompoundTag4 = (CompoundTag) nbtTagList.tagAt(i3);
			int i5 = CompoundTag4.getByte("Slot") & 255;
			if (i5 < this.fluidContents.length) {
				this.fluidContents[i5] = new FluidStack(CompoundTag4);
			}
		}

		CompoundTag connectionsTag = tag.getCompound("fluidConnections");
		for (Object con : connectionsTag.getValues()) {
			fluidConnections.replace(Direction.values()[Integer.parseInt(((IntTag) con).getTagName())], Connection.values()[((IntTag) con).getValue()]);
		}

		CompoundTag activeFluidSlotsTag = tag.getCompound("fluidActiveSlots");
		for (Object con : activeFluidSlotsTag.getValues()) {
			activeFluidSlots.replace(Direction.values()[Integer.parseInt(((IntTag) con).getTagName())], ((IntTag) con).getValue());
		}

	}

	@Override
	public void tick() {
		if(!(this instanceof TileEntityFluidPipe)){
			extractFluids();
		}
		super.tick();
	}

	public void writeAdditionalData(@NonNull CompoundTag tag) {
		ListTag nBTTagList2 = new ListTag();
		ListTag nbtTagList = new ListTag();
		CompoundTag connectionsTag = new CompoundTag();
		CompoundTag activeFluidSlotsTag = new CompoundTag();
		for (int i3 = 0; i3 < this.fluidContents.length; ++i3) {
			if (this.fluidContents[i3] != null && this.fluidContents[i3].fluid != null) {
				CompoundTag CompoundTag4 = new CompoundTag();
				CompoundTag4.putByte("Slot", (byte) i3);
				this.fluidContents[i3].writeToNBT(CompoundTag4);
				nbtTagList.addTag(CompoundTag4);
			}
		}
		for (Map.Entry<Direction, Integer> entry : activeFluidSlots.entrySet()) {
			Direction dir = entry.getKey();
			activeFluidSlotsTag.putInt(String.valueOf(dir.ordinal()), entry.getValue());
		}
		for (Map.Entry<Direction, Connection> entry : fluidConnections.entrySet()) {
			Direction dir = entry.getKey();
			Connection con = entry.getValue();
			connectionsTag.putInt(String.valueOf(dir.ordinal()), con.ordinal());
		}
		tag.putCompound("fluidConnections", connectionsTag);
		tag.putCompound("fluidActiveSlots", activeFluidSlotsTag);
		tag.put("Fluids", nbtTagList);
		tag.put("Items", nBTTagList2);
	}

	@Override
	public FluidStack getFluidInSlot(int slot) {
		if (this.fluidContents.length == 0) return null;
		if (this.fluidContents[slot] == null || this.fluidContents[slot].fluid == null || this.fluidContents[slot].amount == 0) {
			this.fluidContents[slot] = null;
		}
		return fluidContents[slot];
	}

	@Override
	public int getFluidCapacityForSlot(int slot) {
		return fluidCapacity[slot];
	}

	@Override
	public ArrayList<Fluid> getAllowedFluidsForSlot(int slot) {
		return acceptedFluids.get(slot);
	}

	@Override
	public void setFluidInSlot(int slot, FluidStack fluid) {
		if (fluid == null || fluid.amount == 0 || fluid.fluid == null) {
			this.fluidContents[slot] = null;
			this.onFluidInventoryChanged();
			return;
		}
		if (acceptedFluids.get(slot).contains(fluid.fluid) || acceptedFluids.get(slot).isEmpty()) {
			this.fluidContents[slot] = fluid;
			this.onFluidInventoryChanged();
		}

	}

	@Override
	public int getFluidInventorySize() {
		return fluidContents.length;
	}

	@Override
	public void onFluidInventoryChanged() {
		if (this.worldObj != null) {
			this.worldObj.updateTileEntityChunkAndSendToPlayer(tilePos, this);
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}

	@Override
	public int getTransferSpeed() {
		return transferSpeed;
	}

	public void moveFluids(Direction dir, TileEntityFluidPipe tile) {
		if (EnvironmentHelper.isMultiplayerClient()) return;
		int activeSlot = activeFluidSlots.get(dir);
		if (activeSlot == -1) return;
		if (getFluidIOForSide(dir) == Connection.BOTH || getFluidIOForSide(dir) == Connection.OUTPUT) {
			if (getFluidInSlot(activeSlot) != null) {
				give(dir);
			}
		} else if (getFluidIOForSide(dir) == Connection.BOTH || getFluidIOForSide(dir) == Connection.INPUT) {
			if (tile.getFluidInSlot(0) != null) {
				take(tile.getFluidInSlot(0), dir);
			}
		}
	}

	public void extractFluids() {
		if (EnvironmentHelper.isMultiplayerClient()) return;
		for (Map.Entry<Direction, Connection> e : fluidConnections.entrySet()) {
			Direction dir = e.getKey();
			TileEntity tile = dir.getTileEntity(worldObj, this);
			if (tile instanceof TileEntityFluidPipe) {
				moveFluids(dir, (TileEntityFluidPipe) tile);
				((TileEntityFluidPipe) tile).rememberTicks = 100;
			}
		}
	}

	@Override
	public int getActiveFluidSlotForSide(Direction dir) {
		return activeFluidSlots.get(dir);
	}

	@Override
	public Connection getFluidIOForSide(Direction dir) {
		return fluidConnections.get(dir);
	}

	@Override
	public void setFluidIOForSide(Direction dir, Connection con) {
		fluidConnections.put(dir, con);
	}

	public Vec3i getPosition() {
		return new Vec3i(tilePos);
	}

	@Override
	public void cycleFluidIOForSide(Direction dir) {
		switch (fluidConnections.get(dir)) {
			case NONE:
				fluidConnections.replace(dir, Connection.INPUT);
				break;
			case INPUT:
				fluidConnections.replace(dir, Connection.OUTPUT);
				break;
			case OUTPUT:
				fluidConnections.replace(dir, Connection.BOTH);
				break;
			case BOTH:
				fluidConnections.replace(dir, Connection.NONE);
				break;
		}
	}

	@Override
	public void cycleActiveFluidSlotForSide(Direction dir, boolean backwards) {
		int i = activeFluidSlots.get(dir);
		if (!backwards) {
			if (i < getFluidInventorySize() - 1) {
				activeFluidSlots.replace(dir, i + 1);
			} else {
				activeFluidSlots.replace(dir, -1);
			}
		} else {
			if (i > -1) {
				activeFluidSlots.replace(dir, i - 1);
			} else {
				activeFluidSlots.replace(dir, getFluidInventorySize() - 1);
			}
		}
	}

	@Override
	public void setActiveFluidSlotForSide(Direction dir, int slot) {
		activeFluidSlots.replace(dir, slot);
	}
}
