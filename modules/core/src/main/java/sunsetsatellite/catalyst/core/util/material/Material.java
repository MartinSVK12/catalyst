package sunsetsatellite.catalyst.core.util.material;

import sunsetsatellite.catalyst.Catalyst;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static sunsetsatellite.catalyst.Catalyst.MOD_ID;

public record Material(int id, String modId, String lang, int color, Element element, List<MaterialStack> components, Map<MaterialAttribute<?>,MaterialProperty<?>> properties) {

	public static final Map<Integer, Material> MATERIALS = new HashMap<>();
	public static final Material UNKNOWN = new Material.Builder(0, MOD_ID,"unknown").build();

	public String formula(){
		if(element != null) return element.symbol();
		if (!components.isEmpty()) {
			if (components.size() == 1) {
				MaterialStack stack = components.get(0);
				if (stack.count > 0) {
					return stack.material.formula();
				}
			}
			StringBuilder builder = new StringBuilder();
			for (MaterialStack stack : components) {
				builder.append(stack.formula());
			}
			return builder.toString();
		}
		return "";
	}

	public <T> MaterialProperty<T> property(MaterialAttribute<T> attribute){
		return (MaterialProperty<T>) properties.get(attribute);
	}

	public boolean hasProperty(MaterialAttribute<?> attribute){
		return properties.containsKey(attribute);
	}

	public String name(){
		return Catalyst.translateNameKey("material."+modId+"."+lang);
	}

	public static class Builder {
		private final int id;
		private final String modId;
		private final String lang;
		private int color = 0xFFFFFF;
		private Element element = null;
		private List<MaterialStack> components = new ArrayList<>();
		private Map<MaterialAttribute<?>,MaterialProperty<?>> properties = new HashMap<>();

		public Builder(int id, String modId, String lang) {
			this.id = id;
			this.modId = modId;
			this.lang = lang;
		}

		public static MaterialStack material(Material mat, int amount){
			return new MaterialStack(mat, amount);
		}

		public Builder color(int color) {
			this.color = color;
			return this;
		}

		public Builder element(Element element) {
			this.element = element;
			return this;
		}

		public Builder components(List<MaterialStack> components) {
			this.components = components;
			return this;
		}

		public Builder component(MaterialStack... components) {
			this.components.addAll(Arrays.asList(components));
			return this;
		}

		public Builder property(MaterialProperty<?>... properties) {
			this.properties.putAll(Arrays.stream(properties).collect(Collectors.toMap(MaterialProperty::attribute, Function.identity())));
			return this;
		}

		public Builder properties(Map<MaterialAttribute<?>,MaterialProperty<?>> properties) {
			this.properties = properties;
			return this;
		}

		public Material build() {
			Material material = new Material(id, modId, lang, color, element, components, properties);
			MATERIALS.put(material.id(), material);
			return material;
		}
	}
}
