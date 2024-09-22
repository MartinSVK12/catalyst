package sunsetsatellite.catalyst.multipart.mixin.accessor;

import net.minecraft.client.render.RenderBlockCache;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.core.world.WorldSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = RenderBlocks.class, remap = false)
public interface RenderBlocksAccessor {
	@Accessor
	WorldSource getBlockAccess();
	@Accessor
	RenderBlockCache getCache();
	@Accessor
	float[] getSIDE_LIGHT_MULTIPLIER();
	@Accessor
	boolean getOverbright();
	@Invoker("getBlockBrightness")
	float invokeGetBlockBrightness(WorldSource blockAccess, int x, int y, int z);
}
