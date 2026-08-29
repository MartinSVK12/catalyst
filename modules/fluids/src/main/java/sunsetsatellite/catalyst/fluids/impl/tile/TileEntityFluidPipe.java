package sunsetsatellite.catalyst.fluids.impl.tile;


import net.minecraft.core.block.entity.TileEntity;
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
			float fill = Math.min((float) intFluid.amount / getFluidCapacityForSlot(0), 1);
			if(fill >= 1){
				neighbors.forEach((side, tile) -> {
					if (tile instanceof TileEntityFluidPipe otherPipe){
						give(side);
					}
				});
			}
		}
	}
}
