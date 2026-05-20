package sunsetsatellite.catalyst.screens.component;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.screens.component.option.BasicTextFieldComponent;
import sunsetsatellite.catalyst.screens.util.Colors;

import static sunsetsatellite.catalyst.CatalystScreens.lang;

public class ImageComponent extends GuiComponent {

	public static final String ID = "image";

	public Gui gui;
	public int xScreenSize;
	public int yScreenSize;
	public int posX = 0;
	public int posY = 0;
	public int u = 0;
	public int v = 0;
	public String imageId = "/assets/minecraft/textures/gui/container/container.png";

	public ImageComponent(String name, float x, float y) {
		super(name, 176, 221, new LayoutAbsolute(x,y, ComponentAnchor.CENTER));
	}

	public int generateOriginalPosY() {
		return getLayout().getComponentY(this, yScreenSize);
	}

	public int generateOriginalPosX() {
		return getLayout().getComponentX(this, xScreenSize);
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public void render(HudIngame hudIngame, int xScreenSize, int yScreenSize, float partialTick) {

	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int xScreenSize, int yScreenSize) {
		this.gui = gui;
		this.xScreenSize = xScreenSize;
		this.yScreenSize = yScreenSize;
		posY  = generateOriginalPosY();
		posX = generateOriginalPosX();
		renderComponentPreview(mc, gui, layout, xScreenSize, yScreenSize);
	}

	@Override
	public void render(Screen screen, int xScreenSize, int yScreenSize, float partialTick) {
		this.gui = screen;
		this.xScreenSize = xScreenSize;
		this.yScreenSize = yScreenSize;
		posY = generateOriginalPosY();
		posX = generateOriginalPosX();
		renderComponent(mc, screen, xScreenSize, yScreenSize, partialTick);
	}

	@Override
	public void renderComponent(Minecraft mc, Screen screen, int xSize, int ySize, float partialTick) {
		if(imageId != null && !imageId.isEmpty()) {
			drawIcon(posX, posY, this.xSize, this.ySize, this.u, this.v, imageId, Colors.WHITE);
		}
	}

	@Override
	public void renderComponentPreview(Minecraft mc, Gui gui, Layout layout, int xSize, int ySize) {
		if(imageId != null && !imageId.isEmpty()) {
			drawIcon(posX, posY, this.xSize, this.ySize, this.u, this.v, imageId, Colors.WHITE);
		}
	}

	public void drawIcon(double x, double y, double w, double h, double u, double v, String path, int color){
		float uScale = 0.00390625F;
		float vScale = 0.00390625F;
		GLRenderer.pushFrame();
		float r = (float)(color >> 16 & 0xFF) / 255.0f;
		float g = (float)(color >> 8 & 0xFF) / 255.0f;
		float b = (float)(color & 0xFF) / 255.0f;
		GLRenderer.setColor4f(r, g, b, 1f);

		mc.textureManager.loadTexture(path).bind();

		TessellatorGeneral tessellator = GLRenderer.getTessellator();
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, zLevel, (float)(u + 0) * uScale, (float)(v + h) * vScale);
		tessellator.addVertexWithUV(x + w, y + h, zLevel, (float)(u + w) * uScale, (float)(v + h) * vScale);
		tessellator.addVertexWithUV(x + w, y + 0, zLevel, (float)(u + w) * uScale, (float)(v + 0) * vScale);
		tessellator.addVertexWithUV(x + 0, y + 0, zLevel, (float)(u + 0) * uScale, (float)(v + 0) * vScale);
		tessellator.draw();
		GLRenderer.popFrame();
	}

	@Override
	public String getName() {
		return getKey();
	}

	public void changeImage(String id) {
		imageId = id;
	}

	@Override
	protected void addDefaultOptionSuppliers() {
		super.addDefaultOptionSuppliers();
		addOptionComponentSupplier(()->new BasicTextFieldComponent(lang("image"), null, imageId,
			()->{
				imageId = "/assets/minecraft/textures/gui/background.png";
			},
			(t)->{
				changeImage(t.getText());
			}));
		OptionsCategory uvCategory = new OptionsCategory(lang("uv"));
		uvCategory.withComponent(new BasicTextFieldComponent(lang("u"), null,
			String.valueOf(u),
			()-> u = 0,
			(t) -> u = Catalyst.parseIntSafe(t.getText())
		));
		uvCategory.withComponent(new BasicTextFieldComponent(lang("v"), null,
			String.valueOf(v),
			()-> v = 0,
			(t) -> v = Catalyst.parseIntSafe(t.getText())
		));
		addOptionComponentSupplier(()->uvCategory);
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
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		super.readFromNbt(tag);
		imageId = tag.getString("image");
		CompoundTag uvTag = tag.getCompound("uv");
		u = uvTag.getInteger("u");
		v = uvTag.getInteger("v");
	}
}
