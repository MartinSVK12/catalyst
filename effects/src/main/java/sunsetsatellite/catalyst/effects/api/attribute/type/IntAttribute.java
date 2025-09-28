package sunsetsatellite.catalyst.effects.api.attribute.type;

import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.modifier.type.NumberModifier;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class IntAttribute extends NumberAttribute<Integer>{


	public IntAttribute(String key, Integer defaultValue, Integer minValue, Integer maxValue) {
		super(key, defaultValue, minValue, maxValue);
	}

	public IntAttribute(String key, Integer defaultValue) {
		super(key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public Integer calculate(IHasEffects target) {
		if(target.getContainer().getAttributes().contains(this)){
			for (EffectStack effectStack : target.getContainer().getEffects()) {
				if(effectStack.hasAttribute(this)){
                    int value = getBaseValue();
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
								if(modifier.calculate(effectStack).intValue() > value){
									value = modifier.calculate(effectStack).intValue();
								}
								break;
							}
							case ADD: value += modifier.calculate(effectStack).intValue(); break;
							case SUBTRACT: value -= modifier.calculate(effectStack).intValue(); break;
							case PERCENT_ADD: value += (value/100) * modifier.calculate(effectStack).intValue(); break;
							case PERCENT_SUBTRACT: value -= (value/100) * modifier.calculate(effectStack).intValue(); break;
							case MULTIPLY: value *= modifier.calculate(effectStack).intValue(); break;
							case DIVIDE: value /= modifier.calculate(effectStack).intValue(); break;
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
	public Integer calculate(IHasEffects target, Integer baseValue) {
		if(target.getContainer().getAttributes().contains(this)){
			for (EffectStack effectStack : target.getContainer().getEffects()) {
				if(effectStack.hasAttribute(this)){
					int value = baseValue;
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
								if(modifier.calculate(effectStack).intValue() > value){
									value = modifier.calculate(effectStack).intValue();
								}
								break;
							}
							case ADD: value += modifier.calculate(effectStack).intValue(); break;
							case SUBTRACT: value -= modifier.calculate(effectStack).intValue(); break;
							case PERCENT_ADD: value += (value/100) * modifier.calculate(effectStack).intValue(); break;
							case PERCENT_SUBTRACT: value -= (value/100) * modifier.calculate(effectStack).intValue(); break;
							case MULTIPLY: value *= modifier.calculate(effectStack).intValue(); break;
							case DIVIDE: value /= modifier.calculate(effectStack).intValue(); break;
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
