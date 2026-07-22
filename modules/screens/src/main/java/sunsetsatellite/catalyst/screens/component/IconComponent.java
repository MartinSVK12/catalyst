package sunsetsatellite.catalyst.screens.component;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.client.render.Scissor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.lwjgl.opengl.GL41;
import sunsetsatellite.catalyst.CatalystScreens;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;
import sunsetsatellite.catalyst.screens.component.option.BasicTextFieldComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;



public class IconComponent extends GuiComponent {

	public static final String ID = "icon";
	public static final Gui draw = new Gui();

	public IconCoordinate icon = null;
	public String iconId = "";

	public IconComponent(String name, float x, float y) {
		super(name, 64, 64, new LayoutAbsolute(x,y, ComponentAnchor.TOP_LEFT));
	}


	@Override
	public void renderComponent(Minecraft mc, Screen screen, int x, int y, int xScreenSize, int yScreenSize, float partialTick) {
		if(!visible) return;
		if(icon == null) return;
		draw.drawGuiIcon(x,y,xSize,ySize,icon);
	}

	@Override
	public void renderComponentPreview(Minecraft mc, Gui gui, Layout layout, int x, int y, int xScreenSize, int yScreenSize) {
		if(icon == null) return;
		Scissor.enable(x,y,xSize,ySize);
		draw.drawGuiIcon(x,y,xSize,ySize,icon);
		Scissor.disable();
	}

	@Override
	public Map<String, OptionsComponent> getProperties() {
		Map<String, OptionsComponent> map = new TreeMap<>();
		map.put("icon",new BasicTextFieldComponent(lang("icon"),null,iconId,
			(t)-> setIcon(t.getText())
		));
		return map;
	}

	@Override
	public void addOptions() {
		super.addOptions();
		for (OptionsComponent property : getProperties().values()) {
			addOptionComponentSupplier(()->property);
		}
	}

	public void setIcon(String id){
		iconId = id;
		icon = null;
		try {
			icon = TextureRegistry.getTexture(NamespaceID.fromPool(id));
			if(icon.height == 0 && icon.width == 0){
				CatalystScreens.LOGGER.warn("Icon id '{}' returned empty icon.", id);
			}
		} catch (HardIllegalArgumentException e){
			CatalystScreens.LOGGER.error("Invalid icon id: {}", id);
		}
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		super.writeToNbt(tag);
		tag.putString("icon", iconId);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		super.readFromNbt(tag);
		setIcon(tag.getString("icon"));
	}
}
