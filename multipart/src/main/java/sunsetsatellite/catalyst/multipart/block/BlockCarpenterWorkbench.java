package sunsetsatellite.catalyst.multipart.block;

import net.minecraft.core.block.BlockTileEntity;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.multipart.block.entity.TileEntityCarpenterWorkbench;

import java.util.Random;

public class BlockCarpenterWorkbench extends BlockTileEntity {
	public BlockCarpenterWorkbench(String key, int id) {
		super(key, id, Material.stone);
	}

	@Override
	protected TileEntity getNewBlockEntity() {
		return new TileEntityCarpenterWorkbench();
	}

	public boolean onBlockRightClicked(World world, int i, int j, int k, EntityPlayer entityplayer, Side side, double xHit, double yHit) {
		if (world.isClientSide) {
			return true;
		} else {
			TileEntityCarpenterWorkbench tile = (TileEntityCarpenterWorkbench) world.getBlockTileEntity(i, j, k);
			if (tile != null) {
				Catalyst.displayGui(entityplayer, tile, "Carpenter Workbench");
			}
			return true;
		}
	}

	@Override
	public void onBlockRemoved(World world, int i, int j, int k, int data) {
		dropContents(world, i, j, k);

		super.onBlockRemoved(world, i, j, k, data);
	}

	public void dropContents(World world, int i, int j, int k) {
		IInventory tile = (IInventory) world.getBlockTileEntity(i, j, k);
		if (tile != null) {
			Random random = new Random();
			for (int l = 0; l < tile.getSizeInventory(); ++l) {
				ItemStack itemstack = tile.getStackInSlot(l);
				if (itemstack != null) {
					float f = random.nextFloat() * 0.8F + 0.1F;
					float f1 = random.nextFloat() * 0.8F + 0.1F;
					float f2 = random.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0) {
						int i1 = random.nextInt(21) + 10;
						if (i1 > itemstack.stackSize) {
							i1 = itemstack.stackSize;
						}

						itemstack.stackSize -= i1;
						EntityItem entityitem = new EntityItem(world, (float) i + f, (float) j + f1, (float) k + f2, new ItemStack(itemstack.itemID, i1, itemstack.getMetadata(),itemstack.getData()));
						float f3 = 0.05F;
						entityitem.xd = (float) random.nextGaussian() * f3;
						entityitem.yd = (float) random.nextGaussian() * f3 + 0.2F;
						entityitem.zd = (float) random.nextGaussian() * f3;
						world.entityJoinedWorld(entityitem);
					}
				}
			}
		}
	}
}
