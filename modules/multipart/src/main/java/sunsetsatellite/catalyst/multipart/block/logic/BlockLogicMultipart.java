package sunsetsatellite.catalyst.multipart.block.logic;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.core.util.section.ISideInteractable;
import sunsetsatellite.catalyst.multipart.api.ISupportsMultiparts;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.tile.TileEntityMultipart;

import java.util.ArrayList;
import java.util.List;

public class BlockLogicMultipart extends BlockLogic implements ISideInteractable {
	public BlockLogicMultipart(@NotNull Block<?> block) {
		super(block, Materials.STONE);
		block.withEntity(TileEntityMultipart::new);
	}

	@Override
	public boolean needsItemToShowOutline() {
		return false;
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlockOnCondition(@NotNull WorldSource source, @NotNull TilePosc tilePos) {
		return false;
	}

	@Override
	public @NotNull ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, @NotNull TilePosc tilePos, int data, @Nullable TileEntity tileEntity) {
		if (tileEntity instanceof ISupportsMultiparts) {
			List<ItemStack> list = new ArrayList<>();
			for (Multipart multipart : ((ISupportsMultiparts) tileEntity).getParts().values()) {
				if (multipart == null) continue;
				ItemStack stack = new ItemStack(CatalystMultipart.multipartItem, 1, 0);
				CompoundTag tag = new CompoundTag();
				CompoundTag multipartTag = new CompoundTag();
				multipartTag.putString("Type", multipart.type.name);
				multipartTag.putInt("Block", multipart.block.id());
				multipartTag.putInt("Meta", multipart.meta);
				if (multipart.side != null) {
					multipartTag.putInt("Side", multipart.side.id);
				}
				tag.putCompound("Multipart", multipartTag);
				stack.setData(tag);
				list.add(stack);
			}
			return list.toArray(new ItemStack[0]);
		}
		return null;
	}
}
