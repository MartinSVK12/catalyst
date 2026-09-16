package sunsetsatellite.catalyst.fluids.impl.tile;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.IntTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.AveragingCounter;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.DirectionMap;
import sunsetsatellite.catalyst.core.util.io.IFluidIO;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;
import sunsetsatellite.catalyst.fluids.api.IFluidInventory;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TileEntityFluidPipe extends TileEntity implements IFluidIO {

	public AveragingCounter averageFlow = new AveragingCounter();
	public float size = 0.3f;

	public ArrayList<ArrayList<Fluid>> acceptedFluids = new ArrayList<>(1);
	public DirectionMap<Connection> fluidConnections = new DirectionMap<>(Connection.BOTH);

	public static final int DEFAULT_TRAVEL_DELAY = 12;
	public static final int DEFAULT_INPUT_TICKS = 60;
	public static final int DEFAULT_OUTPUT_TICKS = 80;
	public static final int DEFAULT_OUTPUT_COOLDOWN_TICKS = 30;

	public int fluidCapacity = 250;
	public int flowRate = 20;
	public int travelDelay = DEFAULT_TRAVEL_DELAY;
	public int outputCooldown = DEFAULT_OUTPUT_COOLDOWN_TICKS;
	public int inputTicks = DEFAULT_INPUT_TICKS;
	public int outputTicks = DEFAULT_OUTPUT_TICKS;
	public int counter = 0;

	public final DirectionMap<Connection> internalConnections = new DirectionMap<>(Connection.NONE);
	// how many ticks does a side stay in input mode
	public final DirectionMap<Integer> inputActiveTicks = new DirectionMap<>(0);
	// how many ticks does a side stay in output mode
	public DirectionMap<Integer> outputActiveTicks;
	// how many ticks will a side NOT switch to output mode
	public DirectionMap<Integer> outputCooldownTicks;

	public Map<Orientation, Section> sections = new HashMap<>();
	public Fluid fluid;

	public TileEntityFluidPipe() {
		for (Orientation o : Orientation.values()) {
			sections.put(o, new Section());
		}
		acceptedFluids.add(new ArrayList<>());
		acceptedFluids.get(0).addAll(Fluid.fluidMap.values());
		outputCooldownTicks = new DirectionMap<>(outputCooldown);
		outputActiveTicks = new DirectionMap<>(outputTicks);
	}

	public enum Orientation {
		EAST(Direction.X_POS), WEST(Direction.X_NEG), UP(Direction.Y_POS), DOWN(Direction.Y_NEG), SOUTH(Direction.Z_POS), NORTH(Direction.Z_NEG), CENTER(null);

		public final Direction dir;

		Orientation(Direction dir){
			this.dir = dir;
		}

		public static Orientation fromDir(Direction dir){
			return dir == null ? CENTER : Orientation.values()[dir.ordinal()];
		}
	}

	public class Section {
		public int amount;

		public int counter = 0;
		// amount that is currently being transferred, inaccessible
		private int[] transferring = new int[travelDelay];

		public int fill(int fillAmount, boolean simulate){
			int fill = Math.min(Math.max(0,fillAmount), getMaxFillRate());
			if(!simulate){
				transferring[counter] += fill;
				this.amount += fill;
			}
			return fill;
		}

		public int drain(int drainAmount, boolean simulate){
			int drain = (int) Math.max(0,Catalyst.multiMin(getAmount(), drainAmount, getFlowRate()));
			if(!simulate){
				this.amount -= drain;
			}
			return drain;
		}

		public void finishTransfer(){
			transferring[counter] = 0;
		}

		public int getAmount() {
			int sum = 0;
			for (int i : transferring) {
				sum += i;
			}
			return amount - sum;
		}

		public int getMaxFillRate(){
			return Math.max(0, Math.min(getCapacity() - amount, getFlowRate() - transferring[counter]));
		}

		public void readFromNbt(CompoundTag compoundTag) {
			this.amount = compoundTag.getShort("amount");

			ListTag list = compoundTag.getList("transferring");
			for (int i = 0; i < travelDelay; i++) {
				if(i < list.tagCount()){
					transferring[i] = (int) list.tagAt(i).getValue();
				}
			}
		}

		public void writeToNbt(CompoundTag subTag) {
			subTag.putShort("amount", (short) amount);

			ListTag list = new ListTag();
			for (int i = 0; i < travelDelay; i++) {
				list.addTag(new IntTag(transferring[i]));
			}
			subTag.put("transferring", list);
		}
	}

	public int getCapacity() {
		return fluidCapacity;
	}

	public int getFlowRate() {
		return flowRate;
	}

	@Override
	public void tick() {
		super.tick();
		if(worldObj == null) return;
		if(EnvironmentHelper.isMultiplayerClient()) return;
		counter++;
		if(counter >= travelDelay || counter < 0){
			counter = 0;
		}
		moveFluids();
		averageFlow.set(worldObj, getFluidAmount());
	}

	private void moveFluids() {
		if(worldObj == null) return;
		if(fluid != null){
			int outputs = update(counter);
			if(fluid != null){
				moveOutside(outputs);
				moveFromCenter();
				moveToCenter();
			}
		} else {
			updateEmpty();
		}
	}

	private void moveFromCenter() {
		int pushAmount = sections.get(Orientation.CENTER).amount;
		int totalAvailable = sections.get(Orientation.CENTER).getAmount();
		if (totalAvailable < 1 || pushAmount < 1) {
			return;
		}

		int testAmount = getFlowRate();
		List<Direction> dirs = new ArrayList<>();
		for (Direction direction : Direction.values()) {
			if (internalConnections.get(direction) == Connection.OUTPUT) {
				dirs.add(direction);
			}
		}

		if(!dirs.isEmpty()){
			float min = Math.min(getFlowRate() * dirs.size(), totalAvailable) / (float) getFlowRate() / dirs.size();
			for (Direction dir : dirs) {
				int available = sections.get(Orientation.CENTER).fill(testAmount, true);
				int amountToPush = (int) (available * min);
				if (amountToPush < 1) {
					amountToPush = 1;
				}

				amountToPush = sections.get(Orientation.CENTER).drain(amountToPush, true);
				if (amountToPush > 0) {
					int filled = sections.get(Orientation.fromDir(dir)).fill(amountToPush, false);
					sections.get(Orientation.CENTER).drain(filled, false);
				}
			}
		}
	}

	private void moveToCenter() {
		int inputs = 0;
		int capacityRemaining = getCapacity() - sections.get(Orientation.CENTER).amount;
		DirectionMap<Integer> inputPerTick = new DirectionMap<>(0);

		for (Direction dir : Direction.values()) {
			inputPerTick.put(dir, 0);
			if(internalConnections.get(dir) != Connection.OUTPUT){
				inputPerTick.put(dir, sections.get(Orientation.fromDir(dir)).drain(getFlowRate(), true));
				inputs++;
			}
		}

		float min = Math.min(getFlowRate() * inputs, capacityRemaining) / (float) getFlowRate() / inputs;

		for (Direction dir : Direction.values()) {
			if(internalConnections.get(dir) != Connection.OUTPUT && inputPerTick.get(dir) > 0){
				int drain = (int) (inputPerTick.get(dir) * min);
				if (drain < 1) {
					drain++;
				}

				int amountToPush = sections.get(Orientation.fromDir(dir)).drain(drain, true);
				if (amountToPush > 0) {
					int filled = sections.get(Orientation.CENTER).fill(amountToPush, false);
					sections.get(Orientation.fromDir(dir)).drain(filled, false);
				}
			}
		}
	}

	private void moveOutside(int outputs) {
		if(worldObj == null) return;
		if(outputs > 0){
			for (Direction dir : Direction.values()) {
				if(internalConnections.get(dir) == Connection.OUTPUT){
					if (dir.getTileEntity(worldObj, this) instanceof TileEntityFluidPipe pipe) {
						Section section = sections.get(Orientation.fromDir(dir));
						FluidStack liquidToPush = new FluidStack(fluid, section.drain(getFlowRate(), true));
						if(liquidToPush.amount > 0){
							int originalAmount = liquidToPush.amount;
							FluidStack resultStack = pipe.insertFluid(liquidToPush, dir.getOpposite());
							int filled = resultStack == null ? originalAmount : originalAmount - resultStack.amount;

							if (filled <= 0) {
								outputActiveTicks.put(dir, outputActiveTicks.get(dir)-1);
							} else {
								section.drain(filled, false);
							}
						}
					} else if(dir.getTileEntity(worldObj, this) instanceof TileEntityFluidContainer fluidInv){
						Section section = sections.get(Orientation.fromDir(dir));
						FluidStack liquidToPush = new FluidStack(fluid, section.drain(getFlowRate(), true));
						if (liquidToPush.amount > 0) {
							int originalAmount = liquidToPush.amount;
							fluidInv.take(liquidToPush, dir.getOpposite());
							int filled = originalAmount - liquidToPush.amount;

							if (filled <= 0) {
								outputActiveTicks.put(dir, outputActiveTicks.get(dir)-1);
							} else {
								section.drain(filled, false);
							}
						}
					}
				}
			}
		}
	}

	public FluidStack insertFluid(FluidStack stack, Direction direction) {
		if(stack == null) return null;
		if(!acceptedFluids.get(0).contains(stack.fluid)) return stack;
		int filled = fill(direction, stack, false);

		if (filled >= stack.amount) {
			return null;
		} else {
			FluidStack newStack = stack.copy();
			newStack.amount -= filled;
			return newStack;
		}
	}

	public int fill(Direction dir, FluidStack stack, boolean simulate) {

		if (!inputOpen(dir)) {
			return 0;
		}

		if (stack == null || (fluid != null && ( !stack.fluid.equals(fluid) || !acceptedFluids.get(0).contains(stack.fluid) ))) {
			return 0;
		}

		int filled = sections.get(Orientation.fromDir(dir)).fill(stack.amount, simulate);

		if (!simulate && filled > 0) {
			if (fluid == null) {
				setFluid(stack.fluid);
			}

			internalConnections.put(dir, Connection.INPUT);
			inputActiveTicks.put(dir, inputTicks);
		}

		return filled;
	}

	private int update(int counter) {
		int availableOutputs = 0;
		int fluidAmount = 0;
		for (Orientation or : Orientation.values()) {
			Section section = sections.get(or);
			fluidAmount += section.amount;
			section.counter = counter;
			section.finishTransfer();

			if(or == Orientation.CENTER) continue;

			Direction dir = or.dir;

			if(internalConnections.get(dir) == Connection.INPUT){
				inputActiveTicks.put(dir, inputActiveTicks.get(dir) - 1);
				if(inputActiveTicks.get(dir) <= 0){
					internalConnections.put(dir, Connection.NONE);
				}
				continue;
			}
			if (!outputOpen(dir)) {
				internalConnections.put(dir, Connection.NONE);
				continue;
			}
			if (outputCooldownTicks.get(dir) > 0) {
				outputCooldownTicks.put(dir, outputCooldownTicks.get(dir) - 1);
				continue;
			}
			if (outputActiveTicks.get(dir) <= 0) {
				internalConnections.put(dir, Connection.NONE);
				outputCooldownTicks.put(dir, outputCooldown);
				outputActiveTicks.put(dir, outputTicks);
				continue;
			}
			if (isPipeConnected(dir) && outputOpen(dir)) {
				internalConnections.put(dir, Connection.OUTPUT);
				availableOutputs++;
			}
		}

		if(fluidAmount == 0){
			setFluidToNull();
		}
		return availableOutputs;
	}

	public boolean isPipeConnected(Direction dir) {
		if(worldObj == null) return false;
		TileEntity tile = dir.getTileEntity(worldObj, this);
		return tile instanceof TileEntityFluidPipe || tile instanceof IFluidInventory;
	}

	private boolean outputOpen(Direction dir) {
		return getFluidIOForSide(dir) == Connection.OUTPUT || getFluidIOForSide(dir) == Connection.BOTH;
	}

	private boolean inputOpen(Direction dir) {
		return getFluidIOForSide(dir) == Connection.INPUT || getFluidIOForSide(dir) == Connection.BOTH;
	}

	private void updateEmpty() {
		for (Direction dir : Direction.values()) {
			if(internalConnections.get(dir) == Connection.INPUT){
				if(inputActiveTicks.get(dir) > 0){
					inputActiveTicks.put(dir, inputActiveTicks.get(dir) - 1);
				} else {
					internalConnections.put(dir, Connection.NONE);
				}
			}

			if(outputCooldownTicks.get(dir) > 0){
				outputCooldownTicks.put(dir, outputCooldownTicks.get(dir) - 1);
			}
		}
	}

	private void setFluidToNull(){
		fluid = null;
	}

	private void setFluid(Fluid fluid) {
		this.fluid = fluid;
	}

	private void setFluid(FluidStack stack) {
		fluid = stack.fluid;
	}

	public static int getDirOrdinal(Direction dir){
		if(dir == null) return 6;
		return dir.ordinal();
	}

	public int getFluidAmount(){
		int amount = 0;
		for (Orientation value : Orientation.values()) {
			if(sections.get(value) != null){
				amount += sections.get(value).getAmount();
			}
		}
		return amount;
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
		fluidConnections.put(dir, con);
	}

	public Vec3i getPosition() {
		return new Vec3i(tilePos);
	}

	@Override
	public void cycleFluidIOForSide(Direction dir) {
		switch (fluidConnections.get(dir)) {
			case NONE -> fluidConnections.replace(dir, Connection.INPUT);
			case INPUT -> fluidConnections.replace(dir, Connection.OUTPUT);
			case OUTPUT -> fluidConnections.replace(dir, Connection.BOTH);
			case BOTH -> fluidConnections.replace(dir, Connection.NONE);
		}
	}

	@Override
	public void cycleActiveFluidSlotForSide(Direction dir, boolean backwards) {

	}

	@Override
	public void setActiveFluidSlotForSide(Direction dir, int slot) {

	}

	public void readAdditionalData(@NotNull CompoundTag tag) {
		CompoundTag connectionsTag = tag.getCompound("fluidConnections");
		for (Object con : connectionsTag.getValues()) {
			fluidConnections.replace(Direction.values()[Integer.parseInt(((IntTag) con).getTagName())], Connection.values()[((IntTag) con).getValue()]);
		}
		if (tag.containsKey("fluid")) {
			try {
				setFluid(Fluid.fluidMap.get(NamespaceID.fromPool(tag.getString("fluid"))));
			} catch (HardIllegalArgumentException ignored) {
				setFluidToNull();
			}
		} else {
			setFluidToNull();
		}

		for (Orientation or : Orientation.values()) {
			if (tag.containsKey("section[" + or.ordinal() + "]")) {
				CompoundTag compound = tag.getCompound("section[" + or.ordinal() + "]");
				if (compound.containsKey("fluid")) {
					FluidStack stack = new FluidStack(compound);
					if (fluid == null) {
						setFluid(stack);
					}
					if (stack.fluid.equals(fluid)) {
						sections.get(or).readFromNbt(compound);
					}
				} else {
					sections.get(or).readFromNbt(compound);
				}
			}
			if (or != Orientation.CENTER) {
				internalConnections.put(or.dir, Connection.values()[tag.getShort("internalConnection[" + or.ordinal() + "]")]);
			}
		}
	}

	public void writeAdditionalData(@NotNull CompoundTag tag) {
		CompoundTag connectionsTag = new CompoundTag();
		for (Map.Entry<Direction, Connection> entry : fluidConnections.entrySet()) {
			Direction dir = entry.getKey();
			Connection con = entry.getValue();
			connectionsTag.putInt(String.valueOf(dir.ordinal()), con.ordinal());
		}
		tag.putCompound("fluidConnections", connectionsTag);

		if (fluid != null) {
			tag.putString("fluid", fluid.id.toString());

			for (Orientation or : Orientation.values()) {
				CompoundTag subTag = new CompoundTag();
				sections.get(or).writeToNbt(subTag);
				tag.put("section[" + or.ordinal() + "]", subTag);
				if (or != Orientation.CENTER) {
					tag.putShort("internalConnection[" + or.ordinal() + "]", (short) internalConnections.get(or.dir).ordinal());
				}
			}
		}
	}
}
