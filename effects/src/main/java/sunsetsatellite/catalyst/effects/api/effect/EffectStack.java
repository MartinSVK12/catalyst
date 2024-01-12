package sunsetsatellite.catalyst.effects.api.effect;

import com.mojang.nbt.CompoundTag;
import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;

import java.util.Objects;

public class EffectStack {
	private final Effect effect;
	private final int duration;
	private int timeLeft;
	private int amount;
	private State state = State.INACTIVE;

	public enum State {
		INACTIVE,
		ACTIVE,
		PAUSED,
		FINISHED
	}

	public EffectStack(IHasEffects target, Effect effect) {
		this.effect = effect;
		this.duration = Attributes.EFFECT_DURATION.calculate(target,effect.getDefaultDuration());
		this.amount = 1;
	}

	public EffectStack(IHasEffects target, Effect effect, int amount) {
		this.effect = effect;
		this.duration = Attributes.EFFECT_DURATION.calculate(target,effect.getDefaultDuration());
		this.amount = amount;
	}

	public EffectStack(IHasEffects target, Effect effect, int duration, int amount) {
		this.effect = effect;
		this.duration = Attributes.EFFECT_DURATION.calculate(target,duration);
		this.amount = amount;
	}

	public EffectStack(CompoundTag tag){
		this.effect = Effects.getInstance().getItem(tag.getString("id"));
		this.duration = tag.getInteger("duration");
		this.amount = tag.getInteger("amount");
		this.state = State.valueOf(tag.getString("state"));
		this.timeLeft = tag.getInteger("timeLeft");
	}

	public <T> void start(EffectContainer<T> container){
		if(state == State.INACTIVE){
			state = State.ACTIVE;
			timeLeft = duration;
			effect.activated(this,container);
		}
	}

	public <T> void pause(EffectContainer<T> container){
		if(state == State.ACTIVE){
			state = State.PAUSED;
			effect.paused(this,container);
		}
	}

	public <T> void unpause(EffectContainer<T> container){
		if(state == State.PAUSED){
			state = State.ACTIVE;
			effect.unpaused(this,container);
		}
	}

	public <T> void tick(EffectContainer<T> effectContainer) {
		if(state == State.ACTIVE){
			if(timeLeft > 0){
				timeLeft--;
				effect.tick(this,effectContainer);
			} else {
				state = State.FINISHED;
				effect.expired(this,effectContainer);
			}
		}
	}

	public <T> void add(int amount, EffectContainer<T> effectContainer) {
		if(state == State.ACTIVE){
			this.amount += amount;
			if (Objects.requireNonNull(effect.getTimeType()) == EffectTimeType.RESET) {
				timeLeft = duration;
			}
			effect.stackAdded(this,effectContainer);
		}
	}

	public <T> void subtract(int amount, EffectContainer<T> effectContainer) {
		if(state == State.ACTIVE){
			this.amount -= amount;
			effect.stackSubtracted(this,effectContainer);
		}
	}

	public boolean isActive() {
		return state == State.ACTIVE;
	}

	public boolean isPaused() {
		return state == State.PAUSED;
	}

	public boolean isFinished(){
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

	public boolean hasAttribute(Attribute<?> attribute){
		for (Modifier<?> modifier : effect.getModifiers()) {
			if(modifier.attribute.equals(attribute)){
				return true;
			}
		}
		return false;
	}

	public void saveToNbt(CompoundTag tag){
		tag.putString("id",effect.id);
		tag.putInt("duration",duration);
		tag.putInt("timeLeft",timeLeft);
		tag.putInt("amount",amount);
		tag.putString("state",state.name());
	}

}
