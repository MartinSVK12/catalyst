package sunsetsatellite.catalyst.effects.mixin;

import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.gui.container.ScreenInventory;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.CatalystEffectsClient;
import sunsetsatellite.catalyst.effects.api.effect.options.EffectDisplayPlace;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.effect.render.EffectRendererManager;

@Mixin(value = ScreenInventory.class,remap = false)
public abstract class ScreenInventoryMixin extends ScreenContainerAbstract {

	@Unique
	private Player player;
	@Unique
	private final EffectRendererManager effects = new EffectRendererManager();

	private ScreenInventoryMixin(MenuAbstract container) {
		super(container);
	}

	@Inject(method = "<init>",at = @At("TAIL"))
	public void init(Player player, CallbackInfo ci) {
		this.player = player;
	}

	@Inject(method = "render",at = @At("TAIL"))
	public void drawEffects(int mouseX, int mouseY, float partialTick, CallbackInfo ci){
		if (CatalystEffectsClient.keybinds.getEffectDisplayPlaceEnumOption().value == EffectDisplayPlace.INVENTORY || CatalystEffectsClient.keybinds.getEffectDisplayPlaceEnumOption().value == EffectDisplayPlace.BOTH) {
			effects.drawEffectIndicators(((IHasEffects)player).getContainer(),mc,mouseX,mouseY,partialTick);
		}
	}
}
