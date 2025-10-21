package sunsetsatellite.catalyst.effects.interfaces.mixins;

import net.minecraft.client.option.OptionEnum;
import sunsetsatellite.catalyst.effects.api.effect.options.EffectDisplayPlace;
import sunsetsatellite.catalyst.effects.api.effect.options.EffectExtraHealthDisplayStyle;

public interface IKeybinds {
	OptionEnum<EffectDisplayPlace> getEffectDisplayPlaceEnumOption();

	OptionEnum<EffectExtraHealthDisplayStyle> getExtraHealthDisplayStyle();
}
