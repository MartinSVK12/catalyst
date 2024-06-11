package sunsetsatellite.catalyst.fluids.mixin;

import net.minecraft.core.HitResult;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemBucketEmpty;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import sunsetsatellite.catalyst.CatalystFluids;

import java.util.List;


@Mixin(value = ItemBucketEmpty.class, remap = false)

public abstract class ItemBucketEmptyMixin {
	@Shadow
	public static boolean useBucket(EntityPlayer player, ItemStack itemToGive) {
		return false;
	}

	@Inject(
		method = "onItemRightClick", at = @At(value = "JUMP",ordinal = 3,shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void emptyBucket(ItemStack itemstack, World world, EntityPlayer entityplayer, CallbackInfoReturnable<ItemStack> cir){
		HitResult movingobjectposition = getHitResult(world,entityplayer);
		if (movingobjectposition.hitType == HitResult.HitType.TILE) {
			int i = movingobjectposition.x;
			int j = movingobjectposition.y;
			int k = movingobjectposition.z;

			if (world.getBlockId(i, j, k) != 0){
				Block block = Block.blocksList[world.getBlockId(i, j, k)-1];
				if (block instanceof BlockFluid && world.getBlockMetadata(i, j, k) == 0) {
					List<Item> containers = CatalystFluids.FLUIDS.findFilledContainersWithContainer((BlockFluid) block,entityplayer.inventory.getCurrentItem().getItem());
					if(!containers.isEmpty()){
						ItemStack stack = new ItemStack(containers.get(0));
						if (useBucket(entityplayer, stack)) {
							world.setBlockWithNotify(i, j, k, 0);
							entityplayer.swingItem();
						}
					}
				}
			}
		}
	}

	@Unique
	private HitResult getHitResult(World world, EntityPlayer entityplayer){
		double d3;
		float f5;
		float f = 1.0f;
		float f1 = entityplayer.xRotO + (entityplayer.xRot - entityplayer.xRotO) * f;
		float f2 = entityplayer.yRotO + (entityplayer.yRot - entityplayer.yRotO) * f;
		double d = entityplayer.xo + (entityplayer.x - entityplayer.xo) * (double) f;
		double d1 = entityplayer.yo + (entityplayer.y - entityplayer.yo) * (double) f + 1.62 - (double) entityplayer.heightOffset;
		double d2 = entityplayer.zo + (entityplayer.z - entityplayer.zo) * (double) f;
		Vec3d vec3d = Vec3d.createVector(d, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.01745329f - 3.141593f);
		float f4 = MathHelper.sin(-f2 * 0.01745329f - 3.141593f);
		float f7 = f4 * (f5 = -MathHelper.cos(-f1 * 0.01745329f));
		Vec3d vec3d1 = vec3d.addVector((double) f7 * (d3 = 5.0), (double) ((MathHelper.sin(-f1 * 0.01745329f))) * d3, (double) (f3 * f5) * d3);
		return world.checkBlockCollisionBetweenPoints(vec3d, vec3d1, true);
	}

}
