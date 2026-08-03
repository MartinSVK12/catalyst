package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import net.minecraft.core.world.save.LevelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.BlockChangeInfo;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.IAbsoluteWorldTime;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.ITileEntityInit;
import sunsetsatellite.catalyst.core.util.network.NetworkManager;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

@Mixin(value = World.class, remap = false)
public abstract class WorldMixin implements IAbsoluteWorldTime {

	@Final
	@Shadow
	private LevelData levelData;

	@Shadow
	@Final
	public Dimension dimension;

	@Shadow
	public abstract int getBlockData(@NotNull TilePosc tilePos);

	@Shadow
	public abstract @NotNull Block<?> getBlockType(@NotNull TilePosc tilePos);

	@Shadow
	public abstract @Nullable TileEntity getTileEntity(@NotNull TilePosc tilePos);

	@Unique
	private final World thisAs = (World) ((Object) this);

	@Inject(method = {
		"<init>(Lnet/minecraft/core/world/Dimension;Lnet/minecraft/core/world/save/LevelStorage;Lnet/minecraft/core/world/settings/WorldConfiguration;Lnet/minecraft/core/world/save/LevelData;Lnet/minecraft/core/world/save/DimensionData;)V",
	}, at = @At("TAIL"))
	public void init4(CallbackInfo ci) {
		NetworkManager.updateAllNets();
		Catalyst.DIMENSION_LOAD_SIGNAL.emit(thisAs);
	}

	@Inject(method = "setBlockType", at = @At("RETURN"))
	public void setBlock(@NotNull TilePosc tilePos, @NotNull Block<?> block, CallbackInfoReturnable<Boolean> cir) {
		Catalyst.ANY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs, new Vec3i(tilePos), block, getBlockData(tilePos)));
		if (getTileEntity(tilePos) != null || block.id() == 0) {
			Catalyst.TILE_ENTITY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs, new Vec3i(tilePos), block, getBlockData(tilePos)));
		}
	}

	@Inject(method = "setBlockData", at = @At("RETURN"))
	public void setBlockMetadata(TilePosc tilePos, int data, CallbackInfoReturnable<Boolean> cir) {
		Catalyst.ANY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs, new Vec3i(tilePos), getBlockType(tilePos), data));
		if (getTileEntity(tilePos) != null) {
			Catalyst.TILE_ENTITY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs, new Vec3i(tilePos), getBlockType(tilePos), data));
		}
	}

	@Inject(method = "setBlockTypeData", at = @At("RETURN"))
	public void setBlockAndMetadata(@NotNull TilePosc tilePos, @NotNull Block<?> block, int data, CallbackInfoReturnable<Boolean> cir) {
		Catalyst.ANY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs, new Vec3i(tilePos), block, data));
		if (getTileEntity(tilePos) != null || block.id() == 0) {
			Catalyst.TILE_ENTITY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs, new Vec3i(tilePos), block, getBlockData(tilePos)));
		}
	}

	@Inject(method = "setBlockTypeDataRaw", at = @At("RETURN"))
	public void setBlockTypeDataRaw(@NotNull TilePosc tilePos, @NotNull Block<?> block, int data, CallbackInfoReturnable<Boolean> cir) {
		Catalyst.ANY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs, new Vec3i(tilePos), block, data));
		if (getTileEntity(tilePos) != null || block.id() == 0) {
			Catalyst.TILE_ENTITY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs, new Vec3i(tilePos), block, getBlockData(tilePos)));
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/util/debug/Debug;change(Ljava/lang/String;)V", ordinal = 5, shift = At.Shift.AFTER))
	public void tick(CallbackInfo ci) {
		((IAbsoluteWorldTime) this.levelData).setAbsoluteWorldTime(((IAbsoluteWorldTime) this.levelData).getAbsoluteWorldTime() + 1L);
	}

	@Override
	public long getAbsoluteWorldTime() {
		return ((IAbsoluteWorldTime) levelData).getAbsoluteWorldTime();
	}

	@Override
	public void setAbsoluteWorldTime(long value) {
		((IAbsoluteWorldTime) levelData).setAbsoluteWorldTime(value);
	}

	@Inject(method = "saveWorldData", at = @At(value = "HEAD"))
	public void worldSaveSignal(CallbackInfo ci) {
		Catalyst.DIMENSION_SAVE_SIGNAL.emit(thisAs);
	}


	@Inject(method = "setTileEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/chunk/Chunk;setTileEntity(Lnet/minecraft/core/world/pos/ChunkTilePosc;Lnet/minecraft/core/block/entity/TileEntity;)Z", shift = At.Shift.AFTER))
	public void initTE(TilePosc tilePos, TileEntity tileEntity, CallbackInfo ci) {
		if (tileEntity != null && getBlockType(tilePos).id() == 0) {
			tileEntity.invalidate();
		} else if (tileEntity != null) {
			if (!((ITileEntityInit) tileEntity).isInitialized()) {
				((ITileEntityInit) tileEntity).setInitialized();
				((ITileEntityInit) tileEntity).init(getBlockType(tilePos));
			}
		}
	}

	@Inject(method = "updateTileEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/block/entity/TileEntity;tick()V", shift = At.Shift.BEFORE))
	public void initTE2(CallbackInfo ci, @Local(name = "tileEntity") TileEntity tileEntity) {
		if (tileEntity != null && getBlockType(tileEntity.tilePos).id() == 0) {
			tileEntity.invalidate();
		} else if (tileEntity != null) {
			if (!((ITileEntityInit) tileEntity).isInitialized()) {
				((ITileEntityInit) tileEntity).setInitialized();
				((ITileEntityInit) tileEntity).init(getBlockType(tileEntity.tilePos));
			}
		}
	}
}
