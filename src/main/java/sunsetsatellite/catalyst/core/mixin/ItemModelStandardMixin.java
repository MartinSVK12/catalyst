package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.item.Item;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.Vec4f;
import sunsetsatellite.catalyst.core.util.IColorOverride;

@Mixin(value = ItemModelStandard.class,remap = false)
public abstract class ItemModelStandardMixin extends ItemModel implements IColorOverride {
    private ItemModelStandardMixin(Item item) {
        super(item);
    }

    @Unique
    private Vec4f colorOverride = new Vec4f(1);
    @Unique
    private boolean fullbright = false;

    @Inject(method = "renderTexturedQuad(Lnet/minecraft/client/render/tessellator/Tessellator;IILnet/minecraft/client/render/stitcher/IconCoordinate;ZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/tessellator/Tessellator;startDrawingQuads()V", shift = At.Shift.AFTER))
    public void disableLightmap(Tessellator tessellator, int x, int y, IconCoordinate icon, boolean flipX, boolean flipY, CallbackInfo ci) {
        GL11.glColor4d(colorOverride.x,colorOverride.y,colorOverride.z,colorOverride.w);
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
