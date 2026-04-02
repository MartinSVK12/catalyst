package sunsetsatellite.catalyst.multiblocks;

import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

public class StructureSaver {

	public static CompoundTag serialize(@NotNull String name, @NotNull List<BlockInstance> blocks, boolean saveTileEntityData, @Nullable BlockInstance origin) {
		CompoundTag structureData = new CompoundTag();
		CompoundTag blocksTag = new CompoundTag();
		CompoundTag tileEntitiesTag = new CompoundTag();
		CompoundTag substitutionsTag = new CompoundTag();
		CompoundTag originTag = new CompoundTag();

		for (int i = 0; i < blocks.size(); i++) {
			BlockInstance block = blocks.get(i);
			CompoundTag blockTag = new CompoundTag();
			CompoundTag posTag = new CompoundTag();
			boolean isTile = block.block.isEntityTile;
			block.pos.writeToNBT(posTag);
			blockTag.putString("id", block.block.namespaceId().toString());
			blockTag.putInt("meta", block.meta);
			blockTag.putBoolean("tile", isTile);
			blockTag.putCompound("pos", posTag);
			blocksTag.put(String.valueOf(i), blockTag);
			if (isTile) {
				if (block.tile != null && saveTileEntityData) {
					CompoundTag data = new CompoundTag();
					block.tile.writeToNBT(data);
					blockTag.putCompound("data", data);
				}
				tileEntitiesTag.put(String.valueOf(i), blockTag);
			}
		}

		structureData.putString("Name", name);
		structureData.putCompound("Blocks", blocksTag);
		structureData.putCompound("TileEntities", tileEntitiesTag);
		structureData.putCompound("Substitutions", substitutionsTag);
		if(origin != null) {
			CompoundTag posTag = new CompoundTag();
			new Vec3i().writeToNBT(posTag);
			boolean isTile = origin.block.isEntityTile;
			originTag.putString("id", origin.block.namespaceId().toString());
			originTag.putInt("meta", origin.meta);
			originTag.putBoolean("tile", isTile);
			originTag.putCompound("pos", posTag);
			structureData.putCompound("Origin", originTag);
		}

		return structureData;
	}

	public static UUID save(CompoundTag data, World world) {
		try {
			UUID uuid = UUID.randomUUID();
			File file = world.getSaveHandler().getDataFile("struct_" + uuid);
			if (file == null) return null;
			NbtIo.writeCompressed(data, Files.newOutputStream(file.toPath()));
			return uuid;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
