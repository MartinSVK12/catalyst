package sunsetsatellite.catalyst.screens.component.base;

import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponent;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.client.gui.popup.PopupBuilder;
import net.minecraft.client.gui.popup.PopupScreen;
import net.minecraft.core.Global;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.CatalystScreens;
import sunsetsatellite.catalyst.CatalystScreensClient;
import sunsetsatellite.catalyst.screens.component.option.BasicTextFieldComponent;
import sunsetsatellite.catalyst.screens.component.option.ButtonWithLabelComponent;
import sunsetsatellite.catalyst.screens.component.option.ClickableButtonComponent;
import sunsetsatellite.catalyst.screens.component.option.SceneComponentInstanceComponent;
import sunsetsatellite.catalyst.screens.screen.ScreenGuiEditor;
import sunsetsatellite.catalyst.screens.util.GuiComponents;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static sunsetsatellite.catalyst.CatalystScreens.lang;


public class ComponentPicker extends HudComponent {

	public ScreenGuiEditor editor;
	public static int componentSpawned = 0;

	public ComponentPicker(ScreenGuiEditor editor) {
		super("component_picker", 0, 0, new LayoutAbsolute(0,0, ComponentAnchor.CENTER));
		this.editor = editor;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void render(HudIngame hud, int xSizeScreen, int ySizeScreen, float partialTick) {

	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int xSizeScreen, int ySizeScreen) {

	}

	@Override
	public List<Supplier<OptionsComponent>> getOptionSuppliers() {
		addDefaultOptionSuppliers();
		List<Supplier<OptionsComponent>> list = new ArrayList<>(super.getOptionSuppliers());
		super.getOptionSuppliers().clear();

		OptionsCategory sceneComponents = new OptionsCategory(lang("components"));
		//sceneComponents.collapsed = true;

		editor.components.forEach((name, component) -> {
			sceneComponents.withComponent(new SceneComponentInstanceComponent(name, component,null, lang("open")) {
				@Override
				protected void buttonClicked(int mouseButton, int x, int y, int width, int height, int relativeMouseX, int relativeMouseY) {
					editor.selectedComponent = this.component;
					editor.openContextMenu(this.component, x, y);
				}
			});
		});

		list.add(()->sceneComponents);

		return list;
	}

	@Override
	protected void addDefaultOptionSuppliers() {
		OptionsCategory components = new OptionsCategory(lang("components"));
		for (String s : GuiComponents.getComponents().keySet()) {
			components.withComponent(new ButtonWithLabelComponent("gui.component."+s, "", lang("add")) {
				@Override
				public void resetValue() {

				}

				@Override
				public boolean isDefault() {
					return true;
				}

				@Override
				protected void buttonClicked(int mouseButton, int x, int y, int width, int height, int relativeMouseX, int relativeMouseY) {
					try {
						String name = s+componentSpawned;
						editor.components.put(name, GuiComponents.getComponent(s).getDeclaredConstructor(String.class, float.class, float.class).newInstance(name,0.5f,0.5f));
						componentSpawned++;
					} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
					         NoSuchMethodException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
		addOptionComponentSupplier(()-> components);
		OptionsCategory scene = new OptionsCategory(lang("scene"));
		OptionsCategory save = new OptionsCategory(lang("save"));
		OptionsCategory load = new OptionsCategory(lang("load"));
		scene.withComponent(save);
		scene.withComponent(load);
		BasicTextFieldComponent nameField = new BasicTextFieldComponent(lang("name"), "", "scene", (t) -> {});
		save.withComponent(nameField);
		save.withComponent(new ClickableButtonComponent(lang("save"),()->{
			CompoundTag sceneTag = new CompoundTag();
			for (HudComponent component : ComponentPicker.this.editor.components.values()) {
				if(component instanceof IGuiComponent guiComponent){
					CompoundTag c = new CompoundTag();
					guiComponent.writeToNbt(c);
					sceneTag.putCompound(guiComponent.getName(), c);
				}
			}
			boolean success = saveScene(sceneTag, nameField.getText());
			if(success){
				PopupScreen popup = new PopupBuilder(editor, 64)
					.closeOnClickOut(0)
					.withLabel(lang("saved")).build();
				popup.zLevel = 999;
				mc.displayScreen(popup);
			} else {
				PopupScreen popup = new PopupBuilder(editor, 64)
					.closeOnClickOut(0)
					.withLabel(lang("failedSave")).build();
				popup.zLevel = 999;
				mc.displayScreen(popup);
			}
		}));
		BasicTextFieldComponent fileField = new BasicTextFieldComponent(lang("id"), "", "scene", (t) -> {});
		load.withComponent(fileField);
		load.withComponent(new ClickableButtonComponent(lang("load"),()->{
			CompoundTag loaded = CatalystScreens.loadSceneNbt(fileField.getText());
			if(loaded != null){
				CatalystScreensClient.loadScene(loaded, editor.components);
			}
		}));
		addOptionComponentSupplier(()->scene);
	}

	public static boolean saveScene(final @NotNull CompoundTag tag, final @NotNull String id) {
		try {
			final @NotNull File directory = new File(Global.accessor.getMinecraftDir(), "scenes");
			if (!directory.exists()) {
				if (!directory.mkdirs()) {
					return false;
				}
			}
			final @NotNull File file = new File(directory, id + ".nbt");
			if (!file.exists()) {
				if (!file.createNewFile()) {
					return false;
				}
			}
			try (final @NotNull FileOutputStream stream = new FileOutputStream(file)) {
				NbtIo.writeCompressed(tag, stream);
			}
			return true;
		} catch (final @NotNull IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
