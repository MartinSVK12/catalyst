package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.ITileEntityInit;

@Mixin(value = WorldClient.class, remap = false)
public abstract class WorldClientMixin extends World {
	private WorldClientMixin(@NotNull World parent, @NotNull Dimension dimension) {
		super(parent, dimension);
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
