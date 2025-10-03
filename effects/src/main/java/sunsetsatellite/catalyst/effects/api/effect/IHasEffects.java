package sunsetsatellite.catalyst.effects.api.effect;

import net.minecraft.core.data.tag.Tag;

import java.util.Set;

public interface IHasEffects {
	EffectContainer<?> getContainer();

	boolean isImmuneTo(Tag<Effect> tag);
	Set<Tag<Effect>> getImmunities();
}
