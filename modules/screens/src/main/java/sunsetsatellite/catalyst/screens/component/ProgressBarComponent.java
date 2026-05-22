package sunsetsatellite.catalyst.screens.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.gui.options.components.OptionsComponent;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;
import sunsetsatellite.catalyst.screens.component.option.BasicTextFieldComponent;
import sunsetsatellite.catalyst.screens.component.option.PropertyCategory;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ProgressBarComponent extends GuiComponent {

	public static final String ID = "progressBar";

	public int progress = 0;
	public int max = 100;

	public ImageComponent background = new ImageComponent("progress_bar_background", 0, 0);
	public ImageComponent foreground = new ImageComponent("progress_bar_foreground", 0, 0);
	public final List<GuiComponent> components = List.of(background, foreground);

	public ProgressBarComponent(String name, float x, float y) {
		super(name, 24, 18, new LayoutAbsolute(x,y, ComponentAnchor.TOP_LEFT));
		background.xSize = xSize;
		background.ySize = ySize;
		foreground.xSize = xSize;
		foreground.ySize = ySize;
		background.uScale = xSize;
		background.vScale = ySize;
		foreground.uScale = xSize;
		foreground.vScale = ySize;
		background.changeImage("/assets/catalyst-screens/textures/gui/sprites/arrow_empty.png");
		foreground.changeImage("/assets/catalyst-screens/textures/gui/sprites/arrow_filled.png");
	}

	public int getProgressScaled(int length) {
		if(max == 0) return 0;
		return this.progress * length / max;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public void renderComponent(Minecraft mc, Screen screen, int x, int y, int xScreenSize, int yScreenSize, float partialTick) {
		for (GuiComponent component : components) {
			setSubComponentRenderProperties(gui, component, x, y,false);
			component.renderComponent(mc, screen, component.posX, component.posY, xScreenSize, yScreenSize, partialTick);
		}
	}

	@Override
	public void renderComponentPreview(Minecraft mc, Gui gui, Layout layout, int x, int y, int xScreenSize, int yScreenSize) {
		for (GuiComponent component : components) {
			setSubComponentRenderProperties(gui, component, x, y,false);
			component.renderComponentPreview(mc, gui, layout, component.posX, component.posY, xScreenSize, yScreenSize);
		}
	}

	@Override
	public void addOptions() {
		super.addOptions();
		for (OptionsComponent property : getProperties().values()) {
			addOptionComponentSupplier(()->property);
		}
	}

	public void setProgress(int value) {
		this.progress = Math.min(max,value);
		int counter = getProgressScaled(xSize);
		foreground.xSize = counter+1;
	}

	@Override
	public Map<String, OptionsComponent> getProperties() {
		Map<String, OptionsComponent> map = new TreeMap<>();
		map.put("progress", new BasicTextFieldComponent(lang("progress"),null,String.valueOf(progress),
			()->{
				progress = 0;
			},
			(t)->{
				setProgress(Catalyst.parseIntSafe(t.getText()));
			}
		));
		map.put("max", new BasicTextFieldComponent(lang("max"),null,String.valueOf(max),
			()->{
				max = 100;
			},
			(t)->{
				max = Math.max(1,Catalyst.parseIntSafe(t.getText()));
			}
		));
		PropertyCategory fg = new PropertyCategory(lang("foreground"));
		PropertyCategory bg = new PropertyCategory(lang("background"));
		Map<String, OptionsComponent> fgProps = foreground.getProperties();
		fgProps.remove("uv");
		fgProps.forEach(fg::withComponent);
		Map<String, OptionsComponent> bgProps = background.getProperties();
		bgProps.remove("uv");
		bgProps.forEach(bg::withComponent);
		map.put("foreground",fg);
		map.put("background",bg);
		return map;
	}

	@Override
	public String getId() {
		return ID;
	}
}
