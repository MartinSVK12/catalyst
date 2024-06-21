package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.phys.AABB;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sunsetsatellite.catalyst.core.util.ISideInteractable;
import sunsetsatellite.catalyst.core.util.IWrench;

@Mixin(value = RenderGlobal.class,remap = false)
public class RenderGlobalMixin {

	@Shadow
	private Minecraft mc;

	@Redirect(method = "drawSelectionBox",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderGlobal;drawOutlinedBoundingBox(Lnet/minecraft/core/util/phys/AABB;)V"))
	public void drawOutlinedSectionedBoundingBox(RenderGlobal instance, AABB aabb, @Local int j)
	{
		if(Block.blocksList[j] instanceof ISideInteractable && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ISideInteractable){
			double minX = aabb.minX;
			double minY = aabb.minY;
			double minZ = aabb.minZ;
			double maxX = aabb.maxX;
			double maxY = aabb.maxY;
			double maxZ = aabb.maxZ;
			Tessellator tessellator = Tessellator.instance;

			tessellator.startDrawing(GL11.GL_LINE_STRIP);
			tessellator.addVertex(minX, minY, minZ);
			tessellator.addVertex(maxX, minY, minZ);
			tessellator.addVertex(maxX, minY, maxZ);
			tessellator.addVertex(minX, minY, maxZ);
			tessellator.addVertex(minX, minY, minZ);
			tessellator.draw();

			tessellator.startDrawing(GL11.GL_LINE_STRIP);
			tessellator.addVertex(minX, maxY, minZ);
			tessellator.addVertex(maxX, maxY, minZ);
			tessellator.addVertex(maxX, maxY, maxZ);
			tessellator.addVertex(minX, maxY, maxZ);
			tessellator.addVertex(minX, maxY, minZ);
			tessellator.draw();

			//bottom
			tessellator.startDrawing(GL11.GL_LINE_STRIP);
			tessellator.addVertex(minX+0.3f, minY, minZ);
			tessellator.addVertex(maxX-0.3f, minY, minZ);
			tessellator.addVertex(maxX-0.3f, minY, maxZ);
			tessellator.addVertex(minX+0.3f, minY, maxZ);
			tessellator.addVertex(minX+0.3f, minY, minZ);
			tessellator.draw();

			tessellator.startDrawing(GL11.GL_LINE_STRIP);
			tessellator.addVertex(minX, minY, minZ+0.3f);
			tessellator.addVertex(maxX, minY, minZ+0.3f);
			tessellator.addVertex(maxX, minY, maxZ-0.3f);
			tessellator.addVertex(minX, minY, maxZ-0.3f);
			tessellator.addVertex(minX, minY, minZ+0.3f);
			tessellator.draw();

			//top
			tessellator.startDrawing(GL11.GL_LINE_STRIP);
			tessellator.addVertex(minX+0.3f, maxY, minZ);
			tessellator.addVertex(maxX-0.3f, maxY, minZ);
			tessellator.addVertex(maxX-0.3f, maxY, maxZ);
			tessellator.addVertex(minX+0.3f, maxY, maxZ);
			tessellator.addVertex(minX+0.3f, maxY, minZ);
			tessellator.draw();

			tessellator.startDrawing(GL11.GL_LINE_STRIP);
			tessellator.addVertex(minX, maxY, minZ+0.3f);
			tessellator.addVertex(maxX, maxY, minZ+0.3f);
			tessellator.addVertex(maxX, maxY, maxZ-0.3f);
			tessellator.addVertex(minX, maxY, maxZ-0.3f);
			tessellator.addVertex(minX, maxY, minZ+0.3f);
			tessellator.draw();

			//sides
			tessellator.startDrawing(GL11.GL_LINE_STRIP);
			tessellator.addVertex(minX, minY+0.3f, minZ);
			tessellator.addVertex(maxX, minY+0.3f, minZ);
			tessellator.addVertex(maxX, minY+0.3f, maxZ);
			tessellator.addVertex(minX, minY+0.3f, maxZ);
			tessellator.addVertex(minX, minY+0.3f, minZ);
			tessellator.draw();

			tessellator.startDrawing(GL11.GL_LINE_STRIP);
			tessellator.addVertex(minX, maxY-0.3f, minZ);
			tessellator.addVertex(maxX, maxY-0.3f, minZ);
			tessellator.addVertex(maxX, maxY-0.3f, maxZ);
			tessellator.addVertex(minX, maxY-0.3f, maxZ);
			tessellator.addVertex(minX, maxY-0.3f, minZ);
			tessellator.draw();

			tessellator.startDrawing(GL11.GL_LINES);
			tessellator.addVertex(minX+0.3f, minY, minZ);
			tessellator.addVertex(minX+0.3f, maxY, minZ);
			tessellator.addVertex(maxX-0.3f, minY, minZ);
			tessellator.addVertex(maxX-0.3f, maxY, minZ);
			tessellator.addVertex(minX+0.3f, minY, maxZ);
			tessellator.addVertex(minX+0.3f, maxY, maxZ);
			tessellator.addVertex(maxX-0.3f, minY, maxZ);
			tessellator.addVertex(maxX-0.3f, maxY, maxZ);
			tessellator.draw();

			tessellator.startDrawing(GL11.GL_LINES);
			tessellator.addVertex(minX, minY, minZ+0.3f);
			tessellator.addVertex(minX, maxY, minZ+0.3f);
			tessellator.addVertex(maxX, minY, minZ+0.3f);
			tessellator.addVertex(maxX, maxY, minZ+0.3f);
			tessellator.addVertex(minX, minY, maxZ-0.3f);
			tessellator.addVertex(minX, maxY, maxZ-0.3f);
			tessellator.addVertex(maxX, minY, maxZ-0.3f);
			tessellator.addVertex(maxX, maxY, maxZ-0.3f);
			tessellator.draw();

			//cube outline
			tessellator.startDrawing(GL11.GL_LINES);
			tessellator.addVertex(minX, minY, minZ);
			tessellator.addVertex(minX, maxY, minZ);
			tessellator.addVertex(maxX, minY, minZ);
			tessellator.addVertex(maxX, maxY, minZ);
			tessellator.addVertex(maxX, minY, maxZ);
			tessellator.addVertex(maxX, maxY, maxZ);
			tessellator.addVertex(minX, minY, maxZ);
			tessellator.addVertex(minX, maxY, maxZ);
			tessellator.draw();
		} else {
			instance.drawOutlinedBoundingBox(aabb);
		}
	}
}
