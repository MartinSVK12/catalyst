package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import sunsetsatellite.catalyst.core.util.mp.IMpGui;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerMixin implements IMpGui {
}
