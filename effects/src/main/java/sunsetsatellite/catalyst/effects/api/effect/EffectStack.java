package sunsetsatellite.catalyst.effects.api.effect;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.net.SyncEffectContainerForEntityNetworkMessage;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;

public class EffectStack {
	private final Effect effect;
	private final int duration;
	private int timeLeft;
	private int amount;
	private State state = State.INACTIVE;
	private int tickCount = 0;

	public enum State {
		INACTIVE,
		ACTIVE,
		PAUSED,
		FINISHED
	}

	public EffectStack(IHasEffects target, Effect effect) {
		this.effect = effect;
		this.duration = Attributes.EFFECT_DURATION.calculate(target, effect.getDefaultDuration());
		this.amount = 1;
	}

	public EffectStack(IHasEffects target, Effect effect, int amount) {
		this.effect = effect;
		this.duration = Attributes.EFFECT_DURATION.calculate(target, effect.getDefaultDuration());
		this.amount = Math.min(amount, effect.getMaxStack());
	}

	public EffectStack(IHasEffects target, Effect effect, int duration, int amount) {
		this.effect = effect;
		this.duration = Attributes.EFFECT_DURATION.calculate(target, duration);
		this.amount = Math.min(amount, effect.getMaxStack());
	}

	public EffectStack(CompoundTag tag) {
		this.effect = Effects.getInstance().getItem(tag.getString("id"));
		this.duration = tag.getInteger("duration");
		this.amount = tag.getInteger("amount");
		this.timeLeft = tag.getInteger("timeLeft");
		this.tickCount = tag.getInteger("tickCount");
		this.state = State.valueOf(tag.getString("state"));
	}

	public <T> void start(EffectContainer<T> effectContainer) {
		if (state == State.INACTIVE) {
			state = State.ACTIVE;
			timeLeft = duration;
			effect.activated(this, effectContainer);
			syncEffectsStack(effectContainer);
		}
	}

	public <T> void pause(EffectContainer<T> effectContainer) {
		if (state == State.ACTIVE) {
			state = State.PAUSED;
			effect.paused(this, effectContainer);
			syncEffectsStack(effectContainer);
		}
	}

	public <T> void unpause(EffectContainer<T> effectContainer) {
		if (state == State.PAUSED) {
			state = State.ACTIVE;
			effect.unpaused(this, effectContainer);
			syncEffectsStack(effectContainer);
		}
	}

	public <T> void finish(EffectContainer<T> effectContainer) {
		if (state == State.ACTIVE) {
			if(effect.isStackSizeDecaying()){
				timeLeft = duration;
				amount -= effect.getDecayAmount();
				effect.stackSubtracted(this, effectContainer);
				syncEffectsStack(effectContainer);
			}else {
				timeLeft = 0;
				state = State.FINISHED;
				effect.expired(this, effectContainer);
			}
		}
	}

	public <T> void tick(EffectContainer<T> effectContainer) {
		if (state == State.ACTIVE) {
			tickCount++;
			if (effect.getTimeType() == EffectTimeType.PERMANENT) {
				effect.tick(this, effectContainer);
				return;
			}
			if (timeLeft > 0) {
				timeLeft--;
				effect.tick(this, effectContainer);
			} else {
				this.finish(effectContainer);
			}
		}
	}

	public <T> void add(int amount, EffectContainer<T> effectContainer) {
		this.amount += amount;
		if (effect.getTimeType() == EffectTimeType.RESET) {
			timeLeft = duration;
			effect.stackRefreshed(this, effectContainer);
		}
		if (effect.getTimeType() == EffectTimeType.ADD) {
			timeLeft += effect.getDurationIncrease();
		}

		if (amount > 0) {
			effect.stackAdded(this, effectContainer);
		}

		syncEffectsStack(effectContainer);
	}

	public <T> void subtract(int amount, EffectContainer<T> effectContainer) {
		this.amount -= amount;

		if (amount > 0) {
			effect.stackSubtracted(this, effectContainer);
		}
		syncEffectsStack(effectContainer);
	}

	private static <T> void syncEffectsStack(EffectContainer<T> effectContainer) {
		if (EnvironmentHelper.isServerEnvironment()) {
			NetworkHandler.sendToAllPlayers(new SyncEffectContainerForEntityNetworkMessage((Entity) effectContainer.getParent()));
		}
	}

	public boolean isActive() {
		return state == State.ACTIVE;
	}

	public boolean isPaused() {
		return state == State.PAUSED;
	}

	public boolean isFinished() {
		return state == State.FINISHED;
	}

	public int getTimeLeft() {
		return timeLeft;
	}

	public int getAmount() {
		return amount;
	}

	public Effect getEffect() {
		return effect;
	}

	public int getDuration() {
		return duration;
	}

	public int gettickCount() {
		return tickCount;
	}

	public boolean hasAttribute(Attribute<?> attribute) {
		for (Modifier<?> modifier : effect.getModifiers()) {
			if (modifier.attribute.equals(attribute)) {
				return true;
			}
		}
		return false;
	}

	public void saveToNbt(CompoundTag tag) {
		tag.putString("id", effect.id);
		tag.putInt("duration", duration);
		tag.putInt("timeLeft", timeLeft);
		tag.putInt("amount", amount);
		tag.putInt("tickCount", tickCount);
		tag.putString("state", state.name());
	}

}
