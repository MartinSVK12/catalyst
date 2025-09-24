package sunsetsatellite.catalyst.effects.api.attribute.type;

import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.modifier.type.DoubleModifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.FloatModifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.IntModifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.NumberModifier;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
					List<NumberModifier<? extends Number>> validModifiers = effectStack
						.getEffect()
						.getModifiers()
						.stream()
						.filter((M)->M.attribute.getClass().isAssignableFrom(this.getClass()))
						.map((M)->{
							if(M instanceof NumberModifier){
								return ((NumberModifier<? extends Number>)M);
							} else {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(M -> M.type))
						.collect(Collectors.toList());
					for (NumberModifier<? extends Number> modifier : validModifiers) {
						switch (modifier.type){
							case SET: {
								if(modifier.value.doubleValue() > value){
									value = modifier.value.doubleValue();
								}
								break;
							}
							case ADD: value += modifier.value.doubleValue(); break;
							case SUBTRACT: value -= modifier.value.doubleValue(); break;
							case PERCENT_ADD: value += (value/100) * modifier.value.doubleValue(); break;
							case PERCENT_SUBTRACT: value -= (value/100) * modifier.value.doubleValue(); break;
							case MULTIPLY: value *= modifier.value.doubleValue(); break;
							case DIVIDE: value /= modifier.value.doubleValue(); break;
						}
					}
					return Math.min(this.maxValue, Math.max(value, this.minValue));
				}
			}
			return getBaseValue();
		}
		throw new IllegalStateException(String.format("Target '%s' doesn't contain attribute '%s'", target, this.getName()));
	}

	@Override
	public Double calculate(IHasEffects target, Double baseValue) {
		if(target.getContainer().getAttributes().contains(this)){
			for (EffectStack effectStack : target.getContainer().getEffects()) {
				if(effectStack.hasAttribute(this)){
					double value = baseValue;
					List<? extends NumberModifier<? extends Number>> validModifiers = effectStack
						.getEffect()
						.getModifiers()
						.stream()
						.filter((M)->M.attribute.getClass().isAssignableFrom(this.getClass()))
						.map((M)->{
							if(M instanceof NumberModifier){
								return ((NumberModifier<? extends Number>)M);
							} else {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(M -> M.type))
						.collect(Collectors.toList());
					for (NumberModifier<? extends Number> modifier : validModifiers) {
						switch (modifier.type){
							case SET: {
								if(modifier.value.doubleValue() > value){
									value = modifier.value.doubleValue();
								}
								break;
							}
							case ADD: value += modifier.value.doubleValue(); break;
							case SUBTRACT: value -= modifier.value.doubleValue(); break;
							case PERCENT_ADD: value += (value/100) * modifier.value.doubleValue(); break;
							case PERCENT_SUBTRACT: value -= (value/100) * modifier.value.doubleValue(); break;
							case MULTIPLY: value *= modifier.value.doubleValue(); break;
							case DIVIDE: value /= modifier.value.doubleValue(); break;
						}
					}
					return Math.min(this.maxValue, Math.max(value, this.minValue));
				}
			}
			return baseValue;
		}
		throw new IllegalStateException(String.format("Target '%s' doesn't contain attribute '%s'", target, this.getName()));
	}
}
