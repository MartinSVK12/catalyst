package sunsetsatellite.catalyst.core.util.material;

import sunsetsatellite.catalyst.core.util.SmallDigits;

import java.util.Objects;

public final class MaterialStack {
	public Material material;
	public int count;

	public MaterialStack(Material material, int count) {
		this.material = material;
		this.count = count;
	}

	public String formula() {
		String formula = material.formula();

		StringBuilder builder = new StringBuilder(formula.length());
		if (formula.isEmpty()) {
			builder.append("?");
		} else if (material.components().size() > 1) {
			builder.append("(");
			builder.append(formula);
			builder.append(")");
		} else {
			builder.append(formula);
		}
		if (count > 1) {
			builder.append(SmallDigits.toSmallDownNumber(String.valueOf(count)));
		}
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (MaterialStack) obj;
		return Objects.equals(this.material, that.material) &&
			this.count == that.count;
	}

	@Override
	public int hashCode() {
		return Objects.hash(material, count);
	}

	@Override
	public String toString() {
		return "MaterialStack[" +
			"material=" + material + ", " +
			"count=" + count + ']';
	}

}
