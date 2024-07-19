package sunsetsatellite.catalyst.effects.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.client.option.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.effects.api.effect.EffectContainer;
import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;

@Mixin(value = Minecraft.class,remap = false)
public abstract class MinecraftMixin {

	@Unique
	private final Minecraft thisAs = (Minecraft) ((Object)this);

	@Shadow
	public GameSettings gameSettings;

	@Shadow
	public EntityPlayerSP thePlayer;
	@Inject(
		method = "respawn",
		at = @At("HEAD")
	)
	public void saveEffectsOnDeath(boolean flag, int i, CallbackInfo ci, @Share("effectContainer") LocalRef<EffectContainer<?>> eff) {
		eff.set(((IHasEffects) thePlayer).getContainer());
	}

	@Inject(
		method = "respawn",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/controller/PlayerController;adjustPlayer(Lnet/minecraft/core/entity/player/EntityPlayer;)V",shift = At.Shift.AFTER)
	)
	public void restoreEffectsOnRespawn(boolean flag, int i, CallbackInfo ci, @Share("effectContainer") LocalRef<EffectContainer<?>> eff) {
		for (EffectStack effect : eff.get().getEffects()) {
			if(effect.getEffect().isPersistent()) ((IHasEffects) thePlayer).getContainer().add(effect);
		}
	}
}
