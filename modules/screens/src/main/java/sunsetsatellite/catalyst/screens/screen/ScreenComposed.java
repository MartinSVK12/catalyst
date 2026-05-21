package sunsetsatellite.catalyst.screens.screen;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.component.HudComponent;
import net.minecraft.client.render.renderer.GLRenderer;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.CatalystScreens;
import sunsetsatellite.catalyst.CatalystScreensClient;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;

import java.util.HashMap;
import java.util.Map;

public class ScreenComposed extends Screen {

	public ScreenComposed(Map<String, GuiComponent> components) {
		this(components, null);
	}

	public ScreenComposed(Map<String, GuiComponent> components, @Nullable Screen parent) {
		super(parent);
		this.components.putAll(components);
	}

	public ScreenComposed(String scenePath, @Nullable Screen parent) {
		super(parent);
		CatalystScreensClient.loadScene(CatalystScreens.loadSceneNbt(scenePath), components);
	}

	public ScreenComposed(String scenePath) {
		this(scenePath, null);
	}

	public final Map<String, GuiComponent> components = new HashMap<>();

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
