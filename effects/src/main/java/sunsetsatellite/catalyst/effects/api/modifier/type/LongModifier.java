package sunsetsatellite.catalyst.effects.api.modifier.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;

public final class LongModifier extends NumberModifier<Long>{
	public LongModifier(Attribute<Long> attribute, ModifierType type, long value) {
		super(attribute, type, value);
	}
}
