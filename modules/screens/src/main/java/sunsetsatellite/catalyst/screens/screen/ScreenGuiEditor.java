package sunsetsatellite.catalyst.screens.screen;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponent;
import net.minecraft.client.gui.hud.component.HudComponentMovable;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.gui.hud.component.layout.LayoutSnap;
import net.minecraft.client.gui.options.ScreenOptions;
import net.minecraft.client.gui.options.components.KeyBindingComponent;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.popup.PopupBuilder;
import net.minecraft.client.gui.popup.PopupScreen;
import net.minecraft.client.input.InputDevice;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Option;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import sunsetsatellite.catalyst.CatalystScreens;
import sunsetsatellite.catalyst.screens.component.base.ComponentPicker;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;
import sunsetsatellite.catalyst.screens.util.Colors;
import sunsetsatellite.catalyst.screens.util.Options;

import java.util.*;
import java.util.function.Supplier;

import static sunsetsatellite.catalyst.CatalystScreens.lang;


public class ScreenGuiEditor extends Screen {
	private final List<GuiComponent> componentsUnderMouse = new ArrayList<>();
	public final Map<String, GuiComponent> components = new HashMap<>();

	public HudComponent selectedComponent = null;
	private boolean isDragging = false;
	private int clickMouseX = 0;
	private int clickMouseY = 0;

	private HudComponent heldComponent = null;
	private ComponentAnchor heldAnchor = ComponentAnchor.TOP_LEFT;
	private HudComponent snappedComponent = null;
	private ComponentAnchor snappedAnchor = null;
	private int nudgeTimer = 0;

	private final List<OptionsComponent> activeContextComponents = new ArrayList<>();
	private OptionsComponent clickedContextComponent = null;
	private int clickedContextComponentY = 0;
	private int contextMenuX, contextMenuY, contextMenuWidth, contextMenuHeight;
	private ScreenOptions contextOptionsScreen;

	private boolean showOnboarding = false;

	public ScreenGuiEditor(Screen parent) {
		super(parent);
	}

	@Override
	public void init() {
		super.init();

		this.showOnboarding = !GameSettings.SEEN_HUD_EDITOR_ONBOARDING.value;

		OptionsPage contextPage = new OptionsPage("context_menu", null);
		this.contextOptionsScreen = new ScreenOptions(this, contextPage);
		this.contextOptionsScreen.opened(this.width, this.height);
		this.contextOptionsScreen.fontRenderer = this.fontRenderer;
		this.contextOptionsScreen.width = this.width;
		this.contextOptionsScreen.height = this.height;
	}

