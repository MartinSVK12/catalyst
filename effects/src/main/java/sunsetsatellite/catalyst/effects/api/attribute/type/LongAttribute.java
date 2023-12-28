package sunsetsatellite.catalyst.effects.api.attribute.type;

import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.modifier.type.LongModifier;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class LongAttribute extends NumberAttribute<Long> {


	public LongAttribute(String key, Long defaultValue, Long minValue, Long maxValue) {
		super(key, defaultValue, minValue, maxValue);
	}

	public LongAttribute(String key, Long defaultValue) {
		super(key, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
	}

	@Override
	public Long calculate(IHasEffects target) {
		if(target.getContainer().getAttributes().contains(this)){
			for (EffectStack effectStack : target.getContainer().getEffects()) {
				if(effectStack.hasAttribute(this)){
					long value = getBaseValue();
					List<LongModifier> validModifiers = effectStack
						.getEffect()
						.getModifiers()
						.stream()
						.map((M)->{
							if(M instanceof LongModifier){
								return ((LongModifier)M);
							} else {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(M -> M.type))
						.toList();
					for (LongModifier modifier : validModifiers) {
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
	public Long calculate(IHasEffects target, Long baseValue) {
		if(target.getContainer().getAttributes().contains(this)){
			for (EffectStack effectStack : target.getContainer().getEffects()) {
				if(effectStack.hasAttribute(this)){
					long value = baseValue;
					List<LongModifier> validModifiers = effectStack
						.getEffect()
						.getModifiers()
						.stream()
						.map((M)->{
							if(M instanceof LongModifier){
								return ((LongModifier)M);
							} else {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(M -> M.type))
						.toList();
					for (LongModifier modifier : validModifiers) {
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
