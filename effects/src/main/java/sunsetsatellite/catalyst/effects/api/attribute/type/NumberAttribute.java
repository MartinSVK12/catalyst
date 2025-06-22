package sunsetsatellite.catalyst.effects.api.attribute.type;

import net.minecraft.core.util.collection.Pair;
import sunsetsatellite.catalyst.effects.api.attribute.Attribute;

import java.time.temporal.ValueRange;

public abstract class NumberAttribute<T extends Number> extends Attribute<T> {

	protected final T minValue;
	protected final T maxValue;

	public NumberAttribute(String key, T defaultValue, T minValue, T maxValue) {
		super(key, defaultValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}
