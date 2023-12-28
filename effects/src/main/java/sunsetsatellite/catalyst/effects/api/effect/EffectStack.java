package sunsetsatellite.catalyst.effects.api.effect;

import com.mojang.nbt.CompoundTag;
import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.attribute.type.IntAttribute;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.*;

import java.util.Comparator;
import java.util.List;
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

	public void tick(){
		if(state == State.ACTIVE){
			if(timeLeft > 0){
				timeLeft--;
			} else {
				state = State.FINISHED;
			}
		}
	}

	public void start(){
		if(state == State.INACTIVE){
			state = State.ACTIVE;
			timeLeft = duration;
		}
	}

	public void pause(){
		if(state == State.ACTIVE){
			state = State.PAUSED;
		}
	}

	public void unpause(){
		if(state == State.PAUSED){
			state = State.ACTIVE;
		}
	}

	public void add(int amount){
		if(state == State.ACTIVE){
			this.amount += amount;
			if (Objects.requireNonNull(effect.getTimeType()) == EffectTimeType.RESET) {
				timeLeft = duration;
			}
		}
	}

	public void subtract(int amount){
		if(state == State.ACTIVE){
			this.amount -= amount;
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
