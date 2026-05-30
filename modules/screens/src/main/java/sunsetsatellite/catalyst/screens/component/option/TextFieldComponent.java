package sunsetsatellite.catalyst.screens.component.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.TextFieldElement;
import net.minecraft.client.gui.options.components.ButtonComponent;
import net.minecraft.client.gui.text.TextFieldEditor;
import net.minecraft.client.render.window.CursorShape;
import org.jspecify.annotations.Nullable;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public abstract class TextFieldComponent extends ButtonComponent implements TextFieldElement.ITextChangeListener {
	private final TextFieldElement textField;
	private final TextFieldEditor editor;
	public String buttonText;

    public TextFieldComponent(String translationKey, @Nullable String tooltipTranslationKey, String defaultText) {
        super(translationKey, tooltipTranslationKey);
		this.textField = new TextFieldElement(mc.currentScreen, mc.font, 0, 0, 150, 18, defaultText, "");
		this.textField.setMaxStringLength(Integer.MAX_VALUE);
		this.textField.setTextChangeListener(this);
		this.editor = new TextFieldEditor(this.textField);
		this.buttonText = defaultText;
    }

	@Override
	public void init(final Minecraft mc) {
		super.init(mc);
		this.textField.parent = mc.currentScreen;
	}

    protected void buttonClicked(int mouseButton, int x, int y, int width, int height, int relativeMouseX, int relativeMouseY) {
		this.textField.mouseClicked(this.textField.xPosition + relativeMouseX, this.textField.yPosition + relativeMouseY, 0);
	}

	@Override
    protected void renderButton(int x, int y, int relativeButtonX, int relativeButtonY, int buttonWidth, int buttonHeight, int relativeMouseX, int relativeMouseY) {
        super.renderButton(x, y, relativeButtonX, relativeButtonY, buttonWidth, buttonHeight, relativeMouseX, relativeMouseY);
        this.textField.xPosition = x + relativeButtonX;
        this.textField.yPosition = y + relativeButtonY;
        this.textField.width = buttonWidth;
        this.textField.height = buttonHeight;
        this.textField.drawTextBox();
		this.textField.updateCursor(mc, x + relativeMouseX, y + relativeMouseY);
        if (relativeMouseX >= relativeButtonX && relativeMouseX < relativeButtonX + buttonWidth && relativeMouseY >= relativeButtonY && relativeButtonY < relativeButtonY + buttonHeight) {
            mc.currentScreen.setDesiredCursor(CursorShape.HAND);
        }

    }

	@Override
	public void onMouseClick(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		super.onMouseClick(mouseButton, x, y, width, relativeMouseX, relativeMouseY);
	}

	@Override
	public void onKeyPress(int keyCode, char character) {
		if (this.textField.isFocused && keyCode != Keyboard.KEY_ESCAPE && keyCode != Keyboard.KEY_RETURN) {
			this.textField.textboxKeyTyped(character, keyCode);
		} else if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RETURN) {
			if(keyCode == Keyboard.KEY_RETURN){
				textEntered(textField);
			}
			this.textField.setFocused(false);
		}
	}

	public TextFieldComponent setFocused(boolean focused){
		this.textField.setFocused(focused);
		return this;
	}

	public TextFieldComponent lock(){
		this.textField.isEnabled = false;
		return this;
	}

	public TextFieldComponent unlock(){
		this.textField.isEnabled = true;
		return this;
	}

	public String getText(){
		return textField.getText();
	}

	@Override
    public void resetValue() {
		textField.setText("");
    }

	@Override
    public boolean isDefault() {
        return !textField.isEnabled;
    }

	@Override
	public abstract void textChanged(TextFieldElement textFieldElement);

	public abstract void textEntered(TextFieldElement textFieldElement);
}
