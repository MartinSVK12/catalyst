package sunsetsatellite.catalyst.energy.simple.test.block;

import net.minecraft.core.block.entity.TileEntity;
import sunsetsatellite.catalyst.core.util.network.NetworkType;
import sunsetsatellite.catalyst.energy.simple.test.tile.TileEntitySimpleGenerator;

public class BlockSimpleGenerator extends BlockEnergy {
	public BlockSimpleGenerator(String key, int id) {
		super(key, id);
	}

	@Override
	protected TileEntity getNewBlockEntity() {
		return new TileEntitySimpleGenerator();
	}

	@Override
	public NetworkType getType() {
		return NetworkType.CATALYST_ENERGY;
	}
}
