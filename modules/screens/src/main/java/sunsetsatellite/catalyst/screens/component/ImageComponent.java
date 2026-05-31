package sunsetsatellite.catalyst.screens.component;

import com.mojang.nbt.tags.CompoundTag;
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
import sunsetsatellite.catalyst.screens.util.Colors;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;



public class ImageComponent extends GuiComponent {

	public static final String ID = "image";

	public int u = 0;
	public int v = 0;
	public int uScale = 256;
	public int vScale = 256;
	public String imageId = "/assets/minecraft/textures/gui/container/container.png";

	public ImageComponent(String name, float x, float y) {
		super(name, 176, 221, new LayoutAbsolute(x,y, ComponentAnchor.TOP_LEFT));
	}



	@Override
	public void renderComponent(Minecraft mc, Screen screen, int x, int y, int xScreenSize, int yScreenSize, float partialTick) {
		if(!visible) return;
		if(imageId != null && !imageId.isEmpty()) {
			drawIcon(x, y, this.xSize, this.ySize, this.u, this.v, this.uScale, this.vScale, imageId, Colors.WHITE);
		}
	}

	@Override
	public void renderComponentPreview(Minecraft mc, Gui gui, Layout layout, int x, int y, int xScreenSize, int yScreenSize) {
		if(imageId != null && !imageId.isEmpty()) {
			drawIcon(x,y,this.xSize, this.ySize, this.u, this.v, this.uScale, this.vScale, imageId, Colors.WHITE);
		}
	}

	public void changeImage(String id) {
		imageId = id;
	}

	@Override
	public Map<String, OptionsComponent> getProperties() {
		Map<String, OptionsComponent> map = new TreeMap<>();
		map.put("image",new BasicTextFieldComponent(lang("image"), null, imageId,
			()->{
				imageId = "/assets/minecraft/textures/gui/background.png";
			},
			(t)->{
				changeImage(t.getText());
			})
		);
		PropertyCategory uvCategory = new PropertyCategory(lang("uv"));
		uvCategory.withComponent("u",new BasicTextFieldComponent(lang("u"), null,
			String.valueOf(u),
			()-> u = 0,
			(t) -> u = Catalyst.parseIntSafe(t.getText())
		));
		uvCategory.withComponent("uScale",new BasicTextFieldComponent(lang("uScale"), null,
			String.valueOf(uScale),
			()-> uScale = 256,
			(t) -> uScale = Catalyst.parseIntSafe(t.getText())
		));
		uvCategory.withComponent("v",new BasicTextFieldComponent(lang("v"), null,
			String.valueOf(v),
			()-> v = 0,
			(t) -> v = Catalyst.parseIntSafe(t.getText())
		));
		uvCategory.withComponent("vScale",new BasicTextFieldComponent(lang("vScale"), null,
			String.valueOf(vScale),
			()-> vScale = 256,
			(t) -> vScale = Catalyst.parseIntSafe(t.getText())
		));
		map.put("uv",uvCategory);
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
		tag.putString("image", imageId);
		CompoundTag uvTag = new CompoundTag();
		uvTag.putInt("u", u);
		uvTag.putInt("v", v);
		uvTag.putInt("uScale", uScale);
		uvTag.putInt("vScale", vScale);
		tag.put("uv", uvTag);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		super.readFromNbt(tag);
		imageId = tag.getString("image");
		CompoundTag uvTag = tag.getCompound("uv");
		u = uvTag.getInteger("u");
		v = uvTag.getInteger("v");
		uScale = uvTag.getInteger("uScale");
		vScale = uvTag.getInteger("vScale");
	}
}
