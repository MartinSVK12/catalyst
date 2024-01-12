package sunsetsatellite.catalyst.effects.api.effect;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.Tag;
import sunsetsatellite.catalyst.effects.api.attribute.Attribute;

import java.util.*;

public class EffectContainer<T> {
	private final List<EffectStack> effects = new ArrayList<>();
	private final Set<Attribute<?>> attributes = new HashSet<>();
	private final T parent;

    public EffectContainer(T parent) {
        this.parent = parent;
    }

    public Set<Attribute<?>> getAttributes() {
		return attributes;
	}

	public List<EffectStack> getEffects() {
		return Collections.unmodifiableList(effects);
	}

	public T getParent() {
		return parent;
	}

	public void add(EffectStack effectStack){
		for (EffectStack effect : effects) {
			if(effect.getEffect() == effectStack.getEffect()){
				if(effect.getAmount()+ effectStack.getAmount() <= effect.getEffect().getMaxStack()){
					effect.add(effectStack.getAmount(),this);
					return;
				} else {
					return;
				}
			}
		}
		effects.add(effectStack);
	}

	public void subtract(EffectStack effectStack){
		for (EffectStack effect : effects) {
			if(effect.getEffect() == effectStack.getEffect()){
				effect.subtract(effectStack.getAmount(),this);
				return;
			}
		}
	}

	public void remove(Effect effect){
		List<EffectStack> copy = new ArrayList<>(effects);
		for (EffectStack effectStack : copy) {
			if(effectStack.getEffect() == effect){
				effects.remove(effectStack);
				effectStack.getEffect().removed(effectStack,this);
			}
		}
	}
	public void removeAll() {
		List<EffectStack> copy = new ArrayList<>(effects);
		for (EffectStack effectStack : copy) {
			effectStack.getEffect().removed(effectStack,this);
			effects.remove(effectStack);
		}
	}

	public boolean hasEffect(Effect effect) {
        for (EffectStack effectStack : effects) {
            if (effectStack.getEffect() == effect) {
                return true;
            }
        }
		return false;
    }

	public boolean hasAttribute(Attribute<?> attribute) {
        return attributes.contains(attribute);
    }


	public void tick(){
		List<EffectStack> copy = new ArrayList<>(effects);
		for (EffectStack effectStack : copy) {
			effectStack.tick(this);
			if(effectStack.getAmount() < 1){
				effects.remove(effectStack);
			}
			if(effectStack.isFinished()){
				effects.remove(effectStack);
			}
		}
	}

	public void saveToNbt(CompoundTag tag){
        for (int i = 0; i < effects.size(); i++) {
            EffectStack effect = effects.get(i);
            CompoundTag effectTag = new CompoundTag();
            effect.saveToNbt(effectTag);
            tag.putCompound(String.valueOf(i),effectTag);
        }
	}

	public void loadFromNbt(CompoundTag tag){
		for (Tag<?> value : tag.getValues()) {
			if(value instanceof CompoundTag){
				effects.add(new EffectStack((CompoundTag) value));
			}
		}
	}
}
