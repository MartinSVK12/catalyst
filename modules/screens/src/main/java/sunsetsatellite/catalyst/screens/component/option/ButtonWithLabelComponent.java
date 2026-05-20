package sunsetsatellite.catalyst.screens.component.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.options.components.ButtonComponent;
import net.minecraft.client.render.window.CursorShape;
import net.minecraft.core.lang.I18n;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class ButtonWithLabelComponent extends ButtonComponent {
    private final ButtonElement button;
	public String buttonText;

    public ButtonWithLabelComponent(String translationKey, @Nullable String tooltipTranslationKey, String buttonText) {
        super(translationKey, tooltipTranslationKey);
        this.button = new ButtonElement(0, 0, 0, 150, 20, I18n.getInstance().translateKey(buttonText));
		this.buttonText = buttonText;
    }

    protected abstract void buttonClicked(int mouseButton, int x, int y, int width, int height, int relativeMouseX, int relativeMouseY);

	@Override
    protected void renderButton(int x, int y, int relativeButtonX, int relativeButtonY, int buttonWidth, int buttonHeight, int relativeMouseX, int relativeMouseY) {
        this.button.displayString = I18n.getInstance().translateKey(buttonText);
        super.renderButton(x, y, relativeButtonX, relativeButtonY, buttonWidth, buttonHeight, relativeMouseX, relativeMouseY);
        this.button.xPosition = x + relativeButtonX;
        this.button.yPosition = y + relativeButtonY;
        this.button.width = buttonWidth;
        this.button.height = buttonHeight;
        this.button.drawButton(mc, x + relativeMouseX, y + relativeMouseY);
        if (relativeMouseX >= relativeButtonX && relativeMouseX < relativeButtonX + buttonWidth && relativeMouseY >= relativeButtonY && relativeButtonY < relativeButtonY + buttonHeight) {
            mc.currentScreen.setDesiredCursor(CursorShape.HAND);
        }

    }

	@Override
    public void resetValue() {

    }

	@Override
    public boolean isDefault() {
        return true;
    }
}
