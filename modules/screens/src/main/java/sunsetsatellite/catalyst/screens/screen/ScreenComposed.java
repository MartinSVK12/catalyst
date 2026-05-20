package sunsetsatellite.catalyst.screens.screen;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.component.HudComponent;
import net.minecraft.client.render.renderer.GLRenderer;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.CatalystScreens;
import sunsetsatellite.catalyst.screens.component.GuiComponent;

import java.util.HashMap;
import java.util.Map;

public class ScreenComposed extends Screen {

	public ScreenComposed(Map<String, HudComponent> components) {
		this(components, null);
	}

	public ScreenComposed(Map<String, HudComponent> components, @Nullable Screen parent) {
		super(parent);
		this.components.putAll(components);
	}

	public ScreenComposed(String scenePath, @Nullable Screen parent) {
		super(parent);
		load(CatalystScreens.loadScene(scenePath));
	}

	public ScreenComposed(String scenePath) {
		this(scenePath, null);
	}

	public final Map<String, HudComponent> components = new HashMap<>();

	public void load(CompoundTag tag){
		if(tag == null) return;
		for (Tag<?> value : tag.getValues()) {
			components.put(value.getTagName(), GuiComponent.create(((CompoundTag) value)));
		}
		GuiComponent.incompleteLinks.forEach(((layout, name) -> {
			if(components.get(name) == null){
				CatalystScreens.LOGGER.error("Could not find component with id: {}", name);
			}
			layout.setParent(components.get(name));
		}));
		GuiComponent.incompleteLinks.clear();
	}

	@Override
	public void render(int mx, int my, float partialTick) {
		int width = this.mc.resolution.getScaledWidthScreenCoords();
		int height = this.mc.resolution.getScaledHeightScreenCoords();

		for (final HudComponent c : components.values()) {
			if(c instanceof GuiComponent component){
				if (component.isVisible()) {
					GLRenderer.pushFrame();
					component.renderComponentScaled(this, width, height, partialTick);
					GLRenderer.popFrame();
				}
			}
		}

		super.render(mx, my, partialTick);
	}
}
