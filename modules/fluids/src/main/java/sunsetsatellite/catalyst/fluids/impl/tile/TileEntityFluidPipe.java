package sunsetsatellite.catalyst.fluids.impl.tile;


import net.minecraft.core.block.entity.TileEntity;
import sunsetsatellite.catalyst.core.util.AveragingCounter;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.util.HashMap;

public abstract class TileEntityFluidPipe extends TileEntityFluidContainer {
	public TileEntityFluidContainer last = null;

	public float size = 0.5f;

	public int rememberTicks = 0;
	public int maxRememberTicks = 100;

	public AveragingCounter averageFlow = new AveragingCounter();

	public TileEntityFluidPipe() {
		fluidCapacity[0] = 2000;
		transferSpeed = 20;
		for (Direction dir : Direction.values()) {
			fluidConnections.put(dir, Connection.BOTH);
			activeFluidSlots.put(dir, 0);
		}
		acceptedFluids.get(0).addAll(Fluid.fluidMap.values());

	}

	@Override
	public void tick() {
		super.tick();
		if(getFluidInSlot(0) != null){
			averageFlow.set(worldObj, getFluidInSlot(0).amount);
		}
		rememberTicks++;
		if (rememberTicks >= maxRememberTicks) {
			rememberTicks = 0;
			last = null;
		}
		HashMap<Direction, TileEntity> neighbors = new HashMap<>();
		for (Direction dir : Direction.values()) {
			neighbors.put(dir, dir.getTileEntity(worldObj, this));
		}
		FluidStack intFluid = getFluidInSlot(0);
		if(intFluid != null){
			neighbors.forEach((side, tile) -> {
				if (tile instanceof TileEntityFluidPipe otherPipe){
					give(side);
				}
			});
		}
		/*neighbors.forEach((side, tile) -> {
			if (tile instanceof TileEntityFluidPipe inv && !tile.equals(last)) {
				Integer activeSlot = inv.activeFluidSlots.get(side.getOpposite());
				FluidStack intFluid = getFluidInSlot(0);
				FluidStack extFluid = inv.getFluidInSlot(activeSlot);
				if (intFluid != null && extFluid == null) {
					last = inv;
					inv.last = this;
					give(side);
				} else if (intFluid == null && extFluid != null) {
					last = inv;
					inv.last = this;
					take(extFluid, side);
				} else if (intFluid != null) { //if both internal and external aren't null
					last = inv;
					inv.last = this;
					if (intFluid.amount < extFluid.amount) {
						take(extFluid, side);
					} else {
						give(side);
					}
				}
			}
		});*/
	}
}
