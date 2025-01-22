package sunsetsatellite.catalyst.energy.electric.example.data;

import sunsetsatellite.catalyst.core.util.DataInitializer;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.energy.electric.api.VoltageTier;
import sunsetsatellite.catalyst.energy.electric.api.WireProperties;
import sunsetsatellite.catalyst.energy.electric.example.block.BlockCable;
import sunsetsatellite.catalyst.energy.electric.example.block.BlockElectricBatteryBox;
import sunsetsatellite.catalyst.energy.electric.example.block.BlockElectricGenerator;
import sunsetsatellite.catalyst.energy.electric.example.block.BlockElectricMachine;
import sunsetsatellite.catalyst.energy.electric.example.block.model.BlockModelCable;
import sunsetsatellite.catalyst.energy.electric.example.block.model.BlockModelWithOverlay;
import turniplabs.halplibe.helper.BlockBuilder;

import static sunsetsatellite.catalyst.CatalystEnergyExample.LOGGER;
import static sunsetsatellite.catalyst.CatalystEnergyExample.MOD_ID;
import static sunsetsatellite.catalyst.energy.electric.example.data.ElectricConfig.getBlockId;

public class ElectricBlocks extends DataInitializer {

	public static BlockCable testWireUlv1x;
	public static BlockCable testWireLv1x;
	public static BlockCable testWireMv1x;
	public static BlockCable testWireHv1x;
	public static BlockCable testWireEv1x;
	public static BlockCable testWireUv1x;
	public static BlockCable testWireOv1x;

	public static BlockElectricGenerator generator;
	public static BlockElectricMachine machine;
	public static BlockElectricBatteryBox batteryBox;

	public void init() {
		if (initialized) return;
		LOGGER.info("Initializing blocks...");

		testWireUlv1x = new BlockBuilder(MOD_ID)
			.setTextures("catalyst-energy-electric-example:block/wire")
			.setBlockModel(BlockModelCable::new)
			.build(new BlockCable(getBlockId("testWireUlv1x"), new WireProperties(1,false,true, WireMaterials.ulvTest)));

		testWireLv1x = new BlockBuilder(MOD_ID)
			.setTextures("catalyst-energy-electric-example:block/wire")
			.setBlockModel(BlockModelCable::new)
			.build(new BlockCable(getBlockId("testWireLv1x"), new WireProperties(2,false,true, WireMaterials.lvTest)));

		testWireMv1x = new BlockBuilder(MOD_ID)
			.setTextures("catalyst-energy-electric-example:block/wire")
			.setBlockModel(BlockModelCable::new)
			.build(new BlockCable(getBlockId("testWireMv1x"), new WireProperties(3, false, true, WireMaterials.mvTest)));

        testWireHv1x = new BlockBuilder(MOD_ID)
			.setTextures("catalyst-energy-electric-example:block/wire")
			.setBlockModel(BlockModelCable::new)
			.build(new BlockCable(getBlockId("testWireHv1x"), new WireProperties(4, false, true, WireMaterials.hvTest)));

        testWireEv1x = new BlockBuilder(MOD_ID)
			.setTextures("catalyst-energy-electric-example:block/wire")
			.setBlockModel(BlockModelCable::new)
			.build(new BlockCable(getBlockId("testWireEv1x"), new WireProperties(8, false, true, WireMaterials.evTest)));

        testWireUv1x = new BlockBuilder(MOD_ID)
			.setTextures("catalyst-energy-electric-example:block/wire")
			.setBlockModel(BlockModelCable::new)
			.build(new BlockCable(getBlockId("testWireUv1x"), new WireProperties(12, false, true, WireMaterials.uvTest)));

        testWireOv1x = new BlockBuilder(MOD_ID)
			.setTextures("catalyst-energy-electric-example:block/wire")
			.setBlockModel(BlockModelCable::new)
			.build(new BlockCable(getBlockId("testWireOv1x"), new WireProperties(16, false, true, WireMaterials.ovTest)));

		generator = new BlockBuilder(MOD_ID)
			.setBlockModel((block)-> new BlockModelWithOverlay(block,"casing")
				.changeOverlay(Direction.Z_NEG,"generator_front"))
			.build(new BlockElectricGenerator("generator", getBlockId("generator"), VoltageTier.LV));

		machine = new BlockBuilder(MOD_ID)
			.setBlockModel((block)-> new BlockModelWithOverlay(block,"casing")
				.changeOverlay(Direction.Z_NEG,"machine_overlay_front"))
			.build(new BlockElectricMachine("machine", getBlockId("machine"), VoltageTier.LV));

		batteryBox = new BlockBuilder(MOD_ID)
			.setBlockModel((block)-> new BlockModelWithOverlay(block,"casing")
				.changeOverlay(Direction.Z_NEG,"energy_out"))
			.build(new BlockElectricBatteryBox("batteryBox", getBlockId("batteryBox"), VoltageTier.LV));

		setInitialized(true);
	}

}
