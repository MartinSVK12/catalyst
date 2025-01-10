package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.data.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.core.util.tile.TEFeature;
import sunsetsatellite.catalyst.core.util.tile.feature.ItemContainerFeature;
import sunsetsatellite.catalyst.energy.simple.test.block.BlockBatteryBox;
import sunsetsatellite.catalyst.energy.simple.test.block.BlockSimpleGenerator;
import sunsetsatellite.catalyst.energy.simple.test.block.BlockSimpleMachine;
import sunsetsatellite.catalyst.energy.simple.test.block.BlockWire;
import sunsetsatellite.catalyst.energy.simple.test.tile.TileEntityBatteryBox;
import sunsetsatellite.catalyst.energy.simple.test.tile.TileEntitySimpleGenerator;
import sunsetsatellite.catalyst.energy.simple.test.tile.TileEntitySimpleMachine;
import sunsetsatellite.catalyst.energy.simple.test.tile.TileEntityWire;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;


public class CatalystEnergy implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "catalyst-energy";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler config;

	public static final Tag<Block> ENERGY_CONDUITS_CONNECT = Tag.of("energy_conduits_connect");
	public static final Tag<Block> WIRES_CONNECT = Tag.of("wires_connect");

	public static final String ITEM_CONTAINER_FEATURE = "catalyst-energy:feature/item_container";

	public static final BlockWire wire = new BlockBuilder(MOD_ID)
		.setTextures("catalyst-energy:block/wire")
		.addTags(ENERGY_CONDUITS_CONNECT)
		.build(new BlockWire("wire",1550));
	public static final BlockBatteryBox box = new BlockBuilder(MOD_ID)
		.setTextures("catalyst-energy:block/battery_box")
		.addTags(ENERGY_CONDUITS_CONNECT)
		.build(new BlockBatteryBox("box",1551));
	public static final BlockSimpleGenerator generator = new BlockBuilder(MOD_ID)
		.setTextures("catalyst-energy:block/machine_side")
		.setSouthTexture("catalyst-energy:block/generator")
		.addTags(ENERGY_CONDUITS_CONNECT)
		.build(new BlockSimpleGenerator("generator",1552));
	public static final BlockSimpleMachine machine = new BlockBuilder(MOD_ID)
		.setTextures("catalyst-energy:block/machine_side")
		.setSouthTexture("catalyst-energy:block/machine")
		.addTags(ENERGY_CONDUITS_CONNECT)
		.build(new BlockSimpleMachine("machine",1553));

	static {
		Toml configToml = new Toml("Catalyst: Energy configuration file.");
		configToml.addEntry("energyName","Energy");
		configToml.addEntry("energySuffix","E");
		config = new TomlConfigHandler(MOD_ID,configToml);
	}
	public static final String ENERGY_NAME = config.getString("energyName");
	public static final String ENERGY_SUFFIX = config.getString("energySuffix");

	public static double map(double valueCoord1,
							 double startCoord1, double endCoord1,
							 double startCoord2, double endCoord2) {

		final double EPSILON = 1e-12;
		if (Math.abs(endCoord1 - startCoord1) < EPSILON) {
			throw new ArithmeticException("Division by 0");
		}

		double ratio = (endCoord2 - startCoord2) / (endCoord1 - startCoord1);
		return ratio * (valueCoord1 - startCoord1) + startCoord2;
	}

    @Override
    public void onInitialize() {
		EntityHelper.createTileEntity(TileEntityBatteryBox.class,"BatteryBox");
		EntityHelper.createTileEntity(TileEntitySimpleGenerator.class,"SimpleGenerator");
		EntityHelper.createTileEntity(TileEntitySimpleMachine.class,"SimpleMachine");
		EntityHelper.createTileEntity(TileEntityWire.class,"Wire");

		TEFeature.registerFeature(ITEM_CONTAINER_FEATURE, ItemContainerFeature.class);

        LOGGER.info("Catalyst: Energy initialized.");
    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}
}
