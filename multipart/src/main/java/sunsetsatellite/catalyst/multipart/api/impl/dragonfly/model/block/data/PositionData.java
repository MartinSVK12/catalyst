package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data;


import java.util.Arrays;

public class PositionData {
	public double[] rotation;
	public double[] translation;
	public double[] scale;
	public PositionData(){
		this(new double[3],  new double[3], new double[]{1,1,1});
	}
	public PositionData(double[] rotation, double[] translation, double[] scale){
		this.rotation = rotation;
		this.translation = translation;
		this.scale = scale;
	}
	public String toString(){
		return
			"rotation: " + Arrays.toString(rotation) + "\n" +
			"translation: " + Arrays.toString(translation) + "\n" +
			"scale: " + Arrays.toString(scale) + "\n";
	}
}
