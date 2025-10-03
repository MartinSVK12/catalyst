package sunsetsatellite.catalyst.effects.api.effect;

import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("UnusedReturnValue")
public class EffectTagDispatcher {
	protected EffectTagDispatcher() {}

	private static final HashMap<Class<? extends IHasEffects>, Set<Tag<Effect>>> immunityMap = new HashMap<>();
	private static final HashMap<Class<? extends IHasEffects>, Set<Tag<Effect>>> removedImmunityMap = new HashMap<>();

	public static @NotNull Set<Tag<Effect>> getImmunitiesFor(Class<? extends Entity> holder) {
		Set<Tag<Effect>> tags = new HashSet<>();

		List<Class<? extends IHasEffects>> classList = findGrandpa((Class<? extends IHasEffects>) holder);

		for (int i = classList.size()-1; i >= 0; i--) {
			Set<Tag<Effect>> superTags = immunityMap.get(classList.get(i));
			Set<Tag<Effect>> removedSuperTags = removedImmunityMap.get(classList.get(i));

			if (superTags != null) tags.addAll(superTags);

			if (removedSuperTags != null) {
				for (Tag<Effect> tag : removedSuperTags) {
					tags.remove(tag);
				}
			}
		}

		return tags;
	}

	private static List<Class<? extends IHasEffects>> findGrandpa(Class<? extends IHasEffects> target) {
		List<Class<? extends IHasEffects>> list = new ArrayList<>();

		Class<? extends IHasEffects> clazz = target;
		while (IHasEffects.class.isAssignableFrom(clazz)) {
			list.add(clazz);
			Class<?> zuper = clazz.getSuperclass();
			if (IHasEffects.class.isAssignableFrom(zuper)) { clazz = (Class<? extends IHasEffects>) zuper; }
			else break;
		}

		return list;
	}

	@SafeVarargs
	public static void setImmunityFor(Class<? extends Entity> clazz, Tag<Effect>... tags) {
		Set<Tag<Effect>> immunities = immunityMap.computeIfAbsent((Class) clazz, n -> new HashSet<>());
		Set<Tag<Effect>> removedImmunities = removedImmunityMap.computeIfAbsent((Class) clazz, n -> new HashSet<>());

		for (Tag<Effect> tag : tags) {
			immunities.add(tag);
			removedImmunities.remove(tag);
		}
	}

	@SafeVarargs
	public static void removeImmunityFor(Class<? extends Entity> clazz, Tag<Effect>... tags) {
		Set<Tag<Effect>> immunities = immunityMap.computeIfAbsent((Class) clazz, n -> new HashSet<>());
		Set<Tag<Effect>> removedImmunities = removedImmunityMap.computeIfAbsent((Class) clazz, n -> new HashSet<>());

		for (Tag<Effect> tag : tags) {
			if (immunities.contains(tag)) immunities.remove(tag);
			else removedImmunities.add(tag);
		}
	}
}
