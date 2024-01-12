package sunsetsatellite.catalyst.effects;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;

public class ItemGiveEffect extends Item {

	public Effect effect;

	public ItemGiveEffect(String name, int id, Effect effect) {
		super(name, id);
		this.effect = effect;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced) {
		super.onItemUse(itemstack, entityplayer, world, blockX, blockY, blockZ, side, xPlaced, yPlaced);
		EffectStack eff = new EffectStack(((IHasEffects)entityplayer), effect);
		((IHasEffects)entityplayer).getContainer().add(eff);
		eff.start(((IHasEffects)entityplayer).getContainer());
		entityplayer.destroyCurrentEquippedItem();
		return true;
	}
}
