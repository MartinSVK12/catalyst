package sunsetsatellite.catalyst.screens.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.HudIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.CatalystScreensClient;
import sunsetsatellite.catalyst.screens.screen.ScreenComposed;

@Mixin(value = HudIngame.class, remap = false)
public class HudIngameMixin {

	@Shadow
	protected Minecraft mc;

	@Inject( method = "updateTick", at = @At("TAIL"))
	public void updateTick(CallbackInfo ci) {
		if (CatalystScreensClient.testKey.isPressed() && this.mc.currentScreen == null) {
			this.mc.displayScreen(new ScreenComposed("test"));
		}
	}

}
