package sunsetsatellite.catalyst.screens.component.option;

import net.minecraft.client.gui.TextFieldElement;
import org.jspecify.annotations.Nullable;
import sunsetsatellite.catalyst.core.util.Procedure;

import java.util.function.Consumer;

public class BasicTextFieldComponent extends TextFieldComponent {
	private final Consumer<TextFieldElement> textEnteredFunc;
	private final Procedure resetValueFunc;

	public BasicTextFieldComponent(String translationKey,
	                               @Nullable String tooltipTranslationKey,
	                               String defaultText)
	{
		super(translationKey, tooltipTranslationKey, defaultText);
		this.textEnteredFunc = (t) -> {};
		this.resetValueFunc = () -> {};
	}

	public BasicTextFieldComponent(String translationKey,
	                               @Nullable String tooltipTranslationKey,
	                               String defaultText,
	                               Procedure resetValueFunc,
	                               Consumer<TextFieldElement> textEnteredFunc)
	{
		super(translationKey, tooltipTranslationKey, defaultText);
		this.textEnteredFunc = textEnteredFunc;
		this.resetValueFunc = resetValueFunc;
	}

	public BasicTextFieldComponent(String translationKey,
	                               @Nullable String tooltipTranslationKey,
	                               String defaultText,
	                               Consumer<TextFieldElement> textEnteredFunc)
	{
		super(translationKey, tooltipTranslationKey, defaultText);
		this.textEnteredFunc = textEnteredFunc;
		this.resetValueFunc = () -> {};
	}

	@Override
	public void resetValue() {
		resetValueFunc.run();
	}

	@Override
	public void textChanged(TextFieldElement textFieldElement) {

	}

	@Override
	public void textEntered(TextFieldElement textFieldElement) {
		textEnteredFunc.accept(textFieldElement);
	}
}
