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
import sunsetsatellite.catalyst.screens.component.base.HasServerComponent;
import sunsetsatellite.catalyst.screens.component.option.BasicTextFieldComponent;
import sunsetsatellite.catalyst.screens.component.server.SlotServerComponent;
import sunsetsatellite.catalyst.screens.util.SlotType;

import java.util.Map;
import java.util.TreeMap;

public class SlotComponent extends GuiComponent implements HasServerComponent {

	public final IconComponent image = new IconComponent("slot_icon", 0, 0);
	public static final String ID = "slot";

	public int index = 0;
	public SlotType type = SlotType.INVENTORY;

	public SlotComponent(String name, float x, float y) {
		super(name, 18, 18, new LayoutAbsolute(x,y, ComponentAnchor.TOP_LEFT));
		image.xSize = 18;
		image.ySize = 18;
		image.setIcon("catalyst-screens:gui/slot");
		onSizeChanged.connect((s, t)->{
			image.xSize = t.xSize();
			image.ySize = t.ySize();
		});
	}



	@Override
	public void renderComponent(Minecraft mc, Screen screen, int x, int y, int xScreenSize, int yScreenSize, float partialTick) {
		if(!visible) return;
		setSubComponentRenderProperties(gui, image, x,y,true);
		image.renderComponent(mc, screen, posX, posY, xScreenSize, yScreenSize, partialTick);
	}

	@Override
	public void renderComponentPreview(Minecraft mc, Gui gui, Layout layout, int x, int y, int xScreenSize, int yScreenSize) {
		setSubComponentRenderProperties(gui, image, x,y,true);
		image.renderComponentPreview(mc, gui, layout, posX,posY, xScreenSize, yScreenSize);
	}

	@Override
	public Map<String, OptionsComponent> getProperties() {
		Map<String, OptionsComponent> map = new TreeMap<>();
		map.put("icon",	image.getProperties().get("icon"));
		map.put("index", new BasicTextFieldComponent(lang("index"), null, String.valueOf(index),
			()->{
				index = 0;
			},
			(t)->{
				index = Catalyst.parseIntSafe(t.getText());
			})
		);
		map.put("type", new ToggleableOptionComponent<>(new OptionEnum<>("slotType", SlotType.class, type)){
			@Override
			protected void onChanged() {
				type = this.option.value;
			}
		});
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
	public SlotServerComponent toServer(){
		int x = realX() + 1;
		int y = realY() + 1;
		return new SlotServerComponent(index, x, y, xSize, ySize, type);
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		super.writeToNbt(tag);
		tag.putInt("index", index);
		tag.putInt("type", type.ordinal());
		tag.putString("icon", image.iconId);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		super.readFromNbt(tag);
		this.index = tag.getInteger("index");
		this.type = SlotType.values()[tag.getInteger("type")];
		image.setIcon(tag.getString("icon"));
	}
}
