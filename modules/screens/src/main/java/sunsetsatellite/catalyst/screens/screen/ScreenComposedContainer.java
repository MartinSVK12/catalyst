package sunsetsatellite.catalyst.screens.screen;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import sunsetsatellite.catalyst.CatalystScreens;
import sunsetsatellite.catalyst.CatalystScreensClient;
import sunsetsatellite.catalyst.core.util.vector.Vec2i;
import sunsetsatellite.catalyst.fluids.impl.ScreenFluid;
import sunsetsatellite.catalyst.fluids.util.FluidItemContainer;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;
import sunsetsatellite.catalyst.screens.component.base.HasServerComponent;
import sunsetsatellite.catalyst.screens.menu.MenuComposed;

import java.util.*;
import java.util.List;

public class ScreenComposedContainer extends ScreenFluid {

	public int lastMx = 0;
	public int lastMy = 0;
	public float lastTick = 0;

	public final Map<String, GuiComponent> components = new HashMap<>();

	public ScreenComposedContainer(Map<String, GuiComponent> components, ContainerInventory playerInv, FluidItemContainer inv) {
		super(new MenuComposed(playerInv, inv));
		this.components.putAll(components);
		setSize();
	}

	public ScreenComposedContainer(ContainerInventory playerInv, FluidItemContainer inv, CompoundTag tag) {
		super(new MenuComposed(playerInv, inv));
		CatalystScreensClient.loadScene(CatalystScreens.loadSceneNbt(tag.getString("scene")), components);
		setSize();
	}

	public ScreenComposedContainer(MenuComposed menuComposed, CompoundTag tag) {
		super(menuComposed);
		CatalystScreensClient.loadScene(CatalystScreens.loadSceneNbt(tag.getString("scene")), components);
		setSize();
	}

	public CompoundTag makeServerData(){
		CompoundTag tag = new CompoundTag();
		for (GuiComponent c : components.values()) {
			if (c instanceof HasServerComponent s) {
				CompoundTag slotTag = new CompoundTag();
				s.toServer().writeToNbt(slotTag);
				CompoundTag typeTag = new CompoundTag();
				typeTag.putString("type", ((GuiComponent) s).getId());
				typeTag.put("data", slotTag);
				tag.put(UUID.randomUUID().toString(), typeTag);
			}
		}
		return tag;
	}

	private void setSize() {
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
	public void render(int mx, int my, float partialTick) {
		lastMx = mx;
		lastMy = my;
		lastTick = partialTick;
		super.render(mx, my, partialTick);
	}

	public boolean isHoveringOverComponent(GuiComponent component, int mx, int my){
		return mx >= component.realX() && my >= component.realY() && mx <= component.realX() + component.xSize && my <= component.realY() + component.ySize;
	}

	public List<GuiComponent> sortedValues(){
		return components.values().stream().sorted(Comparator.comparingInt((c)->c.zLevel)).toList();
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonNum) {
		super.mouseClicked(mx, my, buttonNum);
		for (final GuiComponent c :sortedValues()) {
			if (c.isVisible()) {
				if(isHoveringOverComponent(c, mx, my)){
					c.onClick.emit(new GuiComponent.Clicked(c, mx, my, buttonNum));
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		//drawRect(0, 0, this.xSize, this.ySize, 0xFFFF0000);

		for (final GuiComponent c : sortedValues()) {
			if (c.isVisible()) {
				GLRenderer.pushFrame();
				c.renderComponentScaled(this, xSize, ySize, lastTick);
				GLRenderer.popFrame();
				if(isHoveringOverComponent(c, lastMx, lastMy)){
					if(!c.hovering){
						c.hovering = true;
						c.onHoverStart.emit(new GuiComponent.Hovered(c, lastMx, lastMy));
					}
					c.onHover.emit(new GuiComponent.Hovered(c, lastMx, lastMy));
				} else {
					if(c.hovering){
						c.hovering = false;
						c.onHoverEnd.emit(new GuiComponent.Hovered(c, lastMx, lastMy));
					}
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick) {
		if(!((MenuComposed) inventorySlots).initialized){
			((MenuComposed) inventorySlots).init(makeServerData());
		}
	}
}
