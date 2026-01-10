package sunsetsatellite.catalyst.multipart.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import sunsetsatellite.catalyst.core.util.vector.Vec2f;
import sunsetsatellite.catalyst.core.util.vector.Vec3f;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {

	@Shadow
	public HitResult objectMouseOver;

	@WrapOperation(method = "clickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/controller/PlayerController;useOrPlaceItemStackOnTile(Lnet/minecraft/core/entity/player/Player;Lnet/minecraft/core/world/World;Lnet/minecraft/core/item/ItemStack;IIILnet/minecraft/core/util/helper/Side;DD)Z"))
	private boolean fixUsagePosition(PlayerController instance, Player player, World world, ItemStack itemstack, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced, Operation<Boolean> original) {
		HitResult hit = objectMouseOver;
		if (hit.hitType == HitResult.HitType.TILE) {
			Vec3f vec3f = new Vec3f(hit.location.x, hit.location.y, hit.location.z);
			Vec2f clickPosition = vec3f.subtract(vec3f.copy().floor()).abs().set(hit.side.getAxis(), 0).toVec2f();
			switch (hit.side) {
				case NORTH:
					clickPosition.x = 1 - clickPosition.x;
					break;
				case EAST: {
					double temp1 = clickPosition.y;
					double temp2 = clickPosition.x;
					clickPosition.x = 1 - temp1;
					clickPosition.y = temp2;
					break;
				}
				case SOUTH:
					//no change needed
					break;
				case WEST: {
					double temp1 = clickPosition.y;
					double temp2 = clickPosition.x;
					clickPosition.x = temp1;
					clickPosition.y = temp2;
					break;
				}
			}
			return original.call(instance, player, world, itemstack, blockX, blockY, blockZ, side, clickPosition.x, clickPosition.y);
		}
		return original.call(instance, player, world, itemstack, blockX, blockY, blockZ, side, xPlaced, yPlaced);
	}

}
