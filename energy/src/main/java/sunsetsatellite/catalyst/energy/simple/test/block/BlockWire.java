package sunsetsatellite.catalyst.energy.simple.test.block;

import net.minecraft.core.block.BlockTileEntity;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.core.util.ConduitCapability;
import sunsetsatellite.catalyst.core.util.IConduitBlock;
import sunsetsatellite.catalyst.core.util.network.NetworkComponent;
import sunsetsatellite.catalyst.core.util.network.NetworkType;
import sunsetsatellite.catalyst.energy.simple.test.tile.TileEntityWire;

public class BlockWire extends BlockTileEntity implements IConduitBlock, NetworkComponent {
	public BlockWire(String key, int id) {
		super(key, id, Material.metal);
	}

	@Override
	protected TileEntity getNewBlockEntity() {
		return new TileEntityWire();
	}

	@Override
	public ConduitCapability getConduitCapability() {
		return ConduitCapability.CATALYST_ENERGY;
	}

	@Override
	public NetworkType getType() {
		return NetworkType.CATALYST_ENERGY;
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, EntityPlayer player, Side side, double xHit, double yHit) {
		TileEntity tile = world.getBlockTileEntity(x,y,z);
		if(tile instanceof TileEntityWire) {
			TileEntityWire wire = (TileEntityWire) tile;
			player.sendMessage(String.format("N: %s",wire.energyNet));
		}
		return super.onBlockRightClicked(world, x, y, z, player, side, xHit, yHit);
	}
}
