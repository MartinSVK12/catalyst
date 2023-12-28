package sunsetsatellite.catalyst.effects.api.attribute.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;
import sunsetsatellite.catalyst.effects.api.modifier.type.BooleanModifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.DoubleModifier;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class BooleanAttribute extends Attribute<Boolean> {
	public BooleanAttribute(String key, boolean defaultValue) {
		super(key, defaultValue);
	}

	@Override
	public Boolean calculate(IHasEffects target) {
		if(target.getContainer().getAttributes().contains(this)){
			for (EffectStack effectStack : target.getContainer().getEffects()) {
				if(effectStack.hasAttribute(this)){
					boolean value = getBaseValue();
					List<BooleanModifier> validModifiers = effectStack
						.getEffect()
						.getModifiers()
						.stream()
						.map((M)->{
							if(M instanceof BooleanModifier){
								return ((BooleanModifier)M);
							} else {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(M -> M.type))
						.toList();
					for (BooleanModifier modifier : validModifiers) {
                        if (Objects.requireNonNull(modifier.type) == ModifierType.SET) {
                            if (modifier.value != value) {
                                return modifier.value;
                            }
                        }
					}
				}
			}
			return getBaseValue();
		}
		throw new IllegalStateException("target doesn't contain attribute");//return getBaseValue();
	}

	@Override
	public Boolean calculate(IHasEffects target, Boolean baseValue) {
		if(target.getContainer().getAttributes().contains(this)){
			for (EffectStack effectStack : target.getContainer().getEffects()) {
				if(effectStack.hasAttribute(this)){
					boolean value = baseValue;
					List<BooleanModifier> validModifiers = effectStack
						.getEffect()
						.getModifiers()
						.stream()
						.map((M)->{
							if(M instanceof BooleanModifier){
								return ((BooleanModifier)M);
							} else {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(M -> M.type))
						.toList();
					for (BooleanModifier modifier : validModifiers) {
						if (Objects.requireNonNull(modifier.type) == ModifierType.SET) {
							if (modifier.value != value) {
								return modifier.value;
							}
						}
					}
				}
			}
			return baseValue;
		}
		throw new IllegalStateException("target doesn't contain attribute");//return getBaseValue();
	}
}
