package sunsetsatellite.catalyst.effects.api.effect;

import net.minecraft.core.data.registry.Registry;
import sunsetsatellite.catalyst.CatalystEffects;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;
import sunsetsatellite.catalyst.effects.api.modifier.type.IntModifier;

import static sunsetsatellite.catalyst.CatalystEffects.listOf;

public class Effects extends Registry<Effect> {

	public static final Effect DURATION_BOOST = new Effect(
		"effect.catalyst.durationBoost",
		CatalystEffects.MOD_ID+":duration_boost",
		"",
		0xFFAAFF00,
		listOf(new IntModifier(Attributes.EFFECT_DURATION, ModifierType.MULTIPLY,2)),
		EffectTimeType.RESET,
		20*10,
		1
	).setPersistent();

	public Effects(){
		register(DURATION_BOOST.id, DURATION_BOOST);
	}

	private static final Effects INSTANCE = new Effects();

	public static Effects getInstance() {
		return INSTANCE;
	}
}
