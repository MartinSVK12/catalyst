package sunsetsatellite.catalyst.effects.interfaces.mixins;

import net.minecraft.client.option.EnumOption;
import sunsetsatellite.catalyst.effects.api.effect.EffectDisplayPlace;

public interface IKeybinds {
	EnumOption<EffectDisplayPlace> getEffectDisplayPlaceEnumOption();
}
