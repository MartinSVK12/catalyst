package sunsetsatellite.catalyst.energy.electric.example.item;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.TextFormatting;
import sunsetsatellite.catalyst.core.util.ICustomDescription;
import sunsetsatellite.catalyst.core.util.NumberUtil;
import sunsetsatellite.catalyst.energy.electric.api.IElectricItem;
import sunsetsatellite.catalyst.energy.electric.api.IVoltageTiered;
import sunsetsatellite.catalyst.energy.electric.api.VoltageTier;

public class ItemBattery extends Item implements IElectricItem, ICustomDescription, IVoltageTiered {

	public VoltageTier tier;

	public ItemBattery(String name, int id, VoltageTier tier) {
		super(name, id);
		this.tier = tier;
		setMaxStackSize(1);
	}

	@Override
	public String getDescription(ItemStack stack) {
		return "";
	}

	@Override
	public String getPersistentDescription(ItemStack stack) {
		long maxSeconds = ((getCapacity(stack) / (getMaxVoltage(stack) * getMaxOutputAmperage(stack))) / 20);
		long seconds = ((getEnergy(stack) / (getMaxVoltage(stack) * getMaxOutputAmperage(stack))) / 20);
		long mAh = (long) ((maxSeconds / 60f / 60f) * 1000);
		return String.format(
			"%sEnergy: %s%s/%sJ %s(%s%s %sremaining)\n" +
			"%sBattery Voltage: %s%sV %s(%s%s%s)\n" +
			"%sLasts for %s%s %susing %s%dA@%sV %s(%s%s%s)\n",
			TextFormatting.LIGHT_GRAY, TextFormatting.YELLOW, NumberUtil.formatMetric(getEnergy(stack)),  NumberUtil.formatMetric(getCapacity(stack)), TextFormatting.LIGHT_GRAY, TextFormatting.WHITE,
			NumberUtil.formatTime(seconds), TextFormatting.LIGHT_GRAY, TextFormatting.LIGHT_GRAY,
			TextFormatting.LIME, NumberUtil.formatMetric(tier.maxVoltage), TextFormatting.LIGHT_GRAY, tier.textColor, tier.name(), TextFormatting.LIGHT_GRAY,
			TextFormatting.LIGHT_GRAY, TextFormatting.LIME, NumberUtil.formatTime(maxSeconds), TextFormatting.LIGHT_GRAY,
			TextFormatting.ORANGE,getMaxOutputAmperage(stack),NumberUtil.formatMetric(getMaxVoltage(stack)),TextFormatting.LIGHT_GRAY,TextFormatting.WHITE,mAh < 1000 ? mAh+"mAh" : NumberUtil.formatMetric(mAh/1000f)+ "Ah",
			TextFormatting.LIGHT_GRAY
		);
	}

	@Override
	public long getEnergy(ItemStack stack) {
		return stack.getData().getLong("Energy");
	}

	@Override
	public long getCapacity(ItemStack stack) {
		return 128000;
	}

	@Override
	public long getMaxVoltage(ItemStack stack) {
		return tier.maxVoltage;
	}

	@Override
	public long getMaxInputAmperage(ItemStack stack) {
		return 1;
	}

	@Override
	public long getMaxOutputAmperage(ItemStack stack) {
		return 1;
	}

	@Override
	public long charge(ItemStack stack, long energy) {
		long remaining = getCapacityRemaining(stack);
		long charge = Math.min(energy, remaining);
		if(charge+getEnergy(stack) > getCapacity(stack)){
			return 0;
		}
		stack.getData().putLong("Energy",getEnergy(stack)+charge);
		return charge;
	}

	@Override
	public long discharge(ItemStack stack, long energy) {
		long charge = Math.min(getEnergy(stack),energy);
		if(getEnergy(stack)-charge < 0){
			return 0;
		}
		stack.getData().putLong("Energy",getEnergy(stack)-charge);
		return charge;
	}

	@Override
	public VoltageTier getTier() {
		return tier;
	}
}
