package sunsetsatellite.catalyst.effects.mixin;


import net.minecraft.client.option.EnumOption;
import net.minecraft.client.option.GameSettings;
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
	private GameSettings thisAs = ((GameSettings)(Object)this);

	@Unique
	public EnumOption<EffectDisplayPlace> effectDisplayPlaceEnumOption = new EnumOption<>(thisAs,"catalyst-effect.displayEffectsIn", EffectDisplayPlace.class,EffectDisplayPlace.INVENTORY);

	@Override
	public EnumOption<EffectDisplayPlace> getEffectDisplayPlaceEnumOption() {
		return effectDisplayPlaceEnumOption;
	}
}
