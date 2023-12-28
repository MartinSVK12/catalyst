package sunsetsatellite.catalyst.effects.api.modifier.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;

public final class IntModifier extends NumberModifier<Integer>{
	public IntModifier(Attribute<Integer> attribute, ModifierType type, int value) {
		super(attribute, type, value);
	}
}
