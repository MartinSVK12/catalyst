package sunsetsatellite.catalyst.energy.electric.base;

import net.minecraft.core.block.Block;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Direction;

public abstract class TileEntityElectricStorage extends TileEntityElectricDevice {

	@Override
	public void init(Block<?> block) {
		super.init(block);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public boolean canProvide(@NotNull Direction dir) {
		return true;
	}
}
