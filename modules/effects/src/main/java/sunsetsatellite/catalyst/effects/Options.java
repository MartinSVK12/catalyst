package sunsetsatellite.catalyst.effects;

import net.minecraft.client.option.OptionBoolean;
import net.minecraft.client.option.OptionEnum;
import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.effects.api.effect.options.EffectDisplayPlace;
import sunsetsatellite.catalyst.effects.api.effect.options.EffectExtraHealthDisplayStyle;

public class Options {
	public static OptionEnum<EffectDisplayPlace> effectDisplayPlaceEnumOption = new OptionEnum<>(
		"catalyst-effects.displayEffectsIn",
		EffectDisplayPlace.class,
		EffectDisplayPlace.INVENTORY
	);

	public static OptionEnum<EffectExtraHealthDisplayStyle> effectExtraHealthDisplayStyleEnumOption = new OptionEnum<>(
		"catalyst-effects.displayExtraHealthAs",
		EffectExtraHealthDisplayStyle.class,
		EffectExtraHealthDisplayStyle.EXTRA_BARS
	);

	public static OptionBoolean renderAttributeIcon = new OptionBoolean(
		"catalyst-effects.renderAttributeIcon",
		false
	);
}
