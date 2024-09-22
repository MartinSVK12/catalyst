package sunsetsatellite.catalyst.multipart.mixin;

import net.minecraft.client.render.RenderEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.helper.ModelHelper;

import java.util.List;

@Mixin(value = RenderEngine.class, remap = false)
public class RenderEngineMixin {
	@Inject(method = "refreshTextures(Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V", ordinal = 0, shift = At.Shift.BEFORE))
	private void refreshModels(List<Throwable> errors, CallbackInfo ci){
		ModelHelper.refreshModels();
	}
}
