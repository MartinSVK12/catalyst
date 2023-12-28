package sunsetsatellite.catalyst.effects.api.effect;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EffectContainer {
	private final List<EffectStack> effects = new ArrayList<>();
	private final List<Attribute<?>> attributes = new ArrayList<>();

	public List<Attribute<?>> getAttributes() {
		return attributes;
	}

	public List<EffectStack> getEffects() {
		return Collections.unmodifiableList(effects);
	}

	public void add(EffectStack effectStack){
		for (EffectStack effect : effects) {
			if(effect.getEffect() == effectStack.getEffect()){
				if(effect.getAmount()+ effectStack.getAmount() <= effect.getEffect().getMaxStack()){
					effect.add(effectStack.getAmount());
					return;
				} else {
					return;
				}
			}
		}
		effects.add(effectStack);
	}

	public void subtract(EffectStack effectStack){
		for (EffectStack effect : effects) {
			if(effect.getEffect() == effectStack.getEffect()){
				effect.subtract(effectStack.getAmount());
				return;
			}
		}
	}

	public void remove(Effect effect){
		List<EffectStack> copy = new ArrayList<>(effects);
		for (EffectStack effectStack : copy) {
			if(effectStack.getEffect() == effect){
				effects.remove(effectStack);
			}
		}
	}

	public void tick(){
		List<EffectStack> copy = new ArrayList<>(effects);
		for (EffectStack effectStack : copy) {
			effectStack.tick();
			if(effectStack.isFinished()){
				effects.remove(effectStack);
			}
		}
	}
}