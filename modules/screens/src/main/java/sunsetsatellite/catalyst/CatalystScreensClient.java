package sunsetsatellite.catalyst;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.KeyBindingComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.components.ShortcutComponent;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.client.input.InputDevice;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.input.Keyboard;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;
import sunsetsatellite.catalyst.screens.screen.ScreenGuiEditor;
import sunsetsatellite.catalyst.screens.util.Options;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.util.dependency.Key;

import java.util.Map;

public class CatalystScreensClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		GameSettings.register(testKey);
		ClientEvents.AFTER_CLIENT_START.listen(Key.of(CatalystScreens.MOD_ID), this::afterClientStart);
	}

	public void afterClientStart() {
		Minecraft mc = Minecraft.getMinecraft();
		((OptionsCategory) OptionsPages.GENERAL.getComponents().get(1)).withComponent(new ShortcutComponent("gui.options.page.general.button.edit_gui", () -> mc.displayScreen(new ScreenGuiEditor(mc.currentScreen))));
		OptionsCategory guiEditorCategory = new OptionsCategory("gui.options.page.controls.category.gui_editor")
			.withComponent(new KeyBindingComponent(Options.KEY_GUI_EDITOR_SELECT))
			.withComponent(new KeyBindingComponent(Options.KEY_GUI_EDITOR_OPEN_MAIN))
			.withComponent(new KeyBindingComponent(Options.KEY_GUI_EDITOR_OPEN_CONTEXT))
			.withComponent(new KeyBindingComponent(Options.KEY_GUI_EDITOR_NUDGE_UP))
			.withComponent(new KeyBindingComponent(Options.KEY_GUI_EDITOR_NUDGE_DOWN))
			.withComponent(new KeyBindingComponent(Options.KEY_GUI_EDITOR_NUDGE_LEFT))
			.withComponent(new KeyBindingComponent(Options.KEY_GUI_EDITOR_NUDGE_RIGHT))
			.withComponent(new KeyBindingComponent(Options.KEY_GUI_EDITOR_NUDGE_FAST))
			.withComponent(new KeyBindingComponent(Options.KEY_GUI_EDITOR_RESET))
			.withComponent(new KeyBindingComponent(Options.KEY_GUI_EDITOR_EXIT))
			.withComponent(new KeyBindingComponent(Options.KEY_GUI_EDITOR_ONBOARDING));
		OptionsPages.CONTROLS.withComponent(guiEditorCategory);
	}

	public static void loadScene(CompoundTag tag, Map<String, GuiComponent> components){
		if(tag == null) return;
		for (Tag<?> value : tag.getValues()) {
			components.put(value.getTagName(), GuiComponent.create(((CompoundTag) value)));
		}
		GuiComponent.incompleteLinks.forEach(((layout, name) -> {
			if(components.get(name) == null){
				CatalystScreens.LOGGER.error("Could not find component with id: {}", name);
			}
			layout.setParent(components.get(name));
		}));
		GuiComponent.incompleteLinks.clear();
	}

	public static void addSettingsPage() {

	}

	public static KeyBinding testKey = new KeyBinding(CatalystScreens.MOD_ID+".key.test").bind(InputDevice.keyboard, Keyboard.KEY_NUMPAD1).setDefault(InputDevice.keyboard, Keyboard.KEY_NUMPAD1);
}
