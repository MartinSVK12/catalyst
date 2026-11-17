package sunsetsatellite.catalyst.core.util.material;

public record MaterialAttribute<T>(String name) {
	public MaterialProperty<T> property(){
		return new MaterialProperty<>((MaterialAttribute<Void>) this);
	}

	public MaterialProperty<T> property(T value){
		return new MaterialProperty<>(this, value);
	}
}
