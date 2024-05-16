package sunsetsatellite.catalyst.fluids.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.RenderEngine;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ItemModelFluid extends ItemModelStandard {
	public ItemModelFluid(Item item, String namespace) {
		super(item, namespace);
	}

	public void renderItemIntoGui(Tessellator tessellator, FontRenderer fontrenderer, RenderEngine renderengine, ItemStack itemStack, int x, int y, int height, int width, float brightness, float alpha) {
		if (itemStack != null) {
			Minecraft mc = Minecraft.getMinecraft(this);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glEnable(2884);
			IconCoordinate textureIndex = this.getIcon(mc.thePlayer, itemStack);
			GL11.glDisable(2896);
			textureIndex.parentAtlas.bindTexture();
			if (this.useColor) {
				int color = this.getColor(itemStack);
				float r = (float)(color >> 16 & 255) / 255.0F;
				float g = (float)(color >> 8 & 255) / 255.0F;
				float b = (float)(color & 255) / 255.0F;
				GL11.glColor4f(r * brightness, g * brightness, b * brightness, alpha);
			} else {
				GL11.glColor4f(brightness, brightness, brightness, alpha);
			}

			this.renderTexturedQuad(tessellator, x, y, height, width, textureIndex,false,false);
			GL11.glEnable(2896);
			GL11.glEnable(2884);
			GL11.glDisable(3042);
		}
	}

	protected void renderTexturedQuad(Tessellator tessellator, int x, int y, int height, int width, IconCoordinate icon, boolean flipX, boolean flipY) {
		final float z = 0.0F;
		double uMin = icon.getIconUMin();
		double uMax = icon.getIconUMax();
		double vMin = icon.getIconVMin();
		double vMax = icon.getIconVMax();
		if (flipX){
			double _uMin = uMin;
			uMin = uMax;
			uMax = _uMin;
		}
		if (flipY){
			double _vMin = vMin;
			vMin = vMax;
			vMax = _vMin;
		}
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x,      y + height, z, uMin, vMax);
		tessellator.addVertexWithUV(x + width, y + height, z, uMax, vMax);
		tessellator.addVertexWithUV(x + width, y,      z, uMax, vMin);
		tessellator.addVertexWithUV(x,      y,      z, uMin, vMin);
		tessellator.draw();
	}
}
