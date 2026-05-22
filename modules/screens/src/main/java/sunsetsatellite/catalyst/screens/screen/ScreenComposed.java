package sunsetsatellite.catalyst.screens.screen;

import net.minecraft.client.gui.Screen;
import net.minecraft.client.render.renderer.GLRenderer;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.CatalystScreens;
import sunsetsatellite.catalyst.CatalystScreensClient;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

		for (final GuiComponent c : sortedValues()) {
			if (c.isVisible()) {
				GLRenderer.pushFrame();
				c.renderComponentScaled(this, width, height, partialTick);
				GLRenderer.popFrame();
				if(isHoveringOverComponent(c, mx, my)){
					if(!c.hovering){
						c.hovering = true;
						c.onHoverStart.emit(new GuiComponent.Hovered(c, mx,my));
					}
					c.onHover.emit(new GuiComponent.Hovered(c, mx,my));
				} else {
					if(c.hovering){
						c.hovering = false;
						c.onHoverEnd.emit(new GuiComponent.Hovered(c, mx,my));
					}
				}
			}
		}

		super.render(mx, my, partialTick);
	}

	public boolean isHoveringOverComponent(GuiComponent component, int mx, int my){
		return mx >= component.realX() && my >= component.realY() && mx <= component.realX() + component.xSize && my <= component.realY() + component.ySize;
	}

	public List<GuiComponent> sortedValues(){
		return components.values().stream().sorted(Comparator.comparingInt((c)->c.zLevel)).toList();
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonNum) {
		super.mouseClicked(mx, my, buttonNum);
		for (final GuiComponent c :sortedValues()) {
			if (c.isVisible()) {
				if(isHoveringOverComponent(c, mx, my)){
					c.onClick.emit(new GuiComponent.Clicked(c, mx, my, buttonNum));
				}
			}
		}
	}
}
