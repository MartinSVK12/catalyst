package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.processed;


import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.core.util.helper.Side;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.FaceData;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.ModelData;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.vector.Vector3f;

public class BlockFace {
	protected FaceData faceData;
	protected double[] uvScaled;
	protected Side side;
	public final Vector3f[] vertices;
	protected final String[] vertexUVMap;
	protected String[] vertexKeyMap = new String[4];
	protected double[][] vertexUVs;
	public BlockCube parentCube;
	public BlockFace(BlockCube cube, String key){
		this.faceData = cube.cubeData.faces.get(key);
		this.side = ModelData.keyToSide.get(key);
		this.parentCube = cube;
		generateUVs(cube);
		switch (side){
			case NORTH:
				vertexKeyMap = new String[]{"-+-", "++-", "+--", "---"};
				vertexUVMap = new String[]{"+-", "--", "-+", "++"};
				break;
			case SOUTH:
				vertexKeyMap = new String[]{"-++", "--+", "+-+", "+++"};
				vertexUVMap = new String[]{"--", "-+", "++", "+-"};
				break;
			case EAST:
				vertexKeyMap = new String[]{"+-+", "+--", "++-", "+++"};
				vertexUVMap = new String[]{"-+", "++", "+-", "--"};
				break;
			case WEST:
				vertexKeyMap = new String[]{"-++", "-+-", "---", "--+"};
				vertexUVMap = new String[]{"+-", "--", "-+", "++"};
				break;
			case TOP:
				vertexKeyMap = new String[]{"+++", "++-", "-+-", "-++"};
				vertexUVMap = new String[]{"++", "+-", "--", "-+"};
				break;
			case BOTTOM:
				vertexKeyMap = new String[]{"--+", "---", "+--", "+-+"};
				vertexUVMap = new String[]{ "--", "-+", "++", "+-"};
				break;
			default:
				vertexUVMap = null;
		}
		IconCoordinate texture = TextureRegistry.getTexture(parentCube.parentModel.getTexture(getTexture()).toString());
		vertices = new Vector3f[]{parentCube.vertices.get(vertexKeyMap[0]), parentCube.vertices.get(vertexKeyMap[1]), parentCube.vertices.get(vertexKeyMap[2]), parentCube.vertices.get(vertexKeyMap[3])};
		vertexUVs = new double[][]{generateVertexUV(texture, 0), generateVertexUV(texture, 1), generateVertexUV(texture, 2), generateVertexUV(texture, 3)};
	}
	protected void generateUVs(BlockCube cube){
		uvScaled = new double[4];
		double[] _uvs = new double[0];
		if (faceData.uv == null){
			float xDif = cube.cubeData.to[0] - cube.cubeData.from[0];
			float yDif = cube.cubeData.to[1] - cube.cubeData.from[1];
			float zDif = cube.cubeData.to[2] - cube.cubeData.from[2];
			switch (side){
				case NORTH:
				case SOUTH:
					_uvs = new double[]{cube.cubeData.from[0], 16 - cube.cubeData.to[1], cube.cubeData.from[0] + xDif, 16 - cube.cubeData.to[1] + yDif};
					break;
				case EAST:
				case WEST:
					_uvs = new double[]{cube.cubeData.from[2], 16 - cube.cubeData.to[1], cube.cubeData.from[2] + zDif, 16 - cube.cubeData.to[1] + yDif};
					break;
				case TOP:
				case BOTTOM:
					_uvs = new double[]{cube.cubeData.from[0], 16 - cube.cubeData.to[2], cube.cubeData.from[0] + xDif, 16 - cube.cubeData.to[2] + zDif};
					break;
			}

		} else {
			_uvs = faceData.uv;
		}

		for (int i = 0; i < _uvs.length; i++) {
			if (i == 0 || i == 2){ // u
				uvScaled[i] = _uvs[i] / 16;
			} else { // v
				uvScaled[i] = (16 - _uvs[i]) / 16;
			}
		}
	}
	public double uMin(){
		return uvScaled[0];
	}
	public double vMin(){ return uvScaled[1];}
	public double uMax(){
		return uvScaled[2];
	}
	public double vMax(){
		return uvScaled[3];
	}
	public Side getSide(){ return side;}
	public String getTexture(){
		return faceData.texture;
	}
	public double[] generateVertexUV(IconCoordinate texture, int point){
		double atlasUMin = texture.getSubIconU(uMin());
		double atlasUMax = texture.getSubIconU(uMax());
		double atlasVMin = texture.getSubIconV(1 - vMin());
		double atlasVMax = texture.getSubIconV(1 - vMax());
//		if (uMin() < 0.0 || uMax() > 1.0) { // Cap U value
//			atlasUMin = texX / terrainAtlasWidth;
//			atlasUMax = (texX + (TextureFX.tileWidthTerrain - 0.01f)) / terrainAtlasWidth;
//		}
//		if (vMin() < 0.0 || vMax() > 1.0) { // Cap V value
//			atlasVMin = texY / terrainAtlasWidth;
//			atlasVMax = (texY + (TextureFX.tileWidthTerrain - 0.01f)) / terrainAtlasWidth;
//		}

		String uvKey = vertexUVMap[point];
		double u = uvKey.charAt(0) == '-' ? atlasUMin : atlasUMax;
		double v = uvKey.charAt(1) == '-' ? atlasVMin : atlasVMax;
		return new double[]{u, v};
	}
	public boolean cullFace(int x, int y, int z, boolean renderOuterSide){
		return !renderOuterSide && side == faceData.cullface;
	}
	public boolean useTint(){
		return faceData.tintindex >= 0;
	}
	public double[][] getVertexUVs(){
		return vertexUVs;
	}
	public double[] getVertexUV(int index){
		int val = index + faceData.rotation/90;
		if (val < 0) val += 4;
		return vertexUVs[val%4];
	}
	public boolean getFullBright(){
		return faceData.fullbright;
	}
}
