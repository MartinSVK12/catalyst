package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.render.block.model.BlockModel;
import org.spongepowered.asm.mixin.Mixin;
import sunsetsatellite.catalyst.core.util.model.IColorOverride;
import sunsetsatellite.catalyst.core.util.model.IFullbright;

@Mixin(value = BlockModel.class, remap = false)
public abstract class BlockModelMixin implements IFullbright, IColorOverride {

	@Override
	public void enableFullbright() {

	}

	@Override
	public void disableFullbright() {

	}

	@Override
	public void overrideColor(float r, float g, float b, float alpha) {

	}

	@Override
	public void enableColorOverride() {

	}

	@Override
	public void disableColorOverride() {

	}
}
