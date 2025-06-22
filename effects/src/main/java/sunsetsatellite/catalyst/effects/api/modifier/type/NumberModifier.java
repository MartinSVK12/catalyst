package sunsetsatellite.catalyst.effects.api.modifier.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.attribute.type.NumberAttribute;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;

public abstract class NumberModifier<T extends Number> extends Modifier<T> {
	public NumberModifier(Attribute<T> attribute, ModifierType type, T value) {
		super(attribute, type, value);
	}
}
