package sunsetsatellite.catalyst.multipart.block;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.block.BlockTileEntity;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.core.util.ISideInteractable;
import sunsetsatellite.catalyst.multipart.api.ISupportsMultiparts;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.block.entity.TileEntityMultipart;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockMultipart extends BlockTileEntity implements ISideInteractable {
	public BlockMultipart(String key, int id) {
		super(key, id, Material.stone);
		isLitInteriorSurface = true;
	}

	@Override
	protected TileEntity getNewBlockEntity() {
		return new TileEntityMultipart();
	}

	@Override
	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		super.onBlockPlaced(world, x, y, z, side, entity, sideHeight);
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
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
		if(tileEntity instanceof ISupportsMultiparts){
			List<ItemStack> list = new ArrayList<>();
			for (Multipart multipart : ((ISupportsMultiparts) tileEntity).getParts().values()) {
				if(multipart == null) continue;
				ItemStack stack = new ItemStack(CatalystMultipart.multipartItem,1, 0);
				CompoundTag tag = new CompoundTag();
				CompoundTag multipartTag = new CompoundTag();
				multipartTag.putString("Type",multipart.type.name);
				multipartTag.putInt("Block", multipart.block.id);
				multipartTag.putInt("Meta", multipart.meta);
				if(multipart.side != null){
					multipartTag.putInt("Side", multipart.side.getId());
				}
				tag.putCompound("Multipart",multipartTag);
				stack.setData(tag);
				list.add(stack);
			}
			return list.toArray(new ItemStack[0]);
		}
		return null;
	}

	@Override
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList<AABB> aabbList) {
		TileEntity tile = world.getBlockTileEntity(x,y,z);
		if(tile instanceof ISupportsMultiparts) {
			if (((ISupportsMultiparts) tile).getParts().values().stream().allMatch(Objects::isNull)) {
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
			} else {
				((ISupportsMultiparts) tile).getParts().forEach((dir,multipart)->{
					if(multipart == null) return;
					double d = Catalyst.map(multipart.type.thickness,1,16,0,1);
					switch (dir){
						case X_POS:{
							AABB bb = new AABB(x+(1-d),y,z,x+1,y+1,z+1);
							aabbList.add(bb);
							break;
						}
						case X_NEG:{
							AABB bb = new AABB(x,y,z,x+d,y+1,z+1);
							aabbList.add(bb);
							break;
						}
						case Y_POS:{
							AABB bb = new AABB(x,y+(1-d),z,x+1,y+1,z+1);
							aabbList.add(bb);
							break;
						}
						case Y_NEG:{
							AABB bb = new AABB(x,y,z,x+1,y+d,z+1);
							aabbList.add(bb);
							break;
						}
						case Z_POS: {
							AABB bb = new AABB(x,y,z+(1-d),x+1,y+1,z+1);
							aabbList.add(bb);
							break;
						}
						case Z_NEG: {
							AABB bb = new AABB(x,y,z,x+1,y+1,z+d);
							aabbList.add(bb);
							break;
						}

					}

				});
				return;
			}
		}
		super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
	}
}
