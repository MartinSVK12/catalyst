package sunsetsatellite.catalyst.screens.util;

import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;
import sunsetsatellite.catalyst.screens.component.base.IGuiComponent;

import java.util.HashMap;
import java.util.Map;

public class GuiComponents {

	private static final Map<String, Class<? extends GuiComponent>> components = new HashMap<>();

	public static Map<String, Class<? extends GuiComponent>> getComponents() {
		return components;
	}

	public static @Nullable Class<? extends GuiComponent> getComponent(String name) {
		return components.get(name);
	}

	public static <T extends Class<? extends GuiComponent>> T register(String name, T component) {
		components.put(name, component);
		return component;
	}

}
