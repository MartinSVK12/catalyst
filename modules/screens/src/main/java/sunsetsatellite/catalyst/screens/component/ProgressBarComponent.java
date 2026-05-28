package sunsetsatellite.catalyst.screens.component;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.client.option.OptionEnum;
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
	public Type type = Type.HORIZONTAL;

	public ImageComponent background = new ImageComponent("progress_bar_background", 0, 0);
	public ImageComponent foreground = new ImageComponent("progress_bar_foreground", 0, 0);
	public final List<GuiComponent> components = List.of(background, foreground);

	public enum Type {
		HORIZONTAL, VERTICAL,
		HORIZONTAL_INVERTED
	}

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
		onSizeChanged.connect((s, t)->{
			background.xSize = t.xSize();
			foreground.xSize = t.xSize();
			background.ySize = t.ySize();
			foreground.ySize = t.ySize();
			background.uScale = t.xSize();
			foreground.uScale = t.xSize();
			background.vScale = t.ySize();
			foreground.vScale = t.ySize();
		});
	}

	public int getProgressScaled(int length) {
		if(max == 0) return 0;
		return this.progress * length / max;
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
		((LayoutAbsolute) foreground.layout).setYOffset(0);
		((LayoutAbsolute) foreground.layout).setXOffset(0);
		foreground.v = 0;
		foreground.u = 0;
		foreground.xSize = xSize;
		foreground.ySize = ySize;
		switch (type) {
			case HORIZONTAL -> {
				foreground.xSize = getProgressScaled(xSize);
			}
			case HORIZONTAL_INVERTED -> {
				int counter = getProgressScaled(xSize);
				((LayoutAbsolute) foreground.layout).setXOffset(xSize - counter);
				foreground.u = xSize - counter;
				foreground.xSize = counter;
			}
			case VERTICAL -> {
				int counter = getProgressScaled(ySize);
				((LayoutAbsolute) foreground.layout).setYOffset(ySize - counter);
				foreground.v = ySize - counter;
				foreground.ySize = counter;
			}
		}
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
		map.put("type",new ToggleableOptionComponent<>(new OptionEnum<>("progressBarType", Type.class, type)){
			@Override
			protected void onChanged() {
				setProgress(progress);
				type = this.option.value;
			}
		});
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
	public void writeToNbt(CompoundTag tag) {
		super.writeToNbt(tag);
		tag.putInt("progress", progress);
		tag.putInt("max", max);
		tag.putString("type", type.name());
		CompoundTag bgTag = new CompoundTag();
		background.writeToNbt(bgTag);
		tag.putCompound("background", bgTag);
		CompoundTag fgTag = new CompoundTag();
		foreground.writeToNbt(fgTag);
		tag.putCompound("foreground", fgTag);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		super.readFromNbt(tag);
		max = tag.getInteger("max");
		setProgress(tag.getInteger("progress"));
		type = Type.valueOf(tag.getString("type"));
		background.readFromNbt(tag.getCompound("background"));
		foreground.readFromNbt(tag.getCompound("foreground"));
	}

	@Override
	public String getId() {
		return ID;
	}
}
