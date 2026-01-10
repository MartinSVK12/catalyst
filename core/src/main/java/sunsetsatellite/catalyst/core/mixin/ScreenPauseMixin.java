package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ScreenPause;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.Catalyst;

@Mixin(value = ScreenPause.class, remap = false)
public abstract class ScreenPauseMixin extends Screen {

	@Inject(method = "buttonClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/achievement/stat/StatsCounter;add(Lnet/minecraft/core/achievement/stat/Stat;I)V", shift = At.Shift.AFTER))
	protected void buttonClicked(ButtonElement button, CallbackInfo ci) {
		if (!this.mc.isMultiplayerWorld()) {
			Catalyst.WORLD_QUIT_SIGNAL.emit(this.mc.currentWorld);
		} else {
			Catalyst.DISCONNECT_SIGNAL.emit(this.mc.currentWorld);
		}
	}

}
