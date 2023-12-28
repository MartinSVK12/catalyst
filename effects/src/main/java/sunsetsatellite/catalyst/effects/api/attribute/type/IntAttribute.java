package sunsetsatellite.catalyst.effects.api.attribute.type;

import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.IntModifier;

import java.util.ArrayList;
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
					List<IntModifier> validModifiers = effectStack
						.getEffect()
						.getModifiers()
						.stream()
						.map((M)->{
							if(M instanceof IntModifier){
								return ((IntModifier)M);
							} else {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(M -> M.type))
						.toList();
					for (IntModifier modifier : validModifiers) {
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
	public Integer calculate(IHasEffects target, Integer baseValue) {
		if(target.getContainer().getAttributes().contains(this)){
			for (EffectStack effectStack : target.getContainer().getEffects()) {
				if(effectStack.hasAttribute(this)){
					int value = baseValue;
					List<IntModifier> validModifiers = effectStack
						.getEffect()
						.getModifiers()
						.stream()
						.map((M)->{
							if(M instanceof IntModifier){
								return ((IntModifier)M);
							} else {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(M -> M.type))
						.toList();
					for (IntModifier modifier : validModifiers) {
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
					return Math.min(this.maxValue, Math.max(value * effectStack.getAmount(), this.minValue));
				}
			}
			return baseValue;
		}
		throw new IllegalStateException("target doesn't contain attribute");//return getBaseValue();
	}
}
