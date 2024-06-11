package sunsetsatellite.catalyst.core.util;


import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.TextFormatting;

public interface ICustomDescription {

    String getDescription(ItemStack stack);

	default String getPersistentDescription(ItemStack stack) {
		return "";
	};
}
