package sunsetsatellite.catalyst.multipart.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import org.useless.dragonfly.DragonFly;
import org.useless.dragonfly.mixins.mixin.accessor.RenderBlocksAccessor;
import org.useless.dragonfly.model.block.processed.BlockCube;
import org.useless.dragonfly.model.block.processed.BlockFace;
import org.useless.dragonfly.model.block.processed.ModernBlockModel;
import org.useless.dragonfly.utilities.vector.Vector3f;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.block.model.ModernMultipartBlockModel;

import java.awt.*;

import static org.useless.dragonfly.utilities.vector.Vector3f.origin;

public class MultipartModelRenderer {
	public static Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
	private static final int rotationX = 0;
	private static final int rotationY = 0;
	public static boolean renderModelNormal(Tessellator tessellator, ModernBlockModel model, Block block, double x, double y, double z, Side side, Multipart part) {
		boolean didRender;
		didRender = renderStandardModelWithColorMultiplier(tessellator, model, block, x, y, z, 1, 1, 1, side, part);
		return didRender;
	}

	public static void renderModelFace(Tessellator tessellator, BlockFace face, double x, double y, double z) {
		double[] uvTL;
		double[] uvBL;
		double[] uvBR;
		double[] uvTR;
		if (getRenderBlocks().overrideBlockTexture != null) {
			uvTL = face.generateVertexUV(getRenderBlocks().overrideBlockTexture, 0);
			uvBL = face.generateVertexUV(getRenderBlocks().overrideBlockTexture, 1);
			uvBR = face.generateVertexUV(getRenderBlocks().overrideBlockTexture, 2);
			uvTR = face.generateVertexUV(getRenderBlocks().overrideBlockTexture, 3);
		} else {
			uvTL = face.getVertexUV(0);
			uvBL = face.getVertexUV(1);
			uvBR = face.getVertexUV(2);
			uvTR = face.getVertexUV(3);
		}


		Vector3f[] faceVertices = new Vector3f[4];
		for (int i = 0; i < faceVertices.length; i++) {
			faceVertices[i] = face.vertices[i].rotateAroundX(origin, rotationX).rotateAroundY(origin, rotationY);

		}
		Vector3f vtl = faceVertices[0];
		Vector3f vbl = faceVertices[1];
		Vector3f vbr = faceVertices[2];
		Vector3f vtr = faceVertices[3];

		if (getRenderBlocks().enableAO) {
			// Top Left
			tessellator.setColorOpaque_F(getRenderBlocks().colorRedTopLeft, getRenderBlocks().colorGreenTopLeft, getRenderBlocks().colorBlueTopLeft);
			tessellator.addVertexWithUV(x + vtl.x, y + vtl.y, z + vtl.z, uvTL[0], uvTL[1]);

			// Bottom Left
			tessellator.setColorOpaque_F(getRenderBlocks().colorRedBottomLeft, getRenderBlocks().colorGreenBottomLeft, getRenderBlocks().colorBlueBottomLeft);
			tessellator.addVertexWithUV(x + vbl.x, y + vbl.y, z + vbl.z, uvBL[0], uvBL[1]);

			// Bottom Right
			tessellator.setColorOpaque_F(getRenderBlocks().colorRedBottomRight, getRenderBlocks().colorGreenBottomRight, getRenderBlocks().colorBlueBottomRight);
			tessellator.addVertexWithUV(x + vbr.x, y + vbr.y, z + vbr.z, uvBR[0], uvBR[1]);

			// Top Right
			tessellator.setColorOpaque_F(getRenderBlocks().colorRedTopRight, getRenderBlocks().colorGreenTopRight, getRenderBlocks().colorBlueTopRight);
			tessellator.addVertexWithUV(x + vtr.x, y + vtr.y, z + vtr.z, uvTR[0], uvTR[1]);
		} else {
			tessellator.addVertexWithUV(x + vtl.x, y + vtl.y, z + vtl.z, uvTL[0], uvTL[1]); // Top Left
			tessellator.addVertexWithUV(x + vbl.x, y + vbl.y, z + vbl.z, uvBL[0], uvBL[1]); // Bottom Left
			tessellator.addVertexWithUV(x + vbr.x, y + vbr.y, z + vbr.z, uvBR[0], uvBR[1]); // Bottom Right
			tessellator.addVertexWithUV(x + vtr.x, y + vtr.y, z + vtr.z, uvTR[0], uvTR[1]); // Top Right
		}
	}

