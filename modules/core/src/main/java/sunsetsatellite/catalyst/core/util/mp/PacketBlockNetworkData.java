package sunsetsatellite.catalyst.core.util.mp;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.network.NetworkManager;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class PacketBlockNetworkData implements NetworkMessage {

	public CompoundTag networks;
	public int dimension;

	public PacketBlockNetworkData(World world) {
		networks = new CompoundTag();
		this.dimension = world.dimension.id;
		NetworkManager.netsToTag(world, networks);
	}

	public PacketBlockNetworkData() {

	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeInt(dimension);
		packet.writeCompoundTag(networks);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		dimension = packet.readInt();
		networks = packet.readCompoundTag();
	}

	@Override
	public void handle(NetworkContext context) {
		if (EnvironmentHelper.isMultiplayerClient()) {
			if (context.player.world.dimension.id == dimension) {
				NetworkManager.clearNets(context.player.dimension);
				NetworkManager.netsFromTag(context.player.world, networks);
			}
		}
	}
}
