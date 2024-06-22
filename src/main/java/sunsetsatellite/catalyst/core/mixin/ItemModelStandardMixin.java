package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.IFullbright;

@Mixin(value = ItemModelStandard.class,remap = false)
public abstract class ItemModelStandardMixin extends ItemModel implements IFullbright {
    private ItemModelStandardMixin(Item item) {
        super(item);
    }

	@Unique
    private boolean fullbright = false;

    @Inject(method = "renderTexturedQuad(Lnet/minecraft/client/render/tessellator/Tessellator;IILnet/minecraft/client/render/stitcher/IconCoordinate;ZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/tessellator/Tessellator;startDrawingQuads()V", shift = At.Shift.AFTER))
    public void disableLightmap(Tessellator tessellator, int x, int y, IconCoordinate icon, boolean flipX, boolean flipY, CallbackInfo ci) {
        if(LightmapHelper.isLightmapEnabled() && fullbright) tessellator.setLightmapCoord(LightmapHelper.getLightmapCoord(15,15));
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
