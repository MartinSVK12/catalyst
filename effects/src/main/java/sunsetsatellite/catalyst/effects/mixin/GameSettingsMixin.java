package sunsetsatellite.catalyst.effects.mixin;

import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.OptionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.effects.api.effect.EffectDisplayPlace;
import sunsetsatellite.catalyst.effects.interfaces.mixins.IKeybinds;

@Mixin(
        value = GameSettings.class,
        remap = false
)
public class GameSettingsMixin
    implements IKeybinds
{
	private final GameSettings thisAs = ((GameSettings)(Object)this);

	@Unique
	public OptionEnum<EffectDisplayPlace> effectDisplayPlaceEnumOption = new OptionEnum<>(thisAs,"catalyst-effect.displayEffectsIn", EffectDisplayPlace.class,EffectDisplayPlace.INVENTORY);

	@Override
	public OptionEnum<EffectDisplayPlace> getEffectDisplayPlaceEnumOption() {
		return effectDisplayPlaceEnumOption;
	}
}
