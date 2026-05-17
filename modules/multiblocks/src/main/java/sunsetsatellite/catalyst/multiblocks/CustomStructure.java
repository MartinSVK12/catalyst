package sunsetsatellite.catalyst.multiblocks;

import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.structure.SavedStructure;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import sunsetsatellite.catalyst.CatalystMultiblocks;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CustomStructure extends Structure {

    public World world;
    public boolean hasOrigin = false;
	public String path;
	public String name;

    public CustomStructure(String id, World world, boolean placeAir, boolean replaceBlocks) {
        super("custom", id, new CompoundTag(), placeAir, replaceBlocks);
        this.world = world;
        this.translateKey = id;
		this.path = world.getLevelStorage().getDataFile("struct_" + translateKey).getPath();
		loadFromNBT(id);
		this.name = data.getString("Name");
    }

	/**
	 * Loads a vanilla BTA! structure from a file and converts it to a Catalyst structure.
	 * @param id The path to the structure file.
	 */
	public CustomStructure(String id){
		super("custom", Path.of(id).getFileName().toString(), new CompoundTag(), true, true);
		this.path = id;
		this.name = Path.of(id).getFileName().toString();

		List<BlockInstance> list = new ArrayList<>();

		SavedStructure s = SavedStructure.load(id);

		if(s == null) throw new NullPointerException("Structure at '" + id + "' couldn't be loaded.");
		final @NotNull Vector3ic size = s.getSize();
		final @NotNull Vector3i tempVector = new Vector3i();
		for (int dx = 0; dx < size.x(); dx++) {
			for (int dy = 0; dy < size.y(); dy++) {
				for (int dz = 0; dz < size.z(); dz++) {
					final @NotNull Block<?> block = s.getBlockType(tempVector.set(dx, dy, dz));
					final int data = s.getBlockData(tempVector);
					list.add(new BlockInstance(block, new Vec3i(tempVector), data, null));
				}
			}
		}

		this.data = StructureSaver.serialize(name, list, false, null);
	}

    @Override
    public String getTranslatedName() {
        return name;
    }

    @Override
    public String getFullFilePath() {
        return path;
    }

    @Override
    protected void loadFromNBT(String id) {
        try {
            File file = world.getLevelStorage().getDataFile("struct_" + id);
            if (file == null) return;
            data = NbtIo.readCompressed(Files.newInputStream(file.toPath()));
            hasOrigin = data.containsKey("Origin");
        } catch (IOException e) {
            CatalystMultiblocks.LOGGER.error("Failed to load structure: {}", id);
            e.printStackTrace();
        }
    }

    @Override
    public BlockInstance getOrigin() {
        if(hasOrigin) super.getOrigin();
        return null;
    }

    @Override
    public BlockInstance getOrigin(Vec3i origin) {
        if(hasOrigin) super.getOrigin(origin);
        return null;
    }

    @Override
    public BlockInstance getOrigin(World world, Vec3i origin) {
        if(hasOrigin) super.getOrigin(world, origin);
        return null;
    }

    @Override
    public BlockInstance getOrigin(Vec3i origin, Direction dir) {
        if(hasOrigin) super.getOrigin(origin, dir);
        return null;
    }
}
