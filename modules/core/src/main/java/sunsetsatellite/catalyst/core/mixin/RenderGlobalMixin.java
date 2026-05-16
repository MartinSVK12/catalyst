package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.client.render.renderer.DrawMode;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.section.ISideInteractable;

@Mixin(value = RenderGlobal.class, remap = false)
public class RenderGlobalMixin {

	@Final
	@Shadow
	private Minecraft mc;

	@Shadow
	private WorldClient world;

	@WrapOperation(method = "drawSelectionBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderGlobal;drawTileSelectionBoxRaw(Lnet/minecraft/client/render/camera/ICamera;Lnet/minecraft/core/world/pos/TilePosc;DDD)V"))
	public void drawOutlinedSectionedBoundingBox(RenderGlobal instance, ICamera camera, TilePosc tilePos, double offsetX, double offsetY, double offsetZ, Operation<Void> original, @Local(name = "block") Block<?> block) {
		AABBdc aabb = block.getSelectionAABB(world, tilePos);
		if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ISideInteractable && ((ISideInteractable) mc.thePlayer.getCurrentEquippedItem().getItem()).alwaysShowOutlineWhenHeld()) {
			double minX = aabb.minX();
			double minY = aabb.minY();
			double minZ = aabb.minZ();
			double maxX = aabb.maxX();
			double maxY = aabb.maxY();
			double maxZ = aabb.maxZ();
			Tessellator tessellator = GLRenderer.getTessellator();

			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX, minY, minZ);
			tessellator.addVertex(maxX, minY, minZ);
			tessellator.addVertex(maxX, minY, maxZ);
			tessellator.addVertex(minX, minY, maxZ);
			tessellator.addVertex(minX, minY, minZ);
			tessellator.draw();

			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX, maxY, minZ);
			tessellator.addVertex(maxX, maxY, minZ);
			tessellator.addVertex(maxX, maxY, maxZ);
			tessellator.addVertex(minX, maxY, maxZ);
			tessellator.addVertex(minX, maxY, minZ);
			tessellator.draw();

			//bottom
			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX + 0.3f, minY, minZ);
			tessellator.addVertex(maxX - 0.3f, minY, minZ);
			tessellator.addVertex(maxX - 0.3f, minY, maxZ);
			tessellator.addVertex(minX + 0.3f, minY, maxZ);
			tessellator.addVertex(minX + 0.3f, minY, minZ);
			tessellator.draw();

			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX, minY, minZ + 0.3f);
			tessellator.addVertex(maxX, minY, minZ + 0.3f);
			tessellator.addVertex(maxX, minY, maxZ - 0.3f);
			tessellator.addVertex(minX, minY, maxZ - 0.3f);
			tessellator.addVertex(minX, minY, minZ + 0.3f);
			tessellator.draw();

			//top
			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX + 0.3f, maxY, minZ);
			tessellator.addVertex(maxX - 0.3f, maxY, minZ);
			tessellator.addVertex(maxX - 0.3f, maxY, maxZ);
			tessellator.addVertex(minX + 0.3f, maxY, maxZ);
			tessellator.addVertex(minX + 0.3f, maxY, minZ);
			tessellator.draw();

			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX, maxY, minZ + 0.3f);
			tessellator.addVertex(maxX, maxY, minZ + 0.3f);
			tessellator.addVertex(maxX, maxY, maxZ - 0.3f);
			tessellator.addVertex(minX, maxY, maxZ - 0.3f);
			tessellator.addVertex(minX, maxY, minZ + 0.3f);
			tessellator.draw();

			//sides
			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX, minY + 0.3f, minZ);
			tessellator.addVertex(maxX, minY + 0.3f, minZ);
			tessellator.addVertex(maxX, minY + 0.3f, maxZ);
			tessellator.addVertex(minX, minY + 0.3f, maxZ);
			tessellator.addVertex(minX, minY + 0.3f, minZ);
			tessellator.draw();

			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX, maxY - 0.3f, minZ);
			tessellator.addVertex(maxX, maxY - 0.3f, minZ);
			tessellator.addVertex(maxX, maxY - 0.3f, maxZ);
			tessellator.addVertex(minX, maxY - 0.3f, maxZ);
			tessellator.addVertex(minX, maxY - 0.3f, minZ);
			tessellator.draw();

			tessellator.startDrawing(DrawMode.LINES);
			tessellator.addVertex(minX + 0.3f, minY, minZ);
			tessellator.addVertex(minX + 0.3f, maxY, minZ);
			tessellator.addVertex(maxX - 0.3f, minY, minZ);
			tessellator.addVertex(maxX - 0.3f, maxY, minZ);
			tessellator.addVertex(minX + 0.3f, minY, maxZ);
			tessellator.addVertex(minX + 0.3f, maxY, maxZ);
			tessellator.addVertex(maxX - 0.3f, minY, maxZ);
			tessellator.addVertex(maxX - 0.3f, maxY, maxZ);
			tessellator.draw();

			tessellator.startDrawing(DrawMode.LINES);
			tessellator.addVertex(minX, minY, minZ + 0.3f);
			tessellator.addVertex(minX, maxY, minZ + 0.3f);
			tessellator.addVertex(maxX, minY, minZ + 0.3f);
			tessellator.addVertex(maxX, maxY, minZ + 0.3f);
			tessellator.addVertex(minX, minY, maxZ - 0.3f);
			tessellator.addVertex(minX, maxY, maxZ - 0.3f);
			tessellator.addVertex(maxX, minY, maxZ - 0.3f);
			tessellator.addVertex(maxX, maxY, maxZ - 0.3f);
			tessellator.draw();

			//cube outline
			tessellator.startDrawing(DrawMode.LINES);
			tessellator.addVertex(minX, minY, minZ);
			tessellator.addVertex(minX, maxY, minZ);
			tessellator.addVertex(maxX, minY, minZ);
			tessellator.addVertex(maxX, maxY, minZ);
			tessellator.addVertex(maxX, minY, maxZ);
			tessellator.addVertex(maxX, maxY, maxZ);
			tessellator.addVertex(minX, minY, maxZ);
			tessellator.addVertex(minX, maxY, maxZ);
			tessellator.draw();
		}
		ISideInteractable blockLogic = Catalyst.blockLogic(block, ISideInteractable.class);
		if (blockLogic != null && (!blockLogic.needsItemToShowOutline()) || (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ISideInteractable)) {
			double minX = aabb.minX();
			double minY = aabb.minY();
			double minZ = aabb.minZ();
			double maxX = aabb.maxX();
			double maxY = aabb.maxY();
			double maxZ = aabb.maxZ();
			Tessellator tessellator = GLRenderer.getTessellator();

			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX, minY, minZ);
			tessellator.addVertex(maxX, minY, minZ);
			tessellator.addVertex(maxX, minY, maxZ);
			tessellator.addVertex(minX, minY, maxZ);
			tessellator.addVertex(minX, minY, minZ);
			tessellator.draw();

			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX, maxY, minZ);
			tessellator.addVertex(maxX, maxY, minZ);
			tessellator.addVertex(maxX, maxY, maxZ);
			tessellator.addVertex(minX, maxY, maxZ);
			tessellator.addVertex(minX, maxY, minZ);
			tessellator.draw();

			//bottom
			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX + 0.3f, minY, minZ);
			tessellator.addVertex(maxX - 0.3f, minY, minZ);
			tessellator.addVertex(maxX - 0.3f, minY, maxZ);
			tessellator.addVertex(minX + 0.3f, minY, maxZ);
			tessellator.addVertex(minX + 0.3f, minY, minZ);
			tessellator.draw();

			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX, minY, minZ + 0.3f);
			tessellator.addVertex(maxX, minY, minZ + 0.3f);
			tessellator.addVertex(maxX, minY, maxZ - 0.3f);
			tessellator.addVertex(minX, minY, maxZ - 0.3f);
			tessellator.addVertex(minX, minY, minZ + 0.3f);
			tessellator.draw();

			//top
			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX + 0.3f, maxY, minZ);
			tessellator.addVertex(maxX - 0.3f, maxY, minZ);
			tessellator.addVertex(maxX - 0.3f, maxY, maxZ);
			tessellator.addVertex(minX + 0.3f, maxY, maxZ);
			tessellator.addVertex(minX + 0.3f, maxY, minZ);
			tessellator.draw();

			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX, maxY, minZ + 0.3f);
			tessellator.addVertex(maxX, maxY, minZ + 0.3f);
			tessellator.addVertex(maxX, maxY, maxZ - 0.3f);
			tessellator.addVertex(minX, maxY, maxZ - 0.3f);
			tessellator.addVertex(minX, maxY, minZ + 0.3f);
			tessellator.draw();

			//sides
			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX, minY + 0.3f, minZ);
			tessellator.addVertex(maxX, minY + 0.3f, minZ);
			tessellator.addVertex(maxX, minY + 0.3f, maxZ);
			tessellator.addVertex(minX, minY + 0.3f, maxZ);
			tessellator.addVertex(minX, minY + 0.3f, minZ);
			tessellator.draw();

			tessellator.startDrawing(DrawMode.LINE_STRIP);
			tessellator.addVertex(minX, maxY - 0.3f, minZ);
			tessellator.addVertex(maxX, maxY - 0.3f, minZ);
			tessellator.addVertex(maxX, maxY - 0.3f, maxZ);
			tessellator.addVertex(minX, maxY - 0.3f, maxZ);
			tessellator.addVertex(minX, maxY - 0.3f, minZ);
			tessellator.draw();

			tessellator.startDrawing(DrawMode.LINES);
			tessellator.addVertex(minX + 0.3f, minY, minZ);
			tessellator.addVertex(minX + 0.3f, maxY, minZ);
			tessellator.addVertex(maxX - 0.3f, minY, minZ);
			tessellator.addVertex(maxX - 0.3f, maxY, minZ);
			tessellator.addVertex(minX + 0.3f, minY, maxZ);
			tessellator.addVertex(minX + 0.3f, maxY, maxZ);
			tessellator.addVertex(maxX - 0.3f, minY, maxZ);
			tessellator.addVertex(maxX - 0.3f, maxY, maxZ);
			tessellator.draw();

			tessellator.startDrawing(DrawMode.LINES);
			tessellator.addVertex(minX, minY, minZ + 0.3f);
			tessellator.addVertex(minX, maxY, minZ + 0.3f);
			tessellator.addVertex(maxX, minY, minZ + 0.3f);
			tessellator.addVertex(maxX, maxY, minZ + 0.3f);
			tessellator.addVertex(minX, minY, maxZ - 0.3f);
			tessellator.addVertex(minX, maxY, maxZ - 0.3f);
			tessellator.addVertex(maxX, minY, maxZ - 0.3f);
			tessellator.addVertex(maxX, maxY, maxZ - 0.3f);
			tessellator.draw();

			//cube outline
			tessellator.startDrawing(DrawMode.LINES);
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

	/*@Unique
	private RenderBlocks blockRenderer;

	@Inject(method = "renderEntities", at = @At("TAIL"))
	public void renderWorld(ICamera camera, float partialTick, CallbackInfo ci) {
		if (((IKeybinds) mc.gameSettings).getNetworkRenderOption().value) {
			double x = camera.getX(partialTick);
			double y = camera.getY(partialTick);
			double z = camera.getZ(partialTick);
			Set<Network> nets = NetworkManager.getNetsForDimension(worldObj.dimension.id);
			ArrayList<BlockInstance> list = new ArrayList<>();
			for (Network net : nets) {
				for (Vec3i position : net.getPositions()) {
					list.add(new BlockInstance(Blocks.SAND, position, null));
				}
			}
			blockRenderer = new RenderBlocks(new HologramWorld(list));
			for (Network net : nets) {
				for (Vec3i position : net.getPositions()) {
					GL11.glPushMatrix();
					GL11.glDisable(GL11.GL_LIGHTING);
					//GL11.glDisable(GL11.GL_DEPTH_TEST);
					BlockModel<?> model = BlockModelDispatcher.getInstance().getDispatch(Blocks.SAND);
					GL11.glTranslated(position.x - x + 0.5f, position.y - y + 0.5f, position.z - z + 0.5f);
					((IFullbright) model).enableFullbright();
					((IColorOverride) model).enableColorOverride();
					((IColorOverride) model).overrideColor(net.getColor().getRed() / 255f, net.getColor().getGreen() / 255f, net.getColor().getBlue() / 255f, 0.5f);
					GL11.glScalef(1.01f, 1.01f, 1.01f);
					drawBlock(Tessellator.instance,
						model
					);
					((IColorOverride) model).disableColorOverride();
					((IFullbright) model).disableFullbright();
					GL11.glEnable(GL11.GL_LIGHTING);
					//GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glPopMatrix();

				}
			}
		}
	}

	@Unique
	private void drawBlock(Tessellator tessellator, BlockModel<?> model) {
		TextureRegistry.blockAtlas.bind();
		GL11.glPushMatrix();
		RenderBlocks renderBlocks = BlockModel.renderBlocks;
		BlockModel.setRenderBlocks(blockRenderer);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		model.renderBlockOnInventory(tessellator, 0, 1, null);
		BlockModel.setRenderBlocks(renderBlocks);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}*/

}
