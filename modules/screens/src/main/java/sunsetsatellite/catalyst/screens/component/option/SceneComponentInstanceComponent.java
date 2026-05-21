package sunsetsatellite.catalyst.screens.component.option;

import org.jspecify.annotations.Nullable;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;

public abstract class SceneComponentInstanceComponent extends ButtonWithLabelComponent {
	public final GuiComponent component;

	public SceneComponentInstanceComponent(String name, GuiComponent component, @Nullable String tooltipTranslationKey, String buttonText) {
		super(name, tooltipTranslationKey, buttonText);
		this.component = component;
	}
}
