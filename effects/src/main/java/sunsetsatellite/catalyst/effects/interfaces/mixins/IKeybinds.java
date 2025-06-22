package sunsetsatellite.catalyst.effects.interfaces.mixins;

import net.minecraft.client.option.OptionEnum;
import sunsetsatellite.catalyst.effects.api.effect.EffectDisplayPlace;

public interface IKeybinds {
	OptionEnum<EffectDisplayPlace> getEffectDisplayPlaceEnumOption();
}
