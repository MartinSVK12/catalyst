package sunsetsatellite.catalyst.multipart.mixin;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.useless.dragonfly.data.block.mojang.CompiledBlockModelMojangData;
import org.useless.dragonfly.models.block.mojang.StaticBlockModelMojang;

@Mixin(value = StaticBlockModelMojang.class, remap = false)
public interface StaticBlockModelMojangAccessor {

	@Accessor
	@NotNull CompiledBlockModelMojangData getCompiled();

}
