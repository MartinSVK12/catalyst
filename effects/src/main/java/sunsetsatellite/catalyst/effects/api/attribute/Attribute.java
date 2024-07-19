package sunsetsatellite.catalyst.effects.api.attribute;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.EntityCreeper;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.lang.I18n;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public abstract class Attribute<T> {
	protected final String key;
	protected final T baseValue;
	protected boolean isDefault = false;
	protected List<Class<? extends Entity>> validFor = new ArrayList<>(Collections.singleton(Entity.class));

	public Attribute(String key, T defaultValue) {
		this.key = key;
		this.baseValue = defaultValue;
	}

	public Attribute<T> setAsDefault(){
		this.isDefault = true;
		return this;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public Attribute<T> setValidEntities(List<Class<? extends Entity>> validFor) {
		this.validFor = validFor;
		return this;
	}

	public List<Class<? extends Entity>> getValidEntities() {
		return validFor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Attribute<?> attribute = (Attribute<?>) o;

		if (!Objects.equals(key, attribute.key)) return false;
        return Objects.equals(baseValue, attribute.baseValue);
    }

	@Override
	public String toString() {
		return "Attribute{" + getName() +": "+ baseValue +'}';
	}

	public String getKey() {
		return key;
	}

	public T getBaseValue() {
		return baseValue;
	}

	public String getName() {
		return I18n.getInstance().translateNameKey(key);
	}

	public String getDesc() {
		return I18n.getInstance().translateDescKey(key);
	}

	public abstract T calculate(IHasEffects target);
	public abstract T calculate(IHasEffects target, T baseValue);
}
