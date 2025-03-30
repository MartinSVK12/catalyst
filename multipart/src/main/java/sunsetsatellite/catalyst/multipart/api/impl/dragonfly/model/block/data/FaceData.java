package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data;


import net.minecraft.core.util.helper.Side;

import java.util.Arrays;

public class FaceData {

	public double[] uv;
	public String texture;
	public Side cullface;
	public int rotation; // can only be values 0, 90, 180, or 270
	public int tintindex; // Should mean white tint, unsure how to handle other values seems to pass it into BlockColor to get a color from there in vanilla
	public boolean fullbright; // True if face should always render at full brightness

	public FaceData(){
		this(null, null, Side.NONE, 0, -1, false);
	}
	public FaceData(double[] uv, String texture, Side cullface, int rotation, int tintindex, boolean fullbright){
		this.uv = uv;
        this.texture = texture;
        this.cullface = cullface;
        this.rotation = rotation;
        this.tintindex = tintindex;
        this.fullbright = fullbright;
	}

	public String toString(){
		return
			"uv: " + Arrays.toString(uv) + "\n" +
			"texture: " + texture + "\n" +
			"cullface: " + cullface + "\n" +
			"rotation: " + rotation + "\n" +
			"tintindex: " + tintindex + "\n" +
			"fullbright: " + fullbright;
	}
}
