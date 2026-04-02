package sunsetsatellite.catalyst.effects.api.attribute;

import net.minecraft.core.data.registry.Registry;
import sunsetsatellite.catalyst.effects.api.attribute.type.IntAttribute;

public class Attributes extends Registry<Attribute<?>> {

	public static final IntAttribute EFFECT_DURATION = (IntAttribute) new IntAttribute("attribute.catalyst.effectDuration", 1).setAsDefault();

	public static IntAttribute EXTRA_HEALTH = (IntAttribute) new IntAttribute("attribute.catalyst.extraHealth", 0).setAsDefault().setIcon("extra_health.png");

	public Attributes() {
		register("catalyst-effects:effect_duration", EFFECT_DURATION);
		register("catalyst-effects:extra_health", EXTRA_HEALTH);
	}

	private static final Attributes INSTANCE = new Attributes();

	public static Attributes getInstance() {
		return INSTANCE;
	}
}
