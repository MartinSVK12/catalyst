package sunsetsatellite.catalyst.energy.electric.example.tile;

import net.minecraft.core.block.Block;
import sunsetsatellite.catalyst.energy.electric.base.TileEntityElectricConductor;
import sunsetsatellite.catalyst.energy.electric.example.block.BlockCable;

public class TileEntityCable extends TileEntityElectricConductor {

	@Override
	public void init(Block block) {
		properties = ((BlockCable) block).properties;
		voltageRating = properties.getMaterial().getMaxVoltage().maxVoltage;
		ampRating = (long) properties.getSize() * properties.getMaterial().getDefaultAmps();
	}

	@Override
	public void onOvercurrent() {

	}

	@Override
	public void onOvervoltage(long voltage) {

	}
}

