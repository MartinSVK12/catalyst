package sunsetsatellite.catalyst.effects.api.effect.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.EffectStack;

@Environment(EnvType.CLIENT)
public class EffectRenderer<T extends Effect> {

	private final T effect;
	private int color;

	public EffectRenderer(T effect) {
		this.effect = effect;
	}

	void drawScreenEffect(Minecraft minecraft, Gui gui, EffectStack stack, int screenWidth, int screenHeight, int mouseX, int mouseY, float partialTick) {
		this.drawScreenEffect(minecraft, gui, stack, screenWidth, screenHeight, partialTick);
	}

	void drawScreenEffect(Minecraft minecraft, Gui gui, EffectStack stack, int screenWidth, int screenHeight, float partialTick) {
	}

	public int getColor() {
		return this.color;
	}

	public EffectRenderer<?> setColor(int color) {
		this.color = color;
		return this;
	}

	public boolean shouldDisplayScreenEffect() {
		return false;
	}

	public boolean shouldDisplayIcon() {
		return true;
	}

	private String iconPath;
	private IconCoordinate iconCoordinate;

	public EffectRenderer<T> setIcon(IconCoordinate icon) {
		this.iconCoordinate = icon;
		this.iconPath = null;
		return this;
	}

	public EffectRenderer<T> setIcon(String icon) {
		this.iconCoordinate = null;
		this.iconPath = icon;
		return this;
	}

	public void drawIcon(Minecraft mc, Gui gui, int x, int y) {
		if (iconPath != null) {
			mc.textureManager.loadTexture("/assets/" + effect.id.split(":")[0] + "/effects/icons/" + this.iconPath).bind();
			GLRenderer.setColor4d(1d,1d,1d,1d);
			gui.drawTexturedModalRect(x, y, 0, 0, 20, 20, 16, 1 / 16f);
		} else if (this.iconCoordinate != null) {
			GLRenderer.setColor4d(1d,1d,1d,1d);
			gui.drawTexturedIcon(x, y, 20, 20, iconCoordinate);
		}
	}
}
