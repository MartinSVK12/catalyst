package sunsetsatellite.catalyst.effects.api.attribute;

import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.Registry;
import sunsetsatellite.catalyst.effects.api.attribute.type.FloatAttribute;
import sunsetsatellite.catalyst.effects.api.attribute.type.IntAttribute;
import sunsetsatellite.catalyst.effects.api.attribute.type.LongAttribute;

public class Attributes extends Registry<Attribute<?>> {

	public static final IntAttribute EFFECT_DURATION = new IntAttribute("attribute.catalyst.effectDuration",1);

	public Attributes(){
		register("catalyst-effects:effect_duration",EFFECT_DURATION);
	}

	private static final Attributes INSTANCE = new Attributes();

	public static Attributes getInstance() {
		return INSTANCE;
	}
}
