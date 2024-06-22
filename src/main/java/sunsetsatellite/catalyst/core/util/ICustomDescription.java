package sunsetsatellite.catalyst.core.util;


import net.minecraft.core.item.ItemStack;

public interface ICustomDescription {

    String getDescription(ItemStack stack);

	default String getPersistentDescription(ItemStack stack) {
		return "";
	}
}
