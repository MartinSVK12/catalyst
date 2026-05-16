package sunsetsatellite.catalyst.core.util;

import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

public class BlockChangeInfo {

	public Block<?> block;
	public int meta;
	public World world;
	public Vec3i pos;

	public BlockChangeInfo(World world, Vec3i pos, Block<?> block, int meta) {
		this.block = block;
		this.meta = meta;
		this.world = world;
		this.pos = pos;
	}
}
