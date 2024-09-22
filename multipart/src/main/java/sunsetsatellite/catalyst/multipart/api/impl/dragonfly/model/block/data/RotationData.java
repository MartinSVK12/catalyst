package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data;


import net.minecraft.core.util.helper.Axis;

import java.util.Arrays;

public class RotationData {
	public float[] origin;
	public Axis axis;
	public float angle;
	public boolean rescale;
	public RotationData(){
		this(new float[3], Axis.NONE, 0, false);
	}
	public RotationData(float[] origin, Axis axis, float angle, boolean rescale){
        this.origin = origin;
        this.axis = axis;
        this.angle = angle;
        this.rescale = rescale;
    }
	public String toString(){
		return
			"origin: " + Arrays.toString(origin) + "\n" +
			"axis: " + axis + "\n" +
			"angle: " + angle + "\n" +
			"rescale: " + rescale;
	}
}
