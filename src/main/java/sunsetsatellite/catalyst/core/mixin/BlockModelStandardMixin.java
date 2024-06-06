package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.Vec4f;
import sunsetsatellite.catalyst.core.util.IColorOverride;

@Debug(export = true)
@Mixin(value = BlockModelStandard.class,remap = false)
public abstract class BlockModelStandardMixin extends BlockModel<Block> implements IColorOverride {

    @Unique
    private Vec4f colorOverride = new Vec4f(1);
    @Unique
    private boolean fullbright = false;

    private BlockModelStandardMixin(Block block) {
        super(block);
    }

    @Inject(method = "renderBlockOnInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/model/BlockModelStandard;setBlockBoundsForItemRender()V", ordinal = 0))
    public void renderBlockOnInventory(Tessellator tessellator, int metadata, float brightness, float alpha, CallbackInfo ci) {
        GL11.glColor4d(colorOverride.x,colorOverride.y,colorOverride.z,colorOverride.w);
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
    public void enableFullbright() {
        fullbright = true;
    }

    @Override
    public void disableFullbright() {
        fullbright = false;
    }
}
