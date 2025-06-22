package sunsetsatellite.catalyst.effects.api.modifier.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;

public final class BooleanModifier extends Modifier<Boolean> {
	public BooleanModifier(Attribute<Boolean> attribute, ModifierType type, boolean value) {
		super(attribute, type, value);
		if(type != ModifierType.SET){
			throw new IllegalArgumentException("Invalid type for modifier!");
		}
	}

	@Override
	public String toString() {
		return value ? "Enabled: " : "Disabled: " + attribute.getName();
	}
}
