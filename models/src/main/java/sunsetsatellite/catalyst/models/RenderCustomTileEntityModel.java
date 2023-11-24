package sunsetsatellite.catalyst.models;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.block.entity.TileEntity;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.Vec2f;
import sunsetsatellite.catalyst.core.util.Vec3f;

public class RenderCustomTileEntityModel<T extends TileEntity> extends TileEntityRenderer<T> {
    @Override
    public void doRender(T tileEntity, double x, double y, double z, float g) {
        if(tileEntity.getBlockType() != null && tileEntity.getBlockType() instanceof ICustomModel){
            render(x,y,z,tileEntity,((ICustomModel) tileEntity.getBlockType()).getModel());
        }
    }

    public static boolean render(double x, double y, double z, TileEntity tileEntity, NBTModel model){
        GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		if(model.rotatable){
			Direction dir = Direction.getDirectionFromSide(tileEntity.worldObj.getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
			Vec3f rot = Direction.getDirectionFromSide(tileEntity.worldObj.getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)).getVecF();
			GL11.glTranslated(x,y,z);

			switch (dir.getName()){
				case "WEST":
					break;
				case "EAST":
					GL11.glRotated(180,0,1,0);
					break;
				case "SOUTH":
					GL11.glRotated(90,0,1,0);
					break;
				case "NORTH":
					GL11.glRotated(90,0,-1,0);
					break;
			}
		}
		for (ModelSurface surface : model.surfaces) {
			Tessellator tessellator = Tessellator.instance;
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glPushMatrix();
			tessellator.startDrawingQuads();
			GL11.glScaled(surface.scale.x,surface.scale.y,surface.scale.z);
			double angle = Math.toDegrees(Math.acos(surface.rotation.w))*2;

			GL11.glTranslated(surface.translation.x,surface.translation.y,surface.translation.z);
			if(!(model.rotatable)){
				GL11.glTranslated(x,y,z);
			}
			GL11.glTranslatef(0.5f,0,0.5f);

			GL11.glRotated(angle,surface.rotation.x,surface.rotation.y,surface.rotation.z);

			for (int index : surface.indices) {
				String texture = model.textures[surface.texture];
				Minecraft.getMinecraft(Minecraft.class).renderEngine.bindTexture(Minecraft.getMinecraft(Minecraft.class).renderEngine.getTexture(texture));
				Vec3f vertex = surface.vertices[index];
				Vec2f uv = surface.uvs[index].copy();
				Vec3f normals = surface.normals[index];
				tessellator.setNormal((float) normals.x, (float) normals.y, (float) normals.z);
				tessellator.addVertexWithUV(vertex.x, vertex.y, vertex.z, uv.x, uv.y);
			}
			tessellator.draw();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glPopMatrix();
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		return true;
    }
}
