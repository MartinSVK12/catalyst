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
import sunsetsatellite.catalyst.screens.component.server.ServerComponent;
import sunsetsatellite.catalyst.screens.component.server.SlotGridServerComponent;
import sunsetsatellite.catalyst.screens.util.SlotType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SlotGridComponent extends GuiComponent implements HasServerComponent {

	public static final String ID = "slotGrid";

	public List<SlotComponent> slots = new ArrayList<>();

	public int rows = 3;
	public int columns = 3;

	public SlotType type = SlotType.INVENTORY;

	public SlotGridComponent(String name, float x, float y) {
		super(name, 18*3, 18*3, new LayoutAbsolute(x,y, ComponentAnchor.TOP_LEFT));

		resize(rows, columns);
	}

	@Override
	public void renderComponent(Minecraft mc, Screen screen, int x, int y, int xScreenSize, int yScreenSize, float partialTick) {
		if(!visible) return;
		for (GuiComponent component : slots) {
			component.gui = gui;
			component.xScreenSize = xSize;
			component.yScreenSize = ySize;
			component.posX = x + component.realX();
			component.posY = y + component.realY();
			component.renderComponent(mc, screen, component.posX, component.posY, xScreenSize, yScreenSize, partialTick);
		}
	}

	@Override
	public void renderComponentPreview(Minecraft mc, Gui gui, Layout layout, int x, int y, int xScreenSize, int yScreenSize) {
		for (GuiComponent component : slots) {
			component.gui = gui;
			component.xScreenSize = xSize;
			component.yScreenSize = ySize;
			component.posX = x + component.realX();
			component.posY = y + component.realY();
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

	@Override
	public Map<String, OptionsComponent> getProperties() {
		Map<String, OptionsComponent> map = new TreeMap<>();
		map.put("rows", new BasicTextFieldComponent(lang("rows"), null, String.valueOf(rows),
			()->{
				rows = 3;
			},
			(t)->{
				rows = Catalyst.parseIntSafe(t.getText());
				resize(rows, columns);
			})
		);
		map.put("columns", new BasicTextFieldComponent(lang("columns"), null, String.valueOf(columns),
			()->{
				columns = 3;
			},
			(t)->{
				columns = Catalyst.parseIntSafe(t.getText());
				resize(rows, columns);
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

	public void resize(int rows, int columns){
		this.columns = columns;
		this.rows = rows;
		xSize = 18*columns;
		ySize = 18*rows;

		slots.clear();

		for (int j = 0; j < rows; j++) {
			for (int i = 0; i < columns; i++) {
				SlotComponent slot = new SlotComponent("slot_" + (i + j * columns), 0, 0);
				((LayoutAbsolute) slot.layout).setXOffset(18 * i);
				((LayoutAbsolute) slot.layout).setYOffset(j * 18);
				slot.type = type;
				slot.index = i + j * columns;
				slots.add(slot);
			}

		}
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public ServerComponent toServer() {
		return new SlotGridServerComponent(realX()+1, realY()+1, rows, columns, type);
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		super.writeToNbt(tag);
		tag.putInt("rows", rows);
		tag.putInt("columns", columns);
		tag.putInt("type", type.ordinal());
		//tag.putString("icon", image.iconId);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		super.readFromNbt(tag);
		rows = tag.getInteger("rows");
		columns = tag.getInteger("columns");
		this.type = SlotType.values()[tag.getInteger("type")];
		//image.setIcon(tag.getString("icon"));
		resize(rows, columns);
	}
}
