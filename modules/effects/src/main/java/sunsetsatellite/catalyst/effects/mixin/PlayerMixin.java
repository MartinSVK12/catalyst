package sunsetsatellite.catalyst.effects.mixin;

import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.modifier.IItemWithModifiers;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;

import java.util.ArrayList;
import java.util.Map;

@Mixin(value = Player.class, remap = false)
public class PlayerMixin extends Mob {

	private PlayerMixin(@Nullable World world) {
		super(world);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void init(World world, CallbackInfo ci) {
		((IHasEffects<Player>) this).getContainer().additionalModifierSuppliers.add((player) -> {
			ArrayList<Modifier<?>> modifiers = new ArrayList<>();

			for (int i = 0; i < player.inventory.armorInventory.length; i++) {
				ItemStack stack = player.inventory.armorInventory[i];
				if (stack != null && stack.getItem() instanceof IItemWithModifiers) {
					Map<Modifier<?>, Boolean> itemModifiers = ((IItemWithModifiers) stack.getItem()).getModifiers((IHasEffects<?>) player, stack, i + 100);
					for (Map.Entry<Modifier<?>, Boolean> itemModifierEntry : itemModifiers.entrySet()) {
						if (itemModifierEntry.getValue()) {
							modifiers.add(itemModifierEntry.getKey());
						}
					}
				}
			}

			return modifiers;
		});
	}
}
