package sunsetsatellite.catalyst.energy.simple.test.block;

import net.minecraft.core.block.BlockTileEntity;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.core.util.network.NetworkComponent;
import sunsetsatellite.catalyst.energy.simple.impl.TileEntityEnergyBase;

public abstract class BlockEnergy extends BlockTileEntity implements NetworkComponent {
	public BlockEnergy(String key, int id) {
		super(key, id, Material.metal);
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, EntityPlayer player, Side side, double xHit, double yHit) {
		TileEntity tile = world.getBlockTileEntity(x,y,z);
		if(tile instanceof TileEntityEnergyBase) {
			TileEntityEnergyBase box = (TileEntityEnergyBase) tile;
			player.sendMessage(String.format("Energy: %d/%d",box.getEnergy(),box.getCapacity()));
			player.sendMessage(String.format("N: %s",box.energyNet));
		}
		return super.onBlockRightClicked(world, x, y, z, player, side, xHit, yHit);
	}
}
