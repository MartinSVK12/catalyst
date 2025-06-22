package sunsetsatellite.catalyst.effects.api.modifier.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.attribute.type.NumberAttribute;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;

public final class FloatModifier extends NumberModifier<Float> {
	public FloatModifier(Attribute<Float> attribute, ModifierType type, float value) {
		super(attribute, type, value);
	}
}
