package sunsetsatellite.catalyst.effects.api.modifier.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;

import java.util.function.BiFunction;

public final class DoubleModifier extends NumberModifier<Double>{
	public DoubleModifier(Attribute<Double> attribute, ModifierType type, double value) {
		super(attribute, type, value, (baseValue,stack) -> baseValue * stack);
	}

	public DoubleModifier(Attribute<Double> attribute, ModifierType type, Double value, BiFunction<Double, Integer, Double> stackFunction) {
		super(attribute, type, value, stackFunction);
	}
}
