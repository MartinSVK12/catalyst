package sunsetsatellite.catalyst.energy.electric.base;

import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Direction;

public abstract class TileEntityElectricGenerator extends TileEntityElectricBase {

	@Override
	public void tick() {
		super.tick();
		ampsUsing = 0;
	}

	@Override
	public boolean canReceive(@NotNull Direction dir) {
		return false;
	}

	@Override
	public boolean canProvide(@NotNull Direction dir) {
		return true;
	}

	@Override
	public long receiveEnergy(@NotNull Direction dir, long amperage) {
		return 0;
	}

}
