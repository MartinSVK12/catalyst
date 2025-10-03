package sunsetsatellite.catalyst.effects.mixin;

import net.minecraft.core.entity.animal.MobAnimal;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.effects.api.effect.EffectTags;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;


@Mixin(value = MobAnimal.class, remap = false)
public class MobAnimalMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	public void setTags(World world, CallbackInfo ci) {
		IHasEffects self = ((IHasEffects) this);
		self.setImmuneTo(EffectTags.PASSIVE_ARE_IMMUNE);
	}
}
