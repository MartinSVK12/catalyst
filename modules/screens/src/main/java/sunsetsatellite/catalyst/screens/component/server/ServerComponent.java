package sunsetsatellite.catalyst.screens.component.server;

import com.mojang.nbt.tags.CompoundTag;

public abstract class ServerComponent {

	public abstract void writeToNbt(CompoundTag tag);
	public abstract void readFromNbt(CompoundTag tag);

}
