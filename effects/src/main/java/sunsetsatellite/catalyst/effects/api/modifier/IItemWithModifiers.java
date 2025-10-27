package sunsetsatellite.catalyst.effects.api.modifier;

import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;

import java.util.Map;

public interface IItemWithModifiers {
	Map<Modifier<?>,Boolean> getModifiers(IHasEffects<?> target, ItemStack stack, int slot);
}
