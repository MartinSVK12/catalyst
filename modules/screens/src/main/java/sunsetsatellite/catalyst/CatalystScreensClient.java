package sunsetsatellite.catalyst;

import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.components.ShortcutComponent;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.client.input.InputDevice;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.core.Global;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import sunsetsatellite.catalyst.screens.component.ComponentPicker;
import sunsetsatellite.catalyst.screens.component.ImageComponent;
import sunsetsatellite.catalyst.screens.component.TextComponent;
import sunsetsatellite.catalyst.screens.screen.ScreenGuiEditor;
import sunsetsatellite.catalyst.screens.util.GuiComponents;
import turniplabs.halplibe.util.ClientStartEntrypoint;
import turniplabs.halplibe.util.OptionsInitEntrypoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CatalystScreensClient implements ClientModInitializer, ClientStartEntrypoint, OptionsInitEntrypoint {
	@Override
	public void onInitializeClient() {

	}

	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {
		GuiComponents.register(TextComponent.ID, TextComponent.class);
		GuiComponents.register(ImageComponent.ID, ImageComponent.class);
		Minecraft mc = Minecraft.getMinecraft();
		((OptionsCategory) OptionsPages.GENERAL.getComponents().get(1)).withComponent(new ShortcutComponent("gui.options.page.general.button.edit_gui", () -> mc.displayScreen(new ScreenGuiEditor(mc.currentScreen))));
	}

	public static void addSettingsPage() {

	}

	public static KeyBinding testKey = new KeyBinding(CatalystScreens.MOD_ID+".key.test").bind(InputDevice.keyboard, Keyboard.KEY_NUMPAD1).setDefault(InputDevice.keyboard, Keyboard.KEY_NUMPAD1);

	@Override
	public void initOptions() {
		GameSettings.register(testKey);
	}
}
