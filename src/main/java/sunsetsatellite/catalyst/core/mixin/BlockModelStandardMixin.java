package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.block.color.BlockColor;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.IFullbright;
import sunsetsatellite.catalyst.core.util.Vec4f;
import sunsetsatellite.catalyst.core.util.IColorOverride;

import java.nio.FloatBuffer;


@Mixin(value = BlockModelStandard.class,remap = false)
public abstract class BlockModelStandardMixin extends BlockModel<Block> implements IColorOverride, IFullbright {

	private boolean overrideColor = false;
    @Unique
    private Vec4f colorOverride = new Vec4f(1);
    @Unique
    private boolean fullbright = false;

    private BlockModelStandardMixin(Block block) {
        super(block);
    }

    @Inject(method = "renderBlockOnInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/model/BlockModelStandard;setBlockBoundsForItemRender()V", ordinal = 0))
    public void renderBlockOnInventory(Tessellator tessellator, int metadata, float brightness, float alpha, CallbackInfo ci) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(GL11.GL_CURRENT_COLOR, buffer);
		if(overrideColor) GL11.glColor4d(colorOverride.x * buffer.get(0),colorOverride.y * buffer.get(1),colorOverride.z * buffer.get(2),colorOverride.w * buffer.get(3));
    }

    @Inject(method = "renderBlockOnInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/tessellator/Tessellator;startDrawingQuads()V", shift = At.Shift.AFTER))
    public void disableLightmap(Tessellator tessellator, int metadata, float brightness, float alpha, CallbackInfo ci) {
        if(LightmapHelper.isLightmapEnabled() && fullbright) tessellator.setLightmapCoord(LightmapHelper.getLightmapCoord(15,15));
    }


    @Override
    public void overrideColor(float r, float g, float b, float alpha) {
        colorOverride = new Vec4f(r,g,b,alpha);
    }

	@Override
	public void enableColorOverride() {
		overrideColor = true;
	}

	@Override
	public void disableColorOverride() {
		overrideColor = false;
	}

	@Override
    public void enableFullbright() {
        fullbright = true;
    }

    @Override
    public void disableFullbright() {
        fullbright = false;
    }
}
