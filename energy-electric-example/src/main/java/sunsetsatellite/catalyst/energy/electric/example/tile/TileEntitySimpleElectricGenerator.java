package sunsetsatellite.catalyst.energy.electric.example.tile;

import net.minecraft.core.block.Block;
import sunsetsatellite.catalyst.energy.electric.base.TileEntityElectricGenerator;
import sunsetsatellite.catalyst.energy.electric.example.block.BlockElectric;

public class TileEntitySimpleElectricGenerator extends TileEntityElectricGenerator {
	@Override
	public void init(Block block) {
		super.init(block);
		maxAmpsOut = 1;
		maxAmpsIn = 0;
		maxVoltageIn = 0;
		maxVoltageOut = getTier((BlockElectric) block).maxVoltage;
		capacity = getTier((BlockElectric) block).maxVoltage * 64L;
	}

	@Override
	public void tick() {
		internalAddEnergy(maxVoltageOut * maxAmpsOut);
		super.tick();
	}

	@Override
	public void onOvervoltage(long voltage) {

	}
}
