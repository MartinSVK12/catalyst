package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data;


import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.Utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CubeData {
	public float[] from;
	public float[] to;
	public RotationData rotation;
	public boolean shade;
	public Map<String, FaceData> faces;
	//	@SerializedName("color")
//	public int color = 0; // Block bench exports this value, but I don't see it mention on the wiki https://minecraft.wiki/w/Tutorials/Models
	public CubeData(){
		this(new float[3], new float[3], null, true, new HashMap<>());
	}
	public CubeData(float[] from, float[] to, RotationData rotation, boolean shade, Map<String, FaceData> faces){
		this.from = from;
		this.to = to;
		this.rotation = rotation;
		this.shade = shade;
		this.faces = faces;
	}
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("from: ").append(Arrays.toString(from)).append("\n");
		builder.append("to: ").append(Arrays.toString(to)).append("\n");
		builder.append("rotation: ").append("\n");
		builder.append(Utilities.tabBlock(rotation.toString(), 1));
		builder.append("shade: ").append(shade).append("\n");
//		builder.append("color: ").append(color).append("\n");
		builder.append("faces: ").append("\n");
		for (String key: faces.keySet()) {
			builder.append("\t").append(key).append(": \n");
			builder.append(Utilities.tabBlock(faces.get(key).toString(), 2));
		}
		return builder.toString();
	}
}
