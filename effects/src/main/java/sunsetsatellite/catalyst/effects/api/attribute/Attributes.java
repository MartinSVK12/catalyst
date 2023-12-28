package sunsetsatellite.catalyst.effects.api.attribute;

import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.Registry;
import sunsetsatellite.catalyst.effects.api.attribute.type.FloatAttribute;
import sunsetsatellite.catalyst.effects.api.attribute.type.IntAttribute;
import sunsetsatellite.catalyst.effects.api.attribute.type.LongAttribute;

public class Attributes extends Registry<Attribute<?>> {

	public static final IntAttribute EFFECT_DURATION = new IntAttribute("attribute.catalyst.effectDuration",1);
	public static final IntAttribute ATTACK = new IntAttribute("attribute.catalyst.attack",1);

	public Attributes(){
		register("catalyst:effect_duration",EFFECT_DURATION);
		register("catalyst:attack",ATTACK);
		Registries.getInstance().register("catalyst:attributes",this);
	}

	private static final Attributes INSTANCE = new Attributes();

	public static Attributes getInstance() {
		return INSTANCE;
	}
}
