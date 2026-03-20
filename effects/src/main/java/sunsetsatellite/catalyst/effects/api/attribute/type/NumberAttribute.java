package sunsetsatellite.catalyst.effects.api.attribute.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.NumberModifier;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class NumberAttribute<T extends Number> extends Attribute<T> {

	protected final T minValue;
	protected final T maxValue;

	public NumberAttribute(String key, T defaultValue, T minValue, T maxValue) {
		super(key, defaultValue);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	protected List<NumberModifier<? extends Number>> validateModifiers(List<Modifier<?>> modifiers) {
		return modifiers.stream()
			.filter((M) -> M.attribute.getKey().equals(this.getKey()))
			.map((M) -> {
				if (M instanceof NumberModifier) {
					return ((NumberModifier<? extends Number>) M);
				} else {
					return null;
				}
			})
			.filter(Objects::nonNull)
			.sorted(Comparator.comparing(M -> M.type))
			.collect(Collectors.toList());
	}

}
