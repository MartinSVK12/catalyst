package sunsetsatellite.catalyst.multipart.block.model;


import net.minecraft.core.util.helper.Side;
import sunsetsatellite.catalyst.multipart.api.Multipart;

public class MultipartInternalModel {

	public final Side side;
	public final Multipart part;
	public ModernMultipartBlockModel model;
	public int rotationX;
	public int rotationY;

	public MultipartInternalModel(ModernMultipartBlockModel model, Side side, Multipart part) {
		this.side = side;
		this.part = part;
		this.model = model;
		this.rotationX = 0;
		this.rotationY = 0;
	}
}
