package sunsetsatellite.catalyst.effects.api.modifier.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;

import java.util.function.BiFunction;

public final class IntModifier extends NumberModifier<Integer>{
	public IntModifier(Attribute<Integer> attribute, ModifierType type, int value) {
		super(attribute, type, value, (baseValue, stack)->baseValue * stack);
	}

	public IntModifier(Attribute<Integer> attribute, ModifierType type, Integer value, BiFunction<Integer, Integer, Integer> stackFunction) {
		super(attribute, type, value, stackFunction);
	}
}
