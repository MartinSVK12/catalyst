package sunsetsatellite.catalyst.effects.api.modifier.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.attribute.type.NumberAttribute;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;

import java.util.function.BiFunction;

public final class FloatModifier extends NumberModifier<Float> {
	public FloatModifier(Attribute<Float> attribute, ModifierType type, float value) {
		super(attribute, type, value, (baseValue,stack) -> baseValue * stack);
	}

	public FloatModifier(Attribute<Float> attribute, ModifierType type, Float value, BiFunction<Float, Integer, Float> stackFunction) {
		super(attribute, type, value, stackFunction);
	}
}
