package sunsetsatellite.catalyst.effects.api.modifier.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;

public final class DoubleModifier extends NumberModifier<Double>{
	public DoubleModifier(Attribute<Double> attribute, ModifierType type, double value) {
		super(attribute, type, value);
	}
}
