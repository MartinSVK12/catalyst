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
import sunsetsatellite.catalyst.CatalystScreens;
import sunsetsatellite.catalyst.CatalystScreensClient;
import sunsetsatellite.catalyst.core.util.vector.Vec2i;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;
import sunsetsatellite.catalyst.screens.component.option.BasicTextFieldComponent;
import sunsetsatellite.catalyst.screens.component.option.ClickableButtonComponent;
import sunsetsatellite.catalyst.screens.screen.ScreenGuiEditor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;



public class SubsceneComponent extends GuiComponent {

	public static final String ID = "subscene";

	public String scene = "";
	public final Map<String, GuiComponent> components = new HashMap<>();

	public SubsceneComponent(String name, float x, float y) {
		super(name, 32, 32, new LayoutAbsolute(x,y, ComponentAnchor.TOP_LEFT));
		onHover.connect((s, t)->{
			for (GuiComponent component : components.values()) {
				if(isHoveringOverComponent(component, t.mx(), t.my())){
					component.onHover.emit(new Hovered(component, t.mx(), t.my()));
				}
			}
		});
		onClick.connect((s, t)->{
			for (GuiComponent component : components.values()) {
				if(isHoveringOverComponent(component, t.mx(), t.my())){
					component.onClick.emit(new Clicked(component, t.mx(), t.my(), t.button()));
				}
			}
		});
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public void renderComponent(Minecraft mc, Screen screen, int x, int y, int xScreenSize, int yScreenSize, float partialTick) {
		if(components.isEmpty()){
			drawRect(x,y,x+xSize, y+ySize, 0xFFFF00FF);
		}
		GLRenderer.pushFrame();
		//Scissor.enable(x,y, xSize, ySize);
		for (GuiComponent component : components.values()) {
			component.gui = gui;
			component.xScreenSize = xScreenSize;
			component.yScreenSize = yScreenSize;
			component.posX = realX() + component.realX();
			component.posY = realY() + component.realY();
			component.renderComponentPreview(mc, gui, layout, posX, posY, xScreenSize, yScreenSize);
		}
		//Scissor.disable();
		GLRenderer.popFrame();
	}

	@Override
	public void renderComponentPreview(Minecraft mc, Gui gui, Layout layout, int x, int y, int xScreenSize, int yScreenSize) {
		if(components.isEmpty()){
			drawRect(x,y,x+xSize, y+ySize, 0xFFFF00FF);
		}
		GLRenderer.pushFrame();
		//Scissor.enable(x,y, xSize, ySize);
		for (GuiComponent component : components.values()) {
			component.gui = gui;
			component.xScreenSize = xScreenSize;
			component.yScreenSize = yScreenSize;
			component.posX = x + component.realX();
			component.posY = y + component.realY();
			component.renderComponentPreview(mc, gui, layout, component.posX, component.posY, xScreenSize, yScreenSize);
		}
		//Scissor.disable();
		GLRenderer.popFrame();
	}

	@Override
	public Map<String, OptionsComponent> getProperties() {
		Map<String, OptionsComponent> map = new HashMap<>();
		map.put("scene",new BasicTextFieldComponent(lang("scene"), null, scene,
			components::clear,
			(t)->{
				scene = t.getText();
				setScene(scene);
			}
		));
		map.put("unpack",new ClickableButtonComponent(lang("unpack"),
			()->{
				if(gui instanceof ScreenGuiEditor editor){
					editor.components.putAll(components);
					editor.components.remove(getName());
				}
			}
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

	public void setScene(String scene){
		components.clear();
		CatalystScreensClient.loadScene(CatalystScreens.loadSceneNbt(scene), components);
		setSubsceneSize();
	}

	private void setSubsceneSize() {
		Vec2i gMin = new Vec2i();
		Vec2i gMax = new Vec2i();
		for (GuiComponent component : components.values()) {
			Vec2i min = new Vec2i(component.realX(), component.realY());
			Vec2i max = min.copy().add(new Vec2i(component.xSize, component.ySize));
			if(min.x < gMin.x) gMin.x = min.x;
			if(min.y < gMin.y) gMin.y = min.y;
			if(max.x > gMax.x) gMax.x = max.x;
			if(max.y > gMax.y) gMax.y = max.y;
		}
		xSize = gMax.copy().subtract(gMin).x;
		ySize = gMax.copy().subtract(gMin).y;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		super.writeToNbt(tag);
		tag.putString("scene", scene);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		super.readFromNbt(tag);
		this.scene = tag.getString("scene");
		setScene(scene);
	}
}
