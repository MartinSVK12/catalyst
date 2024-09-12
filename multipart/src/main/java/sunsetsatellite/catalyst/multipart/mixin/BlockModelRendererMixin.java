package sunsetsatellite.catalyst.multipart.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.useless.dragonfly.model.block.BlockModelRenderer;
import org.useless.dragonfly.model.block.processed.BlockCube;
import org.useless.dragonfly.model.block.processed.ModernBlockModel;
import sunsetsatellite.catalyst.multipart.block.model.ModernMultipartBlockModel;

@Mixin(value = BlockModelRenderer.class,remap = false)
public class BlockModelRendererMixin {

	@Inject(method = "renderStandardModelWithColorMultiplier",at = @At(value = "INVOKE", target = "Lorg/useless/dragonfly/model/block/processed/BlockFace;getFullBright()Z", ordinal = 0, shift = At.Shift.BEFORE))
	private static void fixMultipartLighting1(Tessellator tessellator, ModernBlockModel model, Block block, int x, int y, int z, float r, float g, float b, CallbackInfoReturnable<Boolean> cir,
											  @Local(name = "blockBrightness") float blockBrightnessRef,
											  @Local(name = "sideBrightness") LocalFloatRef sideBrightnessRef) {
		if(model instanceof ModernMultipartBlockModel) sideBrightnessRef.set(blockBrightnessRef);
	}

	@Shadow
	private static int rotationX;

	@Inject(method = "renderStandardModelWithColorMultiplier",at = @At(value = "INVOKE", target = "Lorg/useless/dragonfly/model/block/processed/BlockFace;useTint()Z", shift = At.Shift.BEFORE))
	private static void fixMultipartLighting2(Tessellator tessellator, ModernBlockModel model, Block block, int x, int y, int z, float r, float g, float b, CallbackInfoReturnable<Boolean> cir,
											  @Local(name = "cube") BlockCube cube,
											  @Local(name = "side") Side side,
											  @Local(name = "red") LocalFloatRef redRef,
											  @Local(name = "green") LocalFloatRef greenRef,
											  @Local(name = "blue") LocalFloatRef blueRef,
											  @Local(name = "rBottom") float rBottom,
											  @Local(name = "gBottom") float gBottom,
											  @Local(name = "bBottom") float bBottom,
											  @Local(name = "rTop") float rTop,
											  @Local(name = "gTop") float gTop,
											  @Local(name = "bTop") float bTop
	) {
		if(model instanceof ModernMultipartBlockModel) {
			if (cube.shade()) {
				switch (side) {
					//top and bottom fields on vertical rotation being swapped is NOT an oversight, it fixes a lighting bug
					case TOP:
						if (rotationX == 90 || rotationX == -90) {
							redRef.set(rBottom);
							greenRef.set(gBottom);
							blueRef.set(bBottom);
						} else {
							redRef.set(rTop);
							greenRef.set(gTop);
							blueRef.set(bTop);
						}
						break;
					case BOTTOM:
						if (rotationX == 90 || rotationX == -90) {
							redRef.set(rTop);
							greenRef.set(gTop);
							blueRef.set(bTop);
						} else {
							redRef.set(rBottom);
							greenRef.set(gBottom);
							blueRef.set(bTop);
						}
						break;
				}
			}
		}
	}

}
