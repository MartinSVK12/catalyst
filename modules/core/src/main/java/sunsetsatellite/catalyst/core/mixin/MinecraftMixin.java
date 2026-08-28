package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import net.minecraft.core.world.save.ISaveFormat;
import net.minecraft.core.world.save.LevelStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.vector.Vec2f;
import sunsetsatellite.catalyst.core.util.vector.Vec3f;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {

	@Shadow
	private ISaveFormat saveFormat;

	@Shadow
	@Nullable
	public HitResult objectMouseOver;

	@Inject(method = "startWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/save/ISaveFormat;getLevelStorage(Ljava/lang/String;Z)Lnet/minecraft/core/world/save/LevelStorage;"))
	public void startWorld(String worldDirName, CallbackInfo ci) {
		LevelStorage saveHandler = this.saveFormat.getLevelStorage(worldDirName, false);
		Catalyst.WORLD_LOAD_SIGNAL.emit(saveHandler);
	}

	@WrapOperation(method = "clickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/controller/PlayerController;useOrPlaceItemStackOnTile(Lnet/minecraft/core/entity/player/Player;Lnet/minecraft/core/world/World;Lnet/minecraft/core/item/ItemStack;Lnet/minecraft/core/world/pos/TilePosc;Lnet/minecraft/core/util/helper/Side;DD)Z"))
	public boolean fixUsagePosition(PlayerController instance, Player player, World world, ItemStack itemStack, TilePosc tilePos, Side side, double xPlaced, double yPlaced, Operation<Boolean> original){
		HitResult hitResult = objectMouseOver;
		if(hitResult instanceof HitResult.Tile hit) {
			Vec3f vec3f = new Vec3f(hit.location);
			Vec2f clickPosition = vec3f.subtract(vec3f.copy().floor()).abs().set(hit.side.axis(), 0).toVec2f();
			if(clickPosition == null){
				return original.call(instance, player, world, itemStack, tilePos, side, xPlaced, yPlaced);
			}
			switch (hit.side) {
				case NORTH -> clickPosition.x = 1 - clickPosition.x;
				case EAST -> {
					double temp1 = clickPosition.y;
					double temp2 = clickPosition.x;
					clickPosition.x = 1 - temp1;
					clickPosition.y = temp2;
				}
				case SOUTH -> {
					//no change needed
				}
				case WEST -> {
					double temp1 = clickPosition.y;
					double temp2 = clickPosition.x;
					clickPosition.x = temp1;
					clickPosition.y = temp2;
				}
			}
			return original.call(instance, player, world, itemStack, tilePos, side, clickPosition.x, clickPosition.y);
		}
		return original.call(instance, player, world, itemStack, tilePos, side, xPlaced, yPlaced);
	}

}
