package sunsetsatellite.catalyst.effects.mixin;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.inventory.InventoryPlayer;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.CatalystEffects;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.effect.EffectContainer;
import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.Effects;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;

@Mixin(value = EntityPlayer.class,remap = false)
public abstract class EntityPlayerMixin extends EntityLiving implements IHasEffects {

	@Unique
	public EffectContainer effectContainer = new EffectContainer();

	private EntityPlayerMixin(World world) {
		super(world);
	}

	@Inject(method = "<init>",at = @At("TAIL"))
	public void init(World world, CallbackInfo ci){
		effectContainer.getAttributes().add(Attributes.EFFECT_DURATION);
		effectContainer.getAttributes().add(Attributes.ATTACK);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci){
		effectContainer.tick();

	}

	@Redirect(method = "attackTargetEntityWithCurrentItem",at = @At(value = "INVOKE",target = "Lnet/minecraft/core/player/inventory/InventoryPlayer;getDamageVsEntity(Lnet/minecraft/core/entity/Entity;)I",ordinal = 0))
	public int attack(InventoryPlayer instance, Entity entity){
		int baseDamage = instance.getDamageVsEntity(entity);
		CatalystEffects.LOGGER.info("Attack: "+Attributes.ATTACK.calculate(this, baseDamage));
		return Attributes.ATTACK.calculate(this, baseDamage);
	}

	@Override
	public EffectContainer getContainer() {
		return effectContainer;
	}
}
