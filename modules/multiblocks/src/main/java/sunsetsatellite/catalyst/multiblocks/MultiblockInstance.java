package sunsetsatellite.catalyst.multiblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Global;
import net.minecraft.core.block.entity.TileEntity;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.BlockChangeInfo;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.Signal;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class MultiblockInstance implements Signal.Listener<BlockChangeInfo> {

	private boolean valid = false;
	public final TileEntity origin;
	public final Multiblock data;

	public List<BlockInstance> cachedSubstitutions = new ArrayList<>();
	public List<BlockInstance> cachedBlocks = new ArrayList<>();
	public List<BlockInstance> cachedTEs = new ArrayList<>();
	public Direction cachedDirection;
	public Vec3i cachedPosition;

	public MultiblockInstance(TileEntity origin, Multiblock data) {
		this.origin = origin;
		this.data = data;
		Catalyst.ANY_BLOCK_CHANGED_SIGNAL.connect(this);
		valid = verifyIntegrity();
	}

	@Override
	public void signalEmitted(Signal<BlockChangeInfo> signal, BlockChangeInfo blockChanged) {
		if (signal != Catalyst.ANY_BLOCK_CHANGED_SIGNAL) return;
		if (origin == null) {
			valid = false;
			return;
		}
		//todo: might not be the best idea
		Vec3i pos = new Vec3i(origin.tilePos);
		if (blockChanged.pos.distanceTo(pos) > 64) {
			return;
		}
		if (origin.worldObj == null) {
			valid = false;
			return;
		}
		if (origin.worldObj.getTileEntity(origin.tilePos) != origin || origin.worldObj.getBlockType(origin.tilePos).id() == 0 || (!Global.isServer && origin.worldObj != Minecraft.getMinecraft().currentWorld)) {
			valid = false;
			Catalyst.ANY_BLOCK_CHANGED_SIGNAL.disconnect(this);
			return;
		}
		valid = verifyIntegrity();
	}

	public boolean verifyIntegrity() {
		if (origin.worldObj != null) {
			Vec3i pos = new Vec3i(origin.tilePos);
			Direction dir = Direction.getDirectionFromSide(origin.worldObj.getBlockData(origin.tilePos));
			if(dir != cachedDirection || !pos.equals(cachedPosition)){
				cachedSubstitutions = data.getSubstitutions(pos, dir);
				cachedBlocks = data.getBlocks(pos, dir);
				cachedTEs = data.getTileEntities(origin.worldObj, pos, dir);
				cachedDirection = dir;
				cachedPosition = pos;
			}
			return data.isValidAtSilent(origin.worldObj, cachedBlocks, cachedSubstitutions);
		} else {
			return false;
		}

	}

	public boolean isValid() {

		return valid;
	}
}
