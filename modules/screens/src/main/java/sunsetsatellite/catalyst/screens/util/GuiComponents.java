package sunsetsatellite.catalyst.screens.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.screens.component.*;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;
import sunsetsatellite.catalyst.screens.component.base.IGuiComponent;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
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

	static {
		register(TextComponent.ID, TextComponent.class);
		register(ImageComponent.ID, ImageComponent.class);
		register(SubsceneComponent.ID, SubsceneComponent.class);
		register(ButtonComponent.ID, ButtonComponent.class);
		register(IconComponent.ID, IconComponent.class);
		register(SlotComponent.ID, SlotComponent.class);
		register(InventoryComponent.ID, InventoryComponent.class);
	}

}
