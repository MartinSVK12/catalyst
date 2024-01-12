package sunsetsatellite.catalyst.effects.mixin;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.effect.EffectContainer;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;

import java.util.Arrays;

@Mixin(value = Entity.class, remap = false)
public class EntityMixin implements IHasEffects {

	@Unique
	private final Entity thisAs = (Entity) ((Object)this);

	@Unique
	public EffectContainer<Entity> effectContainer = new EffectContainer<>(thisAs);

	@Inject(method = "<init>", at = @At("TAIL"))
	public void init(World world, CallbackInfo ci){
		effectContainer.getAttributes().add(Attributes.EFFECT_DURATION);
	}

	@Override
	public EffectContainer<Entity> getContainer() {
		return effectContainer;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci){
		effectContainer.tick();

	}

	@Inject(method = "saveWithoutId",at = @At("TAIL"))
	public void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		CompoundTag effects = new CompoundTag();
		getContainer().saveToNbt(effects);
		tag.putCompound("Effects",effects);
	}

	@Inject(method = "load",at = @At("TAIL"))
	public void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		getContainer().loadFromNbt(tag.getCompound("Effects"));
	}
}
