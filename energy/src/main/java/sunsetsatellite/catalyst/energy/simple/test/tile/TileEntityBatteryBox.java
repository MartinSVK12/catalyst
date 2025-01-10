package sunsetsatellite.catalyst.energy.simple.test.tile;

import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.energy.simple.impl.TileEntityEnergyDevice;

public class TileEntityBatteryBox extends TileEntityEnergyDevice {

	public TileEntityBatteryBox() {
		capacity = 8192;
		maxProvide = 48;
		maxReceive = 64;
	}

	@Override
	public boolean canProvide(@NotNull Direction dir) {
		return true;
	}
}
