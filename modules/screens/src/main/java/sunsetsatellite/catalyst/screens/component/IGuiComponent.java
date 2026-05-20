package sunsetsatellite.catalyst.screens.component;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.component.layout.Layout;

public interface IGuiComponent {
	void render(Screen screen, int xScreenSize, int yScreenSize, float partialTick);

	void renderComponent(Minecraft mc, Screen screen, int xSize, int ySize, float partialTick);

	void renderComponentPreview(Minecraft mc, Gui gui, Layout layout, int xSize, int ySize);

	String getName();
	String getId();

	void writeToNbt(CompoundTag tag);
	void readFromNbt(CompoundTag tag);
}
