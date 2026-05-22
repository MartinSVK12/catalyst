package sunsetsatellite.catalyst.screens.packet;

import com.mojang.nbt.tags.CompoundTag;
import org.jspecify.annotations.NonNull;
import sunsetsatellite.catalyst.screens.menu.MenuComposed;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class NetworkMessageSendServerData implements NetworkMessage {

	public CompoundTag tag;

	public NetworkMessageSendServerData(CompoundTag tag) {
		this.tag = tag;
	}

	public NetworkMessageSendServerData() {
	}

	@Override
	public void encodeToUniversalPacket(@NonNull UniversalPacket packet) {
		packet.writeCompoundTag(tag);
	}

	@Override
	public void decodeFromUniversalPacket(@NonNull UniversalPacket packet) {
		tag = packet.readCompoundTag();
	}

	@Override
	public void handleServerEnv(NetworkContext context) {
		if(context.player.containerMenu instanceof MenuComposed menu) {
			if(!menu.initialized){
				menu.init(tag);
			}
		}
	}
}
