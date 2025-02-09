package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.ITileEntityInit;

@Mixin(value = TileEntity.class,remap = false)
public class TileEntityMixin implements ITileEntityInit {

	@Unique
	protected boolean initialized = false;

	@Override
	public void init(Block block) {
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public void setInitialized() {
		initialized = true;
	}
}
