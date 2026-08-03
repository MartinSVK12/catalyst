package sunsetsatellite.catalyst.core.util.mp;

import net.minecraft.core.block.Blocks;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.BlockChangeInfo;
import sunsetsatellite.catalyst.core.util.network.Network;
import sunsetsatellite.catalyst.core.util.network.NetworkManager;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class PacketRemoveNetworkBlock implements NetworkMessage {

	public int x;
	public int y;
	public int z;
	public int dim;

	public PacketRemoveNetworkBlock() {
	}

	public PacketRemoveNetworkBlock(int x, int y, int z, int dim) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dim = dim;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeInt(this.x);
		packet.writeInt(this.y);
		packet.writeInt(this.z);
		packet.writeInt(this.dim);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.x = packet.readInt();
		this.y = packet.readInt();
		this.z = packet.readInt();
		this.dim = packet.readInt();
	}

	@Override
	public void handle(NetworkContext context) {
		if (EnvironmentHelper.isMultiplayerClient()) {
			if (context.player.world.dimension.id == dim) {
				Vec3i v = new Vec3i(x, y, z);
				Network net = NetworkManager.getNet(context.player.world, v);
				if (net != null) {
					net.update();
				}
				NetworkManager.removeBlock(new BlockChangeInfo(context.player.world, v, Blocks.AIR, 0));
			}
		}
	}
}
