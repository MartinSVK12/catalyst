package sunsetsatellite.catalyst.core.util;

import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

public class BlockChangeInfo {

	public int id;
	public int meta;
	public World world;
	public Vec3i pos;

	public BlockChangeInfo(World world, Vec3i pos, int id, int meta) {
		this.id = id;
		this.meta = meta;
		this.world = world;
		this.pos = pos;
	}
}
