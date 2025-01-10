package sunsetsatellite.catalyst.energy.simple.test.block;

import net.minecraft.core.block.entity.TileEntity;
import sunsetsatellite.catalyst.core.util.network.NetworkType;
import sunsetsatellite.catalyst.energy.simple.test.tile.TileEntityBatteryBox;

public class BlockBatteryBox extends BlockEnergy {
	public BlockBatteryBox(String key, int id) {
		super(key, id);
	}

	@Override
	protected TileEntity getNewBlockEntity() {
		return new TileEntityBatteryBox();
	}

	@Override
	public NetworkType getType() {
		return NetworkType.CATALYST_ENERGY;
	}


}
