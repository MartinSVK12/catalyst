package sunsetsatellite.catalyst.effects.api.effect.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tessellator.TessellatorShader;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.EffectStack;

public class TintEffectRender<T extends Effect> extends EffectRenderer<T> {

	private final String vignette;
	private final int tint;

	public TintEffectRender(T effect, String vignette, int tint) {
		super(effect);
		this.vignette = vignette;
		this.tint = tint;
	}

	@Override
	public boolean shouldDisplayScreenEffect() {
		return true;
	}

	@Override
	void drawScreenEffect(Minecraft minecraft, Gui gui, EffectStack stack, int width, int height, float partialTick) {
		float alpha = calcAlpha(stack);

		if ((!GameSettings.VIGNETTE.value) || vignette == null || vignette.isEmpty()) {
			drawTint(width, height, tint, alpha);
		} else drawVignette(width, height, vignette, alpha);
	}

	private void drawTint(int width, int height, int tint, float alpha) {
		Tessellator tessellator = GLRenderer.getTessellator();
		float r = (float) (tint >> 16 & 0xFF) / 255.0f;
		float g = (float) (tint >> 8 & 0xFF) / 255.0f;
		float b = (float) (tint & 0xFF) / 255.0f;
		GLRenderer.pushFrame();
		GLRenderer.enableState(State.DEPTH_TEST);
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setShader(Shaders.COLOR);
		GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
		GLRenderer.setColor4f(r,g,b,alpha);
		GLRenderer.modelM4f().translate(0, 0, -1);
		tessellator.startDrawingQuads();
		tessellator.addVertex(0, height, 0.0);
		tessellator.addVertex(width, height, 0.0);
		tessellator.addVertex(width, 0, 0.0);
		tessellator.addVertex(0, 0, 0.0);
		tessellator.draw();
		GLRenderer.disableState(State.BLEND);
		GLRenderer.popFrame();
	}

	private void drawVignette(int width, int height, String vignette, float alpha) {
		TextureManager textureManager = Minecraft.getMinecraft().textureManager;
		TessellatorShader tessellator = GLRenderer.getTessellator();
		GLRenderer.pushFrame();
		GLRenderer.enableState(State.DEPTH_TEST);
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
		textureManager.bindTexture(textureManager.loadTexture(vignette));
		GLRenderer.setColor4f(1,1,1,alpha);
		int z = -1;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(0, 0, z, 0, 0);
		tessellator.addVertexWithUV(0, height, z, 0, 1);
		tessellator.addVertexWithUV(width, height, z, 1, 1);
		tessellator.addVertexWithUV(width, 0, z, 1, 0);
		tessellator.draw();
		GLRenderer.popFrame();
		GLRenderer.disableState(State.BLEND);
	}

	public float calcAlpha(EffectStack effectStack) {
		float currentAmount = (float) effectStack.getDuration() * (effectStack.getAmount() - 1);
		float totalTime = (float) effectStack.getDuration() * effectStack.getEffect().getMaxStack();
		return (currentAmount + effectStack.getTimeLeft()) / totalTime;
	}
}
