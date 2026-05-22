package sunsetsatellite.catalyst.screens.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.gui.options.components.OptionsComponent;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;
import sunsetsatellite.catalyst.screens.component.base.HasServerComponent;
import sunsetsatellite.catalyst.screens.component.server.InventoryServerComponent;
import sunsetsatellite.catalyst.screens.component.server.ServerComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryComponent extends GuiComponent implements HasServerComponent {

	public static final String ID = "inventory";

	public List<SlotComponent> slots = new ArrayList<>();

	public InventoryComponent(String name, float x, float y) {
		super(name, 162, 76, new LayoutAbsolute(x,y, ComponentAnchor.TOP_LEFT));

		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 9; i++) {
				SlotComponent slot = new SlotComponent("slot_" + (i + j * 9 + 9), 0, 0);
				((LayoutAbsolute) slot.layout).setXOffset(18 * i);
				((LayoutAbsolute) slot.layout).setYOffset(j * 18);
				slot.index = i + j * 9 + 9;
				slots.add(slot);
			}

		}

		for (int k = 0; k < 9; k++) {
			SlotComponent slot = new SlotComponent("slot_" + k, 0, 0);
			((LayoutAbsolute) slot.layout).setXOffset((18 * k));
			((LayoutAbsolute) slot.layout).setYOffset((3 * 18) + 4);
			slot.index = k;
			slots.add(slot);
		}
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public void renderComponent(Minecraft mc, Screen screen, int x, int y, int xScreenSize, int yScreenSize, float partialTick) {
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
		return Map.of();
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public ServerComponent toServer() {
		return new InventoryServerComponent(realX()+1, realY()+1);
	}
}
