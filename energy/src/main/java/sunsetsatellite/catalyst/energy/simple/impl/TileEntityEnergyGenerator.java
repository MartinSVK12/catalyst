package sunsetsatellite.catalyst.energy.simple.impl;

import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Direction;

public abstract class TileEntityEnergyGenerator extends TileEntityEnergyBase {
	@Override
	public boolean canReceive(@NotNull Direction dir) {
		return false;
	}

	@Override
	public boolean canProvide(@NotNull Direction dir) {
		return true;
	}

	@Override
	public long receiveEnergy(@NotNull Direction dir, long energy) {
		return 0;
	}
}
