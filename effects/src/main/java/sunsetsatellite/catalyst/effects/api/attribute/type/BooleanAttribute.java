package sunsetsatellite.catalyst.effects.api.attribute.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;
import sunsetsatellite.catalyst.effects.api.modifier.type.BooleanModifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.DoubleModifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.NumberModifier;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class BooleanAttribute extends Attribute<Boolean> {
	public BooleanAttribute(String key, boolean defaultValue) {
		super(key, defaultValue);
	}

	@Override
	public Boolean calculate(IHasEffects<?> target) {
		return this.calculate(target, this.getBaseValue());
	}

	@Override
	public Boolean calculate(IHasEffects<?> target, Boolean baseValue) {
		if(target.getContainer().getAttributes().contains(this)){

			boolean value = baseValue;
			for (Function<?, List<Modifier<?>>> modifierSupplier : target.getContainer().additionalModifierSuppliers) {
				List<BooleanModifier> validModifiers = validateModifiers(((Function<IHasEffects<?>, List<Modifier<?>>>) modifierSupplier).apply(target));
				for (BooleanModifier modifier : validModifiers) {
					if (Objects.requireNonNull(modifier.type) == ModifierType.SET) {
						if (modifier.value != value) {
							value = modifier.value;
						}
					}
				}
			}

			List<EffectStack> sortedStacks = target.getContainer().getEffects().stream().sorted(Comparator.comparingInt((S) -> S.getEffect().getPriority())).collect(Collectors.toList());

			for (EffectStack effectStack : sortedStacks) {
				if(effectStack.hasAttribute(this) && effectStack.isActive()){

					List<BooleanModifier> validModifiers = validateModifiers(effectStack
						.getEffect()
						.getModifiers());
					for (BooleanModifier modifier : validModifiers) {
						if (Objects.requireNonNull(modifier.type) == ModifierType.SET) {
							if (modifier.value != value) {
								value = modifier.value;
							}
						}
					}
				}
			}

			return value;
		}
		throw new IllegalStateException(String.format("Target '%s' doesn't contain attribute '%s'", target, this.getName()));
	}

	private List<BooleanModifier> validateModifiers(List<Modifier<?>> modifiers){
		return modifiers.stream()
			.filter((M)->M.attribute.getClass().isAssignableFrom(this.getClass()))
			.map((M)->{
				if(M instanceof BooleanModifier){
					return ((BooleanModifier)M);
				} else {
					return null;
				}
			})
			.filter(Objects::nonNull)
			.sorted(Comparator.comparing(M -> M.type))
			.collect(Collectors.toList());
	}
}
