package sunsetsatellite.catalyst.energy.electric.example.tile;

import net.minecraft.core.block.Block;
import sunsetsatellite.catalyst.energy.electric.base.TileEntityElectricDevice;
import sunsetsatellite.catalyst.energy.electric.example.block.BlockElectric;

public class TileEntitySimpleElectricMachine extends TileEntityElectricDevice{
	@Override
	public void init(Block block) {
		super.init(block);
		maxAmpsOut = 0;
		maxAmpsIn = 1;
		maxVoltageIn = getTier((BlockElectric) block).maxVoltage;
		maxVoltageOut = 0;
		capacity = getTier((BlockElectric) block).maxVoltage * 64L;
	}

	@Override
	public void tick() {
		super.tick();
		internalRemoveEnergy(16);
	}

	@Override
	public void onOvervoltage(long voltage) {

	}
}
