package sunsetsatellite.catalyst.core.mixin;


import net.minecraft.client.option.BooleanOption;
import net.minecraft.client.option.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.core.interfaces.mixins.IKeybinds;

@Mixin(
        value = GameSettings.class,
        remap = false
)
public class GameSettingsMixin implements IKeybinds
{
	private final GameSettings thisAs = ((GameSettings)(Object)this);

	@Unique
	BooleanOption networkRenderOption = new BooleanOption(thisAs,"catalyst-core.showNetworkRender",false);

	@Override
	public BooleanOption getNetworkRenderOption() {
		return networkRenderOption;
	}
}
