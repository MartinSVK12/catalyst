package sunsetsatellite.catalyst.effects.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.CatalystEffects;
import sunsetsatellite.catalyst.CatalystEffectsClient;
import sunsetsatellite.catalyst.effects.api.effect.EffectDisplayPlace;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.screen.ScreenEffects;

@Mixin(value = HudIngame.class,remap = false)
public abstract class HudIngameMixin extends Gui {

	private HudIngameMixin(){}

	@Inject(
		method = "renderGameOverlay",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupScaledResolution()V", shift = At.Shift.AFTER)
	)
	public void renderAfterGameOverlay(float partialTicks, boolean flag, int mouseX, int mouseY, CallbackInfo ci) {
		if (CatalystEffectsClient.keybinds.getEffectDisplayPlaceEnumOption().value == EffectDisplayPlace.HUD || CatalystEffectsClient.keybinds.getEffectDisplayPlaceEnumOption().value == EffectDisplayPlace.BOTH) {
			new ScreenEffects().drawEffects(((IHasEffects)Minecraft.getMinecraft().thePlayer).getContainer(),Minecraft.getMinecraft(),mouseX,mouseY,partialTicks);
		}
	}

}
