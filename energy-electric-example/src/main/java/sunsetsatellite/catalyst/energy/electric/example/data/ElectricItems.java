package sunsetsatellite.catalyst.energy.electric.example.data;

import sunsetsatellite.catalyst.core.util.DataInitializer;
import sunsetsatellite.catalyst.energy.electric.example.item.ItemBattery;
import sunsetsatellite.catalyst.energy.electric.example.item.model.ItemModelBattery;
import sunsetsatellite.catalyst.energy.electric.api.VoltageTier;
import turniplabs.halplibe.helper.ItemBuilder;

import static sunsetsatellite.catalyst.CatalystEnergy.LOGGER;
import static sunsetsatellite.catalyst.CatalystEnergyExample.MOD_ID;

public class ElectricItems extends DataInitializer {

	public static ItemBattery battery;

	public void init() {
		if (initialized) return;
		LOGGER.info("Initializing items...");

		battery = new ItemBuilder(MOD_ID)
			.setIcon("catalyst-energy-electric-example:item/battery_0")
			.setItemModel((item)-> new ItemModelBattery(item,MOD_ID))
			.build(new ItemBattery("battery",ElectricConfig.getItemId("battery"), VoltageTier.LV));

		setInitialized(true);
	}
}
