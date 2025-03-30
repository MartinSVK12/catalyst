package sunsetsatellite.catalyst.multipart.mixin;

import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.OptionBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.multipart.interfaces.mixins.IKeybinds;

@Mixin(
        value = GameSettings.class,
        remap = false
)
public class GameSettingsMixin
    implements IKeybinds
{
	private final GameSettings thisAs = ((GameSettings)(Object)this);

	@Unique
	OptionBoolean showMultipartsInTMB = new OptionBoolean(thisAs,"catalyst-multipart.showMultipartsInTMB",false);

	@Override
	public OptionBoolean showMultipartsInTMB() {
		return showMultipartsInTMB;
	}

}
