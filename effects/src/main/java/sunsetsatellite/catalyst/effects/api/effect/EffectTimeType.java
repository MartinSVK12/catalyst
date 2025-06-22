package sunsetsatellite.catalyst.effects.api.effect;

public enum EffectTimeType {
	RESET, //effect time resets if a new stack is applied
	KEEP, //effect time does not reset even if a new stack is applied
	PERMANENT, //effect never expires
}
