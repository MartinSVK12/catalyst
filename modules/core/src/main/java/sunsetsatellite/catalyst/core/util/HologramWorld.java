package sunsetsatellite.catalyst.core.util;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.util.helper.LightIndexHelper;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.pos.TilePosc;
import net.minecraft.core.world.season.SeasonManager;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypes;
import net.minecraft.core.world.weather.WeatherManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.HashMap;
import java.util.List;

public class HologramWorld implements WorldSource {

	private final HashMap<Vec3i, BlockInstance> blocks = new HashMap<>();

	public HologramWorld(List<BlockInstance> structure) {
		for (BlockInstance block : structure) {
			blocks.put(block.pos, block);
		}
	}

	@Override
	public int getHeightBlocks() {
		return 0;
	}

	@Override
	public @NotNull Block<?> getBlockType(@NotNull TilePosc tilePos) {
		Vec3i vec = new Vec3i(tilePos);
		BlockInstance inst = blocks.get(vec);
		if (inst == null) {
			return Blocks.AIR;
		}
		return inst.block;
	}

	@Override
	public @Nullable TileEntity getTileEntity(@NotNull TilePosc tilePos) {
		return null;
	}

	@Override
	public float getBrightness(@NotNull TilePosc tilePos, int lightEmission) {
		return 1;
	}

	@Override
	public byte getLightIndex(@NotNull TilePosc tilePos, int lightEmission) {
		return LightIndexHelper.lightIndex2f(15,15);
	}

	@Override
	public float getLightBrightness(@NotNull TilePosc tilePos) {
		return 1;
	}

	@Override
	public int getBlockData(@NotNull TilePosc tilePos) {
		return 0;
	}

	@Override
	public @NotNull Material getBlockMaterial(@NotNull TilePosc tilePos) {
		return getBlockType(tilePos).getMaterial();
	}

	@Override
	public boolean isBlockOpaqueCube(@NotNull TilePosc tilePos) {
		return getBlockType(tilePos).isSolidRender();
	}

	@Override
	public boolean isBlockNormalCube(@NotNull TilePosc tilePos) {
		return getBlockType(tilePos).getMaterial().isSolid();
	}

	@Override
	public double getBlockTemperature(@NotNull TilePosc tilePos) {
		return 0;
	}

	@Override
	public double getBlockHumidity(@NotNull TilePosc tilePos) {
		return 0;
	}

	@Override
	public double getBlockVariety(@NotNull TilePosc tilePos) {
		return 0;
	}

	@Override
	public @NotNull SeasonManager getSeasonManager() {
		return null;
	}

	@Override
	public @NotNull WeatherManager getWeatherManager() {
		return null;
	}

	@Override
	public @NotNull Biome getBlockBiome(@NotNull TilePosc tilePos) {
		return null;
	}

	@Override
	public int getSavedLightValue(@NotNull LightLayer lightLayer, @NotNull TilePosc tilePos) {
		return 15;
	}

	@Override
	public byte getSavedLightIndex(@NotNull TilePosc tilePos) {
		return LightIndexHelper.lightIndex2f(15,15);
	}

	@Override
	public @NotNull Dimension getDimension() {
		return Dimension.OVERWORLD;
	}

	@Override
	public @NotNull WorldType getWorldType() {
		return WorldTypes.EMPTY;
	}
}
