package sunsetsatellite.catalyst.core.util;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.Objects;

public class BlockInstance {
	@NotNull
	public Block<?> block;
	@NotNull
	public Vec3i pos;
	public int meta = 0;
	public TileEntity tile;
	public Vec3i offset;

	public BlockInstance(@NotNull Block<?> block, @NotNull Vec3i pos, TileEntity tile) {
		this.block = block;
		this.pos = pos;
		this.tile = tile;
	}

	public BlockInstance(@NotNull Block<?> block, @NotNull Vec3i pos, int meta, TileEntity tile) {
		this.block = block;
		this.pos = pos;
		this.tile = tile;
		this.meta = meta;
	}

	public BlockInstance(@NotNull Vec3i pos, @NotNull WorldSource world){
		this.block = Objects.requireNonNull(pos.getBlock(world));
		this.pos = pos;
		this.tile = pos.getTileEntity(world);
		this.meta = pos.getBlockMetadata(world);
	}

	public boolean exists(World world) {
		Block<?> block = pos.getBlock(world);
		int meta = pos.getBlockMetadata(world);
		return block == this.block && (meta == this.meta || this.meta == -1);
	}

	public boolean existsWithTile(World world) {
		Block<?> block = pos.getBlock(world);
		int meta = pos.getBlockMetadata(world);
		TileEntity tile = pos.getTileEntity(world);
		return block == this.block && (meta == this.meta || this.meta == -1) && tile == this.tile;
	}

	public boolean place(World world) {
		if (pos.getBlock(world).id() == 0) {
			world.setBlockTypeNotify(pos.pos, block);
			world.setBlockData(pos.pos, meta);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "BlockInstance{" +
			"block=" + block +
			", pos=" + pos +
			", meta=" + meta +
			", tile=" + tile +
			'}';
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BlockInstance that)) return false;

		return meta == that.meta && Objects.equals(block, that.block) && Objects.equals(pos, that.pos) && Objects.equals(tile, that.tile);
	}

	@Override
	public int hashCode() {
		int result = block.hashCode();
		result = 31 * result + pos.hashCode();
		result = 31 * result + meta;
		result = 31 * result + (tile != null ? tile.hashCode() : 0);
		return result;
	}
}
