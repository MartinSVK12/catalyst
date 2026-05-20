package sunsetsatellite.catalyst.screens.component;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponentMovable;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.gui.hud.component.layout.LayoutSnap;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.client.option.OptionEnum;
import net.minecraft.client.render.renderer.GLRenderer;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.screens.component.option.BasicTextFieldComponent;
import sunsetsatellite.catalyst.screens.util.GuiComponents;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static sunsetsatellite.catalyst.CatalystScreens.lang;

public abstract class GuiComponent extends HudComponentMovable implements IGuiComponent {

	public int zLevel = 0;

	public GuiComponent(String key, int xSize, int ySize, Layout layout) {
		super(key, xSize, ySize, layout);
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
		float scale = getScale();
		if (scale == 1.0f) {
			int x = this.layout.getComponentX(this, xSizeScreen);
			int y = this.layout.getComponentY(this, ySizeScreen);
			GLRenderer.modelM4f().translate(x, y, 0.0f);
			renderComponent(mc, screen, xSizeScreen, ySizeScreen, partialTick);
			return;
		}
		int x = this.layout.getComponentX(this, xSizeScreen);
		int y = this.layout.getComponentY(this, ySizeScreen);
		GLRenderer.modelM4f().translate(x, y, 0.0f);
		GLRenderer.modelM4f().scale(scale, scale, 1.0f);
		GLRenderer.modelM4f().translate(-x, -y, 0.0f);
		renderComponent(mc, screen, xSizeScreen, ySizeScreen, partialTick);
	}

	public final void renderComponentPreviewScaled(Gui gui, Layout layout, int xSizeScreen, int ySizeScreen) {
		float scale = getScale();
		if (scale == 1.0f) {
			renderComponentPreview(mc, gui, layout, xSizeScreen, ySizeScreen);
			return;
		}
		int x = layout.getComponentX(this, xSizeScreen);
		int y = layout.getComponentY(this, ySizeScreen);
		GLRenderer.modelM4f().translate(x, y, 0.0f);
		GLRenderer.modelM4f().scale(scale, scale, 1.0f);
		GLRenderer.modelM4f().translate(-x, -y, 0.0f);
		renderComponentPreview(mc, gui, layout, xSizeScreen, ySizeScreen);
	}

	@Override
	public List<Supplier<OptionsComponent>> getOptionSuppliers() {
		addDefaultOptionSuppliers();
		List<Supplier<OptionsComponent>> list = new ArrayList<>(super.getOptionSuppliers());
		super.getOptionSuppliers().clear();
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
		list.add(()->layoutCategory);
		return list;
	}

	@Override
	protected void addDefaultOptionSuppliers() {
		addOptionComponentSupplier(()->new BasicTextFieldComponent(lang("id"),null, key,
			()->{},
			(t)-> key = t.getText()
		));
		OptionsCategory sizeCategory = new OptionsCategory(lang("size"));
		sizeCategory.withComponent(new BasicTextFieldComponent(lang("width"), null,
			String.valueOf(xSize),
			()-> xSize = 152,
			(t) -> xSize = Catalyst.parseIntSafe(t.getText())
		));
		sizeCategory.withComponent(new BasicTextFieldComponent(lang("height"), null,
			String.valueOf(ySize),
			()-> ySize = 20,
			(t) -> ySize = Catalyst.parseIntSafe(t.getText())
		));
		addOptionComponentSupplier(()->sizeCategory);
		addOptionComponentSupplier(()->new BasicTextFieldComponent(lang("zLevel"),null, String.valueOf(zLevel),
			()-> zLevel = 0,
			(t)-> zLevel = Catalyst.parseIntSafe(t.getText())
		));
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
