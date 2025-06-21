package sunsetsatellite.catalyst.multipart.block.logic;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.block.entity.TileEntityCarpenterWorkbench;

import java.util.Random;

public class BlockLogicCarpenterWorkbench extends BlockLogic {
	public BlockLogicCarpenterWorkbench(Block<?> block) {
		super(block, Material.stone);
		block.withEntity(TileEntityCarpenterWorkbench::new);
	}

	@Override
	public boolean onBlockRightClicked(World world, int i, int j, int k, Player entityplayer, Side side, double xHit, double yHit) {
		if (world.isClientSide) {
			return true;
		} else {
			TileEntityCarpenterWorkbench tile = (TileEntityCarpenterWorkbench) world.getTileEntity(i, j, k);
			if (tile != null) {
				Catalyst.displayGui(entityplayer, tile, CatalystMultipart.key("gui/carpenter_workbench"));
			}
			return true;
		}
	}
}
