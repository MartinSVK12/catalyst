package sunsetsatellite.catalyst.multipart.mixin;

import net.minecraft.client.render.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.helper.MultipartModelHelper;

import java.util.List;

@Mixin(value = TextureManager.class, remap = false)
public class TextureManagerMixin {
	@Inject(method = "refreshTextures(Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;nanoTime()J"))
	private void refreshModels(List<Throwable> errors, CallbackInfo ci) {
		MultipartModelHelper.refreshModels();
	}
}
