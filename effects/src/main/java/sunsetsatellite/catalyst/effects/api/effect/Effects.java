package sunsetsatellite.catalyst.effects.api.effect;

import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.Registry;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystEffects;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;
import sunsetsatellite.catalyst.effects.api.modifier.type.IntModifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.LongModifier;

import java.util.ArrayList;

import static sunsetsatellite.catalyst.CatalystEffects.listOf;

public class Effects extends Registry<Effect> {

	public static final Effect ATTACK_BOOST = new Effect(
		"effect.catalyst.attackBoost",
		CatalystEffects.MOD_ID,
		"signalumsaber.png",
		0xFFAA0000,
		listOf(new IntModifier(Attributes.ATTACK, ModifierType.ADD,2)),
		EffectTimeType.RESET,
		20*30,
		10
		);

	public static final Effect DURATION_BOOST = new Effect(
		"effect.catalyst.durationBoost",
		CatalystEffects.MOD_ID,
		"",
		0xFFAAFF00,
		listOf(new IntModifier(Attributes.EFFECT_DURATION, ModifierType.MULTIPLY,2)),
		EffectTimeType.RESET,
		20*10,
		1
	);

	public Effects(){
		register("catalyst:attack_boost",ATTACK_BOOST);
		register("catalyst:duration_boost",DURATION_BOOST);
		Registries.getInstance().register("catalyst:effects",this);
	}

	private static final Effects INSTANCE = new Effects();

	public static Effects getInstance() {
		return INSTANCE;
	}
}
