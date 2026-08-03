package sunsetsatellite.catalyst;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.options.components.BooleanOptionComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.OptionBoolean;
import net.minecraft.core.item.Items;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.util.dependency.Key;

import java.lang.reflect.InvocationTargetException;

@Environment(EnvType.CLIENT)
public class CatalystClient implements ClientModInitializer {

	public static OptionsPage optionsPage;
	public static OptionsCategory coreCategory;
	public static OptionsCategory fluidsCategory;
	public static OptionsCategory energyCategory;
	public static OptionsCategory multiblocksCategory;
	public static OptionsCategory effectsCategory;
	public static OptionsCategory multipartCategory;

	public static OptionBoolean networkRenderOption = new OptionBoolean("catalyst-core.showNetworkRender", false);


	@Override
	public void onInitializeClient() {
		GameSettings.register(networkRenderOption);
		ClientEvents.AFTER_CLIENT_START.listen(Key.of(Catalyst.MOD_ID),this::afterClientStart);
	}

	public void afterClientStart() {
		optionsPage = new OptionsPage("gui.options.page.catalyst", Items.DUST_REDSTONE.getDefaultStack());
		coreCategory = new OptionsCategory("gui.options.page.catalyst.category.core");
		coreCategory.withComponent(new BooleanOptionComponent(networkRenderOption));
		fluidsCategory = new OptionsCategory("gui.options.page.catalyst.category.fluids");
		energyCategory = new OptionsCategory("gui.options.page.catalyst.category.energy");
		multiblocksCategory = new OptionsCategory("gui.options.page.catalyst.category.multiblocks");
		effectsCategory = new OptionsCategory("gui.options.page.catalyst.category.effects");
		multipartCategory = new OptionsCategory("gui.options.page.catalyst.category.multipart");
		optionsPage
			.withComponent(coreCategory)
			.withComponent(fluidsCategory)
			.withComponent(energyCategory)
			.withComponent(multiblocksCategory)
			.withComponent(effectsCategory)
			.withComponent(multipartCategory);
		OptionsPages.register(optionsPage);

		try {
			Class<?> catalystMultipart = Class.forName("sunsetsatellite.catalyst.CatalystMultipartClient");
			catalystMultipart.getMethod("addSettingsPage").invoke(null);
		} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
		         IllegalAccessException ignored) {

		}

		try {
			Class<?> catalystMultipart = Class.forName("sunsetsatellite.catalyst.CatalystEffectsClient");
			catalystMultipart.getMethod("addSettingsPage").invoke(null);
		} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
		         IllegalAccessException ignored) {

		}
	}
}
