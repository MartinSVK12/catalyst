package sunsetsatellite.catalyst.core.util.mp;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.IScreenActionListener;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class PacketScreenAction implements NetworkMessage {
	public Vec3i pos;
	public Class<? extends TileEntity> tileClass;
	public int id;
	public int button;
	public int channel;

	public PacketScreenAction(int id, int button, int channel, Vec3i pos, Class<? extends TileEntity> tileClass) {
		this.id = id;
		this.button = button;
		this.channel = channel;
		this.pos = pos;
		this.tileClass = tileClass;
	}

	public PacketScreenAction() {
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeInt(id);
		packet.writeInt(button);
		packet.writeInt(channel);
		CompoundTag nbt = new CompoundTag();
		pos.writeToNBT(nbt);
		packet.writeCompoundTag(nbt);
		packet.writeString(TileEntityDispatcher.getIDFromClass(tileClass).toString());
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		id = packet.readInt();
		button = packet.readInt();
		channel = packet.readInt();
		pos = new Vec3i(packet.readCompoundTag());
		tileClass = TileEntityDispatcher.getClassFromID(packet.readString());
	}

	@Override
	public void handle(NetworkContext context) {
		if (EnvironmentHelper.isServerEnvironment()) {
			if (context.player.world != null) {
				TileEntity tileEntity = context.player.world.getTileEntity(pos.x, pos.y, pos.z);
				if (tileEntity instanceof IScreenActionListener && tileEntity.worldObj != null) {
					((IScreenActionListener) tileEntity).buttonClicked(id, button, channel);
				}
			}
		}
	}
}
