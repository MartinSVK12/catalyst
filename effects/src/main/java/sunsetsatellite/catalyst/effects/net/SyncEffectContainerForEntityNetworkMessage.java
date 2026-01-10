package sunsetsatellite.catalyst.effects.net;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.CatalystEffects;
import sunsetsatellite.catalyst.effects.api.effect.EffectContainer;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

import java.util.Optional;

public class SyncEffectContainerForEntityNetworkMessage implements NetworkMessage {

	private int entityID;
	private EffectContainer<?> container;
	private CompoundTag containerNBT;

	public SyncEffectContainerForEntityNetworkMessage() {
	}

	public SyncEffectContainerForEntityNetworkMessage(Entity entity) {
		this.entityID = entity.id;
		this.container = ((IHasEffects<?>) entity).getContainer();
	}


	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeInt(entityID);
		CompoundTag containerNBT = new CompoundTag();
		container.saveToNbt(containerNBT);
		packet.writeCompoundTag(containerNBT);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.entityID = packet.readInt();
		this.containerNBT = packet.readCompoundTag();
	}

	@Override
	public void handle(NetworkContext context) {
		if (EnvironmentHelper.isServerEnvironment()) return;

		World world = context.player.world;

		if (world == null) {
			CatalystEffects.LOGGER.error("Couldn't syncronize entity effects for {}! Is the world real?", entityID);
			return;
		}

		Optional<Entity> entityOption = world.getLoadedEntityList().stream().filter(e -> e.id == entityID).findFirst();

		if (!entityOption.isPresent()) {
			CatalystEffects.LOGGER.error("Couldn't syncronize entity effects for {}! Entity isn't present", entityID);
			return;
		}

		EffectContainer<?> container = ((IHasEffects<?>) entityOption.get()).getContainer();
		container.removeAll();
		container.loadFromNbt(containerNBT);
	}
}
