package sunsetsatellite.catalyst.effects.api.modifier;

public enum ModifierType {
	SET("%s %s"),
	ADD("+%s %s"),
	SUBTRACT("-%s %s"),
	PERCENT_ADD("+%s%% %s"),
	PERCENT_SUBTRACT("-%s%% %s"),
	MULTIPLY("x%s %s"),
	DIVIDE("÷%s %s");

	public final String template;

	ModifierType(String template) {
		this.template = template;
	}
}
