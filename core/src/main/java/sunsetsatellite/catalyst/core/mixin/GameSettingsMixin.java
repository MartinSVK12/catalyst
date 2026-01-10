package sunsetsatellite.catalyst.core.mixin;


import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.OptionBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.IKeybinds;

@Mixin(
	value = GameSettings.class,
	remap = false
)
public class GameSettingsMixin implements IKeybinds {
	private final GameSettings thisAs = ((GameSettings) (Object) this);

	@Unique
	OptionBoolean networkRenderOption = new OptionBoolean(thisAs, "catalyst-core.showNetworkRender", false);

	@Override
	public OptionBoolean getNetworkRenderOption() {
		return networkRenderOption;
	}
}
