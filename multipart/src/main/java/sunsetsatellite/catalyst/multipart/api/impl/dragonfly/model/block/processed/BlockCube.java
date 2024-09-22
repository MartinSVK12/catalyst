package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.processed;


import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Side;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.Utilities;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.CubeData;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.FaceData;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.ModelData;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.vector.Vector3f;
import sunsetsatellite.catalyst.multipart.block.model.ModernMultipartBlockModel;

import java.util.HashMap;

public class BlockCube {
	public CubeData cubeData;
	protected Vector3f fromScaled;
	protected Vector3f toScaled;
	protected boolean[] outerFace;
	public final ModernMultipartBlockModel parentModel;
	protected HashMap<String, Vector3f> vertices = new HashMap<>();
	protected HashMap<String, BlockFace> faces = new HashMap<>();
	private float getScalar(float angle){
		final float modRange = 90f;
		float v = (Math.abs(angle) - (modRange/2));
		if (v < 0) {
			v += modRange;
		}
		float x = (v % modRange) - (modRange/2);
		float slope = (float) Math.tan(Math.toRadians(x));
		float scalar = (float) (Math.sqrt(Math.pow((-0.5 * slope + 0.5), 2) + Math.pow((0.5 * slope + 0.5), 2)) * Math.sqrt(2));
		if (scalar > Math.sqrt(2)){
			throw new IllegalArgumentException(String.valueOf(scalar));
		}
		return scalar;
	}
	public BlockCube(ModernMultipartBlockModel model, CubeData cubeData){
		this.parentModel = model;
		this.cubeData = cubeData;
		outerFace = new boolean[6];

		fromScaled = new Vector3f(cubeData.from[0] / 16f, cubeData.from[1] / 16f, cubeData.from[2] / 16f);
		toScaled = new Vector3f(cubeData.to[0] / 16f, cubeData.to[1] / 16f, cubeData.to[2] / 16f);

		if (cubeData.rotation != null && cubeData.rotation.rescale){
			float angle = cubeData.rotation.angle;

			Axis axis = cubeData.rotation.axis;
			Vector3f re = new Vector3f(cubeData.rotation.origin[0]/ 16f, cubeData.rotation.origin[1]/16f, cubeData.rotation.origin[2]/16f);

			float scalar = getScalar(angle);

			float xf = fromScaled.getX();
			float xt = toScaled.getX();
			float yf = fromScaled.getY();
			float yt = toScaled.getY();
			float zf = fromScaled.getZ();
			float zt = toScaled.getZ();
			Vector3f fromOrigin = (Vector3f) Vector3f.sub(fromScaled, re, null).scale(scalar);
			Vector3f toOrigin = (Vector3f) Vector3f.sub(toScaled, re, null).scale(scalar);
			Vector3f.add(fromOrigin, re, fromOrigin);
			Vector3f.add(toOrigin, re, toOrigin);
			fromScaled = fromOrigin;
			toScaled = toOrigin;
			switch (axis){
				case X:
					fromScaled.setX(xf);
					toScaled.setX(xt);
					break;
				case Y:
					break;
				case Z:
					fromScaled.setZ(zf);
					toScaled.setZ(zt);
					break;
			}
			fromScaled.setY(yf);
			toScaled.setY(yt);
		}

		vertices.put("+++", new Vector3f(xMax(), yMax(), zMax()));
		vertices.put("++-", new Vector3f(xMax(), yMax(), zMin()));
		vertices.put("+-+", new Vector3f(xMax(), yMin(), zMax()));
		vertices.put("+--", new Vector3f(xMax(), yMin(), zMin()));
		vertices.put("-++", new Vector3f(xMin(), yMax(), zMax()));
		vertices.put("-+-", new Vector3f(xMin(), yMax(), zMin()));
		vertices.put("--+", new Vector3f(xMin(), yMin(), zMax()));
		vertices.put("---", new Vector3f(xMin(), yMin(), zMin()));

		if (cubeData.rotation != null){
			vertices.replaceAll((k, v) -> Utilities.rotatePoint(
				vertices.get(k),
				new Vector3f(cubeData.rotation.origin[0]/ 16f, cubeData.rotation.origin[1]/16f, cubeData.rotation.origin[2]/16f),
				cubeData.rotation.axis,
				cubeData.rotation.angle));
		}

		for (String key: cubeData.faces.keySet()) {
			FaceData faceData = cubeData.faces.get(key);
			if (faceData.uv != null){
				if (faceData.uv[0] == faceData.uv[2]){ // Don't render faces that don't have a uv area > 0
					continue;
				}
				if (faceData.uv[1] == faceData.uv[3]){
					continue;
				}
			}
			faces.put(key, new BlockFace(this, key));
		}
		for (int i = 0; i < outerFace.length; i++) {
			outerFace[i] = Utilities.equalFloats(getAxisPosition(Side.getSideById(i)), 0f) || Utilities.equalFloats(getAxisPosition(Side.getSideById(i)), 1f);
		}


	}
	public float getAxisPosition(Side side){
		switch (side){
			case TOP:
				return yMax();
			case BOTTOM:
				return yMin();
			case NORTH:
				return zMin();
			case SOUTH:
				return zMax();
			case WEST:
				return xMin();
			case EAST:
				return xMax();
		}
		throw new RuntimeException("Specified side does not exist on a cube!!!");
	}
	public boolean isOuterFace(Side side, int rotationX, int rotationY){
		return outerFace[rotateSide(side, rotationX, rotationY).getId()];
	}
	public Side rotateSide(Side side, int rotationX, int rotationY){
		Side keySide = side;
		Side[] yRot = new Side[]{Side.EAST, Side.SOUTH, Side.WEST, Side.NORTH};
		Side[] xRot = new Side[]{Side.TOP, Side.NORTH, Side.BOTTOM, Side.SOUTH};

		if (rotationX < 0){
			rotationX += 360;
		}
		if (rotationY < 0){
			rotationY += 360;
		}
		int indexY = -1;
		for (int i = 0; i < yRot.length; i++) {
			if (keySide == yRot[i]){
				indexY = i;
			}
		}
		if (indexY != -1){
			keySide = yRot[(indexY + rotationY/90) % yRot.length];
		}

		int indexX = -1;
		for (int i = 0; i < xRot.length; i++) {
			if (keySide == xRot[i]){
				indexX = i;
			}
		}
		if (indexX != -1){
			keySide = xRot[(indexX + (rotationX)/90) % xRot.length];
		}
		return keySide;
	}

	public BlockFace getFaceFromSide(Side side, int rotationX, int rotationY){
		return faces.get(ModelData.sideToKey.get(rotateSide(side, rotationX, rotationY)));
	}
	public HashMap<String, BlockFace> getFaces(){
		return faces;
	}
	public float xMin(){
		return fromScaled.getX();
	}
	public float yMin(){
		return fromScaled.getY();
	}
	public float zMin(){
		return fromScaled.getZ();
	}
	public float xMax(){
		return toScaled.getX();
	}
	public float yMax(){
		return toScaled.getY();
	}
	public float zMax(){
		return toScaled.getZ();
	}
	public Vector3f getMin(){
		return fromScaled;
	}
	public Vector3f getMax(){
		return toScaled;
	}
	public boolean shade() {return cubeData.shade;}
}
