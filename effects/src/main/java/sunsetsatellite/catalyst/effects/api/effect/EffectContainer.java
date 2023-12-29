package sunsetsatellite.catalyst.effects.api.effect;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.Tag;
import sunsetsatellite.catalyst.effects.api.attribute.Attribute;

import java.util.*;

public class EffectContainer {
	private final List<EffectStack> effects = new ArrayList<>();
	private final Set<Attribute<?>> attributes = new HashSet<>();

	public Set<Attribute<?>> getAttributes() {
		return attributes;
	}

	public List<EffectStack> getEffects() {
		return Collections.unmodifiableList(effects);
	}

	public void add(EffectStack effectStack){
		for (EffectStack effect : effects) {
			if(effect.getEffect() == effectStack.getEffect()){
				if(effect.getAmount()+ effectStack.getAmount() <= effect.getEffect().getMaxStack()){
					effect.add(effectStack.getAmount());
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
				effect.subtract(effectStack.getAmount());
				return;
			}
		}
	}

	public void remove(Effect effect){
		List<EffectStack> copy = new ArrayList<>(effects);
		for (EffectStack effectStack : copy) {
			if(effectStack.getEffect() == effect){
				effects.remove(effectStack);
			}
		}
	}

	public void tick(){
		List<EffectStack> copy = new ArrayList<>(effects);
		for (EffectStack effectStack : copy) {
			effectStack.tick();
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
