package sunsetsatellite.catalyst.effects.api.modifier.type;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;

import java.util.function.BiFunction;

public abstract class NumberModifier<T extends Number> extends Modifier<T> {

	public BiFunction<T, Integer, T> stackFunction;

	public NumberModifier(Attribute<T> attribute, ModifierType type, T value, BiFunction<T, Integer, T> stackFunction) {
		super(attribute, type, value);
		this.stackFunction = stackFunction;
	}

	public T calculate(EffectStack effectStack) {
		return stackFunction.apply(value, effectStack.getAmount());
	}
}
