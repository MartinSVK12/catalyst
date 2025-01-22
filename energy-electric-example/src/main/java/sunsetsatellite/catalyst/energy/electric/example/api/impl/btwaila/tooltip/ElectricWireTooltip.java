package sunsetsatellite.catalyst.energy.electric.example.api.impl.btwaila.tooltip;

import net.minecraft.client.render.stitcher.TextureRegistry;
import sunsetsatellite.catalyst.energy.electric.base.TileEntityElectricConductor;
import sunsetsatellite.catalyst.energy.electric.example.tile.TileEntityCable;
import toufoumaster.btwaila.gui.components.AdvancedInfoComponent;
import toufoumaster.btwaila.tooltips.TileTooltip;
import toufoumaster.btwaila.util.ProgressBarOptions;

public class ElectricWireTooltip extends TileTooltip<TileEntityElectricConductor> {
	@Override
	public void initTooltip() {
		addClass(TileEntityCable.class);
	}

	@Override
	public void drawAdvancedTooltip(TileEntityElectricConductor tile, AdvancedInfoComponent c) {
		ProgressBarOptions progressBarOptions = new ProgressBarOptions(170, "     Avg. Current (mA): ", true, false);
		progressBarOptions.fgOptions.setColor(0xFFAA00);
		progressBarOptions.fgOptions.setCoordinate(TextureRegistry.getTexture("minecraft:block/sand"));
		progressBarOptions.bgOptions.setCoordinate(TextureRegistry.getTexture("minecraft:block/obsidian"));
		c.drawProgressBarTextureWithText((int) (tile.getAverageAmpLoad()*1000), (int) (tile.getAmpRating()*1000), progressBarOptions,0);
	}
}
