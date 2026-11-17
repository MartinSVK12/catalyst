package sunsetsatellite.catalyst.core.util.material;

public record MaterialProperty<T>(MaterialAttribute<T> attribute, T value) {

	public MaterialProperty(MaterialAttribute<Void> attribute) {
		this((MaterialAttribute<T>) attribute, null);
	}
}
