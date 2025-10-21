package sunsetsatellite.catalyst.effects.api.effect.render.heartContainer;

import net.minecraft.core.entity.player.Player;

/**
 * Implement if you effect hearts to change when effect is applied
 * or player screen to be affected.
 */
public interface IHasCustomHeartContainer {
    HeartContainer getCustomContainer(Player player);
}