	@Override
	public void tick() {
		super.tick();
		for (OptionsComponent optionsComponent : this.activeContextComponents) {
			optionsComponent.tick();
		}

		if (this.selectedComponent instanceof HudComponentMovable movable && !this.isDragging) {
			int dx = 0;
			int dy = 0;

			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) dy = -1;
			else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) dy = 1;
			else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) dx = -1;
			else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) dx = 1;

			if (dx != 0 || dy != 0) {
				this.nudgeTimer++;
				if (this.nudgeTimer > 8) {
					nudge(movable, dx, dy);
				}
			} else {
				this.nudgeTimer = 0;
			}
		} else {
			this.nudgeTimer = 0;
		}
	}

	@Override
	public void removed() {
		super.removed();
		closeContextMenu();
	}

	private void dismissOnboarding() {
		this.showOnboarding = false;
		GameSettings.SEEN_HUD_EDITOR_ONBOARDING.set(true);
		GameSettings.saveOptions();
		this.mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
	}

	private void nudge(HudComponentMovable movable, int dx, int dy) {
		// Hold Shift to nudge faster
		if (Keyboard.isKeyDown(Options.KEY_GUI_EDITOR_NUDGE_FAST.getKeyCode())) {
			dx *= 10;
			dy *= 10;
		}

		Layout layout = movable.getLayout();

		if (layout instanceof LayoutAbsolute absoluteLayout) {
			absoluteLayout.setXOffset(absoluteLayout.getXOffset() + dx);
			absoluteLayout.setYOffset(absoluteLayout.getYOffset() + dy);
		}
		else if (layout instanceof LayoutSnap snapLayout) {
			snapLayout.setXOffset(snapLayout.getXOffset() + dx);
			snapLayout.setYOffset(snapLayout.getYOffset() + dy);
		}
	}

	@Override
	public void keyPressed(char eventCharacter, int eventKey, int mx, int my) {
		if (this.showOnboarding) {
			if (eventKey == Keyboard.KEY_RETURN || eventKey == Keyboard.KEY_SPACE || eventKey == Keyboard.KEY_ESCAPE) {
				dismissOnboarding();
			}
			return;
		}

		if (ScreenOptions.pickingKeyBinding != null) {
			if (eventKey == Keyboard.KEY_ESCAPE) {
				ScreenOptions.pickingKeyBinding.unbind();
			} else {
				ScreenOptions.pickingKeyBinding.bind(InputDevice.keyboard, eventKey);
			}

			if (ScreenOptions.pickingKeyBindingComponent != null) {
				ScreenOptions.pickingKeyBindingComponent.update();
			}

			ScreenOptions.pickingKeyBinding = null;
			ScreenOptions.pickingKeyBindingComponent = null;
			return;
		}

		// Funnel key presses to the context menu if it is open
		if (!this.activeContextComponents.isEmpty()) {
			if (eventKey == Keyboard.KEY_ESCAPE) {
				closeContextMenu();
				return;
			}
			for (OptionsComponent comp : this.activeContextComponents) {
				comp.onKeyPress(eventKey, eventCharacter);
			}
			return;
		}

		if (eventKey == Options.KEY_GUI_EDITOR_EXIT.getKeyCode()) {
			this.mc.displayScreen(getParentScreen());
			this.mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
			return;
		}

		if (eventKey == Options.KEY_GUI_EDITOR_ONBOARDING.getKeyCode()) {
			this.showOnboarding = true;
			this.mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
			return;
		}

		if(eventKey == Options.KEY_GUI_EDITOR_OPEN_MAIN.getKeyCode()){
			ComponentPicker picker = new ComponentPicker(this);
			this.selectedComponent = picker;
			openContextMenu(picker, mx, my);
			this.mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
		}

		if(eventKey == Keyboard.KEY_DELETE){
			if(!componentsUnderMouse.isEmpty()){
				HudComponent component = componentsUnderMouse.get(0);
				components.remove(component.key);
			}
		}

		if (eventKey == Options.KEY_GUI_EDITOR_RESET.getKeyCode()) {
			PopupScreen popup = new PopupBuilder(this, 256)
				.withLabel("gui."+ lang("deleteAllLabel"))
				.withMessageBox("msgBox", 64, I18n.getInstance().translateKey("gui."+lang("deleteAllMsg")), 48)
				.withButtonGroup("btnGroup", new String[]{"gui."+lang("deleteAll"), "gui."+lang("cancel")}, new int[]{1, 0})
				.withOnCloseListener((id, map) -> {
					if (id == 1) {
						components.clear();
					}
				})
				.build();
			mc.displayScreen(popup);
			//HudComponents.INSTANCE.fromSettingsString(HudComponents.DEFAULT_LAYOUT);
			//for (HudComponent component : components) {
			//	for (Option<?> option : component.getRawOptions()) {
			//		option.resetValueToDefault();
			//	}
			//}
			this.mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
			return;
		}

		// Nudge
		if (this.selectedComponent instanceof HudComponentMovable movable && !this.isDragging) {
			int dx = 0;
			int dy = 0;

			if (eventKey == Options.KEY_GUI_EDITOR_NUDGE_UP.getKeyCode()) dy = -1;
			else if (eventKey == Options.KEY_GUI_EDITOR_NUDGE_DOWN.getKeyCode()) dy = 1;
			else if (eventKey == Options.KEY_GUI_EDITOR_NUDGE_LEFT.getKeyCode()) dx = -1;
			else if (eventKey == Options.KEY_GUI_EDITOR_NUDGE_RIGHT.getKeyCode()) dx = 1;

			if (dx != 0 || dy != 0) {
				nudge(movable, dx, dy);
				this.nudgeTimer = 0;
				return;
			}
		}
		super.keyPressed(eventCharacter, eventKey, mx, my);
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonNum) {
		if (this.showOnboarding) {
			dismissOnboarding();
			return;
		}

		if (ScreenOptions.pickingKeyBinding != null) {
			ScreenOptions.pickingKeyBinding.bind(InputDevice.mouse, buttonNum);
			if (ScreenOptions.pickingKeyBindingComponent != null) {
				ScreenOptions.pickingKeyBindingComponent.update();
				ScreenOptions.pickingKeyBindingComponent = null;
			}
			return;
		}

		// Context Menu Interaction
		if (!this.activeContextComponents.isEmpty()) {
			if (mx >= this.contextMenuX && mx <= this.contextMenuX + this.contextMenuWidth &&
				my >= this.contextMenuY && my <= this.contextMenuY + this.contextMenuHeight) {

				int currentY = (int) (this.contextMenuY + 4 + contextOptionsScreen.optionsScrollAmount);
				int left = this.contextMenuX + 4;
				int width = this.contextMenuWidth - 8;

				for (OptionsComponent comp : this.activeContextComponents) {
					if (my >= currentY && my <= currentY + comp.getHeight()) {
						comp.onMouseClick(buttonNum, left, currentY, width, mx - left, my - currentY);
						this.clickedContextComponent = comp;
						this.clickedContextComponentY = currentY;
						/*if(comp instanceof OptionsCategory){
							int totalHeight = 0;
							for (OptionsComponent c : this.activeContextComponents) {
								totalHeight += c.getHeight();
							}
							this.contextMenuHeight = totalHeight + 8;
						}*/
						break;
					}
					currentY += comp.getHeight();
				}
				return;
			} else {
				closeContextMenu();
			}
		}

		updateComponentsUnderMouse(mx, my);
		boolean clickedComponent = false;

		// Selection and Dragging
		if (buttonNum == Options.KEY_GUI_EDITOR_SELECT.getKeyCode()) {
			if (!this.componentsUnderMouse.isEmpty()) {
				HudComponent componentUnderMouse = this.componentsUnderMouse.get(this.componentsUnderMouse.size() - 1);
				this.selectedComponent = componentUnderMouse;

				if (componentUnderMouse instanceof HudComponentMovable) {
					this.heldComponent = componentUnderMouse;
					this.clickMouseX = mx;
					this.clickMouseY = my;
					this.isDragging = false;

					Layout currentLayout = componentUnderMouse.getLayout();
					this.heldAnchor = getClosestAnchor(
						currentLayout.getComponentX(componentUnderMouse, this.width),
						currentLayout.getComponentY(componentUnderMouse, this.height),
						componentUnderMouse, mx, my);

					clickedComponent = true;

					this.mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
				}
			}
		}

		// Open Context Menu
		if (buttonNum == Options.KEY_GUI_EDITOR_OPEN_CONTEXT.getKeyCode()) {
			if (!this.componentsUnderMouse.isEmpty()) {
				GuiComponent componentUnderMouse = this.componentsUnderMouse.get(this.componentsUnderMouse.size() - 1);
				this.selectedComponent = componentUnderMouse;

				componentUnderMouse.getOptionSuppliers().clear();
				componentUnderMouse.addOptions();
				List<Supplier<OptionsComponent>> optionSuppliers = componentUnderMouse.getOptionSuppliers();

				boolean hasOptions = optionSuppliers != null && !optionSuppliers.isEmpty();

				if (hasOptions) {
					openContextMenu(componentUnderMouse, mx, my);
					clickedComponent = true;

					this.mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
				}
			} else {
				ComponentPicker picker = new ComponentPicker(this);
				this.selectedComponent = picker;
				openContextMenu(picker, mx, my);
				clickedComponent = true;
				this.mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
			}
		}

		if (!clickedComponent && buttonNum == 0) {
			this.selectedComponent = null;
			this.heldComponent = null;
			super.mouseClicked(mx, my, buttonNum);
		}
	}

	public void openContextMenu(HudComponent component, int mx, int my) {
		this.activeContextComponents.clear();
		contextOptionsScreen.optionsScrollAmount = 0;

		// Add Option Components
		if (component.getOptionSuppliers() != null) {
			for (Supplier<OptionsComponent> supplier : component.getOptionSuppliers()) {
				OptionsComponent comp = supplier.get();
				comp.init(this.mc);
				this.activeContextComponents.add(comp);
			}
		}

		// Add KeyBinding Components (Appended to the end)
		if (component.getKeyBindingSuppliers() != null) {
			for (Supplier<KeyBindingComponent> supplier : component.getKeyBindingSuppliers()) {
				KeyBindingComponent comp = supplier.get();
				comp.init(this.mc);
				this.activeContextComponents.add(comp);
			}
		}

		// Dynamic Width Parameters
		int maxTextWidth = 0;
		int sidePadding = 16;
		int textControlSpacing = 20;
		int controlWidth = 150;

		boolean hasContent = false;

		if (component.getRawOptions() != null) {
			for (Option<?> option : component.getRawOptions()) {
				hasContent = true;
				String optionName = option.getDisplayStringName();
				int textWidth = this.fontRenderer.stringWidth(optionName);
				if (textWidth > maxTextWidth) {
					maxTextWidth = textWidth;
				}
			}
		}

		if (component.getRawKeyBindings() != null) {
			for (KeyBinding keyBinding : component.getRawKeyBindings()) {
				hasContent = true;
				controlWidth += 50;
				String keyName = keyBinding.getKeyName();
				int textWidth = this.fontRenderer.stringWidth(keyName);
				if (textWidth > maxTextWidth) {
					maxTextWidth = textWidth;
				}
			}
		}

		if (hasContent) {
			this.contextMenuWidth = sidePadding + maxTextWidth + textControlSpacing + controlWidth;
		} else {
			this.contextMenuWidth = 250;
		}

		// Height Calculation
		int totalHeight = 0;
		for (OptionsComponent comp : this.activeContextComponents) {
			totalHeight += comp.getHeight();
		}
		this.contextMenuHeight = totalHeight + 8;

		// Positional Logic for placement of the context box
		boolean isRightHalf = mx > (this.width / 2);
		boolean isBottomHalf = my > (this.height / 2);

		if (isRightHalf) {
			this.contextMenuX = mx - this.contextMenuWidth; // Place Left
		} else {
			this.contextMenuX = mx; // Place Right
		}

		if (isBottomHalf) {
			this.contextMenuY = my - this.contextMenuHeight; // Place Up
		} else {
			this.contextMenuY = my; // Place Down
		}

		// Screen Bounds Clamping
		if (this.contextMenuX < 0) this.contextMenuX = 0;
		if (this.contextMenuX + this.contextMenuWidth > this.width) this.contextMenuX = this.width - this.contextMenuWidth;
		if (this.contextMenuY < 0) this.contextMenuY = 0;
		if (this.contextMenuY + this.contextMenuHeight > this.height) this.contextMenuY = this.height - this.contextMenuHeight;

		contextOptionsScreen.selectedPage.getComponents().clear();
		for (OptionsComponent ctxComponent : activeContextComponents) {
			contextOptionsScreen.selectedPage.withComponent(ctxComponent);
		}
	}

	private void closeContextMenu() {
		for (OptionsComponent comp : this.activeContextComponents) {
			comp.onClose();
		}
		contextOptionsScreen.selectedPage.getComponents().clear();
		this.activeContextComponents.clear();
		this.clickedContextComponent = null;
	}

	@Override
	public void mouseReleased(int mx, int my, int buttonNum) {
		if (this.clickedContextComponent != null) {
			int left = this.contextMenuX + 4;
			int width = this.contextMenuWidth - 8;
			if (buttonNum >= 0) {
				this.clickedContextComponent.onMouseRelease(buttonNum, left, this.clickedContextComponentY, width, mx - left, my - this.clickedContextComponentY);
				this.clickedContextComponent = null;
			} else {
				this.clickedContextComponent.onMouseMove(left, this.clickedContextComponentY, width, mx - left, my - this.clickedContextComponentY);
			}
			return;
		}

		if (buttonNum == 0) {
			this.heldComponent = null;
			this.heldAnchor = null;
			this.snappedComponent = null;
			this.snappedAnchor = null;
			this.isDragging = false;
		}
		super.mouseReleased(mx, my, buttonNum);
	}

	@Override
	public void render(int mx, int my, float partialTick) {
		if (!this.activeContextComponents.isEmpty() && this.clickedContextComponent != null) {
			this.clickedContextComponent.onMouseMove(this.contextMenuX + 4, this.clickedContextComponentY, this.contextMenuWidth - 8, mx - (this.contextMenuX + 4), my - this.clickedContextComponentY);
		}

		updateComponentsUnderMouse(mx, my);

		if (this.heldComponent != null) {
			updateHeldComponent(mx, my);
		}

		renderTexturedBackground();
		drawStringShadow(mc.font, String.format("MX: %d | MY: %d",mx,my), 4, this.mc.resolution.getScaledHeightScreenCoords() - 22, Colors.WHITE);
		drawStringShadow(mc.font, "Components: "+components.size(), 4,this.mc.resolution.getScaledHeightScreenCoords() - 12, Colors.WHITE);
		drawHudComponents(mx, my);

		if (this.heldComponent != null && this.isDragging) {
			int padding = (int) GameSettings.SCREEN_PADDING.value.floatValue();
			int usableWidth = Math.max(1, this.width - padding * 2);
			int usableHeight = Math.max(1, this.height - padding * 2);
			for (ComponentAnchor anchor : ComponentAnchor.values()) {
				//if (anchor == ComponentAnchor.CENTER) continue;
				drawAnchor(padding, padding, (int) (usableWidth * anchor.xPosition), (int) (usableHeight * anchor.yPosition), anchor);
			}
		}

		super.render(mx, my, partialTick);

		if (!this.activeContextComponents.isEmpty()) {
			drawContextMenu(mx, my);
		}

		if (ScreenOptions.pickingKeyBinding != null) {
			GLRenderer.pushFrame();
			GLRenderer.disableState(State.DEPTH_TEST);

			drawRect(0, 0, this.width, this.height, 0x80000000);

			String actionName = I18n.getInstance().translateKey(ScreenOptions.pickingKeyBinding.getId());
			String prompt = "Press a key or mouse binding to set it as the keybinding for '" + actionName + "'.";

			drawStringCenteredShadow(this.fontRenderer, prompt, this.width / 2, this.height / 2, 0xFFFFFF);

			GLRenderer.enableState(State.DEPTH_TEST);
			GLRenderer.popFrame();
		}

		if (this.showOnboarding) {
			drawOnboardingOverlay();
		}
	}


	private void drawOnboardingOverlay() {
		GLRenderer.pushFrame();
		GLRenderer.disableState(State.DEPTH_TEST);

		drawRect(0, 0, this.width, this.height, 0xCC000000);

		int boxWidth = 440;
		int boxHeight = 230;
		int boxX = (this.width - boxWidth) / 2;
		int boxY = (this.height - boxHeight) / 2;

		drawRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xFF222222);
		drawRect(boxX + 2, boxY + 2, boxX + boxWidth - 2, boxY + boxHeight - 2, 0xFF333333);

		int centerX = this.width / 2;

		TextFormatting gold = TextFormatting.ORANGE;
		TextFormatting white = TextFormatting.WHITE;
		TextFormatting yellow = TextFormatting.YELLOW;
		TextFormatting gray = TextFormatting.GRAY;

		I18n i18n = I18n.getInstance();

		drawStringCenteredShadow(this.fontRenderer, gold + i18n.translateKey("gui.options.guieditor.onboarding.welcome"), centerX, boxY + 15, 0xFFFFFF);

		int startY = boxY + 45;
		int lineSpacing = 16;

		String select     = Options.KEY_GUI_EDITOR_SELECT.getKeyName();
		String main    = Options.KEY_GUI_EDITOR_OPEN_MAIN.getKeyName();
		String context    = Options.KEY_GUI_EDITOR_OPEN_CONTEXT.getKeyName();
		String nudgeUp    = Options.KEY_GUI_EDITOR_NUDGE_UP.getKeyName();
		String nudgeDown  = Options.KEY_GUI_EDITOR_NUDGE_DOWN.getKeyName();
		String nudgeLeft  = Options.KEY_GUI_EDITOR_NUDGE_LEFT.getKeyName();
		String nudgeRight = Options.KEY_GUI_EDITOR_NUDGE_RIGHT.getKeyName();
		String fast       = Options.KEY_GUI_EDITOR_NUDGE_FAST.getKeyName();
		String reset      = Options.KEY_GUI_EDITOR_RESET.getKeyName();
		String onboarding = Options.KEY_GUI_EDITOR_ONBOARDING.getKeyName();
		String exit       = Options.KEY_GUI_EDITOR_EXIT.getKeyName();

		String arrowKeys = nudgeUp + "/" + nudgeDown + "/" + nudgeLeft + "/" + nudgeRight;

		drawStringCenteredShadow(this.fontRenderer, white + String.format(i18n.translateKey("gui.options.hudeditor.onboarding.select"), yellow + select + white), centerX, startY, 0xFFFFFF);
		drawStringCenteredShadow(this.fontRenderer, white + String.format(i18n.translateKey("gui.options.hudeditor.onboarding.drag"), yellow + select + white), centerX, startY + lineSpacing, 0xFFFFFF);
		drawStringCenteredShadow(this.fontRenderer, white + String.format(i18n.translateKey("gui.options.guieditor.onboarding.mainMenu"), yellow + main + white), centerX, startY + (lineSpacing * 2), 0xFFFFFF);
		drawStringCenteredShadow(this.fontRenderer, white + String.format(i18n.translateKey("gui.options.hudeditor.onboarding.context"), yellow + context + white), centerX, startY + (lineSpacing * 3), 0xFFFFFF);
		drawStringCenteredShadow(this.fontRenderer, white + String.format(i18n.translateKey("gui.options.hudeditor.onboarding.nudge"), yellow + arrowKeys + white), centerX, startY + (lineSpacing * 4), 0xFFFFFF);
		drawStringCenteredShadow(this.fontRenderer, white + String.format(i18n.translateKey("gui.options.hudeditor.onboarding.nudge_fast"), yellow + fast + white), centerX, startY + (lineSpacing * 5), 0xFFFFFF);
		drawStringCenteredShadow(this.fontRenderer, white + String.format(i18n.translateKey("gui.options.guieditor.onboarding.reset"), yellow + reset + white), centerX, startY + (lineSpacing * 6), 0xFFFFFF);
		drawStringCenteredShadow(this.fontRenderer, white + String.format(i18n.translateKey("gui.options.guieditor.onboarding.exit"), yellow + exit + white), centerX, startY + (lineSpacing * 7), 0xFFFFFF);

		drawStringCenteredShadow(this.fontRenderer, gray + String.format(i18n.translateKey("gui.options.hudeditor.onboarding.reopen"), yellow + onboarding + gray), centerX, startY + (lineSpacing * 9), 0xFFFFFF);

		int alpha = (int) (Math.abs(Math.sin(System.currentTimeMillis() % 2000 / 2000.0 * Math.PI)) * 245) + 10;
		int promptColor = (alpha << 24) | 0xAAAAAA;

		drawStringCenteredShadow(this.fontRenderer, i18n.translateKey("gui.options.hudeditor.onboarding.begin"), centerX, boxY + boxHeight - 20, promptColor);

		GLRenderer.enableState(State.DEPTH_TEST);
		GLRenderer.popFrame();
	}

	private void drawContextMenu(int mouseX, int mouseY) {
		int minX = this.contextMenuX;
		int maxX = this.contextMenuX + this.contextMenuWidth;
		int minY = this.contextMenuY;
		int maxY = this.contextMenuY + this.contextMenuHeight;

		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
			contextOptionsScreen.scrollOptions(Mouse.getDWheel() / -0.01f);
		} else {
			contextOptionsScreen.scrollOptions(Mouse.getDWheel() / -0.05f);
		}

		GLRenderer.pushFrame();
		GLRenderer.disableState(State.DEPTH_TEST);

		drawRect(minX, minY, maxX, maxY, 0xFF222222);
		drawRect(minX + 2, minY + 2, maxX - 2, maxY - 2, 0xFF333333);

		int currentY = (int) (this.contextMenuY + 4 + contextOptionsScreen.optionsScrollAmount);
		int left = this.contextMenuX + 4;
		int width = this.contextMenuWidth - 8;

		for (OptionsComponent optionsComponent : this.activeContextComponents) {
			optionsComponent.render(this.contextOptionsScreen, left, currentY, width, mouseX - left, mouseY - currentY);
			currentY += optionsComponent.getHeight();
		}

		GLRenderer.popFrame();
	}

	private void updateHeldComponent(int mouseX, int mouseY) {
		if (this.heldComponent == null) return;

		if (!this.isDragging) {
			if (Math.abs(mouseX - this.clickMouseX) > 3 || Math.abs(mouseY - this.clickMouseY) > 3) {
				this.isDragging = true;
				closeContextMenu(); // Cancel menu if dragging starts
				Layout layout = new LayoutAbsolute(0, 0, this.heldAnchor);
				((HudComponentMovable) this.heldComponent).setLayout(layout);
			} else {
				return;
			}
		}

		int xSize = this.heldComponent.getDisplayedXSize();
		int ySize = this.heldComponent.getDisplayedYSize();
		int xOffset = this.heldComponent.getDisplayedAnchorX(this.heldAnchor);
		int yOffset = this.heldComponent.getDisplayedAnchorY(this.heldAnchor);
		int padding = (int) GameSettings.SCREEN_PADDING.value.floatValue();
		int minX = xOffset + padding;
		int maxX = this.width - padding - (xSize - xOffset);
		int minY = yOffset + padding;
		int maxY = this.height - padding - (ySize - yOffset);
		int newX = MathHelper.clamp(mouseX, minX, Math.max(minX, maxX));
		int newY = MathHelper.clamp(mouseY, minY, Math.max(minY, maxY));

		Layout layout = this.heldComponent.getLayout();
		if (layout instanceof LayoutAbsolute absoluteLayout) {
			int usableWidth = Math.max(1, this.width - padding * 2);
			int usableHeight = Math.max(1, this.height - padding * 2);
			absoluteLayout.setXPosition((float) (newX - padding) / usableWidth);
			absoluteLayout.setYPosition((float) (newY - padding) / usableHeight);
		}

		int snapRadius = 10;
		boolean didSnap = false;
		for (HudComponent component : components.values()) {
			if (component == this.heldComponent) continue;
			if (!component.isEnabled()) continue;
			Layout otherLayout = component.getLayout();
			boolean isNested = false;
			Layout nestedLayout = otherLayout;
			for (int i = 0; i < 10; i++) {
				if (nestedLayout instanceof LayoutSnap layoutSnap) {
					if (layoutSnap.getParent() == this.heldComponent) {
						isNested = true;
						break;
					} else if (layoutSnap.getParent() != null) {
						nestedLayout = layoutSnap.getParent().getLayout();
					} else {
						break;
					}
				} else {
					break;
				}
			}
			if (isNested) continue;

			int x = otherLayout.getComponentX(component, this.width);
			int y = otherLayout.getComponentY(component, this.height);

			ComponentAnchor anchor = getAnchorInRadius(x, y, component, newX, newY, snapRadius);
			if (anchor != null && this.heldAnchor.canConnectTo(anchor)) {
				this.snappedComponent = component;
				this.snappedAnchor = anchor;
				LayoutSnap newLayout = new LayoutSnap(component, anchor, this.heldAnchor);
				((HudComponentMovable) this.heldComponent).setLayout(newLayout);
				didSnap = true;
				break;
			}
		}

		if (!didSnap) {
			snapRadius = 20;
			ComponentAnchor anchor = getScreenAnchorInRadius(0, 0, newX, newY, snapRadius);
			if (anchor != null) {
				this.snappedComponent = null;
				this.snappedAnchor = anchor;
				LayoutSnap newLayout = new LayoutSnap(null, this.snappedAnchor, this.heldAnchor);
				((HudComponentMovable) this.heldComponent).setLayout(newLayout);
				didSnap = true;
			}

			if (!didSnap) {
				int usableWidth = Math.max(1, this.width - padding * 2);
				int usableHeight = Math.max(1, this.height - padding * 2);
				LayoutAbsolute newLayout = new LayoutAbsolute((float) (newX - padding) / usableWidth, (float) (newY - padding) / usableHeight, this.heldAnchor);
				((HudComponentMovable) this.heldComponent).setLayout(newLayout);
				this.snappedComponent = null;
				this.snappedAnchor = null;
			}
		}
	}

	private ComponentAnchor getClosestAnchor(int x, int y, HudComponent component, int mouseX, int mouseY) {
		ComponentAnchor anchor = ComponentAnchor.TOP_LEFT;
		int minDistance = Integer.MAX_VALUE;
		for (ComponentAnchor componentAnchor : ComponentAnchor.values()) {
			//if (componentAnchor == ComponentAnchor.CENTER) continue;
			int xAnchor = component.getDisplayedAnchorX(componentAnchor);
			int yAnchor = component.getDisplayedAnchorY(componentAnchor);
			int distance = (int) Math.sqrt(Math.pow(mouseX - (x + xAnchor), 2) + Math.pow(mouseY - (y + yAnchor), 2));
			if (distance < minDistance) {
				minDistance = distance;
				anchor = componentAnchor;
			}
		}
		return anchor;
	}

	private ComponentAnchor getAnchorInRadius(int x, int y, HudComponent component, int mouseX, int mouseY, int radius) {
		ComponentAnchor anchor = null;
		int minDistance = Integer.MAX_VALUE;
		for (ComponentAnchor componentAnchor : ComponentAnchor.values()) {
			//if (componentAnchor == ComponentAnchor.CENTER) continue;
			int xAnchor = component.getDisplayedAnchorX(componentAnchor);
			int yAnchor = component.getDisplayedAnchorY(componentAnchor);
			int distance = (int) Math.sqrt(Math.pow(mouseX - (x + xAnchor), 2) + Math.pow(mouseY - (y + yAnchor), 2));
			if (distance < minDistance && distance <= radius) {
				minDistance = distance;
				anchor = componentAnchor;
			}
		}
		return anchor;
	}

	private ComponentAnchor getScreenAnchorInRadius(int x, int y, int mouseX, int mouseY, int radius) {
		ComponentAnchor anchor = null;
		int minDistance = Integer.MAX_VALUE;
		int padding = (int) GameSettings.SCREEN_PADDING.value.floatValue();
		int usableWidth = Math.max(1, this.width - padding * 2);
		int usableHeight = Math.max(1, this.height - padding * 2);
		for (ComponentAnchor componentAnchor : ComponentAnchor.values()) {
			//if (componentAnchor == ComponentAnchor.CENTER) continue;
			int xAnchor = padding + (int) (componentAnchor.xPosition * usableWidth);
			int yAnchor = padding + (int) (componentAnchor.yPosition * usableHeight);
			int distance = (int) Math.sqrt(Math.pow(mouseX - (x + xAnchor), 2) + Math.pow(mouseY - (y + yAnchor), 2));
			if (distance < minDistance && distance <= radius) {
				minDistance = distance;
				anchor = componentAnchor;
			}
		}
		return anchor;
	}

	@Override
	public void renderTexturedBackground() {
		super.renderTexturedBackground();
		GLRenderer.globalSetLightEnabled(false);
		TessellatorGeneral tessellator = GLRenderer.getTessellator();
		this.mc.textureManager.loadTexture("/assets/minecraft/textures/gui/designer_bg.png").bind();
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float scale = 32F;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(0.0D, this.height, 0.0D, 0.0D, (float) this.height / scale);
		tessellator.addVertexWithUV(this.width, this.height, 0.0D, (float) this.width / scale, (float) this.height / scale);
		tessellator.addVertexWithUV(this.width, 0.0D, 0.0D, (float) this.width / scale, 0);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0);
		tessellator.draw();
	}

	private void drawHudComponents(int mouseX, int mouseY) {
		HudComponent lastComponent = null;
		if (!this.componentsUnderMouse.isEmpty()) {
			this.componentsUnderMouse.sort(Comparator.comparingInt((c)->c.zLevel));
			lastComponent = this.componentsUnderMouse.get(this.componentsUnderMouse.size() - 1);
		}
		for (HudComponent component : components.values()) {
			if (component == this.heldComponent && this.isDragging) continue;
			if (!component.isEnabled()) continue;
			Layout layout = component.getLayout();
			GLRenderer.pushFrame();
			component.renderPreviewScaled(this, layout, this.width, this.height);
			GLRenderer.popFrame();

			if (component == this.selectedComponent && (!this.isDragging || component != this.heldComponent)) {
				int x = layout.getComponentX(component, this.width);
				int y = layout.getComponentY(component, this.height);
				int xSize = component.getDisplayedXSize();
				int ySize = component.getDisplayedYSize();

				drawBox(x, y, x + xSize, y + ySize, 0xffff0000, 1);

				if (mouseX >= x && mouseX < x + xSize && mouseY >= y && mouseY < y + ySize) {
					ComponentAnchor closestAnchor = getClosestAnchor(x, y, component, mouseX, mouseY);
					int xAnchor = component.getDisplayedAnchorX(closestAnchor);
					int yAnchor = component.getDisplayedAnchorY(closestAnchor);
					drawAnchor(x, y, xAnchor, yAnchor, closestAnchor);
				}

			} else if (component == lastComponent && component != this.selectedComponent) {
				int x = layout.getComponentX(component, this.width);
				int y = layout.getComponentY(component, this.height);
				int xSize = component.getDisplayedXSize();
				int ySize = component.getDisplayedYSize();

				drawBox(x, y, x + xSize, y + ySize, 0xff00ff00, 1);

				ComponentAnchor anchor = getClosestAnchor(x, y, component, mouseX, mouseY);
				int xAnchor = component.getDisplayedAnchorX(anchor);
				int yAnchor = component.getDisplayedAnchorY(anchor);
				drawAnchor(x, y, xAnchor, yAnchor, anchor);
			}
		}

		if (this.heldComponent != null && this.isDragging) {
			Layout layout = this.heldComponent.getLayout();
			GLRenderer.pushFrame();
			this.heldComponent.renderPreviewScaled(this, layout, this.width, this.height);
			GLRenderer.popFrame();
			int x = layout.getComponentX(this.heldComponent, this.width);
			int y = layout.getComponentY(this.heldComponent, this.height);
			int xSize = this.heldComponent.getDisplayedXSize();
			int ySize = this.heldComponent.getDisplayedYSize();
			int xAnchor = this.heldComponent.getDisplayedAnchorX(this.heldAnchor);
			int yAnchor = this.heldComponent.getDisplayedAnchorY(this.heldAnchor);
			drawBox(x, y, x + xSize, y + ySize, 0xffff0000, 1);
			drawAnchor(x, y, xAnchor, yAnchor, this.heldAnchor);
		}
		if (this.snappedComponent != null && this.snappedAnchor != null) {
			Layout layout = this.snappedComponent.getLayout();
			int x = layout.getComponentX(this.snappedComponent, this.width);
			int y = layout.getComponentY(this.snappedComponent, this.height);
			int xAnchor = this.snappedComponent.getDisplayedAnchorX(this.snappedAnchor);
			int yAnchor = this.snappedComponent.getDisplayedAnchorY(this.snappedAnchor);
			drawAnchor(x, y, xAnchor, yAnchor, this.snappedAnchor);
		} else if (this.heldComponent != null && this.isDragging && this.snappedAnchor != null) {
			int padding = (int) GameSettings.SCREEN_PADDING.value.floatValue();
			int usableWidth = Math.max(1, this.width - padding * 2);
			int usableHeight = Math.max(1, this.height - padding * 2);
			int xAnchor = (int) (this.snappedAnchor.xPosition * usableWidth);
			int yAnchor = (int) (this.snappedAnchor.yPosition * usableHeight);
			drawAnchor(padding, padding, xAnchor, yAnchor, this.snappedAnchor);
		}
	}

	private void drawAnchor(int x, int y, int xAnchor, int yAnchor, ComponentAnchor anchor) {
		int color = 0xff0000ff;
		int lineLength = 5;

		GLRenderer.pushFrame();
		GLRenderer.disableState(State.DEPTH_TEST);
		switch (anchor) {
			case TOP_LEFT:
				drawRect(x + xAnchor, y + yAnchor, x + xAnchor + lineLength, y + yAnchor + 1, color);
				drawRect(x + xAnchor, y + yAnchor, x + xAnchor + 1, y + yAnchor + lineLength, color);
				break;
			case TOP_CENTER:
				drawRect(x + xAnchor - lineLength, y + yAnchor, x + xAnchor + lineLength, y + yAnchor + 1, color);
				drawRect(x + xAnchor, y + yAnchor, x + xAnchor + 1, y + yAnchor + lineLength, color);
				break;
			case TOP_RIGHT:
				drawRect(x + xAnchor - lineLength, y + yAnchor, x + xAnchor, y + yAnchor + 1, color);
				drawRect(x + xAnchor - 1, y + yAnchor, x + xAnchor, y + yAnchor + lineLength, color);
				break;
			case CENTER:
				//drawRect(x + xAnchor - lineLength, y + yAnchor, x + xAnchor + lineLength, y + yAnchor + 1, color);
				drawRect(x + xAnchor - lineLength, y + yAnchor - 1, x + xAnchor + lineLength, y + yAnchor, color);
				drawRect(x + xAnchor, y + yAnchor - lineLength, x + xAnchor + 1, y + yAnchor + lineLength, color);
				break;
			case CENTER_LEFT:
				drawRect(x + xAnchor, y + yAnchor, x + xAnchor + lineLength, y + yAnchor + 1, color);
				drawRect(x + xAnchor, y + yAnchor - lineLength, x + xAnchor + 1, y + yAnchor + lineLength, color);
				break;
			case CENTER_RIGHT:
				drawRect(x + xAnchor - lineLength, y + yAnchor, x + xAnchor, y + yAnchor + 1, color);
				drawRect(x + xAnchor - 1, y + yAnchor - lineLength, x + xAnchor, y + yAnchor + lineLength, color);
				break;
			case BOTTOM_LEFT:
				drawRect(x + xAnchor, y + yAnchor - 1, x + xAnchor + lineLength, y + yAnchor, color);
				drawRect(x + xAnchor, y + yAnchor - lineLength, x + xAnchor + 1, y + yAnchor, color);
				break;
			case BOTTOM_CENTER:
				drawRect(x + xAnchor - lineLength, y + yAnchor - 1, x + xAnchor + lineLength, y + yAnchor, color);
				drawRect(x + xAnchor, y + yAnchor - lineLength, x + xAnchor + 1, y + yAnchor, color);
				break;
			case BOTTOM_RIGHT:
				drawRect(x + xAnchor - lineLength, y + yAnchor - 1, x + xAnchor, y + yAnchor, color);
				drawRect(x + xAnchor - 1, y + yAnchor - lineLength, x + xAnchor, y + yAnchor, color);
				break;
		}
		GLRenderer.popFrame();
		this.zLevel = 0;
	}

	private void updateComponentsUnderMouse(int mouseX, int mouseY) {
		this.componentsUnderMouse.clear();
		for (GuiComponent component : components.values()) {
			if (!component.isEnabled()) continue;
			Layout layout = component.getLayout();
			int cx = layout.getComponentX(component, this.width);
			int cy = layout.getComponentY(component, this.height);
			int cXSize = component.getDisplayedXSize();
			int cYSize = component.getDisplayedYSize();
			if (mouseX >= cx && mouseX < cx + cXSize && mouseY >= cy && mouseY < cy + cYSize) {
				this.componentsUnderMouse.add(component);
			}
		}
	}
}
