package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.core.util.tile.TEFeature;
import sunsetsatellite.catalyst.core.util.tile.feature.ItemContainerFeature;
import sunsetsatellite.catalyst.energy.electric.example.block.color.BlockColorWire;
import sunsetsatellite.catalyst.energy.electric.example.data.ElectricBlocks;
import sunsetsatellite.catalyst.energy.electric.example.data.ElectricConfig;
import sunsetsatellite.catalyst.energy.electric.example.data.ElectricItems;
import sunsetsatellite.catalyst.energy.electric.example.data.WireMaterials;
import sunsetsatellite.catalyst.energy.electric.example.tile.TileEntityCable;
import sunsetsatellite.catalyst.energy.electric.example.tile.TileEntitySimpleElectricBatteryBox;
import sunsetsatellite.catalyst.energy.electric.example.tile.TileEntitySimpleElectricGenerator;
import sunsetsatellite.catalyst.energy.electric.example.tile.TileEntitySimpleElectricMachine;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;

import static sunsetsatellite.catalyst.energy.electric.example.data.ElectricBlocks.*;


public class CatalystEnergyExample implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "catalyst-energy-electric-example";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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

		ElectricConfig.class.getClass();
		new WireMaterials().init();
		new ElectricBlocks().init();
		new ElectricItems().init();

		TEFeature.registerFeature(CatalystEnergy.ITEM_CONTAINER_FEATURE, ItemContainerFeature.class);

		EntityHelper.createTileEntity(TileEntitySimpleElectricBatteryBox.class,"ElBatteryBox");
		EntityHelper.createTileEntity(TileEntitySimpleElectricGenerator.class,"ElSimpleGenerator");
		EntityHelper.createTileEntity(TileEntitySimpleElectricMachine.class,"ElSimpleMachine");
		EntityHelper.createTileEntity(TileEntityCable.class,"ElCable");

		//Catalyst.GUIS.register("ElBatteryBox",new MpGuiEntry(TileEntitySimpleElectricBatteryBox.class, GuiSimpleElectricBatteryBox.class, ContainerSimpleElectricBatteryBox.class));

        LOGGER.info("Example mod for Catalyst's electric energy system initialized.");
    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {
		BlockColorDispatcher.getInstance().addDispatch(testWireUlv1x,new BlockColorWire());
		BlockColorDispatcher.getInstance().addDispatch(testWireLv1x, new BlockColorWire());
		BlockColorDispatcher.getInstance().addDispatch(testWireMv1x, new BlockColorWire());
		BlockColorDispatcher.getInstance().addDispatch(testWireHv1x, new BlockColorWire());
		BlockColorDispatcher.getInstance().addDispatch(testWireEv1x, new BlockColorWire());
		BlockColorDispatcher.getInstance().addDispatch(testWireUv1x, new BlockColorWire());
		BlockColorDispatcher.getInstance().addDispatch(testWireOv1x, new BlockColorWire());
	}
}
