package sunsetsatellite.catalyst.multipart.mixin;

import net.minecraft.client.render.texturepack.TexturePackList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.useless.dragonfly.data.block.mojang.BlockModelMojangData;

// I really wish access wideners actually worked, so I wouldn't have to do this
@Mixin(value = BlockModelMojangData.Builder.class, remap = false)
public interface BlockModelMojangDataBuilderInvoker {

	@Invoker
	@NotNull BlockModelMojangData callBuild(@NotNull final TexturePackList texturePackList, @NotNull String id);

}
