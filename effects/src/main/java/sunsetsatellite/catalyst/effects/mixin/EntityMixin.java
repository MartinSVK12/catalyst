package sunsetsatellite.catalyst.effects.mixin;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.EffectContainer;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = Entity.class, remap = false)
public class EntityMixin implements IHasEffects {

	@Unique
	private final Entity thisAs = (Entity) ((Object)this);

	@Unique
	public EffectContainer<Entity> effectContainer = new EffectContainer<>(thisAs);

	@Inject(method = "<init>", at = @At("TAIL"))
	public void init(World world, CallbackInfo ci){
		Attributes.getInstance().forEach((A)->{
			if(A.isDefault() && A.getValidEntities().stream().anyMatch(E->E.isAssignableFrom(thisAs.getClass()))) effectContainer.getAttributes().add(A);
		});
	}

	@Override
	public EffectContainer<Entity> getContainer() {
		return effectContainer;
	}

	@Unique
	private final Set<Tag<Effect>> immunities = new HashSet<>();

	@Override
	public boolean isImmuneTo(Tag<Effect> effect) {
		return immunities.contains(effect);
	}

	@Override
	public void setImmuneTo(Tag<Effect> effect) {
		immunities.add(effect);
	}

	@Override
	public void removeImmunityFrom(Tag<Effect> effect) {
		immunities.remove(effect);
	}

	@Override
	public Set<Tag<Effect>> getImmunities() {
		return new HashSet<>(this.immunities);
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
