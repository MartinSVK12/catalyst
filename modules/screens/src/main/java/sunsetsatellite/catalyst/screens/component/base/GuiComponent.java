package sunsetsatellite.catalyst.screens.component.base;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponent;
import net.minecraft.client.gui.hud.component.HudComponentMovable;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.gui.hud.component.layout.LayoutSnap;
import net.minecraft.client.gui.options.components.KeyBindingComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.client.option.OptionEnum;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.Signal;
import sunsetsatellite.catalyst.screens.component.option.BasicTextFieldComponent;
import sunsetsatellite.catalyst.screens.component.server.ServerComponent;
import sunsetsatellite.catalyst.screens.component.server.SlotServerComponent;
import sunsetsatellite.catalyst.screens.util.GuiComponents;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;



public abstract class GuiComponent extends HudComponentMovable implements IGuiComponent {

	public int zLevel = 0;
	public Gui gui;
	public int xScreenSize;
	public int yScreenSize;
	public int posX = 0;
	public int posY = 0;

	public record Hovered(GuiComponent component, int mx, int my){};
	public final Signal<Hovered> onHover = new Signal<>("on_hover");
	public final Signal<Hovered> onHoverStart = new Signal<>("on_hover_start");
	public final Signal<Hovered> onHoverEnd = new Signal<>("on_hover_end");
	public boolean hovering = false;
	public record Clicked(GuiComponent component, int mx, int my, int button){};
	public final Signal<Clicked> onClick = new Signal<>("on_click");

	public GuiComponent(String key, int xSize, int ySize, Layout layout) {
		super(key, xSize, ySize, layout);
	}

	@Override
	public HudComponent hud() {
		return this;
	}

	@Override
	public void render(HudIngame hudIngame, int i, int i1, float v) {
		throw new UnsupportedOperationException();
	}

	public int realX() {
		return getLayout().getComponentX(this, xScreenSize);
	}

	public int realY() {
		return getLayout().getComponentY(this, yScreenSize);
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int xScreenSize, int yScreenSize) {
		this.gui = gui;
		this.xScreenSize = xScreenSize;
		this.yScreenSize = yScreenSize;
		posX = realX();
		posY = realY();
		//drawRect(posX, posY, posX+xSize, posY+ySize, 0xFFFFFF00);
		renderComponentPreview(mc, gui, layout, posX, posY, xScreenSize, yScreenSize);
	}

	@Override
	public void render(Screen screen, int xScreenSize, int yScreenSize, float partialTick) {
		this.gui = screen;
		this.xScreenSize = xScreenSize;
		this.yScreenSize = yScreenSize;
		posX = realX();
		posY = realY();
		renderComponent(mc, screen, posX, posY, xScreenSize, yScreenSize, partialTick);
	}

	public void setSubComponentRenderProperties(Gui gui, GuiComponent component, int x, int y, boolean resize) {
		component.gui = gui;
		component.xScreenSize = xSize;
		component.yScreenSize = ySize;
		component.posX = x + component.realX();
		component.posY = y + component.realY();
		if(resize){
			component.xSize = xSize;
			component.ySize = ySize;
		}
	}

	public static String lang(String key){
		return "options.gui."+key;
	}

	@Override
	public String getName() {
		return getKey();
	}