	public static boolean renderStandardModelWithColorMultiplier(Tessellator tessellator, ModernBlockModel model, Block block, double x, double y, double z, float r, float g, float b, Side partSide, Multipart part) {
		getRenderBlocks().enableAO = false;
		boolean renderedSomething = false;
		float cBottom = 0.5f;
		float cTop = 1.0f;
		float cNorthSouth = 0.8f;
		float cEastWest = 0.6f;
		float rTop = cTop * r;
		float gTop = cTop * g;
		float bTop = cTop * b;
		float rBottom = cBottom;
		float rNorthSouth = cNorthSouth;
		float rEastWest = cEastWest;
		float gBottom = cBottom;
		float gNorthSouth = cNorthSouth;
		float gEastWest = cEastWest;
		float bBottom = cBottom;
		float bNorthSouth = cNorthSouth;
		float bEastWest = cEastWest;
		rBottom *= r;
		rNorthSouth *= r;
		rEastWest *= r;
		gBottom *= g;
		gNorthSouth *= g;
		gEastWest *= g;
		bBottom *= b;
		bNorthSouth *= b;
		bEastWest *= b;
		int xi, yi, zi;
		xi = (int) Math.round(x);
		yi = (int) Math.round(y);
		zi = (int) Math.round(z);
		float blockBrightness = rba().invokeGetBlockBrightness(rba().getBlockAccess(), xi, yi, zi);
		BlockCube[] blockCubes = model.blockCubes;
		for (int i = 0; i < blockCubes.length; i++) {
			BlockCube cube = blockCubes[i];
			if(i < partSide.ordinal() * part.type.cubesPerSide || i >= (partSide.ordinal() * part.type.cubesPerSide) + part.type.cubesPerSide) continue;
			for (Side side : DragonFly.sides) {
				BlockFace face = cube.getFaceFromSide(side, rotationX, rotationY);
				if (face == null) continue;
				int _x = (xi + side.getOffsetX());
				int _y = (yi + side.getOffsetY());
				int _z = (zi + side.getOffsetZ());

				if (!getRenderBlocks().renderAllFaces) {
					if (!renderSide(tessellator, model, cube, side, xi, yi, zi)) continue;
				}

				float sideBrightness;
				if (!cube.isOuterFace(side, rotationX, rotationY) && !block.blockMaterial.isLiquid()) {
					sideBrightness = blockBrightness;
				} else {
					sideBrightness = rba().invokeGetBlockBrightness(rba().getBlockAccess(), _x, _y, _z);
				}
				if (model instanceof ModernMultipartBlockModel) sideBrightness = blockBrightness;

				float red = 1f;
				float green = 1f;
				float blue = 1f;

				if (cube.shade()) {
					switch (side) {
						case TOP:
							if (rotationX == 90 || rotationX == -90) {
								red = rBottom;
								green = gBottom;
								blue = bBottom;
							} else {
								red = rTop;
								green = gTop;
								blue = bTop;
							}
							break;
						case BOTTOM:
							if (rotationX == 90 || rotationX == -90) {
								red = rTop;
								green = gTop;
								blue = bTop;
							} else {
								red = rBottom;
								green = gBottom;
								blue = bBottom;
							}
							break;
						case NORTH:
						case SOUTH:
							red = rNorthSouth;
							green = gNorthSouth;
							blue = bNorthSouth;
							break;
						case WEST:
						case EAST:
							red = rEastWest;
							green = gEastWest;
							blue = bEastWest;
							break;
						default:
							throw new RuntimeException("Specified side does not exist on a cube!!!");
					}
				}

				if (model instanceof ModernMultipartBlockModel) {
					if (cube.shade()) {
						switch (side) {
							//top and bottom fields on vertical rotation being swapped is NOT an oversight, it fixes a lighting bug
							case TOP:
								if (rotationX == 90 || rotationX == -90) {
									red = rBottom;
									green = gBottom;
									blue = bBottom;
								} else {
									red = rTop;
									green = gTop;
									blue = bTop;
								}
								break;
							case BOTTOM:
								if (rotationX == 90 || rotationX == -90) {
									red = rTop;
									green = gTop;
									blue = bTop;
								} else {
									red = rBottom;
									green = gBottom;
									blue = bBottom;
								}
								break;
						}
					}
				}

				if (face.useTint()) {
					Color color = new Color(BlockColorDispatcher.getInstance().getDispatch(block).getWorldColor(mc.theWorld, xi, yi, zi));
					red *= color.getRed() / 255.0f;
					green *= color.getGreen() / 255.0f;
					blue *= color.getBlue() / 255.0f;
				}


				tessellator.setColorOpaque_F(!face.getFullBright() ? red * sideBrightness : 1f, !face.getFullBright() ? green * sideBrightness : 1f, !face.getFullBright() ? blue * sideBrightness : 1f);
				renderModelFace(tessellator, face, x, y, z);
				renderedSomething = true;
			}
		}
		return renderedSomething;
	}
	public static boolean renderSide(Tessellator tessellator, ModernBlockModel model, BlockCube cube, Side side, int x, int y, int z){
		WorldSource blockAccess = rba().getBlockAccess();
		boolean renderOuterSide = blockAccess.getBlock(x, y, z).shouldSideBeRendered(blockAccess, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), side.getId(), blockAccess.getBlockMetadata(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ()));
		return !cube.getFaceFromSide(side, rotationX, rotationY).cullFace(x, y, z, renderOuterSide);
	}
	public static RenderBlocks getRenderBlocks(){
		return BlockModelStandard.renderBlocks;
	}
	public static RenderBlocksAccessor rba(){
		return (RenderBlocksAccessor) getRenderBlocks();
	}
}
