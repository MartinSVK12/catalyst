package sunsetsatellite.catalyst.multiblocks;

import net.minecraft.core.Global;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.*;

public class MultiblockInstance implements Signal.Listener<BlockChangeInfo>{

	private boolean valid = false;
	public final TileEntity origin;
	public final Multiblock data;

	public MultiblockInstance(TileEntity origin, Multiblock data) {
		this.origin = origin;
		this.data = data;
		Catalyst.ANY_BLOCK_CHANGED_SIGNAL.connect(this);
		valid = verifyIntegrity();
	}

	@Override
	public void signalEmitted(Signal<BlockChangeInfo> signal, BlockChangeInfo blockChanged) {
		if(signal != Catalyst.ANY_BLOCK_CHANGED_SIGNAL) return;
		if(origin.worldObj.getBlockTileEntity(origin.x, origin.y, origin.z) != origin || origin.worldObj.getBlockId(origin.x, origin.y, origin.z) == 0 || (!Global.isServer && origin.worldObj != Minecraft.getMinecraft(this).theWorld)) {
			valid = false;
			Catalyst.ANY_BLOCK_CHANGED_SIGNAL.disconnect(this);
			return;
		}
		valid = verifyIntegrity();
	}

	public boolean verifyIntegrity(){
		if(origin.worldObj != null){
			Block block = origin.getBlockType();
			if(block != null){
				return data.isValidAtSilent(origin.worldObj,new BlockInstance(block,new Vec3i(origin.x,origin.y,origin.z),origin), Direction.getDirectionFromSide(origin.worldObj.getBlockMetadata(origin.x,origin.y,origin.z)));
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	public boolean isValid() {

		return valid;
	}
}
