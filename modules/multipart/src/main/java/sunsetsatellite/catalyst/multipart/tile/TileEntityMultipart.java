package sunsetsatellite.catalyst.multipart.tile;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Side;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.multipart.api.ISupportsMultiparts;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.api.MultipartType;

import java.util.HashMap;
import java.util.Map;

public class TileEntityMultipart extends TileEntity implements ISupportsMultiparts {

	public final HashMap<Direction, Multipart> parts = (HashMap<Direction, Multipart>) Catalyst.mapOf(Direction.values(), new Multipart[Direction.values().length]);

	public TileEntityMultipart() {

	}

	@Override
	public void readAdditionalData(@NotNull CompoundTag tag) {
		CompoundTag coversNbt = tag.getCompound("Parts");

		for (Map.Entry<String, Tag<?>> entry : coversNbt.getValue().entrySet()) {
			Direction dir = Direction.values()[Integer.parseInt(entry.getKey())];
			CompoundTag partTag = (CompoundTag) entry.getValue();
			parts.put(dir, new Multipart(partTag));
		}
	}

	@Override
	public void writeAdditionalData(@NotNull CompoundTag tag) {
		CompoundTag coversNbt = new CompoundTag();

		for (Map.Entry<Direction, Multipart> entry : parts.entrySet()) {
			if (entry.getValue() == null) continue;
			CompoundTag partNbt = new CompoundTag();
			entry.getValue().writeToNbt(partNbt);
			coversNbt.putCompound(String.valueOf(entry.getKey().ordinal()), partNbt);
		}

		tag.putCompound("Parts", coversNbt);
	}

	@Override
	public HashMap<Direction, Multipart> getParts() {
		return parts;
	}
}
