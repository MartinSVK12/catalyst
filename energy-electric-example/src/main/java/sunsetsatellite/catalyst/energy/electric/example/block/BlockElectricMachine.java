package sunsetsatellite.catalyst.energy.electric.example.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.TextFormatting;
import sunsetsatellite.catalyst.energy.electric.api.VoltageTier;
import sunsetsatellite.catalyst.energy.electric.example.tile.TileEntitySimpleElectricMachine;

public class BlockElectricMachine extends BlockElectric{
	public BlockElectricMachine(String key, int id, VoltageTier tier) {
		super(key, id, tier);
	}

	@Override
	protected TileEntity getNewBlockEntity() {
		return new TileEntitySimpleElectricMachine();
	}

	@Override
	public String getDescription(ItemStack stack) {
		return "";
	}

	@Override
	public String getPersistentDescription(ItemStack stack) {
		return String.format("%sMax Voltage %sIN%s: %s%dV %s(%s%s%s)\n%sMax Current Draw: %s%dA\n%sEnergy Capacity: %s%dJ",
			TextFormatting.LIGHT_GRAY, TextFormatting.LIME, TextFormatting.LIGHT_GRAY,
			TextFormatting.LIME, tier.maxVoltage, TextFormatting.LIGHT_GRAY, tier.textColor, tier.name(), TextFormatting.LIGHT_GRAY,
			TextFormatting.LIGHT_GRAY, TextFormatting.ORANGE, 1,
			TextFormatting.LIGHT_GRAY, TextFormatting.YELLOW, tier.maxVoltage*64
		);
	}
}
