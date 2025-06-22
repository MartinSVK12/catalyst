package sunsetsatellite.catalyst.effects.api.attribute;

import net.minecraft.core.data.registry.Registry;
import sunsetsatellite.catalyst.effects.api.attribute.type.IntAttribute;

public class Attributes extends Registry<Attribute<?>> {

	public static final IntAttribute EFFECT_DURATION = (IntAttribute) new IntAttribute("attribute.catalyst.effectDuration",1).setAsDefault();

	public Attributes(){
		register("catalyst-effects:effect_duration",EFFECT_DURATION);
	}

	private static final Attributes INSTANCE = new Attributes();

	public static Attributes getInstance() {
		return INSTANCE;
	}
}
