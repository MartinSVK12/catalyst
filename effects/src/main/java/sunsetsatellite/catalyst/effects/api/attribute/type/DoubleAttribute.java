package sunsetsatellite.catalyst.effects.api.attribute.type;

import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.modifier.type.DoubleModifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.IntModifier;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class DoubleAttribute extends NumberAttribute<Double>{


	public DoubleAttribute(String key, Double defaultValue, Double minValue, Double maxValue) {
		super(key, defaultValue, minValue, maxValue);
	}

	public DoubleAttribute(String key, Double defaultValue) {
		super(key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
	}

	@Override
	public Double calculate(IHasEffects target) {
		if(target.getContainer().getAttributes().contains(this)){
			for (EffectStack effectStack : target.getContainer().getEffects()) {
				if(effectStack.hasAttribute(this)){
					double value = getBaseValue();
					List<DoubleModifier> validModifiers = effectStack
						.getEffect()
						.getModifiers()
						.stream()
						.map((M)->{
							if(M instanceof DoubleModifier){
								return ((DoubleModifier)M);
							} else {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(M -> M.type))
						.toList();
					for (DoubleModifier modifier : validModifiers) {
						switch (modifier.type){
							case SET -> {
								if(modifier.value > value){
									value = modifier.value;
								}
							}
							case ADD -> value += modifier.value;
							case SUBTRACT -> value -= modifier.value;
							case PERCENT_ADD -> value += (value/100) * modifier.value;
							case PERCENT_SUBTRACT -> value -= (value/100) * modifier.value;
							case MULTIPLY -> value *= modifier.value;
							case DIVIDE -> value /= modifier.value;
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
	public Double calculate(IHasEffects target, Double baseValue) {
		if(target.getContainer().getAttributes().contains(this)){
			for (EffectStack effectStack : target.getContainer().getEffects()) {
				if(effectStack.hasAttribute(this)){
					double value = baseValue;
					List<DoubleModifier> validModifiers = effectStack
						.getEffect()
						.getModifiers()
						.stream()
						.map((M)->{
							if(M instanceof DoubleModifier){
								return ((DoubleModifier)M);
							} else {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(M -> M.type))
						.toList();
					for (DoubleModifier modifier : validModifiers) {
						switch (modifier.type){
							case SET -> {
								if(modifier.value > value){
									value = modifier.value;
								}
							}
							case ADD -> value += modifier.value;
							case SUBTRACT -> value -= modifier.value;
							case PERCENT_ADD -> value += (value/100) * modifier.value;
							case PERCENT_SUBTRACT -> value -= (value/100) * modifier.value;
							case MULTIPLY -> value *= modifier.value;
							case DIVIDE -> value /= modifier.value;
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
