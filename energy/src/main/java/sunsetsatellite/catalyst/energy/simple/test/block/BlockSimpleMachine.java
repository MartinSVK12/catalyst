package sunsetsatellite.catalyst.energy.simple.test.block;

import net.minecraft.core.block.entity.TileEntity;
import sunsetsatellite.catalyst.core.util.network.NetworkComponent;
import sunsetsatellite.catalyst.core.util.network.NetworkType;
import sunsetsatellite.catalyst.energy.simple.test.tile.TileEntitySimpleMachine;

public class BlockSimpleMachine extends BlockEnergy implements NetworkComponent {
	public BlockSimpleMachine(String key, int id) {
		super(key, id);
	}

	@Override
	protected TileEntity getNewBlockEntity() {
		return new TileEntitySimpleMachine();
	}

	@Override
	public NetworkType getType() {
		return NetworkType.CATALYST_ENERGY;
	}
}
