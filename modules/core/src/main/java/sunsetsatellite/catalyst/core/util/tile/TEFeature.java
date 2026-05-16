package sunsetsatellite.catalyst.core.util.tile;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import sunsetsatellite.catalyst.Catalyst;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class TEFeature {

	protected static Map<String, Class<? extends TEFeature>> AVAILABLE_FEATURES = new HashMap<>();

	public final String id;
	public World world;
	public TilePos pos;

	protected TEFeature(String id, World world, TilePos pos) {
		this.id = id;
		this.world = world;
	}

	protected TEFeature(String id, World world) {
		this.id = id;
		this.world = world;
	}

	public static void registerFeature(String id, Class<? extends TEFeature> feature) {
		AVAILABLE_FEATURES.put(id, feature);
	}

	public static Class<? extends TEFeature> getFeatureClass(String id) {
		return AVAILABLE_FEATURES.get(id);
	}

	public static Map<String, Class<? extends TEFeature>> getFeatureClasses() {
		return Collections.unmodifiableMap(AVAILABLE_FEATURES);
	}

	public static TEFeature createFeature(String id, World world, int x, int y, int z) {
		Class<? extends TEFeature> clazz = AVAILABLE_FEATURES.get(id);
		if (clazz == null) {
			throw new RuntimeException("No tile entity feature with id '" + id + "'!");
		}
		try {
			Constructor<? extends TEFeature> c = clazz.getDeclaredConstructor(String.class, World.class, int.class, int.class, int.class);
			c.setAccessible(true);
			TEFeature feature = c.newInstance(id, world, x, y, z);
			c.setAccessible(false);
			return feature;
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
				 IllegalAccessException e) {
			throw new RuntimeException("Failed to create tile entity feature: '" + id + "'!", e);
		}
	}

	public static TEFeature loadFeature(CompoundTag nbt, World world) {
		String id = nbt.getString("id");
		Class<? extends TEFeature> clazz = AVAILABLE_FEATURES.get(id);
		if (clazz == null) {
			Catalyst.LOGGER.error("No tile entity feature with id '{}'!", id);
			return null;
		}
		try {
			Constructor<? extends TEFeature> c = clazz.getDeclaredConstructor(String.class, World.class);
			c.setAccessible(true);
			TEFeature feature = c.newInstance(id, world);
			feature.readFromNBT(nbt);
			c.setAccessible(false);
			return feature;
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
				 IllegalAccessException e) {
			throw new RuntimeException("Failed to create tile entity feature: '" + id + "'!", e);
		}
	}

	public Block<?> getBlock() {
		return world.getBlockType(pos);
	}

	public int getBlockMetadata() {
		return world.getBlockData(pos);
	}

	public TileEntity getTile() {
		return world.getTileEntity(pos);
	}

	public void readFromNBT(CompoundTag nbttagcompound) {
		int x = nbttagcompound.getInteger("x");
		int y = nbttagcompound.getInteger("y");
		int z = nbttagcompound.getInteger("z");
		pos = new TilePos(x, y, z);
	}

	public void writeToNBT(CompoundTag nbttagcompound) {
		nbttagcompound.putString("id", id);
		nbttagcompound.putInt("x", pos.x);
		nbttagcompound.putInt("y", pos.y);
		nbttagcompound.putInt("z", pos.z);
	}

	public abstract void tick();

	public abstract void init(Block<?> block);

}
