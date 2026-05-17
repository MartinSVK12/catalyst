package sunsetsatellite.catalyst.effects.api.modifier.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;

import java.util.function.BiFunction;

public final class LongModifier extends NumberModifier<Long> {
	public LongModifier(Attribute<Long> attribute, ModifierType type, long value) {
		super(attribute, type, value, (baseValue, stack) -> baseValue * stack);
	}

	public LongModifier(Attribute<Long> attribute, ModifierType type, Long value, BiFunction<Long, Integer, Long> stackFunction) {
		super(attribute, type, value, stackFunction);
	}
}
