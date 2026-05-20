package sunsetsatellite.catalyst.screens.component.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.options.ScreenOptions;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.sound.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ClickableButtonComponent implements OptionsComponent {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final int COMPONENT_SPACING = 2;
	private static final int BUTTON_WIDTH = 200;
	protected final String translationKey;
	protected final ButtonElement button;
	protected final Runnable action;
	protected boolean visible = true;

	public ClickableButtonComponent(String translationKey, Runnable action) {
		this.translationKey = translationKey;
		this.action = action;
		this.button = new ButtonElement(0, 0, 0, 200, 20, "");
	}

	public @Nullable String getTooltipTranslationKey() {
		return null;
	}

	public int getHeight() {
		return 24;
	}

	public void render(@NotNull ScreenOptions screenOptions, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		this.button.xPosition = x + width - this.button.width;
		this.button.yPosition = y + 2;
		this.button.width =  width;
		this.button.displayString = I18n.getInstance().translateKey(this.translationKey);
		this.button.drawButton(mc, x + relativeMouseX, y + relativeMouseY);
	}

	public void onMouseClick(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		this.button.xPosition = x + width / 2 - 100;
		this.button.yPosition = y + 2;
		this.button.width = width;
		if (this.button.mouseClicked(mc, x + relativeMouseX, y + relativeMouseY)) {
			mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
			this.action.run();
		}

	}

	public void onMouseMove(int x, int y, int width, int relativeMouseX, int relativeMouseY) {
	}

	public void onMouseRelease(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
	}

	public void onKeyPress(int keyCode, char character) {
	}

	public boolean matchesSearchTerm(String term) {
		return false;
	}
}
