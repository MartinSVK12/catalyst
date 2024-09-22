package sunsetsatellite.catalyst.multipart.block.model;

import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.helper.ModelHelper;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.PositionData;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.processed.BlockCube;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.processed.BlockFace;
import sunsetsatellite.catalyst.multipart.util.MultipartModelRenderer;

public class BlockModelMultipartItem extends BlockModelStandard<Block> {

	public Multipart multipart;
	public boolean render3d;
	public float renderScale;

	public BlockModelMultipartItem(Block block, boolean render3d, float renderScale) {
		super(block);
		this.render3d = render3d;
		this.renderScale = renderScale;
	}

	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		MultipartInternalModel[] models = getModelsFromState(block, x, y, z, false);
		boolean didRender = false;
		for (MultipartInternalModel model : models) {
			didRender |= MultipartModelRenderer.renderModelNormal(tessellator, model.model, block, x, y, z, Side.NORTH, multipart);
		}
		return didRender;
	}

	@Override
	public void renderBlockOnInventory(Tessellator tessellator, int metadata, float brightness, float alpha, @Nullable Integer lightmapCoordinate) {
		float xOffset;
		float yOffset;
		float zOffset;
		float xScale;
		float yScale;
		float zScale;
		float xRot;
		float yRot;
		float zRot;

		MultipartInternalModel[] models = getModelsFromState(null, 0, 0, 0, false);
		PositionData displayData = ModelHelper.getOrCreateBlockModel(CatalystMultipart.MOD_ID,multipart).getDisplayPosition(CatalystMultipart.renderState);
		switch (CatalystMultipart.renderState) {
			case "ground":
				xScale = (float) displayData.scale[2] * 4;
				yScale = (float) displayData.scale[1] * 4;
				zScale = (float) displayData.scale[0] * 4;

				xOffset = 0.5f * xScale;
				yOffset = 0.5f * yScale;
				zOffset = 0.5f * zScale;

				xOffset -= (float) displayData.translation[2] / 16f;
				yOffset -= (float) displayData.translation[1] / 16f;
				zOffset -= (float) displayData.translation[0] / 16f;

				xRot = (float) displayData.rotation[0];
				yRot = (float) displayData.rotation[1];
				zRot = (float) displayData.rotation[2];
				break;
			case "head":
				GL11.glFrontFace(GL11.GL_CW);
				xScale = (float) displayData.scale[0];
				yScale = (float) displayData.scale[1];
				zScale = (float) displayData.scale[2];

				xOffset = 0.5f * xScale;
				yOffset = 0.5f * yScale;
				zOffset = 0.5f * zScale;

				xOffset -= (float) displayData.translation[0] / 16f;
				yOffset -= (float) displayData.translation[1] / 16f;
				zOffset -= (float) displayData.translation[2] / 16f;

				xRot = (float) displayData.rotation[0];
				yRot = (float) displayData.rotation[1] + 180;
				zRot = (float) displayData.rotation[2];
				break;
			case "firstperson_righthand":
				xScale = (float) displayData.scale[2] * 2.5f;
				yScale = (float) displayData.scale[1] * 2.5f;
				zScale = (float) displayData.scale[0] * 2.5f;

				xOffset = 0.5f * xScale;
				yOffset = 0.5f * yScale;
				zOffset = 0.5f * zScale;

				xOffset -= (float) displayData.translation[2] / 8f;
				yOffset -= (float) displayData.translation[1] / 8f;
				zOffset -= (float) displayData.translation[0] / 8f;

				xRot = (float) displayData.rotation[0];
				yRot = (float) displayData.rotation[1] + 45;
				zRot = (float) displayData.rotation[2];
				break;
			case "thirdperson_righthand":
				GL11.glFrontFace(GL11.GL_CW);
				float scale = 8f/3;
				xScale = (float) displayData.scale[2] * scale;
				yScale = (float) displayData.scale[1] * scale;
				zScale = (float) displayData.scale[0] * scale;

				xOffset = 0.5f * xScale;
				yOffset = 0.5f * yScale;
				zOffset = 0.5f * zScale;

				xOffset -= (float) displayData.translation[2] / 16f;
				yOffset -= (float) displayData.translation[1] / 16f;
				zOffset -= (float) displayData.translation[0] / 16f;

				xRot = (float) -displayData.rotation[2] + 180;
				yRot = (float) displayData.rotation[1] + 45;
				zRot = (float) -displayData.rotation[0] - 100;
				break;
			case "gui":
			default:
				xScale = (float) displayData.scale[2] * 1.6f;
				yScale = (float) displayData.scale[1] * 1.6f;
				zScale = (float) displayData.scale[0] * 1.6f;

				xOffset = 0.5f * xScale;
				yOffset = 0.5f * yScale;
				zOffset = 0.5f * zScale;

				xOffset -= (float) displayData.translation[2] / 16f;
				yOffset -= (float) displayData.translation[1] / 16f;
				zOffset -= (float) (((float) displayData.translation[0] / 16f) + Catalyst.map(multipart.type.thickness,1,16,0.5d,0)); //+ 0.5f for foils, 0 for full blocks

				xRot = (float) displayData.rotation[0] - 30;
				yRot = (float) displayData.rotation[1] - 45;
				zRot = (float) displayData.rotation[2];
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glRotatef(yRot, 0, 1, 0);
		GL11.glRotatef(xRot, 1, 0, 0);
		GL11.glRotatef(zRot, 0, 0, 1);
		GL11.glTranslatef(-xOffset, -yOffset, -zOffset);
		GL11.glScalef(xScale, yScale, zScale);
		for (MultipartInternalModel model : models) {
			if (model.model.blockCubes != null){
				tessellator.startDrawingQuads();
				GL11.glColor4f(brightness, brightness, brightness, 1);
				BlockCube[] blockCubes = model.model.blockCubes;
				for (int i = 0; i < blockCubes.length; i++) {
					BlockCube cube = blockCubes[i];
					if(i < 2 * multipart.type.cubesPerSide || i >= (2 * multipart.type.cubesPerSide) + multipart.type.cubesPerSide) continue;
					for (BlockFace face : cube.getFaces().values()) {
						tessellator.setNormal(face.getSide().getOffsetX(), face.getSide().getOffsetY(), face.getSide().getOffsetZ());
						if (LightmapHelper.isLightmapEnabled() && lightmapCoordinate != null) {
							tessellator.setLightmapCoord(lightmapCoordinate);
						}
						float r = 1;
						float g = 1;
						float b = 1;
						if (face.useTint()) {
							int color = BlockColorDispatcher.getInstance().getDispatch(block).getFallbackColor(metadata);
							r = (float) (color >> 16 & 0xFF) / 255.0f;
							g = (float) (color >> 8 & 0xFF) / 255.0f;
							b = (float) (color & 0xFF) / 255.0f;
						}
						MultipartModelRenderer.renderModelFaceWithColor(tessellator, face, 0, 0, 0, r * brightness, g * brightness, b * brightness);
					}
				}
				tessellator.draw();
			}
		}
		GL11.glDisable(GL11.GL_CULL_FACE); // Deleting this causes render issues on vanilla transparent blocks
		GL11.glTranslatef(xOffset, yOffset, zOffset);
	}

	public MultipartInternalModel[] getModelsFromState(Block block, int x, int y, int z, boolean sourceFromWorld){
		MultipartInternalModel internalModel = new MultipartInternalModel(ModelHelper.getOrCreateBlockModel(CatalystMultipart.MOD_ID, multipart), Side.NORTH, multipart);
		internalModel.model.refreshModel();
		return new MultipartInternalModel[]{internalModel};
	}
}

