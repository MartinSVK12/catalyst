package sunsetsatellite.catalyst.effects.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.effects.Options;
import sunsetsatellite.catalyst.effects.api.effect.EffectContainer;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.effect.options.EffectDisplayPlace;
import sunsetsatellite.catalyst.effects.api.effect.render.EffectRendererManager;

@Mixin(value = HudIngame.class, remap = false)
public abstract class HudIngameMixin extends Gui {

	@Shadow
	protected Minecraft mc;

	private HudIngameMixin() {
	}

	@Unique
	private final EffectRendererManager catalyst$ScreenEffects = new EffectRendererManager();

	@Inject(
		method = "renderGameOverlay",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupScaledResolution()V", shift = At.Shift.AFTER)
	)
	public void renderAfterGameOverlay(float partialTicks, boolean flag, int mouseX, int mouseY, CallbackInfo ci) {
		EffectDisplayPlace effectDisplayPlace = Options.effectDisplayPlaceEnumOption.value;
		EffectContainer<?> player = ((IHasEffects<?>) mc.thePlayer).getContainer();

		if (effectDisplayPlace == EffectDisplayPlace.HUD || effectDisplayPlace == EffectDisplayPlace.BOTH) {
			catalyst$ScreenEffects.drawEffectIndicators(player, mc, mouseX, mouseY, partialTicks);
		}
	}

	@Inject(
		method = "renderGameOverlay(FZII)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/player/inventory/container/ContainerInventory;armorItemInSlot(Lnet/minecraft/core/enums/IArmorShape;)Lnet/minecraft/core/item/ItemStack;",
			ordinal = 0
		)
	)
	public void endRenderGameOverlay(float partialTicks, boolean flag, int mouseX, int mouseY, CallbackInfo ci) {
		EffectContainer<?> player = ((IHasEffects<?>) mc.thePlayer).getContainer();
		catalyst$ScreenEffects.drawScreenEffects(player, mc, mouseX, mouseY, partialTicks);
	}

}