	public static GuiComponent create(CompoundTag tag){
		String id = tag.getString("id");
		try {
			IGuiComponent component = GuiComponents.getComponent(id).getDeclaredConstructor(String.class, float.class, float.class).newInstance("",0.5f, 0.5f);
			component.readFromNbt(tag);
			return (GuiComponent) component;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<LayoutSnap, String> incompleteLinks = new HashMap<>();

	public static Layout createLayout(CompoundTag tag){
		String type = tag.getString("type");
		Layout layout = null;
		if(type.equals("abs")){
			float x = tag.getFloat("x");
			float y = tag.getFloat("y");
			int offsetX = tag.getInteger("offsetX");
			int offsetY = tag.getInteger("offsetY");
			ComponentAnchor anchor = ComponentAnchor.values()[tag.getInteger("anchor")];
			LayoutAbsolute abs = new LayoutAbsolute(x, y, anchor);
			abs.setXOffset(offsetX);
			abs.setYOffset(offsetY);
			layout = abs;
		} else if (type.equals("snap")) {
			int offsetX = tag.getInteger("offsetX");
			int offsetY = tag.getInteger("offsetY");
			ComponentAnchor anchor = ComponentAnchor.values()[tag.getInteger("anchor")];
			ComponentAnchor parentAnchor = ComponentAnchor.values()[tag.getInteger("parentAnchor")];
			String parent = tag.getString("parent");
			LayoutSnap snap = new LayoutSnap(null, parentAnchor, anchor, offsetX, offsetY);
			if(!parent.equals("null")){
				incompleteLinks.put(snap, parent);
			}
			layout = snap;
		}
		return layout;
	}

	public static void saveLayout(CompoundTag tag, Layout layout){
		if (layout instanceof LayoutAbsolute abs) {
			tag.putString("type", "abs");
			tag.putFloat("x", abs.getXPosition());
			tag.putFloat("y", abs.getYPosition());
			tag.putInt("offsetX", abs.getXOffset());
			tag.putInt("offsetY", abs.getYOffset());
			tag.putInt("anchor", abs.getAnchor().ordinal());
		} else if (layout instanceof LayoutSnap snap) {
			tag.putString("type", "snap");
			tag.putInt("offsetX", snap.getXOffset());
			tag.putInt("offsetY", snap.getYOffset());
			tag.putString("parent", snap.getParent() != null ? snap.getParent().getKey() : "null");
			tag.putInt("anchor", snap.getAnchor().ordinal());
			tag.putInt("parentAnchor", snap.getParentAnchor().ordinal());
		}
	}

	public final void renderComponentScaled(Screen screen, int xSizeScreen, int ySizeScreen, float partialTick) {
		render(screen, xSizeScreen, ySizeScreen, partialTick);
		/*float scale = getScale();
		if (scale == 1.0f) {
			int x = this.layout.getComponentX(this, xSizeScreen);
			int y = this.layout.getComponentY(this, ySizeScreen);
			this.xScreenSize = xSizeScreen;
			this.yScreenSize = ySizeScreen;
			GLRenderer.modelM4f().translate(x, y, 0.0f);
			renderComponent(mc, screen, posX, posY, xSizeScreen, ySizeScreen, partialTick);
			return;
		}
		int x = this.layout.getComponentX(this, xSizeScreen);
		int y = this.layout.getComponentY(this, ySizeScreen);
		GLRenderer.modelM4f().translate(x, y, 0.0f);
		GLRenderer.modelM4f().scale(scale, scale, 1.0f);
		GLRenderer.modelM4f().translate(-x, -y, 0.0f);
		renderComponent(mc, screen, posX, posY, xSizeScreen, ySizeScreen, partialTick);*/
	}

	public void drawIcon(double x, double y, double w, double h, double u, double v, double uScale, double vScale, String path, int color){
		double us = 1f/uScale;
		double vs = 1f/vScale;
		GLRenderer.pushFrame();
		float r = (float)(color >> 16 & 0xFF) / 255.0f;
		float g = (float)(color >> 8 & 0xFF) / 255.0f;
		float b = (float)(color & 0xFF) / 255.0f;
		GLRenderer.setColor4f(r, g, b, 1f);

		mc.textureManager.loadTexture(path).bind();

		TessellatorGeneral tessellator = GLRenderer.getTessellator();
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, zLevel, (u + 0) * us, (v + h) * vs);
		tessellator.addVertexWithUV(x + w, y + h, zLevel, (u + w) * us, (v + h) * vs);
		tessellator.addVertexWithUV(x + w, y + 0, zLevel, (u + w) * us, (v + 0) * vs);
		tessellator.addVertexWithUV(x + 0, y + 0, zLevel, (u + 0) * us, (v + 0) * vs);
		tessellator.draw();
		GLRenderer.popFrame();
	}

	public void drawRect(int minX, int minY, int maxX, int maxY, int argb) {
		int temp;
		if (minX < maxX) {
			temp = minX;
			minX = maxX;
			maxX = temp;
		}
		if (minY < maxY) {
			temp = minY;
			minY = maxY;
			maxY = temp;
		}
		float a = (float)(argb >> 24 & 0xFF) / 255.0f;
		float r = (float)(argb >> 16 & 0xFF) / 255.0f;
		float g = (float)(argb >> 8 & 0xFF) / 255.0f;
		float b = (float)(argb & 0xFF) / 255.0f;
		TessellatorGeneral tessellator = GLRenderer.getTessellator();
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setShader(Shaders.COLOR);
		GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
		GLRenderer.setColor4f(r, g, b, a);
		tessellator.startDrawingQuads();
		tessellator.addVertex(minX, maxY, 0.0);
		tessellator.addVertex(maxX, maxY, 0.0);
		tessellator.addVertex(maxX, minY, 0.0);
		tessellator.addVertex(minX, minY, 0.0);
		tessellator.draw();
		GLRenderer.disableState(State.BLEND);
	}

	public static boolean isHoveringOverComponent(GuiComponent component, int mx, int my){
		return mx >= component.realX() && my >= component.realY() && mx <= component.realX() + component.xSize && my <= component.realY() + component.ySize;
	}

	@Override
	protected final void addDefaultOptionSuppliers() {}

	@Override
	public final List<Supplier<KeyBindingComponent>> getKeyBindingSuppliers() {
		return List.of();
	}

	@Override
	public void addOptions() {
		addOptionComponentSupplier(()->new BasicTextFieldComponent(lang("type"), null, getId()).lock());
		addOptionComponentSupplier(()->new BasicTextFieldComponent(lang("id"),null, key,
			()->{},
			(t)-> key = t.getText()
		));
		OptionsCategory sizeCategory = new OptionsCategory(lang("size"));
		sizeCategory.withComponent(new BasicTextFieldComponent(lang("width"), null,
			String.valueOf(xSize),
			()-> xSize = 32,
			(t) -> xSize = Catalyst.parseIntSafe(t.getText())
		));
		sizeCategory.withComponent(new BasicTextFieldComponent(lang("height"), null,
			String.valueOf(ySize),
			()-> ySize = 32,
			(t) -> ySize = Catalyst.parseIntSafe(t.getText())
		));
		addOptionComponentSupplier(()->sizeCategory);
		addOptionComponentSupplier(()->new BasicTextFieldComponent(lang("zLevel"),null, String.valueOf(zLevel),
			()-> zLevel = 0,
			(t)-> zLevel = Catalyst.parseIntSafe(t.getText())
		));
		OptionsCategory layoutCategory = new OptionsCategory(lang("layout"));
		if(layout instanceof LayoutAbsolute abs){
			layoutCategory.withComponent(new BasicTextFieldComponent(lang("x"), null,
				String.valueOf(abs.getXPosition()),
				()-> abs.setXPosition(0),
				(t)-> abs.setXPosition(Catalyst.parseFloatSafe(t.getText()))
			));
			layoutCategory.withComponent(new BasicTextFieldComponent(lang("y"), null,
				String.valueOf(abs.getYPosition()),
				()-> abs.setYPosition(0),
				(t)-> abs.setYPosition(Catalyst.parseFloatSafe(t.getText()))
			));
			layoutCategory.withComponent(new BasicTextFieldComponent(lang("offsetX"), null,
				String.valueOf(abs.getXOffset()),
				()-> abs.setXOffset(0),
				(t)-> abs.setXOffset(Catalyst.parseIntSafe(t.getText()))
			));
			layoutCategory.withComponent(new BasicTextFieldComponent(lang("offsetY"), null,
				String.valueOf(abs.getYOffset()),
				()-> abs.setYOffset(0),
				(t)-> abs.setYOffset(Catalyst.parseIntSafe(t.getText()))
			));
			layoutCategory.withComponent(new ToggleableOptionComponent<>(new OptionEnum<>("anchor", abs.getAnchor().getDeclaringClass(), abs.getAnchor())){
				@Override
				protected void onChanged() {
					abs.setAnchor(this.option.value);
				}
			});
		} else if (layout instanceof LayoutSnap snap) {
			layoutCategory.withComponent(new BasicTextFieldComponent(lang("parent"), null, snap.getParent() != null ? snap.getParent().getKey() : "<null>").lock());
			layoutCategory.withComponent(new ToggleableOptionComponent<>(new OptionEnum<>("parentAnchor", snap.getParentAnchor().getDeclaringClass(), snap.getParentAnchor())){
				@Override
				protected void onChanged() {
					snap.setParentAnchor(this.option.value);
				}
			});
			layoutCategory.withComponent(new BasicTextFieldComponent(lang("offsetX"), null,
				String.valueOf(snap.getXOffset()),
				()-> snap.setXOffset(0),
				(t)-> snap.setXOffset(Catalyst.parseIntSafe(t.getText()))
			));
			layoutCategory.withComponent(new BasicTextFieldComponent(lang("offsetY"), null,
				String.valueOf(snap.getYOffset()),
				()-> snap.setYOffset(0),
				(t)-> snap.setYOffset(Catalyst.parseIntSafe(t.getText()))
			));
			layoutCategory.withComponent(new ToggleableOptionComponent<>(new OptionEnum<>("anchor", snap.getAnchor().getDeclaringClass(), snap.getAnchor())){
				@Override
				protected void onChanged() {
					snap.setAnchor(this.option.value);
				}
			});
		}
		addOptionComponentSupplier(()->layoutCategory);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		key = tag.getString("name");
		CompoundTag size = tag.getCompound("size");
		xSize = size.getInteger("x");
		ySize = size.getInteger("y");
		CompoundTag layout = tag.getCompound("layout");
		setLayout(createLayout(layout));
		zLevel = tag.getInteger("zLevel");
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		tag.putString("id", getId());
		tag.putString("name",key);
		CompoundTag size = new CompoundTag();
		size.putInt("x", xSize);
		size.putInt("y", ySize);
		CompoundTag layout = new CompoundTag();
		saveLayout(layout, getLayout());
		tag.put("size", size);
		tag.put("layout", layout);
		tag.putInt("zLevel", zLevel);
	}
}
