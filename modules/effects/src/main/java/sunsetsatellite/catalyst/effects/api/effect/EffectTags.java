package sunsetsatellite.catalyst.effects.api.effect;

import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.entity.animal.MobAnimal;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.entity.player.Player;

public class EffectTags {
	public static Tag<Effect> HOSTILES_ARE_IMMUNE = Tag.of("hostiles_are_immune");
	public static Tag<Effect> PASSIVE_ARE_IMMUNE = Tag.of("passive_are_immune");
	public static Tag<Effect> PLAYERS_ARE_IMMUNE = Tag.of("players_are_immune");

	public static void assignTags() {
		EffectTagDispatcher.setImmunityFor(MobMonster.class, HOSTILES_ARE_IMMUNE);
		EffectTagDispatcher.setImmunityFor(MobAnimal.class, PASSIVE_ARE_IMMUNE);
		EffectTagDispatcher.setImmunityFor(Player.class, PLAYERS_ARE_IMMUNE);
	}
}
