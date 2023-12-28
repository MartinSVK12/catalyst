package sunsetsatellite.catalyst.effects.api.modifier;

import sunsetsatellite.catalyst.effects.api.attribute.Attribute;

import java.util.Objects;

public abstract class Modifier<T> {
	public Attribute<T> attribute;
	public ModifierType type;
	public T value;

	public Modifier(Attribute<T> attribute, ModifierType type, T value) {
		this.attribute = attribute;
		this.type = type;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format(type.template,value,attribute.getName());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Modifier<?> modifier = (Modifier<?>) o;

        return Objects.equals(attribute, modifier.attribute);
    }
}
