package sunsetsatellite.catalyst.effects.api.effect;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.net.SyncEffectContainerForEntityNetworkMessage;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;

import java.util.*;
import java.util.function.Function;

public class EffectContainer<T> {
	private final List<EffectStack> effects = new ArrayList<>();
	private final Set<Attribute<?>> attributes = new HashSet<>();
	public final List<Function<T,List<Modifier<?>>>> additionalModifierSuppliers = new ArrayList<>();
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
		if (!effectStack.getEffect().canApplyTo((Entity) parent)) return;

		for (EffectStack effect : effects) {
			if(effect.getEffect() == effectStack.getEffect()){
				int amount;
				if(effect.getAmount() + effectStack.getAmount() >= effect.getEffect().getMaxStack()){
					amount = effect.getEffect().getMaxStack() - effect.getAmount();
				}else{
					amount = effectStack.getAmount();
				}
				effect.add(amount,this);
				if (EnvironmentHelper.isServerEnvironment()) NetworkHandler.sendToAllPlayers(new SyncEffectContainerForEntityNetworkMessage((Entity) this.getParent()));
				return;
			}
		}

		effects.add(effectStack);
		if (EnvironmentHelper.isServerEnvironment()) NetworkHandler.sendToAllPlayers(new SyncEffectContainerForEntityNetworkMessage((Entity) this.getParent()));
	}

	public void subtract(EffectStack effectStack){
		for (EffectStack effect : effects) {
			if(effect.getEffect() == effectStack.getEffect()){
				effect.subtract(effectStack.getAmount(),this);

				if (EnvironmentHelper.isServerEnvironment()) NetworkHandler.sendToAllPlayers(new SyncEffectContainerForEntityNetworkMessage((Entity) this.getParent()));
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

		if (EnvironmentHelper.isServerEnvironment()) NetworkHandler.sendToAllPlayers(new SyncEffectContainerForEntityNetworkMessage((Entity) this.getParent()));
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
		for (com.mojang.nbt.tags.Tag<?> value : tag.getValues()) {
			if(value instanceof CompoundTag){
				effects.add(new EffectStack((CompoundTag) value));
			}
		}
	}
}
