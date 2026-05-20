package sunsetsatellite.catalyst.screens.util;

import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.screens.component.IGuiComponent;

import java.util.HashMap;
import java.util.Map;

public class GuiComponents {

	private static final Map<String, Class<? extends IGuiComponent>> components = new HashMap<>();

	public static Map<String, Class<? extends IGuiComponent>> getComponents() {
		return components;
	}

	public static @Nullable Class<? extends IGuiComponent> getComponent(String name) {
		return components.get(name);
	}

	public static <T extends Class<? extends IGuiComponent>> T register(String name, T component) {
		components.put(name, component);
		return component;
	}

}
