package sunsetsatellite.catalyst.effects.api.attribute.type;

import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.modifier.type.FloatModifier;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class FloatAttribute extends NumberAttribute<Float>{


	public FloatAttribute(String key, Float defaultValue, Float minValue, Float maxValue) {
		super(key, defaultValue, minValue, maxValue);
	}

	public FloatAttribute(String key, Float defaultValue) {
		super(key, defaultValue, Float.MIN_VALUE, Float.MAX_VALUE);
	}

	@Override
	public Float calculate(IHasEffects target) {
		if(target.getContainer().getAttributes().contains(this)){
			for (EffectStack effectStack : target.getContainer().getEffects()) {
				if(effectStack.hasAttribute(this)){
					float value = getBaseValue();
					List<FloatModifier> validModifiers = effectStack
						.getEffect()
						.getModifiers()
						.stream()
						.map((M)->{
							if(M instanceof FloatModifier){
								return ((FloatModifier)M);
							} else {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(M -> M.type))
						.collect(Collectors.toList());
					for (FloatModifier modifier : validModifiers) {
						switch (modifier.type){
							case SET: {
								if(modifier.value > value){
									value = modifier.value;
								}
								break;
							}
							case ADD: value += modifier.value; break;
							case SUBTRACT: value -= modifier.value; break;
							case PERCENT_ADD: value += (value/100) * modifier.value; break;
							case PERCENT_SUBTRACT: value -= (value/100) * modifier.value; break;
							case MULTIPLY: value *= modifier.value; break;
							case DIVIDE: value /= modifier.value; break;
						}
					}
					return Math.min(this.maxValue, Math.max(value, this.minValue));
				}
			}
			return getBaseValue();
		}
		throw new IllegalStateException("target doesn't contain attribute");//return getBaseValue();
	}

	@Override
	public Float calculate(IHasEffects target, Float baseValue) {
		if(target.getContainer().getAttributes().contains(this)){
			for (EffectStack effectStack : target.getContainer().getEffects()) {
				if(effectStack.hasAttribute(this)){
					float value = baseValue;
					List<FloatModifier> validModifiers = effectStack
						.getEffect()
						.getModifiers()
						.stream()
						.map((M)->{
							if(M instanceof FloatModifier){
								return ((FloatModifier)M);
							} else {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(M -> M.type))
						.collect(Collectors.toList());
					for (FloatModifier modifier : validModifiers) {
						switch (modifier.type){
							case SET: {
								if(modifier.value > value){
									value = modifier.value;
								}
								break;
							}
							case ADD: value += modifier.value; break;
							case SUBTRACT: value -= modifier.value; break;
							case PERCENT_ADD: value += (value/100) * modifier.value; break;
							case PERCENT_SUBTRACT: value -= (value/100) * modifier.value; break;
							case MULTIPLY: value *= modifier.value; break;
							case DIVIDE: value /= modifier.value; break;
						}
					}
					return Math.min(this.maxValue, Math.max(value, this.minValue));
				}
			}
			return baseValue;
		}
		throw new IllegalStateException("target doesn't contain attribute");//return getBaseValue();
	}
}
