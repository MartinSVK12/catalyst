package sunsetsatellite.catalyst.effects.api.effect;

import net.minecraft.core.data.registry.Registry;
import sunsetsatellite.catalyst.CatalystEffects;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;
import sunsetsatellite.catalyst.effects.api.modifier.type.IntModifier;

import java.util.Collections;

import static sunsetsatellite.catalyst.Catalyst.listOf;

public class Effects extends Registry<Effect> {

	public static final Effect DURATION_BOOST = new Effect(
		"effect.catalyst.durationBoost",
		CatalystEffects.MOD_ID + ":duration_boost",
		listOf(new IntModifier(Attributes.EFFECT_DURATION, ModifierType.MULTIPLY, 2)),
		EffectTimeType.RESET,
		1
	)
		.setPersistent()
		.setDefaultDuration(20 * 10);

	public static final Effect EXTRA_HEALTH = new Effect(
		"effect.catalyst.extraHealth",
		CatalystEffects.MOD_ID + ":extra_health",
		Collections.singletonList(new IntModifier(Attributes.EXTRA_HEALTH, ModifierType.ADD, 1)),
		EffectTimeType.PERMANENT,
		40
	)
		.setPersistent();


	private Effects() {
		register(DURATION_BOOST.id, DURATION_BOOST);
		register(EXTRA_HEALTH.id, EXTRA_HEALTH);
	}

	private static final Effects INSTANCE = new Effects();

	public static Effects getInstance() {
		return INSTANCE;
	}
}
