package sunsetsatellite.catalyst.screens.component;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.gui.options.components.BooleanToggleComponent;
import net.minecraft.client.gui.options.components.OptionsComponent;
import sunsetsatellite.catalyst.core.util.Signal;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;
import sunsetsatellite.catalyst.screens.component.option.PropertyCategory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ButtonComponent extends GuiComponent {

	public static final String ID = "button";

	public final IconComponent image = new IconComponent("btn_icon", 0, 0);
	public final TextComponent text = new TextComponent("btn_text", 0,0);
	public final List<GuiComponent> components = List.of(image, text);

	public record Clicked(ButtonComponent button, int mx, int my, int buttonIdx){};
	public final Signal<Clicked> buttonClicked = new Signal<>("button_clicked");

	public boolean disabled = false;

	public ButtonComponent(String name, float x, float y) {
		super(name, 200, 20, new LayoutAbsolute(x,y, ComponentAnchor.TOP_LEFT));
		text.text = "Button";
		text.centered = true;
		text.color = 0xE0E0E0;
		image.setIcon("minecraft:gui/widgets/button/button");
		onClick.connect((s, t)->{
			if(disabled || !visible) return;
			s.consume();
			buttonClicked.emit(new Clicked(this, t.mx(), t.my(), t.button()));
		});
		onHoverStart.connect((s, t)->{
			if(disabled || !visible) return;
			image.setIcon("minecraft:gui/widgets/button/button_highlighted");
		});
		onHoverEnd.connect((s, t)->{
			if(disabled || !visible) return;
			image.setIcon("minecraft:gui/widgets/button/button");
		});
	}

	@Override
	public void renderComponent(Minecraft mc, Screen screen, int x, int y, int xScreenSize, int yScreenSize, float partialTick) {
		if(!visible) return;
		text.offY = (ySize / 2) - 4;
		for (GuiComponent component : components) {
			setSubComponentRenderProperties(gui, component, x, y,true);
			component.renderComponent(mc, screen, component.posX, component.posY, xScreenSize, yScreenSize, partialTick);
		}
	}

	@Override
	public void renderComponentPreview(Minecraft mc, Gui gui, Layout layout, int x, int y, int xScreenSize, int yScreenSize) {
		text.offY = (ySize / 2) - 4;
		for (GuiComponent component : components) {
			setSubComponentRenderProperties(gui, component, x, y,true);
			component.renderComponentPreview(mc, gui, layout, component.posX, component.posY, xScreenSize, yScreenSize);
		}
	}

	@Override
	public Map<String, OptionsComponent> getProperties() {
		Map<String, OptionsComponent> map = new TreeMap<>();
		map.put("disabled",new BooleanToggleComponent(lang("disabled"),disabled,
			()-> disabled,
			(b)-> {
				disabled = b;
				if(disabled){
					text.color = 0xA0A0A0;
					image.setIcon("minecraft:gui/widgets/button/button_disabled");
				} else {
					text.color = 0xE0E0E0;
					image.setIcon("minecraft:gui/widgets/button/button");
				}
			})
		);
		Map<String, OptionsComponent> textProperties = text.getProperties();
		((PropertyCategory) textProperties.get("textCategory")).remove("color");
		((PropertyCategory) textProperties.get("textCategory")).remove("align");
		map.putAll(textProperties);
		return map;
	}

	@Override
	public void addOptions() {
		super.addOptions();
		for (OptionsComponent property : getProperties().values()) {
			addOptionComponentSupplier(()->property);
		}
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		super.writeToNbt(tag);
		tag.putBoolean("disabled", disabled);
		CompoundTag textTag = new CompoundTag();
		text.writeToNbt(textTag);
		tag.put("text", textTag);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		super.readFromNbt(tag);
		disabled = tag.getBoolean("disabled");
		CompoundTag textTag = tag.getCompound("text");
		text.readFromNbt(textTag);
	}
}
