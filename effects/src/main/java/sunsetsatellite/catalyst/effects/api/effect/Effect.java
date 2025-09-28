package sunsetsatellite.catalyst.effects.api.effect;

import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.lang.I18n;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Effect {
	private final String nameKey;
	public final String id;
	public final String imagePath;
	public final IconCoordinate icon;
	public final int color;
	private final List<Modifier<?>> modifiers;
	private final EffectTimeType effectTimeType;
	private final int defaultDuration;
	private int durationIncrease;
	private final int maxStack;
	private boolean persistent = false;

	public Effect(String nameKey, String id, String imagePath, int color, List<Modifier<?>> modifiers, EffectTimeType effectTimeType, int defaultDuration, int maxStack) {
		this.nameKey = nameKey;
        this.id = id;
        this.imagePath = imagePath;
		this.icon = null;
        this.color = color;
        this.modifiers = modifiers;
		this.effectTimeType = effectTimeType;
		this.defaultDuration = defaultDuration;
		this.maxStack = maxStack;
	}

	public Effect(String nameKey, String id, IconCoordinate icon, int color, List<Modifier<?>> modifiers, EffectTimeType effectTimeType, int defaultDuration, int maxStack) {
		this.nameKey = nameKey;
		this.id = id;
		this.imagePath = null;
		this.icon = icon;
		this.color = color;
		this.modifiers = modifiers;
		this.effectTimeType = effectTimeType;
		this.defaultDuration = defaultDuration;
		this.maxStack = maxStack;
	}

	public Effect setPersistent() {
		this.persistent = true;
		return this;
	}

	public boolean isPersistent() {
		return persistent;
	}

	// amount of time that gets added when a new stack is applied
	// can only be applied to effects with time type ADD
	public Effect setDurationIncrease(int increase) {
		if(effectTimeType != EffectTimeType.ADD){
			throw new IllegalArgumentException("Duration increase can only be applied to effects with time type ADD!");
		}
		this.durationIncrease = increase;
		return this;
	}

	public int getDurationIncrease() {
		if(effectTimeType != EffectTimeType.ADD){
			return 0;
		}
		return durationIncrease;
	}

	public String getNameKey() {
		return nameKey;
	}

	public String getName(){
		return I18n.getInstance().translateNameKey(nameKey);
	}

	public List<Modifier<?>> getModifiers() {
		return Collections.unmodifiableList(modifiers);
	}

	public EffectTimeType getTimeType() {
		return effectTimeType;
	}

	public int getDefaultDuration() {
		return defaultDuration;
	}

	public int getMaxStack() {
		return maxStack;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Effect effect = (Effect) o;

		if (getDefaultDuration() != effect.getDefaultDuration()) return false;
		if (getMaxStack() != effect.getMaxStack()) return false;
		if (getNameKey() != null ? !getNameKey().equals(effect.getNameKey()) : effect.getNameKey() != null)
			return false;
		if (!Objects.equals(id, effect.id)) return false;
		if (!Objects.equals(imagePath, effect.imagePath)) return false;
		if (getModifiers() != null ? !getModifiers().equals(effect.getModifiers()) : effect.getModifiers() != null)
			return false;
        return effectTimeType == effect.effectTimeType;
    }

	public <T> void removed(EffectStack effectStack, EffectContainer<T> effectContainer) {

	}

	public <T> void activated(EffectStack effectStack, EffectContainer<T> container) {

	}

	public <T> void paused(EffectStack effectStack, EffectContainer<T> container) {

	}

	public <T> void unpaused(EffectStack effectStack, EffectContainer<T> container) {

	}

	public <T> void tick(EffectStack effectStack, EffectContainer<T> effectContainer) {

	}

	public <T> void expired(EffectStack effectStack, EffectContainer<T> effectContainer) {

	}

	public <T> void stackAdded(EffectStack effectStack, EffectContainer<T> effectContainer) {

	}

	public <T> void stackSubtracted(EffectStack effectStack, EffectContainer<T> effectContainer) {

	}
}
