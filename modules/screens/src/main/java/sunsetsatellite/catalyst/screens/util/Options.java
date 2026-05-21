package sunsetsatellite.catalyst.screens.util;

import net.minecraft.client.input.InputDevice;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import static net.minecraft.client.option.GameSettings.register;
import static org.lwjgl.input.Keyboard.*;

public class Options {
	public static final @NotNull KeyBinding KEY_GUI_EDITOR_SELECT = register(new KeyBinding("key.guiEditorSelect").setDefault(InputDevice.mouse, 0));
	public static final @NotNull KeyBinding KEY_GUI_EDITOR_OPEN_MAIN = register(new KeyBinding("key.guiEditorOpenMain").setDefault(InputDevice.keyboard, KEY_C));
	public static final @NotNull KeyBinding KEY_GUI_EDITOR_OPEN_CONTEXT = register(new KeyBinding("key.guiEditorOpenContext").setDefault(InputDevice.mouse, 1));
	public static final @NotNull KeyBinding KEY_GUI_EDITOR_EXIT = register(new KeyBinding("key.guiEditorExit").setDefault(InputDevice.keyboard, KEY_ESCAPE));
	public static final @NotNull KeyBinding KEY_GUI_EDITOR_RESET = register(new KeyBinding("key.guiEditorReset").setDefault(InputDevice.keyboard, KEY_R));
	public static final @NotNull KeyBinding KEY_GUI_EDITOR_NUDGE_UP = register(new KeyBinding("key.guiEditorNudgeUp").setDefault(InputDevice.keyboard, KEY_UP));
	public static final @NotNull KeyBinding KEY_GUI_EDITOR_NUDGE_DOWN = register(new KeyBinding("key.guiEditorNudgeDown").setDefault(InputDevice.keyboard, KEY_DOWN));
	public static final @NotNull KeyBinding KEY_GUI_EDITOR_NUDGE_LEFT = register(new KeyBinding("key.guiEditorNudgeLeft").setDefault(InputDevice.keyboard, Keyboard.KEY_LEFT));
	public static final @NotNull KeyBinding KEY_GUI_EDITOR_NUDGE_RIGHT = register(new KeyBinding("key.guiEditorNudgeRight").setDefault(InputDevice.keyboard, Keyboard.KEY_RIGHT));
	public static final @NotNull KeyBinding KEY_GUI_EDITOR_NUDGE_FAST = register(new KeyBinding("key.guiEditorNudgeFast").setDefault(InputDevice.keyboard, KEY_LSHIFT));
	public static final @NotNull KeyBinding KEY_GUI_EDITOR_ONBOARDING = register(new KeyBinding("key.guiEditorOnboarding").setDefault(InputDevice.keyboard, KEY_O));
}
