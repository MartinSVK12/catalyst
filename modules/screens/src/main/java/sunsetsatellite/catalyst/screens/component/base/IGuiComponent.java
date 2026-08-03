package sunsetsatellite.catalyst.screens.component.base;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.component.HudComponent;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.options.components.OptionsComponent;

import java.util.Map;

public interface IGuiComponent {
	void render(Screen screen, int xScreenSize, int yScreenSize, float partialTick);

	void renderComponent(Minecraft mc, Screen screen, int x, int y, int xScreenSize, int yScreenSize, float partialTick);

	void renderComponentPreview(Minecraft mc, Gui gui, Layout layout, int x, int y, int xScreenSize, int yScreenSize);

	Map<String, OptionsComponent> getProperties();
	void addOptions();

	String getName();
	String getId();

	HudComponent hud();

	void writeToNbt(CompoundTag tag);
	void readFromNbt(CompoundTag tag);
}
