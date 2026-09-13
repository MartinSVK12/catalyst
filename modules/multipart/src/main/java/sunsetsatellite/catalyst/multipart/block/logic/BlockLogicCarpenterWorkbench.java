package sunsetsatellite.catalyst.multipart.block.logic;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.tile.TileEntityCarpenterWorkbench;

public class BlockLogicCarpenterWorkbench extends BlockLogic {
	public BlockLogicCarpenterWorkbench(Block<?> block) {
		super(block, Materials.STONE);
		block.withEntity(TileEntityCarpenterWorkbench::new);
	}

	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xHit, double yHit) {
		if (world.isClientSide) {
			return true;
		} else {
			TileEntityCarpenterWorkbench tile = (TileEntityCarpenterWorkbench) world.getTileEntity(tilePos);
			if (tile != null) {
				Catalyst.displayGui(player, tile, CatalystMultipart.key("gui/carpenter_workbench"));
			}
			return true;
		}
	}

}
