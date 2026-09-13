package sunsetsatellite.catalyst.multipart.item;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.IPlaceable;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.util.collection.Pair;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.ICustomDescription;
import sunsetsatellite.catalyst.core.util.section.BlockSection;
import sunsetsatellite.catalyst.core.util.section.ISideInteractable;
import sunsetsatellite.catalyst.core.util.vector.Vec2f;
import sunsetsatellite.catalyst.multipart.api.ISupportsMultiparts;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.api.MultipartType;
import sunsetsatellite.catalyst.multipart.block.logic.BlockLogicMultipart;

import java.util.Objects;
import java.util.Random;

public class ItemMultipart extends Item implements ISideInteractable, ICustomDescription, IPlaceable.PlaceableBlock<BlockLogicMultipart> {

	private static final ItemStack previewStack = new ItemStack(1, 1, 0);

	public ItemMultipart(@NotNull String translationKey, @NotNull String namespaceId, int id) {
		super(translationKey, namespaceId, id);
		setMaxStackSize(64);
	}

	@Override
	public boolean alwaysShowOutlineWhenHeld() {
		return true;
	}

	@Override
	public boolean onUseOnBlock(@NotNull ItemStack stack, @NotNull World world, @Nullable Player player, @NotNull TilePosc blockPos, @NotNull Side side, double xPlaced, double yPlaced) {
		Multipart multipart = getMultipart(stack);
		if (multipart == null) return false;
		Pair<Direction, BlockSection> pair = Catalyst.getBlockSurfaceClickPosition(world, player, side, new Vec2f(xPlaced, yPlaced));
		Side playerFacing = Catalyst.calculatePlayerFacing(player.yRot);
		if (pairIsInvalid(pair)) return false;
		Direction dir = pair.getRight().toDirection(pair.getLeft(), playerFacing);

		TileEntity tile = world.getTileEntity(blockPos);
		if (stack.stackSize <= 0) {
			return false;
		}
		if (tile instanceof ISupportsMultiparts) {
			return addMultipart(tile, dir, stack, player, world, blockPos, multipart, side, xPlaced, yPlaced);
		} else {
			TilePos pos = new TilePos(blockPos);
			if (this.shouldShiftOutOf(stack, world, player, blockPos, side, xPlaced, yPlaced)) pos = blockPos.add(side.direction(), new TilePos());
			boolean b = this.placeOnBlock(stack, world, player, blockPos, side, xPlaced, yPlaced);
			tile = world.getTileEntity(pos);
			if (tile instanceof ISupportsMultiparts partTile) {
				if (partTile.getParts().get(dir) != null) {
					return false;
				}
				partTile.getParts().put(dir, multipart);
			}
			return b;
		}
	}

	private boolean pairIsInvalid(Pair<Direction, BlockSection> pair) {
		return pair == null || pair.getLeft() == null || pair.getRight() == null;
	}

	private boolean addMultipart(TileEntity tile, Direction dir, ItemStack stack, Player player, World world, TilePosc tilePos, Multipart multipart, Side side, double xPlaced, double yPlaced) {
		if (((ISupportsMultiparts) tile).getParts().get(dir) != null) {
			return false;//placeMultipart(tile, dir, stack, player, world, blockX, blockY, blockZ, multipart, side, xPlaced, yPlaced);
		}
		if (stack.consumeItem(player)) {
			((ISupportsMultiparts) tile).getParts().put(dir, multipart);
			world.markBlockDirty(tilePos);
			Block<?> block = Blocks.blocksList[CatalystMultipart.multipartBlock.id()];
			world.playBlockSoundEffect(player, (float) tilePos.x() + 0.5F, (float) tilePos.y() + 0.5F, (float) tilePos.z() + 0.5F, block, EnumBlockSoundEffectType.PLACE);
			return true;
		}
		return false;
	}

	@Override
	public @NonNull CompoundTag getDefaultTag() {
		CompoundTag tag = new CompoundTag();
		CompoundTag multipartTag = new CompoundTag();
		multipartTag.putString("Type", "cover");
		multipartTag.putInt("Block", Blocks.BEDROCK.id());
		multipartTag.putInt("Meta", 0);
		tag.putCompound("Multipart", multipartTag);
		return tag;
	}

	@Override
	public @NonNull String getTranslatedName(ItemStack itemstack) {
		String type = itemstack.getData().getCompound("Multipart").getStringOrDefault("Type", "");
		Block<?> block = Blocks.getBlock(itemstack.getData().getCompound("Multipart").getInteger("Block"));
		int meta = itemstack.getData().getCompound("Multipart").getInteger("Meta");
		if (!Objects.equals(type, "") && MultipartType.types.containsKey(type)) {
			ItemBlock<?> item = (ItemBlock<?>) block.asItem();
			previewStack.itemID = block.id();
			previewStack.setMetadata(meta);
			return I18n.getInstance().translateKey(item.getLanguageKey(previewStack) + ".name") + " " + I18n.getInstance().translateKey("multipart." + type + ".name");
		}
		return super.getTranslatedName(itemstack);
	}

	public Multipart getMultipart(ItemStack itemstack) {
		String type = itemstack.getData().getCompound("Multipart").getStringOrDefault("Type", "");
		Block<?> block = Blocks.getBlock(itemstack.getData().getCompound("Multipart").getInteger("Block"));
		int meta = itemstack.getData().getCompound("Multipart").getInteger("Meta");
		boolean specifiedSideOnly = itemstack.getData().getCompound("Multipart").containsKey("Side");
		if (specifiedSideOnly) {
			Side side = Side.fromId(itemstack.getData().getCompound("Multipart").getInteger("Side"));
			if (!Objects.equals(type, "") && MultipartType.types.containsKey(type)) {
				return new Multipart(MultipartType.types.get(type), block, side, meta);
			}
		}
		if (!Objects.equals(type, "") && MultipartType.types.containsKey(type)) {
			return new Multipart(MultipartType.types.get(type), block, meta);
		}
		return null;
	}

	@Override
	public String getDescription(ItemStack stack) {
		Multipart multipart = getMultipart(stack);
		if (multipart != null) {
			return TextFormatting.GRAY + "16x16x" + multipart.type.thickness + TextFormatting.RESET;
		}
		return "";
	}

	@Override
	public @NotNull Block<BlockLogicMultipart> getBlock() {
		return CatalystMultipart.multipartBlock;
	}

	@Override
	public void onUseByActivator(
		@NotNull ItemStack selfStack, @NotNull World world,
		@NotNull TileEntityActivator activator, @NotNull Random random, @NotNull TilePosc blockPos,
		@NotNull net.minecraft.core.util.helper.Direction direction, double offX, double offY, double offZ
	) {
		this.placeByActivator(selfStack, world, activator, random, blockPos, direction, offX, offY, offZ);
	}
}
