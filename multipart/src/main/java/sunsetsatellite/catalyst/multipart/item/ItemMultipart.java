package sunsetsatellite.catalyst.multipart.item;

import com.mojang.nbt.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.util.collection.Pair;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.core.util.BlockSection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.ICustomDescription;
import sunsetsatellite.catalyst.core.util.ISideInteractable;
import sunsetsatellite.catalyst.multipart.api.ISupportsMultiparts;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.api.MultipartType;

import java.util.Objects;

public class ItemMultipart extends Item implements ISideInteractable, ICustomDescription {
	public ItemMultipart(String name, int id) {
		super(name, id);
	}

	@Override
	public CompoundTag getDefaultTag() {
		CompoundTag tag = new CompoundTag();
		CompoundTag multipartTag = new CompoundTag();
		multipartTag.putString("Type","cover");
		multipartTag.putInt("Block", Block.bedrock.id);
		multipartTag.putInt("Meta", 0);
		tag.putCompound("Multipart",multipartTag);
		return tag;
	}

	@Override
	public String getTranslatedName(ItemStack itemstack) {
		String type = itemstack.getData().getCompound("Multipart").getStringOrDefault("Type", "");
		Block block = Block.getBlock(itemstack.getData().getCompound("Multipart").getInteger("Block"));
		if(!Objects.equals(type, "") && block != null && MultipartType.types.containsKey(type)) {
			return I18n.getInstance().translateKey(block.getKey() + ".name") + " " + I18n.getInstance().translateKey("multipart."+type+".name");
		}
		return super.getTranslatedName(itemstack);
	}

	public Multipart getMultipart(ItemStack itemstack) {
		String type = itemstack.getData().getCompound("Multipart").getStringOrDefault("Type", "");
		Block block = Block.getBlock(itemstack.getData().getCompound("Multipart").getInteger("Block"));
		int meta = itemstack.getData().getCompound("Multipart").getInteger("Meta");
		boolean specifiedSideOnly = itemstack.getData().getCompound("Multipart").containsKey("Side");
		if (specifiedSideOnly) {
			Side side = Side.getSideById(itemstack.getData().getCompound("Multipart").getInteger("Side"));
			if(!Objects.equals(type, "") && block != null && MultipartType.types.containsKey(type)) {
				return new Multipart(MultipartType.types.get(type),block,side,meta);
			}
		}
		if(!Objects.equals(type, "") && block != null && MultipartType.types.containsKey(type)) {
			return new Multipart(MultipartType.types.get(type),block,meta);
		}
		return null;
	}

	@Override
	public boolean onUseItemOnBlock(ItemStack stack, EntityPlayer player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced) {
		Multipart multipart = getMultipart(stack);
		if(multipart == null) return false;
		Pair<Direction, BlockSection> pair = Catalyst.getBlockSurfaceClickPosition(world, player, Minecraft.getMinecraft(this).objectMouseOver);
		Side playerFacing = Catalyst.calculatePlayerFacing(player.yRot);
		if (pairIsInvalid(pair)) return false;
		Direction dir = pair.getRight().toDirection(pair.getLeft(), playerFacing);

		TileEntity tile = world.getBlockTileEntity(blockX, blockY, blockZ);
		if (stack.stackSize <= 0) {
			return false;
		}
		if(tile instanceof ISupportsMultiparts) {
			return addMultipart(tile, dir, stack, player, world, blockX, blockY, blockZ, multipart, side, yPlaced);
		} else {
			return placeMultipart(tile, dir, stack, player, world, blockX, blockY, blockZ, multipart, side, yPlaced);
		}
	}

	private boolean addMultipart(TileEntity tile, Direction dir, ItemStack stack, EntityPlayer player, World world, int blockX, int blockY, int blockZ, Multipart multipart, Side side, double yPlaced){
		if(((ISupportsMultiparts) tile).getParts().get(dir) != null) {
			return placeMultipart(tile, dir, stack, player, world, blockX, blockY, blockZ, multipart, side, yPlaced);
		}
		if(stack.consumeItem(player)){
			((ISupportsMultiparts) tile).getParts().put(dir,multipart);
			world.markBlockDirty(blockX,blockY,blockZ);
			Block block = Block.blocksList[CatalystMultipart.multipartBlock.id];
			world.playBlockSoundEffect(player, (float)blockX + 0.5F, (float)blockY + 0.5F, (float)blockZ + 0.5F, block, EnumBlockSoundEffectType.PLACE);
			return true;
		}
		return false;
	}

	private boolean placeMultipart(TileEntity tile, Direction dir, ItemStack stack, EntityPlayer player, World world, int blockX, int blockY, int blockZ, Multipart multipart, Side side, double yPlaced){
		if (!world.canPlaceInsideBlock(blockX, blockY, blockZ)) {
			blockX += side.getOffsetX();
			blockY += side.getOffsetY();
			blockZ += side.getOffsetZ();
		}

		Block currentBlock = world.getBlock(blockX, blockY, blockZ);
		boolean placedInside = currentBlock != null && !(currentBlock instanceof BlockFluid);
		if (blockY >= 0 && blockY < world.getHeightBlocks()) {
			if (world.canBlockBePlacedAt(CatalystMultipart.multipartBlock.id, blockX, blockY, blockZ, false, side) && stack.consumeItem(player)) {
				Block block = Block.blocksList[CatalystMultipart.multipartBlock.id];
				if (placedInside && !world.isClientSide) {
					world.playSoundEffect(2001, blockX, blockY, blockZ, world.getBlockId(blockX, blockY, blockZ));
				}

				if (world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, CatalystMultipart.multipartBlock.id, this.getPlacedBlockMetadata(stack.getMetadata()))) {
					block.onBlockPlaced(world, blockX, blockY, blockZ, side, player, yPlaced);
					world.playBlockSoundEffect(player, (float)blockX + 0.5F, (float)blockY + 0.5F, (float)blockZ + 0.5F, block, EnumBlockSoundEffectType.PLACE);
				}

				tile = world.getBlockTileEntity(blockX, blockY, blockZ);
				if (tile instanceof ISupportsMultiparts) {
					if(((ISupportsMultiparts) tile).getParts().get(dir) != null) {
						return false;
					}
					((ISupportsMultiparts) tile).getParts().put(dir,multipart);
				}

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean pairIsInvalid(Pair<Direction, BlockSection> pair) {
		return pair == null || pair.getLeft() == null || pair.getRight() == null;
	}

	@Override
	public boolean alwaysShowOutlineWhenHeld() {
		return true;
	}

	@Override
	public String getDescription(ItemStack stack) {
		Multipart multipart = getMultipart(stack);
		if(multipart != null){
			return TextFormatting.GRAY+"16x16x"+multipart.type.thickness+TextFormatting.RESET;
		}
		return "";
	}
}
