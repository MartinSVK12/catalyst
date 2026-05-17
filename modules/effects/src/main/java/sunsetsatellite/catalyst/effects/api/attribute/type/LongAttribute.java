package sunsetsatellite.catalyst.effects.api.attribute.type;

import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.NumberModifier;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class LongAttribute extends NumberAttribute<Long> {


	public LongAttribute(String key, Long defaultValue, Long minValue, Long maxValue) {
		super(key, defaultValue, minValue, maxValue);
	}

	public LongAttribute(String key, Long defaultValue) {
		super(key, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
	}

	@Override
	public Long calculate(IHasEffects<?> target) {
		return this.calculate(target, this.getBaseValue());
	}

	@Override
	public Long calculate(IHasEffects<?> target, Long baseValue) {
		if (target.getContainer().getAttributes().contains(this)) {
			long value = baseValue;

			for (Function<?, List<Modifier<?>>> modifierSupplier : target.getContainer().additionalModifierSuppliers) {
				List<NumberModifier<? extends Number>> modifiers = validateModifiers(((Function<IHasEffects<?>, List<Modifier<?>>>) modifierSupplier).apply(target));

				for (NumberModifier<? extends Number> modifier : modifiers) {
					switch (modifier.type) {
						case SET:
							value = modifier.value.longValue();
							break;
						case ADD:
							value += modifier.value.longValue();
							break;
						case SUBTRACT:
							value -= modifier.value.longValue();
							break;
						case PERCENT_ADD:
							value += (value / 100) * modifier.value.longValue();
							break;
						case PERCENT_SUBTRACT:
							value -= (value / 100) * modifier.value.longValue();
							break;
						case MULTIPLY:
							value *= modifier.value.longValue();
							break;
						case DIVIDE:
							value /= modifier.value.longValue();
							break;
					}
				}
			}

			List<EffectStack> sortedStacks = target.getContainer().getEffects().stream().sorted(Comparator.comparingInt((S) -> S.getEffect().getPriority())).collect(Collectors.toList());

			for (EffectStack effectStack : sortedStacks) {
				if (effectStack.hasAttribute(this) && effectStack.isActive()) {
					List<NumberModifier<? extends Number>> validModifiers = validateModifiers(effectStack.getEffect().getModifiers());
					for (NumberModifier<? extends Number> modifier : validModifiers) {
						switch (modifier.type) {
							case SET:
								value = modifier.calculate(effectStack).longValue();
								break;
							case ADD:
								value += modifier.calculate(effectStack).longValue();
								break;
							case SUBTRACT:
								value -= modifier.calculate(effectStack).longValue();
								break;
							case PERCENT_ADD:
								value += (value / 100) * modifier.calculate(effectStack).longValue();
								break;
							case PERCENT_SUBTRACT:
								value -= (value / 100) * modifier.calculate(effectStack).longValue();
								break;
							case MULTIPLY:
								value *= modifier.calculate(effectStack).longValue();
								break;
							case DIVIDE:
								value /= modifier.calculate(effectStack).longValue();
								break;
						}
					}
				}
			}
			return Math.min(this.maxValue, Math.max(value, this.minValue));
		}
		throw new IllegalStateException(String.format("Target '%s' doesn't contain attribute '%s'", target, this.getName()));
	}
}
