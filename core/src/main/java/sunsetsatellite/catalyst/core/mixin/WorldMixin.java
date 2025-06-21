package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.save.LevelData;
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

@Mixin(value = World.class,remap = false)
public abstract class WorldMixin implements IAbsoluteWorldTime {
	@Shadow public abstract int getBlockMetadata(int x, int y, int z);

	@Shadow public abstract int getBlockId(int x, int y, int z);

	@Shadow public abstract TileEntity getTileEntity(int x, int y, int z);

	@Shadow
	protected LevelData levelData;
	@Shadow
	@Final
	public Dimension dimension;
	@Unique
	private final World thisAs = (World)((Object)this);

	@Inject(method = {
		"<init>(Lnet/minecraft/core/world/World;Lnet/minecraft/core/world/Dimension;)V",
		"<init>(Lnet/minecraft/core/world/save/LevelStorage;Ljava/lang/String;Lnet/minecraft/core/world/Dimension;Lnet/minecraft/core/world/type/WorldType;J)V",
		"<init>(Lnet/minecraft/core/world/save/LevelStorage;Ljava/lang/String;JLnet/minecraft/core/world/Dimension;Lnet/minecraft/core/world/type/WorldType;)V"
	}, at = @At("TAIL"))
	public void init4(CallbackInfo ci){
		NetworkManager.updateAllNets();
		Catalyst.DIMENSION_LOAD_SIGNAL.emit(thisAs);
	}

	@Inject(method = "setBlock", at = @At("RETURN"))
	public void setBlock(int x, int y, int z, int id, CallbackInfoReturnable<Boolean> cir){
		Catalyst.ANY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs,new Vec3i(x,y,z),id,getBlockMetadata(x,y,z)));
		if(getTileEntity(x,y,z) != null || id == 0){
			Catalyst.TILE_ENTITY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs,new Vec3i(x,y,z),id,getBlockMetadata(x,y,z)));
		}
	}

	@Inject(method = "setBlockMetadata", at = @At("RETURN"))
	public void setBlockMetadata(int x, int y, int z, int meta, CallbackInfoReturnable<Boolean> cir){
		Catalyst.ANY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs,new Vec3i(x,y,z),getBlockId(x,y,z),meta));
		if(getTileEntity(x,y,z) != null){
			Catalyst.TILE_ENTITY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs,new Vec3i(x,y,z),getBlockId(x,y,z),meta));
		}
	}

	@Inject(method = "setBlockAndMetadata", at = @At("RETURN"))
	public void setBlockAndMetadata(int x, int y, int z, int id, int meta, CallbackInfoReturnable<Boolean> cir){
		Catalyst.ANY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs,new Vec3i(x,y,z),id,meta));
		if(getTileEntity(x,y,z) != null || id == 0){
			Catalyst.TILE_ENTITY_BLOCK_CHANGED_SIGNAL.emit(new BlockChangeInfo(thisAs,new Vec3i(x,y,z),id,getBlockMetadata(x,y,z)));
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/util/debug/Debug;change(Ljava/lang/String;)V", ordinal = 5, shift = At.Shift.AFTER))
	public void tick(CallbackInfo ci){
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
	public void worldSaveSignal(CallbackInfo ci){
		Catalyst.DIMENSION_SAVE_SIGNAL.emit(thisAs);
	}


	@Inject(method = "setTileEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/chunk/Chunk;setTileEntity(IIILnet/minecraft/core/block/entity/TileEntity;)Z", shift = At.Shift.AFTER))
	public void initTE(int x, int y, int z, TileEntity tileEntity, CallbackInfo ci){
		if(tileEntity != null && getBlockId(x, y, z) == 0){
			tileEntity.invalidate();
		} else if(tileEntity != null){
			if(!((ITileEntityInit) tileEntity).isInitialized()){
				((ITileEntityInit) tileEntity).setInitialized();
				((ITileEntityInit) tileEntity).init(Blocks.getBlock(getBlockId(x,y,z)));
			}
		}
	}

	@Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/block/entity/TileEntity;tick()V", shift = At.Shift.BEFORE))
	public void initTE2(CallbackInfo ci, @Local TileEntity tileentity1){
		if(tileentity1 != null && getBlockId(tileentity1.x, tileentity1.y, tileentity1.z) == 0){
			tileentity1.invalidate();
		} else if (tileentity1 != null) {
			if(!((ITileEntityInit) tileentity1).isInitialized()){
				((ITileEntityInit) tileentity1).setInitialized();
				((ITileEntityInit) tileentity1).init(Blocks.getBlock(getBlockId(tileentity1.x, tileentity1.y, tileentity1.z)));
			}
		}
	}
}
