package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.tessellator.Tessellator;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sunsetsatellite.catalyst.core.util.IColorOverride;
import sunsetsatellite.catalyst.core.util.Vec4f;

@Mixin(value = FontRenderer.class,remap = false)
public class FontRendererMixin implements IColorOverride {

	@Unique
	private Vec4f colorOverride = new Vec4f(1);
	@Unique
	private boolean fullbright = false;

	@Inject(method = "renderStringAtPos",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/tessellator/Tessellator;startDrawingQuads()V",shift = At.Shift.AFTER))
	public void disableLightmap(String text, boolean flag, CallbackInfo ci){
		GL11.glColor4d(colorOverride.x,colorOverride.y,colorOverride.z,colorOverride.w);
		if(LightmapHelper.isLightmapEnabled() && fullbright) Tessellator.instance.setLightmapCoord(LightmapHelper.getLightmapCoord(15,15));
	}

	@Inject(method = "renderDefaultChar",at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBegin(I)V",shift = At.Shift.BEFORE))
	public void disableLightmap2(char c, boolean italic, CallbackInfoReturnable<Float> cir){
		if(LightmapHelper.isLightmapEnabled() && fullbright) LightmapHelper.setLightmapCoord(15,15);
	}

	@Inject(method = "renderUnicodeChar",at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBegin(I)V",shift = At.Shift.BEFORE))
	public void disableLightmap3(char c, boolean italic, CallbackInfoReturnable<Float> cir){
		if(LightmapHelper.isLightmapEnabled() && fullbright) LightmapHelper.setLightmapCoord(15,15);
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
