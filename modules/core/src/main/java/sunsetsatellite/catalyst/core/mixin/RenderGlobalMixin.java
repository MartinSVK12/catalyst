package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.client.render.renderer.*;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.LightIndexHelper;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystClient;
import sunsetsatellite.catalyst.core.util.network.Network;
import sunsetsatellite.catalyst.core.util.network.NetworkManager;
import sunsetsatellite.catalyst.core.util.section.ISideInteractable;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.Set;

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
		aabb = aabb.translate(-offsetX, -offsetY, -offsetZ, new AABBd());
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
			original.call(instance, camera, tilePos, offsetX, offsetY, offsetZ);
			//instance.drawOutlinedBoundingBox(aabb);
		}
	}

	@Inject(method = "renderEntities", at = @At("TAIL"))
	public void renderWorld(ICamera camera, float partialTick, CallbackInfo ci) {
		double x = camera.getX(partialTick);
		double y = camera.getY(partialTick);
		double z = camera.getZ(partialTick);
		if(CatalystClient.networkRenderOption.value){
			Set<Network> nets = NetworkManager.getNetsForDimension(world.dimension.id);
			for (Network net : nets) {
				for (Vec3i position : net.getPositions()) {
					BlockModel<?> model = BlockModelDispatcher.getInstance().getDispatch(Blocks.SAND);
					GLRenderer.pushFrame();
					GLRenderer.setColor1i(net.color.value);
					Lighting.disable();
					GLRenderer.modelM4f().translate((float) (position.x - x + 0.5f), (float) (position.y - y + 0.5f), (float) (position.z - z + 0.5f));
					GLRenderer.modelM4f().scale(1.1f, 1.1f, 1.1f);
					drawBlock(GLRenderer.getTessellator(),
						model,
						0, 1);
					Lighting.enableLight();
					GLRenderer.popFrame();
				}
			}
		}
	}

	@Unique
	public void drawBlock(TessellatorGeneral t, BlockModel<?> model, int meta, float alpha) {
		TextureRegistry.worldAtlas.bind();
		GLRenderer.pushFrame();
		GLRenderer.setShader(Shaders.WORLD);
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
		model.renderStandalone(t, meta, LightIndexHelper.lightIndex2i(15,15));
		GLRenderer.setColor4f(1,1,1,1);
		GLRenderer.disableState(State.BLEND);
		GLRenderer.popFrame();
		GLRenderer.enableState(State.CULL_FACE);
	}

}
